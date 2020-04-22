SUMMARY = "An image for controlling hydroponic systems with a raspberry pi."
HOMEPAGE = "http://bvarner.github.io/"
LICENSE = "MIT"

# Sets us up to use ext4 and generate an rpi-sdimg that'll boot
SDIMG_ROOTFS_TYPE = "ext4"
IMAGE_FSTYPES += "rpi-sdimg"

KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2 i2c-dev ti-ads1015 "
KERNEL_MODULE_PROBECONF += "bcm2835-v4l2 i2c-dev ti-ads1015 "
KERNEL_DEVICETREE += " overlays/srf04.dtbo overlays/4channel-relay.dtbo overlays/ads1115.dtbo"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

IMAGE_LINGUAS = "en-us"

# For now, let us write.
#IMAGE_FEATURES += "read-only-rootfs"
IMAGE_FEATURES += "ssh-server-openssh"
EXTRA_IMAGE_FEATURES += "debug-tweaks"

IMAGE_FEATURES_remove += "splash"

# Now that all these things are set, include the hwup image.
include recipes-core/images/core-image-minimal.bb

# Core Image stuff...
IMAGE_INSTALL += " \
	${MACHINE_EXTRA_RRECOMMENDS} \
	kernel-modules \
	udev-rules-rpi \
	tzdata \
    devmem2 \
    i2c-tools \
    v4l-utils \
    nano \
"

IMAGE_INSTALL += " \
	userland \
	pidroponics \
"

# WiFi Support
IMAGE_INSTALL += " \
	iw \
    linux-firmware-ralink \
    linux-firmware-rtl8192ce \
    linux-firmware-rtl8192cu \
    linux-firmware-rtl8192su \
    wpa-supplicant \
	avahi-daemon \
	avahi-autoipd \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/UTC ${IMAGE_ROOTFS}/etc/localtime
}

# Sets up an /etc/wpa_supplicant directory, where you can put configurations for 
# wpa_supplicant for your network devices. 
# Enables wpa_supplicant for 802.11 on wlan0
setup_wpa_supplicant() {
	mkdir -p ${IMAGE_ROOTFS}/etc/wpa_supplicant
	cp ${IMAGE_ROOTFS}/etc/wpa_supplicant.conf ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf

	mkdir -p ${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants
	ln -sf /lib/systemd/system/wpa_supplicant-nl80211@.service ${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants/wpa_supplicant-nl80211@wlan0.service
}

disable_gettys() {
	echo "disabling gettys..."
#	rm -f ${IMAGE_ROOTFS}/etc/systemd/system/getty.target.wants/*.service
}

setup_certs() {
	echo "installing SSL certs..."
	mkdir -p ${IMAGE_ROOTFS}/etc/ssl/certs
	
	# Copy local pem files to....
	#cp /path/tofile/on/your/machine ${IMAGE_ROOTFS}/etc/ssl/certs/pi-launch-control.pem
	#cp /path/tofile/on/your/machine ${IMAGE_ROOTFS}/etc/ssl/certs/pi-launch-control-key.pem
}

setup_wifi() {
	echo "Setting up wifi..."	
	
# Copy the echo statements below, and uncomment them.
# Replace YOUR_SSID_NAME below with your actual SSID name.
# Replace the PSK_KEY with the value returned from using the `wpa_passphrase` utility to generate the PSK.

#	echo 'network={' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    ssid="YOUR_SSID_NAME"' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    psk=PSK_KEY' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '}' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf		
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone ; \
    setup_wpa_supplicant ; \
    disable_gettys ; \
    setup_wifi ; \
    setup_certs ;\
"

export IMAGE_BASENAME = "pidroponics-image"
