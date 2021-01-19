#!/bin/bash
#
# Run whatever screen saver or locker in this X instance.
# If it exits with a zero exit code, unlock. Otherwise start it again.
#

SCRIPT_NAME="${BASH_SOURCE[0]##*/}"
SCRIPT_DIR="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
source "$SCRIPT_DIR/common"

#whether we should switch back to the main VT
SWITCH_BACK="${1:-1}"

[ $SWITCH_BACK -eq 0 ] && chvt $MAIN_VT

#runLocker
function runLocker {
  #make sure we have access to X
  cp -f "$XAUTH_BAK" "$XAUTH"

  #start the locker
  eval "$LOCKER" &

  #give it some time to connect to X and start its windows
  #also avoids max CPU usage in case $LOCKER instantly fails due to user or X errors
  sleep 3

  #revoke X access rights for other applications (to be sure nothing else is displayed on that X server)
  #maybe TODO: parse xauth list dynamically and remove all entries
  #NOTE: If the locker wants to start new windows after this, it will fail!
  xauth remove "dom0:$LOCK_DISPLAY"

  #wait for the locker to exit & return its exit code
  wait $!
}

#create a auth token backup for later
[ -f "$XAUTHORITY" ] || error "$XAUTH not found! Did startx code change?"
XAUTH="$XAUTHORITY"
XAUTH_BAK="$XAUTH.bak"
cp -f "$XAUTH" "$XAUTH_BAK" || error "Failed to create an auth token backup."

while : ; do
  runLocker && physlock -L && chvt $MAIN_VT
done
