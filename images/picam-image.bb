SUMMARY = "An image for booting a Raspberry Pi to stream video from the cameara"
HOMEPAGE = "http://bvarner.github.io"
LICENSE = "MIT"

# Sets us up to use ext4 and generate an rpi-sdimg that'll boot
SDIMG_ROOTFS_TYPE = "ext4"
IMAGE_FSTYPES += "rpi-sdimg"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2"
KERNEL_MODULE_PROBECONF += "bcm2835-v4l2"

IMAGE_LINGUAS = "en-us"

#IMAGE_FEATURES += "read-only-rootfs"
EXTRA_IMAGE_FEATURES += "debug-tweaks"


# Now that all these things are set, include the hwup image.
include recipes-core/images/rpi-hwup-image.bb

# Core Image stuff...
IMAGE_INSTALL += " \
	tzdata \
    devmem2 \
    ethtool \
    i2c-tools \
    iproute2 \
    procps \
    sysfsutils \
    unzip \
    util-linux \
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
    dhcp-client \
    wpa-supplicant \
"

# Use our pistream package.
IMAGE_INSTALL += " \
    pistream \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
}

load_v4l_driver() {
	grep 'bcm2835-v4l2' ${IMAGE_ROOTFS}/etc/modules-load.d/piv4l2.conf || echo 'bcm2835-v4l2' >> ${IMAGE_ROOTFS}/etc/modules-load.d/piv4l2.conf
}

# ffmpeg expects the libs in /opt/vc/lib, even though they're in /usr/lib/...
create_opt_lib_dir() {
	mkdir -p ${IMAGE_ROOTFS}/opt/vc/lib
	ln -sf /usr/lib/libopenmaxil.so ${IMAGE_ROOTFS}/opt/vc/lib/libopenmaxil.so
	ln -sf /usr/lib/libbcm_host.so ${IMAGE_ROOTFS}/opt/vc/lib/libbcm_host.so
}

# Sets up an /etc/wpa_supplicant directory, where you can put configurations for 
# wpa_supplicant for your network devices. 
# Enables wpa_supplicant for 802.11 on wlan0
setup_wpa_supplicant() {
	mkdir -p ${IMAGE_ROOTFS}/etc/wpa_supplicant
	cp ${IMAGE_ROOTFS}/etc/wpa_supplicant.conf ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf

	ln -sf /lib/systemd/system/wpa_supplicant-nl80211@.service ${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants/wpa_supplicant-nl80211@wlan0.service
}

disable_gettys() {
	echo "disabling gettys..."
#	rm -f ${IMAGE_ROOTFS}/etc/systemd/system/getty.target.wants/*.service
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone ; \
    load_v4l_driver ; \
    setup_wpa_supplicant ; \    
    disable_gettys ; \
"

export IMAGE_BASENAME = "picam-image"
