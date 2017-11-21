SUMMARY = "A 'garage door' opener web site."
DESCRIPTION = "An apache hosted garage door opener."

HOMEPAGE = "https://bvarner.github.io"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS = "\
	apache2 \
	php \
	avahi \
	wiringpi \
"

RDEPENDS_${PN}_append = "\
	apache2 \
	php \
	php-modphp \
	avahi-daemon \
	avahi-autoipd \
	wiringpi \
"

SRC_URI = "\
	file://pi-garage-door-init.sh \
	file://systemd-units/pi-garage-door.service \
	file://pi-garage-door.service \
	file://httpd.conf \
	file://site/css/style.css \
	file://site/js/jquery-1.10.2.min.js \
	file://site/js/script.js \
	file://site/apple-touch-icon-ipad.png \
	file://site/apple-touch-icon-iphone-retina-display.png \
	file://site/apple-touch-icon-iphone.png \
	file://site/index.php \
"

PR = "master"

inherit systemd

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/pi-garage-door.service ${D}${systemd_unitdir}/system
	
	install -d ${D}${base_bindir}	
	install -m 0755 ${WORKDIR}/pi-garage-door-init.sh ${D}${base_bindir}
	
	install -d ${D}${sysconfdir}/avahi/services
	install -m 0644 ${WORKDIR}/pi-garage-door.service ${D}${sysconfdir}/avahi/services
	
	install -d ${D}${sysconfdir}/pi-garage-door
	install -m 0644 ${WORKDIR}/httpd.conf ${D}${sysconfdir}/pi-garage-door
	
	install -d ${D}/var/www
	cp -r ${WORKDIR}/site/* ${D}/var/www
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "pi-garage-door.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"
