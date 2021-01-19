#!/bin/bash -x
#
# Screenlocker boot script.
# 
# Assumes that SysRq keys are disabled.
#
# Must be started at boot time after VTs are set up as daemon by e.g. systemd.
# Can be run from dom0 command-line for testing. Ctrl-C will send SIGTERM to X.
#
# Use `killall -9 boot && pkill -9 -f 'X :3' && killall -9 xinitrc` as root to exit this script.

SCRIPT_NAME="${BASH_SOURCE[0]##*/}"
SCRIPT_DIR="$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")"
source "$SCRIPT_DIR/common"

#ignoreAllSignals
function ignoreAllSignals {
local i
for ((i=0;i<50;i++)) ; do
  trap '' $i
done
}

[[ "$(whoami)" == "root" ]] || error "This script must be run as root."
command -v "physlock" &> /dev/null || error "physlock must be installed!"

ignoreAllSignals

#make sure the target VT is not running any processes / shows a PAM logon
pkill -t tty$LOCK_VT

switch_back=0
while : ; do
  startx "$XINITRC" $switch_back -- :$LOCK_DISPLAY vt$LOCK_VT #on X lock timeout problems, use xauth -b
  [ $? -eq 0 ] || log "The screenlocker X server instance crashed. Maybe an attack?! Restarting it..."
  switch_back=1 #if we switch back on retries, we may break an existing screen lock
  #maybe TODO: after several failed restarts, fall back to PAM logon (i.e. use physlock directly)
done
