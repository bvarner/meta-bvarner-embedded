PACKAGECONFIG ??= "avdevice avfilter avcodec avformat swresample swscale postproc \
                   bzlib gpl lzma theora x264 omx \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 xv', '', d)}"

PACKAGECONFIG[omx] = "--enable-omx-rpi --enable-mmal"

DEPENDS += "userland"
DEPENDS_${PN} += "userland-dev"

TARGET_CFLAGS += "-I${STAGING_DIR_TARGET}/usr/include/IL"
