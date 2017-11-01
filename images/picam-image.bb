SUMMARY = "An image for booting a Raspberry Pi to stream video from the cameara"
HOMEPAGE = "http://bvarner.github.io"
LICENSE = "MIT"

IMAGE_FSTYPES += "ext4 rpi-sdimg"
SDIMG_ROOTFS_TYPE = "ext4"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2"
KERNEL_MODULE_PROBECONF += "bcm2835-v4l2"

IMAGE_LINGUAS = "en-us"

IMAGE_FEATURES += "ssh-server-openssh"

# Now that all these things are set, include the hwup image.
include recipes-core/images/rpi-hwup-image.bb

# Core Image stuff...
IMAGE_INSTALL += " \
	tzdata \
	userland \
	bzip2 \
    devmem2 \
    dosfstools \
    ethtool \
    fbset \
    findutils \
    i2c-tools \
    iperf \
    iproute2 \
    less \
    memtester \
    nano \
    procps \
    rsync \
    sysfsutils \
    unzip \
    util-linux \
    wget \
    zip \
    v4l-utils \
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

# This image pulls in our ffmpeg.
IMAGE_INSTALL += " \
    ffmpeg \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
}

load_v4l_driver() {
	grep 'bcm2835-v4l2' ${IMAGE_ROOTFS}/etc/modules-load.d/piv4l2.conf || echo 'bcm2835-v4l2' >> ${IMAGE_ROOTFS}/etc/modules-load.d/piv4l2.conf
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone ; \
    load_v4l_driver ; \
"

export IMAGE_BASENAME = "picam-image"
