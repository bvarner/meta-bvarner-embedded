FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://hx711.cfg \
            file://hx711-rocketstand-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
            file://hx711.c;subdir=drivers/iio/adc \
"

KERNEL_DEVICETREE += "overlays/hx711-rocketstand.dtbo"
KERNEL_MODULE_AUTOLOAD += "iio-trig-sysfs"
KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2"
