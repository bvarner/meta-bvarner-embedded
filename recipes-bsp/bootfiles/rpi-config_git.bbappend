do_deploy_append() {
    if [ "${HX711-ROCKETSTAND}" = "1" ]; then
        echo "# Enable HX711 load cell" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtoverlay=hx711-rocketstand" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ "${ENABLE_HC-SR04}" = "1" ]; then
    	echo "# Enable HC-SR04 ultrasonic transponders" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtoverlay=srf04" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ "${ENABLE_4_CHANNEL_RELAY}" = "1" ]; then
    	echo "# Enable 4 Channel Relays" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtoverlay=4channel-relay" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ "${ENABLE_ADS1015_PIDROPONIC}" = "1" ]; then
    	echo "# Enabling pidroponic ADC."
    	echo "dtoverlay=ads1115-pidroponic" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
	fi
}
