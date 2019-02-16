DESCRIPTION = "Tool for embedding .ico & manifest resources in Go programs for Windows."
SECTION = "misc"
HOMEPAGE = "https://github.com/akavel/rsrc"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRCNAME = "rsrc"
PKG_NAME = "github.com/akavel/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
"
SRCREV = "${AUTOREV}"

DEPENDS = ""

GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

BBCLASSEXTEND = "native"

inherit go
