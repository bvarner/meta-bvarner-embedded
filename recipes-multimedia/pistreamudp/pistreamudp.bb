SUMMARY = "A service that streams /dev/video0 to UDP in an mpegts."
DESCRIPTION = "A service which streams /dev/video0 to an mpegts containing \
               H.264 video encoded with a raspberrypi's omx using ffmpeg."
HOMEPAGE = "https://bvarner.github.io"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS = "\
	ffmpeg \
"

SRC_URI = "\
	file://pistreamudp.sh \
	file://systemd-units/pistreamudp.service \
"

PR = "rc1"

inherit systemd

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/pistreamudp.service ${D}${systemd_unitdir}/system
	
	install -d ${D}${base_bindir}	
	install -m 0755 ${WORKDIR}/pistreamudp.sh ${D}${base_bindir}
}

FILES_${PN} =+ "${systemd_unitdir}/system/pistreamudp.service"

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "pistreamudp.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

