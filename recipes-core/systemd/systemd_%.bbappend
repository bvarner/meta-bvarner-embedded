FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PACKAGECONFIG += "networkd resolved timesyncd"
PACKAGECONFIG_remove = "vconsole"

SRC_URI += "file://wlan.network"

do_install_append() {
	install -d ${D}${sysconfdir}/systemd/network/
	install -m 0644 ${WORKDIR}/*.network ${D}${sysconfdir}/systemd/network/
}
