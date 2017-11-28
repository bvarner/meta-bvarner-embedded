OpenEmbedded bitbake recipes I've cooked up. 
What you see here is a reflection of what I currently have figured out with yocto, and parts of it will improve as I do.

# Images you could build.
## picam-image
	A bare-bones read-only rootfs image that streams a raspicam in 1080HD over a network socket encapsulated in mpegts.
	I normally use this with a pi zero w, and a 3d printed enclosure. All TTYs are disabled, there is no SSH server.

## pigaragedoor-image
	A bare-bones read-only rootfs image that can control a relay board on wiringpi pin 7 (GPIO 4).
	
	When the service starts, it sets the pin HIGH. (It is assumed that pi header pin 7 is connected to an active-low relay board).
	The service exposes a simple web form with a big button (http://garage-door.local), which does an HTTP GET to /press.
	When the GET /press is handled, it toggles the state of the pin for 1 second and sends a redirect to '/' (the form).
	
	The pi boots using kernel 4.9.x and the hostname and http service are published via mDNS (zeroconf / avahi) so you
	don't need to worry about DHCP IP reservations, static IP config, or manual DNS setup.
	
	All TTYs are disabled, there is no SSH server, and root logins are disabled.
	The service is hosted via the hardended Golang HTTP server, and does not host any data off the filesystem.
	All resources are in-memory and compiled into the binary.

# How to use this repository.

## Setup your environment.
 1. You'll need a linux environment to build. Anything supported by yocto-pocky in the rocko release will do nicely: It's also likely others will work.
    * poky 2.2 through 2.3
    * ubuntu 15.04 through 17.04
    * fedora 24 through 26
    * centos 7
    * debian 8 or 9
    * opensuse-42.1 or 42.2
    
    On ubuntu / debian, you'll need to:
    ```
    sudo apt-get install build-essential chrpath diffstat libncurses5-dev texinfo
    ```
    You may also need:
    ```
    sudo apt-get install python2.7
    ```
    
 2. You'll need to create directories for the following:
    * The workspace (where we keep recipes)
    * Your project configurations (where you setup your source and target definitions for what you want to build)
    I've settled on the following structure, which is what I'll follow for this readme.
    
```    
	# Where we'll keep things.
    mkdir -p ~/Documents/yocto-builds/
    
    # Where we'll create project directories 
    # to configure local.conf and bblayers.conf
    # for each project (image/device) we build.
    mkdir -p ~/Documents/yocto-builds/projects
    
    # Where things shared between build will go
    mkdir -p ~/Documents/yocto-builds/shared/sources
    # Where build output goes
    mkdir -p ~/Documents/yocto-builds/shared/tmp
    
```
 3. With those commands out of the way, let's get the stuff we need downloaded.
```
 	cd ~/Documents/yocto-builds/
 	git clone -b rocko git://git.yoctoproject.org/poky.git poky-rocko
    cd poky-rocko
    git clone -b rocko git://git.openembedded.org/meta-openembedded
    git clone -b rocko git://git.yoctoproject.org/meta-security
    git clone -b rocko git://git.yoctoproject.org/meta-raspberrypi
    git clone https://github.com/bvarner/meta-bvarner-embedded.git
```
So now inside of ~/Documents/yocto-builds/poky-rocko you should have all your recipe layers checked out.
These directories shouldn't need much tweaking, unless you're contributing changes back upstream.
If you haven't done a build in a long, long time, cd into each directory you checked out and `git pull` to get updates.
 4. Initialize a project directory.
For this step, I'll use the example of setting up the garage-door-opener-image for a raspberrypi.
We'll use the bitbake oe-init-build-env script to create a basic project layout.
```
	mkdir -p ~/Documents/yocto-builds/projects/garage-door-opener
	cd ~/Documents/yocto-builds
	source poky-rocko/oe-init-build-env ~/Documents/yocto-builds/projects/garage-door-opener
```
This gets you the generic configuration with recipe layers to include and a 'stock' local.conf for setting up your 
target device and image settings. For the garage-door-opener, we need to customize these files.

I've put together some 'stock' files and committed them in my layer. You can overwrite the bitbake defaults with my 
templates and then do some search / replace magic to make sure our directory paths are correct.

Yocto is a bit picky about not using relative paths, we'll start by getting the full path with pwd and go from there.

```
	# Get the full path to our yocto-builds directory and store it in an environment variable.
	cd ~/Documents/yocto-builds
	export YOCTO_DIR=`pwd`
	
	# Copy my configuration templates
	cp ~/Documents/yocto-builds/poky-rocko/meta-bvarner-embedded/project-templates/garage-door-opener/*.conf ~/Documents/yocto-builds/projects/garage-door-opener/conf
	
	# Do the replacements using sed, substituting our 
	sed -i 's|%YOCTO_DIR%|'$YOCTO_DIR'|g' ~/Documents/yocto-builds/projects/garage-door-opener/conf/bblayers.conf
	sed -i 's|%SHARED_DIR%|'$YOCTO_DIR'/shared|g' ~/Documents/yocto-builds/projects/garage-door-opener/conf/local.conf
	sed -i 's|%PROJECT_DIR%|'$YOCTO_DIR'/projects/garage-door-opener|g' ~/Documents/yocto-builds/projects/garage-door-opener/conf/local.conf
```
 4. To build: "source" the bitbake environment pointing to your project directory and then run 'bitbake'. :-)
 
```
	source ~/Documents/yocto-builds/poky-rocko/oe-init-build-env ~/Documents/yocto-builds/projects/garage-door-opener
	bitbake pigaragedoor-image
```
 5. Once built, the raspberrypi SD card image will be in **~/Documents/yocto-builds/shared/tmp/deploy/images/raspberrypi/pigaragedoor-image-raspberrypi.rpi-sdimg**
 
 To get a bootable SD card image you can `dd` that to your SD card device. On my machine, that looks something like this:
```
 	sudo dd if=~/Documents/yocto-builds/shared/tmp/deploy/images/raspberrypi/pigaragedoor.rpi-sdimg of=/dev/mmcblk0 bs=4M
```

Happy Hacking!	
