do_deploy_append() {
    if [ "${HX711-ROCKETSTAND}" = "1" ]; then
        echo "# Enable HX711 load cell" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtoverlay=hx711-rocketstand" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ "${HC-SR04}" = "1" ]; then
    	echo "# Enable HC-SR04 ultrasonic transponders" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtoverlay=srf04" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
}