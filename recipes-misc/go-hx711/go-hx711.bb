DESCRIPTION = "Go HX711 Interface"
SECTION = "misc"
HOMEPAGE = "https://github.com/MichaelS11/go-hx711"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
	git://${GO_IMPORT} \
	file://${BP}/src/${GO_IMPORT} \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "github.com/MichaelS11/go-hx711"
GO_INSTALL = "${GO_IMPORT}/..."

DEPENDS = "\
	periph \
"

RDEPENDS_${PN}-dev_append = "\
	bash \
"

inherit go