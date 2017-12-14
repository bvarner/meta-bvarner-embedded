#!/bin/sh
while : ; do
	sleep 1
	assignedip=`ip addr show | grep 'inet.*dynamic' | cut -d ' ' -f 6 - | cut -d '/' -f 1 -`
	if [ -z "$assignedip" ]; then
		echo "Waiting for IP..."
	else
		fifo="/tmp/vidstream"
		mkfifo $fifo
		
		echo "Attempting to stream camera data @ tcp://$assignedip:8675"
		
		raspivid -ISO 500 -ex fixedfps -mm matrix -w 1920 -h 1080 -n -o - -t 0 -fps 30 -g 90 -ih -pf high -lev 4 -if both -b 14000000 > $fifo &
		ffmpeg -r 30 -an -i $fifo -vcodec copy -f mpegts "tcp://$assignedip:8675?listen=1" -loglevel quiet
		
		killall raspivid
		killall ffmpeg
		
		rm $fifo
	fi
done
