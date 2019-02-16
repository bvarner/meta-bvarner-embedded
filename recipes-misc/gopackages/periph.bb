DESCRIPTION = "Peripherals I/O in Go"
SECTION = "misc"
HOMEPAGE = "https://github.com/google/periph"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

PV = "3.4.0"

SRC_URI = "\
	git://github.com/google/periph;tag=v${PV} \
"

GO_LINKSHARED = ""
GO_IMPORT = "periph.io/x/periph"
GO_INSTALL = "${GO_IMPORT}"

inherit go