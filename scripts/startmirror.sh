#!/bin/bash

# Kill any running python or java procs
./killall.sh

# Get arguments
for var in "$@"
do
	case $var in
		"-sw")
			sw="-Dprism.order=sw"
			echo "USING SOFTWARE RENDERING"
			;;
		"-fullscreen")
			fullscreen=$var
			;;
		*)
	esac
done

# Application banner
figlet -f big 'smart  mirror'

# Start web server and javafx
cd web-service
python main.py &
cd ..
java $sw -jar smart-mirror1.0.jar -jarRun $fullscreen

# Kill all python and java when exiting
./killall.sh
