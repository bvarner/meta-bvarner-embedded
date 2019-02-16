DESCRIPTION = "Simple and fast template engine for Go"
SECTION = "misc"
HOMEPAGE = "https://github.com/valyala/fasttemplate"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRCNAME = "fasttemplate"
PKG_NAME = "github.com/valyala/${SRCNAME}"
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
                                                                                                 