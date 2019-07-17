DESCRIPTION = "Prometheus Core"
SECTION = "misc"
HOMEPAGE = "https://github.com/prometheus/prometheus/"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

SRC_URI = "\
	git://${GO_IMPORT} \
	file://prometheus.yml \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "github.com/prometheus/prometheus"
GO_INSTALL = "${GO_IMPORT}/cmd/..."

RDEPENDS_${PN}_append = "\
	go-runtime \
"

RDEPENDS_${PN}-dev_append = "\
	bash \
"

inherit go

do_install_append() {
	install -d ${D}${sysconfdir}/prometheus
	install -m 0644 ${WORKDIR}/prometheus.yml ${D}${sysconfdir}/prometheus
}
