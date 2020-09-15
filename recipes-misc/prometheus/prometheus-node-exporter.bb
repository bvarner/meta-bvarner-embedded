DESCRIPTION = "Prometheus Node Exporter"
SECTION = "misc"
HOMEPAGE = "https://github.com/prometheus/node_exporter/"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

SRC_URI = "git://${GO_IMPORT};branch=release-1.0 \
	file://systemd-units/node_exporter.service \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "github.com/prometheus/node_exporter"
GO_INSTALL = "${GO_IMPORT}"


RDEPENDS_${PN}_append = "\
	go-runtime \
"

RDEPENDS_${PN}-dev_append = "\
	gawk \
	bash \
"

# The file-rdeps is picking up a dependency to 'ksh' from the openbsd examples.
INSANE_SKIP_${PN}-dev = "file-rdeps"

inherit go systemd

do_install_append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/node_exporter.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "node_exporter.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"
