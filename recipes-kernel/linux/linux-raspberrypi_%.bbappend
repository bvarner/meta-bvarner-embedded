FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# Always include these files, building the drivers as modules, and compiling the device tree overlays.
# The overlays are then activated using the bsp bootfiles, by adding the dtoverlay based upon a value set in the project local.conf.

SRC_URI += "file://hx711.cfg \
            file://hx711-rocketstand-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
            file://srf04.cfg \
            file://srf04-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
"

KERNEL_DEVICETREE += "overlays/hx711-rocketstand.dtbo overlays/srf04.dtbo"
KERNEL_MODULE_AUTOLOAD += "iio-trig-sysfs"
KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2"
