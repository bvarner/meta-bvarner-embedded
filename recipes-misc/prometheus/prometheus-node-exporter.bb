DESCRIPTION = "Prometheus Node Exporter"
SECTION = "misc"
HOMEPAGE = "https://github.com/prometheus/node_exporter/"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

SRC_URI = "git://${GO_IMPORT}"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "github.com/prometheus/node_exporter"
GO_INSTALL = "${GO_IMPORT}"

RDEPENDS_${PN}-dev_append = "\
	gawk \
	bash \
"

inherit go