DESCRIPTION = "Go package providing a streaming version of strconv.Quote."
SECTION = "misc"
HOMEPAGE = "https://github.com/nkovacs/streamquote"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRCNAME = "streamquote"
PKG_NAME = "github.com/nkovacs/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
"
SRCREV = "${AUTOREV}"


DEPENDS = ""

GO_LINKSHARED = ""
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

BBCLASSEXTEND = "native"

inherit go
                                                                                                 