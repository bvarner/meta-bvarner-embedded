FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://hx711.cfg \
            file://hx711-rocketstand-overlay.dts;subdir=git/arch/${ARCH}/boot/dts/overlays \
"

KERNEL_DEVICETREE += "overlays/hx711-rocketstand.dtbo"
