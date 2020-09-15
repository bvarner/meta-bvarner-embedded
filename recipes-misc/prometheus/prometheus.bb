DESCRIPTION = "Prometheus Core"
SECTION = "misc"
HOMEPAGE = "https://github.com/prometheus/prometheus/"

LICENSE = "APSL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58"

SRC_URI = "\
	git://${GO_IMPORT};branch=release-2.21 \
	file://systemd-units/prometheus.service \
"
SRCREV = "${AUTOREV}"

GO_LINKSHARED = ""
GO_IMPORT = "github.com/prometheus/prometheus"
GO_INSTALL = ""

RDEPENDS_${PN}-dev_append = "\
	bash \
"

DEPENDS = "\
	make \
	go-native \
	nodejs-native \
	ca-certificates-native \
	curl-native \
"

inherit go systemd staging

# Forces the ca-certs to be staged properly into STAGING_ETCDIR_NATIVE
do_compile[depends] += "ca-certificates-native:do_prepare_recipe_sysroot"
do_compile[depends] += "curl-native:do_prepare_recipe_sysroot"

FILES_${PN} += "${localstatedir}/prometheus/ui"

# Remove -j <cpu> from the compile step
PARALLEL_MAKE_task-compile = ""

do_compile() {
	# Pre-install yarn.
	npm i yarn
	export PATH=${B}/node_modules/.bin:${B}/src/${GO_IMPORT}/web/ui/react-app/node_modules/.bin:$PATH
	                                                                                                                    
	# Setup the go tmpdir
	export TMPDIR="${GOTMPDIR}"
	
	# Get to the proper directory and invoke `make assets` using the native go
	cd ${B}/src/${GO_IMPORT}
	
	# Setup CURL Config with a .curlrc
	echo "cacert=${STAGING_ETCDIR_NATIVE}/ssl/certs/ca-certificates.crt" > ~/.curlrc
	echo "capath=${STAGING_ETCDIR_NATIVE}/ssl/certs" >> ~/.curlrc

	# Make sure we use the native hosts go inside the makefile
	export GO="${STAGING_BINDIR_NATIVE}/go"
	oe_runmake build
	
	unset GO
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/src/${GO_IMPORT}/prometheus ${D}${bindir}/
	install -m 0755 ${B}/src/${GO_IMPORT}/promtool ${D}${bindir}/
	
	install -d ${D}${sysconfdir}/prometheus
	install -d ${D}${localstatedir}/prometheus/data
	
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/systemd-units/prometheus.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "prometheus.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"
