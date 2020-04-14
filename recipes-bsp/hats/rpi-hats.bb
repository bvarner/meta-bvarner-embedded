DESCRIPTION = "Raspberry Pi HAT utilities"
HOMEPAGE = "https://github.com/raspberrypi/hats.git"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRCNAME = "hats"
PKG_NAME = "rpi-hats"

SRCREV = "${AUTOREV}"
SRC_URI = "\
	git://github.com/raspberrypi/hats.git \
"
PR = "master"

S = "${WORKDIR}/git/eepromutils"

do_compile() {
	${CC} ${LDFLAGS} eepmake.c -o eepmake
	${CC} ${LDFLAGS} eepdump.c -o eepdump
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 eepmake ${D}${bindir}
	install -m 0755 eepdump ${D}${bindir}
	install -m 0755 eepflash.sh ${D}${bindir}
}

BBCLASSEXTEND += "native nativesdk"
