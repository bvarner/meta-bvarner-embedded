SUMMARY = "A service that streams /dev/video0 to tcp in an mpegts."
DESCRIPTION = "A service which streams /dev/video0 to an mpegts containing \
               H.264 video encoded with a raspberrypi's omx using ffmpeg."
HOMEPAGE = "https://bvarner.github.io"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS = "\
	ffmpeg \
	avahi \
	systemd \
"

RDEPENDS_${PN}_append = "\
	ffmpeg \
	avahi-daemon \
	avahi-autoipd \
"

SRC_URI = "\
	file://pistream.sh \
	file://systemd-units/pistream.service \
	file://pistream.service \
"

PR = "rc1"

inherit systemd

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/pistream.service ${D}${systemd_unitdir}/system
	
	install -d ${D}${base_bindir}	
	install -m 0755 ${WORKDIR}/pistream.sh ${D}${base_bindir}
	
	install -d ${D}${sysconfdir}/avahi/services
	install -m 0644 ${WORKDIR}/pistream.service ${D}${sysconfdir}/avahi/services
}

FILES_${PN} =+ "${systemd_unitdir}/system/pistream.service"

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "pistream.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

