#!/bin/sh
# Script started very early in the boot cycle to set pin #7 high, and output.
gpio write 7 1 && gpio mode 7 out
