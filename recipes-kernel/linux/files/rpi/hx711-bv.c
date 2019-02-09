#include <linux/delay.h>
#include <linux/err.h>
#include <linux/interrupt.h>
#include <linux/irqreturn.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/platform_device.h>
#include <linux/preempt.h>
#include <linux/semaphore.h>
#include <linux/spinlock.h>
#include <linux/time.h>
#include <linux/gpio/consumer.h>
#include <linux/iio/iio.h>
#include <linux/iio/sysfs.h>
#include <linux/regulator/consumer.h>

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Andreas Klinger <ak@it-klinger.de>");
MODULE_AUTHOR("Bryan Varner <bryan@varnernet.com>");
MODULE_DESCRIPTION("HX711 bitbanging driver - ADC for weight cells");
MODULE_ALIAS("platform:hx711-gpio");


#define CHANNEL_A	0
#define CHANNEL_B	1

#define GAIN_32		32
#define GAIN_64		64
#define GAIN_128	128

struct hx711_channel_data {
	int	 			channel;
	int				gain;
	int				scale;
	int				pulses;
	bool			enabled;
	bool			gain_set;
	int				value;
};

static struct hx711_channel_data hx711_channel_data[3] = {
	{CHANNEL_A, GAIN_128, 0, 1, false, true, -1},
	{CHANNEL_A, GAIN_64,  0, 3, false, false, -1},
	{CHANNEL_B, GAIN_32,  0, 2, false, true, -1},
};

struct hx711_data {
	struct device		*dev;
	struct gpio_desc	*gpiod_pd_sck;
	struct gpio_desc	*gpiod_dout;
	struct regulator	*reg_avdd;
	
	struct semaphore	raw_io_sem;
	spinlock_t			lock;
	int					irq;
	bool				reset;
	int					next_channel;
};


static ssize_t in_voltage_scale_available_show(struct device *dev,
				struct device_attribute *attr,
				char *buf)
{
	struct iio_dev_attr *iio_attr = to_iio_dev_attr(attr);
	int channel = iio_attr->address;
	int i, len = 0;

	for (i = 0; i < ARRAY_SIZE(hx711_channel_data); i++)
		if (hx711_channel_data[i].channel == channel)
			len += sprintf(buf + len, "0.%09d ",
					hx711_channel_data[i].scale);

	len += sprintf(buf + len, "\n");

	return len;
}

static IIO_DEVICE_ATTR(in_voltage0_scale_available, S_IRUGO,
	in_voltage_scale_available_show, NULL, CHANNEL_A);
static IIO_DEVICE_ATTR(in_voltage1_scale_available, S_IRUGO,
	in_voltage_scale_available_show, NULL, CHANNEL_B);


static struct attribute *hx711_attributes[] = {
	&iio_dev_attr_in_voltage0_scale_available.dev_attr.attr,
	&iio_dev_attr_in_voltage1_scale_available.dev_attr.attr,
	NULL,
};

static const struct attribute_group hx711_attribute_group = {
	.attrs = hx711_attributes,
};

/* Macro to define the channels */
#define HX711_CHANNEL(ch, idx) { \
	.type = IIO_VOLTAGE, \
	.channel = ch, \
	.indexed = 1, \
	.info_mask_separate = BIT(IIO_CHAN_INFO_RAW) | \
						  BIT(IIO_CHAN_INFO_SCALE) | \
						  BIT(IIO_CHAN_INFO_HARDWAREGAIN) | \
						  BIT(IIO_CHAN_INFO_ENABLE), \
	.scan_index = idx, \
	.scan_type = { \
		.sign = 'u', \
		.realbits = 24, \
		.storagebits = 32, \
		.endianness = IIO_CPU, \
	}, \
}

static const struct iio_chan_spec hx711_chan_spec[] = {
	HX711_CHANNEL(CHANNEL_A, 0),
	HX711_CHANNEL(CHANNEL_B, 1),
	IIO_CHAN_SOFT_TIMESTAMP(2),
};



