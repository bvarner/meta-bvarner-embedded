DESCRIPTION = "Use a raspberry pi to control a relay for a garage door opener."
SECTION = "misc"
HOMEPAGE = "https://github.com/bvarner/pi-launch-control/"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

SRCNAME = "pi-launch-control"
PKG_NAME = "github.com/bvarner/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME};branch=develop \
	file://systemd-units/pi-launch-control.service \
	file://avahi/pi-launch-control.service \
"
SRCREV = "${AUTOREV}"

DEPENDS = "\
	periph \
	go-raspicam \
	avahi \
"

RDEPENDS_${PN}_append = "\
	avahi-daemon \
	avahi-autoipd \
"

inherit go.rice systemd

GO_LINKSHARED = ''
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

RICE_ARGS = "-i ${GO_IMPORT}/pi-launch-control"

GO_RICE_APPEND = 'yes'

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/pi-launch-control.service ${D}${systemd_unitdir}/system
	
	install -d ${D}${sysconfdir}/avahi/services
	install -m 0644 ${WORKDIR}/avahi/pi-launch-control.service ${D}${sysconfdir}/avahi/services

	install -d ${D}${sysconfdir}/ssl/certs
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "pi-launch-control.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

# The file-rdeps is picking up a dependency to 'bash' from the shell-script to build this with travis. Yick.
INSANE_SKIP_${PN}-dev = "file-rdeps"