SUMMARY = "An image for booting a Raspberry Pi to control a relay board hooked to a garage door opener."
HOMEPAGE = "http://bvarner.github.io"
LICENSE = "MIT"

# Sets us up to use ext4 and generate an rpi-sdimg that'll boot
SDIMG_ROOTFS_TYPE = "ext4"
IMAGE_FSTYPES += "rpi-sdimg"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

IMAGE_LINGUAS = "en-us"

IMAGE_FEATURES += "read-only-rootfs"
#EXTRA_IMAGE_FEATURES += "debug-tweaks"

# Now that all these things are set, include the hwup image.
include recipes-core/images/rpi-hwup-image.bb

# Core Image stuff...
IMAGE_INSTALL += " \
	tzdata \
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
	pigaragedoor \
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
	rm -f ${IMAGE_ROOTFS}/etc/systemd/system/getty.target.wants/*.service
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
"

export IMAGE_BASENAME = "pigaragedoor-image"