static int hx711_cycle(struct hx711_data *hx711_data) {
	int val;
	struct timespec pulse_start, pulse_checkpoint;
	long pulse_high_target = 1200;
	long pulse_low_enforce = 200;

	/*
	 * if preempted for more then 60us while PD_SCK is high:
	 * hx711 is going in reset
	 * ==> measuring is false
	 */
	preempt_disable();
	getnstimeofday(&pulse_start);
	
	// Push the clock high.
	gpiod_set_value(hx711_data->gpiod_pd_sck, 1);
	ndelay(100); // wait 0.1 us for dout to pull high || low.
	
	// Read the data output value.
	val = gpiod_get_value(hx711_data->gpiod_dout);
	
	/* 
	 * Data sheet says clock high time for pd_sck:
	 *   minimum: 200   ns
	 *   typical: 1000  ns
	 *   maximum: 50000 ns
	 * If we waited at least 1us - 100ns = (900ns) we'll be in line with the
	 * typcial values from the datasheet.
	 */
	getnstimeofday(&pulse_checkpoint);
	// T3 time in the datasheet says we should be high for at least 200ns.
	// typically, 1000ns, with a maximum of 50000ns.
	
	// We'll do some math to make sure we're high at least pulse_high_target.
	ndelay(pulse_high_target - (pulse_checkpoint.tv_nsec - pulse_start.tv_nsec));
	
	/*
	 * here we are not waiting for 0.2 us as suggested by the datasheet,
	 * because the oscilloscope showed in a test scenario
	 * at least 1.15 us for PD_SCK high (T3 in datasheet)
	 * and 0.56 us for PD_SCK low on TI Sitara with 800 MHz
	 */
	gpiod_set_value(hx711_data->gpiod_pd_sck, 0);
	
	// Unless we really want to.
	if (pulse_low_enforce) {
		ndelay(pulse_low_enforce);
	}
	
	// reenable preemption
	preempt_enable();

	return val;
}

//
// Assumes that the conversation call is made after the data pin pulls low.
// 
static int hx711_read(struct hx711_data *hx711_data) {
	int i, ret;
	int value = 0;
	unsigned long flags;
	
	// Critical Section
	dev_printk(KERN_INFO, hx711_data->dev, "spin_lock\n");
	spin_lock_irqsave(&hx711_data->lock, flags);
	dev_printk(KERN_INFO, hx711_data->dev, "  ->lock Acquired.\n");

	// paranoia
	ret = gpiod_get_value(hx711_data->gpiod_dout);
	if (ret) {
		printk(KERN_ERR "data pin not ready.\n");
		ret = -EIO;
	} else {
		// Accumulate everything into 'ret', which should be 0
		// Shift 24 bits in...
		for (i = 0; i < 24; i++) {
			value <<= 1;
			ret = hx711_cycle(hx711_data);
			if (ret) {
				value++;
			}
		}
		
		// Twos compliment
		value ^= 0x800000;
		
		dev_printk(KERN_INFO, hx711_data->dev, "read sensor data: %d\n", value);
	}
	
	// Since there is no maximum time between clock pulses, leave the chip
	// waiting for the pulses to select the next channel and unlock the critical
	// section
	spin_unlock_irqrestore(&hx711_data->lock, flags);
	dev_printk(KERN_INFO, hx711_data->dev, "spin_unlock_irqrestore\n");
	
	return value;
}

static int hx711_read_raw(struct iio_dev *indio_dev,
				const struct iio_chan_spec *chan,
				int *val, int *val2, long mask)
{
	struct hx711_data *hx711_data = iio_priv(indio_dev);
	int i, channel_idx = 0, ret = -EINVAL;
	unsigned long flags;
	
	switch (mask) {
	case IIO_CHAN_INFO_RAW: 
		dev_printk(KERN_INFO, hx711_data->dev, "IIO_CHAN_INFO_RAW: %d\n", chan->channel);

		// Acquire the spinlock
		dev_printk(KERN_INFO, hx711_data->dev, "spin_lock\n");
		spin_lock_irqsave(&hx711_data->lock, flags);
		dev_printk(KERN_INFO, hx711_data->dev, "  ->lock Acquired.\n");
		
		// pulse to select channel & gain
		for (i = 0; i < ARRAY_SIZE(hx711_channel_data); i++) {
			if (hx711_channel_data[i].channel == chan->channel &&
				hx711_channel_data[i].gain_set)
			{
				channel_idx = i;
				break;
			}
		}
		hx711_data->next_channel = channel_idx;
		dev_printk(KERN_INFO, hx711_data->dev, "Next channel targets hx711_channel_data[%d]\n", channel_idx);
		
		
		for (i = hx711_channel_data[channel_idx].pulses; i > 0; i--) {
			// Before the final pulse, enable the IRQ
			if (i == 1) {
				dev_printk(KERN_INFO, hx711_data->dev, "enable_irq\n");
				enable_irq(hx711_data->irq);
			}
			
			// Each pulse should have DOUT returning high.
			dev_printk(KERN_INFO, hx711_data->dev, "pulse...\n");
			if (!hx711_cycle(hx711_data)) {
				dev_printk(KERN_INFO, hx711_data->dev, "   -> data was low during conversation pulse setup\n");
				// DOUT was low during channel / gain selection
				ret = -EINVAL;
				i = 0;
			}
		}
		
		// Release the spinlock
		dev_printk(KERN_INFO, hx711_data->dev, "spin_unlock\n");
		spin_unlock_irqrestore(&hx711_data->lock, flags);
		dev_printk(KERN_INFO, hx711_data->dev, "down_timeout(raw_io_sem)...\n");

		// Next hardware interrupt will read the value into the correct channel.
		ret = down_timeout(&hx711_data->raw_io_sem, usecs_to_jiffies(150000));
		if (ret < 0) {
			dev_printk(KERN_WARNING, hx711_data->dev, "timeout waiting for semaphore\n");
			ret = -EIO;
		} else {
			// read the value
			*val = hx711_channel_data[channel_idx].value;
			dev_printk(KERN_INFO, hx711_data->dev, "value = %d\n", hx711_channel_data[channel_idx].value);
			ret = IIO_VAL_INT;
		}
		
		return ret;
	default: 
		return -EINVAL;
	}
}




