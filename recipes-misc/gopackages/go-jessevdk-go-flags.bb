DESCRIPTION = "go command line option parser."
SECTION = "misc"
HOMEPAGE = "https://github.com/jessevdk/go-flags"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRCNAME = "go-flags"
PKG_NAME = "github.com/jessevdk/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
"
SRCREV = "${AUTOREV}"


DEPENDS = ""

RDEPENDS_${PN}-dev_append = "\
	bash \
"

GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

BBCLASSEXTEND = "native"

inherit go
                                                                                                 