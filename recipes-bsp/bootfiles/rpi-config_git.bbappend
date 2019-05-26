do_deploy_append() {
    if [ "${HX711-ROCKETSTAND}" = "1" ]; then
        echo "# Enable HX711 load cell" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtoverlay=hx711-rocketstand" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
}