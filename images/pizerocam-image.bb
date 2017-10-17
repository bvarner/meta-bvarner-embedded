inherit core-image

SUMMARY = "An image for booting a Raspberry pi Zero to use motion for timecapture"
HOMEPAGE = "http://bvarner.github.io"
LICENSE = "MIT"

EXTRA_IMAGE_FEATURES += "debug-tweaks"

IMAGE_FSTYPES += "ext4 rpi-sdimg"
SDIMG_ROOTFS_TYPE = "ext4"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

IMAGE_LINGUAS = "en-us"

# Core Image stuff...
IMAGE_INSTALL += " \
	openssh openssh-keygen openssh-sftp-server \
	tzdata \
	kernel-modules \
"

# WiFi Support
IMAGE_INSTALL += " \
    iw \
    linux-firmware-bcm43430 \
    linux-firmware-ralink \
    linux-firmware-rtl8192ce \
    linux-firmware-rtl8192cu \
    linux-firmware-rtl8192su \
    wireless-tools \
    wpa-supplicant \
"

# This image pulls in our ffmpeg, with a twist.
IMAGE_INSTALL += " \
    ffmpeg \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
}

disable_bootlogd() {
    echo BOOTLOGD_ENABLE=no > ${IMAGE_ROOTFS}/etc/default/bootlogd
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone ; \
    disable_bootlogd ; \
"
export IMAGE_BASENAME = "pizerocam-image"