static const struct iio_info hx711_iio_info = {
	.driver_module		= THIS_MODULE,
// TODO: .event_attrs
	.attrs			= &hx711_attribute_group,
	.read_raw		= hx711_read_raw,
//	.write_raw		= hx711_write_raw,
//	.write_raw_get_fmt	= hx711_write_raw_get_fmt,
};


/** Called as a hardware interrupt **/
static irqreturn_t hx711_interrupt(int irq, void *private) {
	struct iio_dev *indio_dev = private;
	struct hx711_data *hx711_data = iio_priv(indio_dev);
	int ret;
	
	dev_printk(KERN_INFO, hx711_data->dev, "hardware interrupt handler\n");
	disable_irq_nosync(hx711_data->irq);
	
	// TODO: Move the below into a soft-irq (iio trigger) handler
	
	// Consume the data, leave the chip 'hanging' waiting for a conversation
	ret = hx711_read(hx711_data);
	
	if (hx711_data->reset) {
		hx711_data->reset = false;
	} else if (hx711_data->next_channel >= 0) {
		dev_printk(KERN_INFO, hx711_data->dev, "stashing %d into hx711_channel_data[%d]", ret, hx711_data->next_channel);
		hx711_channel_data[hx711_data->next_channel].value = ret;
		hx711_data->next_channel = -1;

		// Signal that the data is there.
		dev_printk(KERN_INFO, hx711_data->dev, "raw_io_sem up()\n");
		up(&hx711_data->raw_io_sem);
	}
	
	return IRQ_HANDLED;
}



static int hx711_reset(struct hx711_data *hx711_data) {
	dev_printk(KERN_INFO, hx711_data->dev, "reset()\n");

	// Disable the IRQ, and wait for any handlers to complete.
	disable_irq(hx711_data->irq);
	
	// Push the pin High to force a reset.
	gpiod_set_value(hx711_data->gpiod_pd_sck, 1);
	msleep(10); // longer than 60us, then pull low to reset.
	hx711_data->reset = true;
	
	// Enable the IRQ & pull the clock low to initiate a conversation.
	dev_printk(KERN_INFO, hx711_data->dev, "reset() complete\n");
	
	enable_irq(hx711_data->irq);
	gpiod_set_value(hx711_data->gpiod_pd_sck, 0);
	
	return 0;
}

