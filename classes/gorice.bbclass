inherit go

DEPENDS_GORICE_class-target = "go-rice go-rice-native"
DEPENDS_GORICE_class-native =  "go-rice-native"

DEPENDS_append = " ${DEPENDS_GORICE}"

export RICE = "${STAGING_BINDIR_NATIVE}/rice"

RICE_ARGS ?= ''

# Either 'go' || 'syso'
GO_RICE_EMBEDTYPE ?= ''

# Non-empty triggers zip-append.
GO_RICE_APPEND ?= ''
GO_RICE_FILTEROUT ?= '.a$|.so$'

go_list_executables() {
	${GO} list -f '{{.Target}}' ${GOBUILDFLAGS} ${GO_INSTALL} | \
		egrep -v '${GO_RICE_FILTEROUT}' | \
		awk '{ print $1}'
}

go_do_compile_prepend() {
	if [ -n "${GO_RICE_EMBEDTYPE}" ]; then
		${RICE} ${RICE_ARGS} -i ${GO_IMPORT} embed-${GO_RICE_EMBEDTYPE}
	fi
}

go_do_compile_append() {
	if [ -n "${GO_RICE_APPEND}" ]; then
		go_list_executables | while read app; do
			${RICE} ${RICE_ARGS} -i ${GO_IMPORT} append --exec $app
		done
	fi
}
