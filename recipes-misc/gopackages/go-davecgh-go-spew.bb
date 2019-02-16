DESCRIPTION = "Implements a deep pretty printer for Go data structures to aid in debugging"
SECTION = "misc"
HOMEPAGE = "https://github.com/davecgh/go-spew"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d"

SRCNAME = "go-spew"
PKG_NAME = "github.com/davecgh/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}/..."

BBCLASSEXTEND = "native"

inherit go