static int hx711_probe(struct platform_device *pdev) {
	struct device *dev = &pdev->dev;
	struct hx711_data *hx711_data;
	struct iio_dev *indio_dev;
	int ret;
	int i;
	
	dev_printk(KERN_INFO, dev, "probe\n");
	
	indio_dev = devm_iio_device_alloc(dev, sizeof(struct hx711_data));
	if (!indio_dev) {
		dev_err(dev, "failed to allocate IIO device for hx711\n");
		return -ENOMEM;
	}
	
	hx711_data = iio_priv(indio_dev);
	hx711_data->dev = dev;
	
	hx711_data->next_channel = -1;
	sema_init(&hx711_data->raw_io_sem, 0);
	spin_lock_init(&hx711_data->lock);
	
	// PD_SCK clock pin for the HX711. 
	// Serves for power management and clocking, as an output.
	hx711_data->gpiod_pd_sck = devm_gpiod_get(dev, "sck", GPIOD_OUT_LOW);
	if (IS_ERR(hx711_data->gpiod_pd_sck)) {
		dev_err(dev, "failed to get sck-gpiod: err=%ld\n",
					PTR_ERR(hx711_data->gpiod_pd_sck));
		return PTR_ERR(hx711_data->gpiod_pd_sck);
	}
	
	/*
	 * DOUT stands for serial data output of HX711
	 * for the driver it is an input
	 */
	hx711_data->gpiod_dout = devm_gpiod_get(dev, "dout", GPIOD_IN);
	if (IS_ERR(hx711_data->gpiod_dout)) {
		dev_err(dev, "failed to get dout-gpiod: err=%ld\n",
					PTR_ERR(hx711_data->gpiod_dout));
		return PTR_ERR(hx711_data->gpiod_dout);
	}

	hx711_data->reg_avdd = devm_regulator_get(dev, "avdd");
	if (IS_ERR(hx711_data->reg_avdd))
		return PTR_ERR(hx711_data->reg_avdd);

	ret = regulator_enable(hx711_data->reg_avdd);
	if (ret < 0)
		return ret;	
	
	/*
	 * with
	 * full scale differential input range: AVDD / GAIN
	 * full scale output data: 2^24
	 * we can say:
	 *     AVDD / GAIN = 2^24
	 * therefore:
	 *     1 LSB = AVDD / GAIN / 2^24
	 * AVDD is in uV, but we need 10^-9 mV
	 * approximately to fit into a 32 bit number:
	 * 1 LSB = (AVDD * 100) / GAIN / 1678 [10^-9 mV]
	 */
	ret = regulator_get_voltage(hx711_data->reg_avdd);
	if (ret < 0) {
		regulator_disable(hx711_data->reg_avdd);
		return ret;
	}
	/* we need 10^-9 mV */
	ret *= 100;
	
	for (i = 0; i < ARRAY_SIZE(hx711_channel_data); i++) {
		hx711_channel_data[i].scale = 
			ret / hx711_channel_data[i].gain / 1678;
	}
	
	hx711_data->irq = gpiod_to_irq(hx711_data->gpiod_dout);
	if (hx711_data->irq < 0) {
		hx711_data->irq = -ENODEV;
		dev_warn(dev, "Couldn't get IRQ for the device\n");
	} else {
		ret = devm_request_irq(dev, hx711_data->irq, hx711_interrupt,
								IRQF_TRIGGER_FALLING,
								pdev->dev.driver->name, indio_dev);
		
		if (ret < 0) {
			hx711_data->irq = -ENODEV;
			dev_warn(dev, "Couldn't setup IRQ Handler. Hardware Trigger Disabled\n");
		}
	}
	
	platform_set_drvdata(pdev, indio_dev);

	indio_dev->name = "hx711";
	indio_dev->dev.parent = &pdev->dev;
	indio_dev->info = &hx711_iio_info;
	indio_dev->modes = INDIO_DIRECT_MODE;
	indio_dev->channels = hx711_chan_spec;
	indio_dev->num_channels = ARRAY_SIZE(hx711_chan_spec);
	
	ret = iio_device_register(indio_dev);
	if (ret < 0) {
		dev_err(dev, "Couldn't register the device\n");
		regulator_disable(hx711_data->reg_avdd);
	}
	
	dev_printk(KERN_INFO, dev, "probe complete\n");
	
	hx711_reset(hx711_data);
	
	return ret;
}

static int hx711_remove(struct platform_device *pdev) {
	return 0;
}

static void hx711_shutdown(struct platform_device *pdev) {
	struct hx711_data *hx711_data;
	struct iio_dev *indio_dev;
	
	indio_dev = platform_get_drvdata(pdev);
	hx711_data = iio_priv(indio_dev);
	
	dev_printk(KERN_INFO, hx711_data->dev, "shutdown\n");

	gpiod_set_value(hx711_data->gpiod_pd_sck, 1);
	return;
}

static int hx711_suspend(struct platform_device *pdev, pm_message_t state) {
	struct hx711_data *hx711_data;
	struct iio_dev *indio_dev;

	indio_dev = platform_get_drvdata(pdev);
	hx711_data = iio_priv(indio_dev);

	dev_printk(KERN_INFO, hx711_data->dev, "suspend\n");
	
	gpiod_set_value(hx711_data->gpiod_pd_sck, 1);
	return 0;
}

static int hx711_resume(struct platform_device *pdev) {
	struct hx711_data *hx711_data;
	struct iio_dev *indio_dev;

	indio_dev = platform_get_drvdata(pdev);
	hx711_data = iio_priv(indio_dev);
	
	dev_printk(KERN_INFO, hx711_data->dev, "resume\n");

	return hx711_reset(hx711_data);
}	

static const struct of_device_id of_hx711_match[] = {
	{ .compatible = "avia,hx711", },
	{},
};

MODULE_DEVICE_TABLE(of, of_hx711_match);

static struct platform_driver hx711_driver = {
	.probe		= hx711_probe,
	.remove		= hx711_remove,
	.shutdown	= hx711_shutdown,
	.suspend	= hx711_suspend,
	.resume		= hx711_resume,
	.driver		= {
		.name		= "hx711-gpio",
		.of_match_table = of_hx711_match,
	},
};

module_platform_driver(hx711_driver);