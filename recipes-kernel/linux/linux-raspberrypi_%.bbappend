FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# Always include these files, building the drivers as modules, and compiling the device tree overlays.
# The overlays are then activated using the bsp bootfiles, by adding the dtoverlay based upon a value set in the project local.conf.

SRC_URI += "file://hx711.cfg \
            file://hx711-rocketstand-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
            file://srf04.cfg \
            file://srf04-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
            file://4channel-relay-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
            file://ads1015.cfg \
            file://ads1115-pidroponic-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
"

KERNEL_DEVICETREE += "overlays/hx711-rocketstand.dtbo overlays/srf04.dtbo overlays/4channel-relay.dtbo overlays/ads1115-pidroponic.dtbo "

KERNEL_MODULE_AUTOLOAD  += "iio-trig-sysfs "
KERNEL_MODULE_PROBECONF += "iio-trig-sysfs "

KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2 "
KERNEL_MODULE_PROBECONF += "bcm2835-v4l2 "

KERNEL_MODULE_AUTOLOAD += "ntc-thermistor "
KERNEL_MODULE_PROBECONF += "ntc-thermistor "

KERNEL_MODULE_AUTOLOAD += "iio_hwmon "
KERNEL_MODULE_PROBECONF += "iio_hwmon "

KERNEL_MODULE_AUTOLOAD += "i2c-dev "
KERNEL_MODULE_PROBECONF += "i2c-dev "

KERNEL_MODULE_AUTOLOAD += "ti-ads1015 "
KERNEL_MODULE_PROBECONF += "ti-ads1015 "
