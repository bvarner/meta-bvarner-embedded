SUMMARY = "An image for controlling hydroponic systems with a raspberry pi."
HOMEPAGE = "http://bvarner.github.io/"
LICENSE = "MIT"

# Sets us up to use ext4 and generate an rpi-sdimg that'll boot
SDIMG_ROOTFS_TYPE = "ext4"
IMAGE_FSTYPES += "wic wic.bmap"
WKS_FILE = "sdimage-raspberrypi-persistentvar.wks"

KERNEL_DEVICETREE += " overlays/srf04.dtbo overlays/4channel-relay.dtbo overlays/ads1115-pidroponic.dtbo"

# Raspberry pi images...
DEPENDS += "bcm2835-bootfiles"

IMAGE_LINGUAS = "en-us"

IMAGE_FEATURES += "read-only-rootfs"
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
	prometheus \
	prometheus-node-exporter \
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
	wireless-regdb \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/UTC ${IMAGE_ROOTFS}/etc/localtime
}

# Sets up an /etc/wpa_supplicant directory, where you can put configurations for 
# wpa_supplicant for your network devices. 
# Enables wpa_supplicant for 802.11 on wlan0
setup_wpa_supplicant() {
	# Configure the systemd unit
	mkdir -p ${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants
	ln -sf /lib/systemd/system/wpa_supplicant-nl80211@.service ${IMAGE_ROOTFS}/etc/systemd/system/multi-user.target.wants/wpa_supplicant-nl80211@wlan0.service
	
	# Create the config directory and seed with a proper nl80211-wlan0 conf.
	mkdir -p ${IMAGE_ROOTFS}/etc/wpa_supplicant
	
	echo 'ctrl_interface=/var/run/wpa_supplicant' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
	echo 'ctrl_interface_group=0' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
	echo 'update_config=1' >> ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant-nl80211-wlan0.conf
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

prometheus_config() {
	echo "Configuring Prometheus"
	mkdir -p ${IMAGE_ROOTFS}/etc/prometheus

	# Global Config
	echo 'global:' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '  scrape_interval: 10s' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '  external_labels:' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '    monitor: '\''pidroponics-monitor'\' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	
	# Scrape the node-exporter. (using global config)
	echo 'scrape_configs:' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '  - job_name: '\''system'\' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '    static_configs:' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '      - targets: ['\''localhost:9100'\'']' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	
	# Scrape pidroponics.
	echo '  - job_name: '\''pidroponics'\' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '    scrape_interval: 2s' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '    static_configs:' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '      - targets: ['\''localhost:443'\'']' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '    scheme: https' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '    tls_config:' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
	echo '      insecure_skip_verify: true' >> ${IMAGE_ROOTFS}/etc/prometheus/prometheus.yml
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone ; \
    setup_wpa_supplicant ; \
    disable_gettys ; \
    setup_certs ;\
    prometheus_config ;\
"

export IMAGE_BASENAME = "pidroponics-image"
