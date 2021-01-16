#!/bin/bash
#
# Run whatever screen saver or locker in this X instance.
# If it exits with a zero exit code, unlock. Otherwise start it again.
#

MAIN_VT=1
LOCKER="xterm ; [ -f /tmp/unlock ]" #xterm for testing, `touch /tmp/unlock ; exit` to unlock

#make sure physlock is in PATH
PATH="$PATH:/usr/local/bin"

#whether we should switch back to the main VT
SWITCH_BACK="${1:-1}"

[ $SWITCH_BACK -eq 0 ] && chvt $MAIN_VT

while : ; do
  eval "$LOCKER" && physlock -L && chvt $MAIN_VT
done
