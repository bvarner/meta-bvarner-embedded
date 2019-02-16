DESCRIPTION = "Package zipexe attempts to open an executable binary file as a zip file."
SECTION = "misc"
HOMEPAGE = "https://github.com/daaku/go.zipexe"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRCNAME = "go.zipexe"
PKG_NAME = "github.com/daaku/${SRCNAME}"
SRC_URI = "\
	git://${PKG_NAME}.git \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "${PKG_NAME}"
GO_INSTALL = "${GO_IMPORT}"

BBCLASSEXTEND = "native"

inherit go
