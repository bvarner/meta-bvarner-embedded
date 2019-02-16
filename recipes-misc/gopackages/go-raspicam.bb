DESCRIPTION = "Go package for controlling Raspberry Pi Camera Module"
SECTION = "misc"
HOMEPAGE = "https://github.com/dhowden/raspicam"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=8bef8e6712b1be5aa76af1ebde9d6378"

SRC_URI = "\
	git://${GO_IMPORT} \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "github.com/dhowden/raspicam"
GO_INSTALL = "${GO_IMPORT}"

inherit go
