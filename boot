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
XINITRC="$SCRIPT_DIR/xinitrc"
LOCK_VT=3
LOCK_DISPLAY=3

#log [msg]
function log {
local msg="$1"
logger -p daemon.err -t "$SCRIPT_NAME" "[$BASHPID] $msg"
}

#error [msg]
function error {
local msg="$1"
echo "$msg" >&2
exit 1
}

#ignoreAllSignals
function ignoreAllSignals {
local i
for ((i=0;i<50;i++)) ; do
  trap '' $i
done
}

[[ "$(whoami)" == "root" ]] || error "This script must be run as root."
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

#ISSUES: 
# - issues with Qubes OS assigning X windows to other X server?! seems so for new VMs -_-
# - not all screenlockers properly set exit codes (seems hard for e.g. xscreenlocker), at least xsecurelock seems to do it; one can sometimes work around it by checking whether it correctly exited
