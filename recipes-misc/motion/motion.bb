SUMMARY = "Motion, a software motion detector."
DESCRIPTION = "Motion detects motion on image capture devices."

HOMEPAGE = "https://motion-project.github.io"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS = "\
	ffmpeg \
	sqlite3 \
	libjpeg-turbo \
"

RDEPENDS_${PN}_append_raspberrypi = "\
	userland \
"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/Motion-Project/motion.git \
           file://0001-Updated-configure.ac-to-properly-handle-BIN_PATH.patch \
           "

PR = "master"

inherit autotools gettext pkgconfig systemd

S = "${WORKDIR}/git"
B = "${WORKDIR}/git"

EXTRA_OECONF_append = ""
EXTRA_OECONF_append_raspberrypi = "\
    --with-mmal-include=${STAGING_DIR_TARGET}/usr/include \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/motion.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "motion.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"
