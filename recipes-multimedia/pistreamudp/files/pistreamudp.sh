#!/bin/sh
while : ; do
	assignedip=`ip addr show | grep 'inet.*dynamic' | cut -d ' ' -f 6 -`
	if [ -z "$assignedip" ]; then
		echo "Waiting for IP..."
		sleep 1
	else
		echo "Streaming to udp://224.0.0.1:8675"
		# These settings produce video quality congruent with with an iPhone 5s on time-lapse.
		raspivid -w 1280 -h 720 -n -o - -t 0 -fps 30 -g 45 -ih -pf main -lev 4 -if both -b 12475000 | ffmpeg -re -an -i - -vcodec copy -f mpegts udp://224.0.0.1:8675 -loglevel quiet
	fi
done
