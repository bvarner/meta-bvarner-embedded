#!/bin/sh
while : ; do
	assignedip=`ip addr show | grep 'inet.*dynamic' | cut -d ' ' -f 6 -`
	if [ -z "$assignedip" ]; then
		echo "Waiting for IP..."
		sleep 3
	else
		echo "Streaming to udp://224.0.0.1:8675"
		raspivid -n -o - -t 0 -fps 25 -g 125 -ih -pf high -lev 4.2 -if both -b 768000 | ffmpeg -re -an -i - -vcodec copy -f mpegts udp://224.0.0.1:8675 -loglevel quiet
	fi
done
