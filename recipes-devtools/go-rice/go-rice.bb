DESCRIPTION = "go.rice is a Go package that makes working with resources such as html very easy."
SECTION = "misc"
HOMEPAGE = "https://github.com/GeertJohan/go.rice"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=8bef8e6712b1be5aa76af1ebde9d6378"

SRCNAME = "go.rice"
PKG_NAME = "github.com/GeertJohan/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
"
SRCREV = "${AUTOREV}"

DEPENDS = ""

RDEPENDS_${PN}-dev_append = "\
	bash \
"

RDEPENDS_${PN}-staticdev_append = "\
	bash \
	perl \
"

GO_LINKSHARED = ''
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

BBCLASSEXTEND += "native"

inherit go godep
