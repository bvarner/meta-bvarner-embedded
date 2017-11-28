DESCRIPTION = "Use a raspberry pi to control a relay for a garage door opener."
SECTION = "misc"
HOMEPAGE = "https://github.com/bvarner/pigaragedoor/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
	git://${GO_IMPORT} \
	file://systemd-units/pigaragedoor.service \
	file://avahi/pigaragedoor.service \
"

SRCREV = "${AUTOREV}"

GO_IMPORT = "github.com/bvarner/pigaragedoor"
GO_INSTALL = "${GO_IMPORT}"

DEPENDS = "\
	go-rpigpio \
	avahi \
"

RDEPENDS_${PN}_append = "\
	avahi-daemon \
	avahi-autoipd \
"

inherit go systemd

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/pigaragedoor.service ${D}${systemd_unitdir}/system
	
	install -d ${D}${sysconfdir}/avahi/services
	install -m 0644 ${WORKDIR}/avahi/pigaragedoor.service ${D}${sysconfdir}/avahi/services
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "pigaragedoor.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

# The file-rdeps is picking up a dependency to 'bash' from the shell-script to build this with travis. Yick.
INSANE_SKIP_${PN}-dev = "file-rdeps"