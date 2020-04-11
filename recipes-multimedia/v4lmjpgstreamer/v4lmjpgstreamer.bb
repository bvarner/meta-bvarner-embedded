DESCRIPTION = "MJPG Stream server for v4l devices."
SECTION = "misc"
HOMEPAGE = "https://github.com/bvarner/v4lmjpgstreamer/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRCNAME = "v4lmjpgstreamer"
PKG_NAME = "github.com/bvarner/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
	file://systemd-units/${SRCNAME}.service \
	file://avahi/${SRCNAME}.service \
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
	bash \
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

inherit go systemd

GO_LINKSHARED = ''
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/${SRCNAME}.service ${D}${systemd_unitdir}/system
	
	install -d ${D}${sysconfdir}/avahi/services
	install -m 0644 ${WORKDIR}/avahi/${SRCNAME}.service ${D}${sysconfdir}/avahi/services

	install -d ${D}${sysconfdir}/ssl/certs
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "${SRCNAME}.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"