SUMMARY = "An image for booting a Raspberry Pi to control a relay board hooked to a garage door opener."
HOMEPAGE = "http://bvarner.github.io"
LICENSE = "MIT"

# Sets us up to use ext4 and generate an rpi-sdimg that'll boot
SDIMG_ROOTFS_TYPE = "ext4"
IMAGE_FSTYPES += "rpi-sdimg"

KERNEL_MODULE_AUTOLOAD += "bcm2835-v4l2"
KERNEL_MODULE_PROBECONF += "bcm2835-v4l2"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

IMAGE_LINGUAS = "en-us"

# For now, let us write.
#IMAGE_FEATURES += "read-only-rootfs"
IMAGE_FEATURES += "ssh-server-openssh"
EXTRA_IMAGE_FEATURES += "debug-tweaks"

# Now that all these things are set, include the hwup image.
include recipes-core/images/core-image-minimal.bb

# Core Image stuff...
IMAGE_INSTALL += " \
	kernel-modules \
	tzdata \
    devmem2 \
    i2c-tools \
    v4l-utils \
"

IMAGE_INSTALL += " \
    go-hx711 \
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
	avahi-daemon \
	avahi-autoipd \
"

IMAGE_INSTALL += " \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
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

setup_wifi() {
	echo "Setting up wifi..."	
	
# Sets up an ad-hoc SSID when no other networks are available.
#	echo 'ap_scan=2;' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo 'network={' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    ssid="RocketStand"' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    mode=1' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    frequency=2432' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    proto=RSN' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    key_mgmt=WPA-PSK' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    pairwise=CCMP' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    group=CCMP' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '    psk="ignition"' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
#	echo '}' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf		
	
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
"

export IMAGE_BASENAME = "rocketstand-image"
