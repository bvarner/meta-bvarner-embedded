FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PACKAGECONFIG += " randomseed sysusers kmod networkd resolved timesyncd myhostname"
PACKAGECONFIG_remove = "vconsole"

SRC_URI += "\
	file://wlan.network \
	file://wired.network \
"

do_install_append() {
	install -d ${D}${sysconfdir}/systemd/network/
	install -m 0644 ${WORKDIR}/*.network ${D}${sysconfdir}/systemd/network/
#	install -m 0644 ${WORKDIR}/systemd-firstboot.service ${D}${systemd_unitdir}/system/
}
