DESCRIPTION = "Use a raspberry pi to control a relay for a garage door opener."
SECTION = "misc"
HOMEPAGE = "https://github.com/bvarner/pi-launch-control/"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

SRCNAME = "pi-launch-control"
PKG_NAME = "github.com/bvarner/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME};branch=feature/es6app \
	file://systemd-units/pi-launch-control.service \
	file://avahi/pi-launch-control.service \
"
SRCREV = "${AUTOREV}"

DEPENDS = "\
	avahi \
"

RDEPENDS_${PN}_append = "\
	avahi-daemon \
	avahi-autoipd \
"

RDEPENDS_${PN}-staticdev_append = "\
	perl \
"

## begin godep hacking
# godep is being really stupid as a class. It's deleting the Gopkg.toml and lock.
#inherit gorice godep systemd
# So we'll duplicate most of it's functionality here.
DEPENDS_append = " go-dep-native"
do_compile_prepend() {
    ( cd ${WORKDIR}/build/src/${GO_IMPORT} && dep ensure -v )
}
## end godep hacking


inherit gorice systemd

GO_LINKSHARED = ''
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

# Add the pi-launch-control import path to the rice command.
RICE_ARGS = "-v -i ${GO_IMPORT}/pi-launch-control"
# Set it up to append to the exec in a zip format.
GO_RICE_EMBEDTYPE = 'go'
#GO_RICE_APPEND = 'yes'




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