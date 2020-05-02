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
    	echo "dtoverlay=ads1115-hwmon" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtparam=cha_enable=1,cha_cfg=4" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtparam=chb_enable=1,chb_cfg=5" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtparam=chc_enable=1,chc_cfg=6" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    	echo "dtparam=chd_enable=1,chd_cfg=7" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
	fi
}
