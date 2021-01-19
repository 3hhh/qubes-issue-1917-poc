Proof of Concept for https://github.com/QubesOS/qubes-issues/issues/1917#issuecomment-739522026

1. Log in to e.g. VT2, run `./boot` there. It'll start an X server on VT3 and switch you to VT1. (**Note**: Running `./boot` from VT1 (where your main X server runs) tends to cause issues.)
2. Run `./lock` on e.g. VT1. You'll be switched to VT3 and cannot switch back. `xterm` is displayed as pseudo locker.
3. To unlock: `touch /tmp/unlock && exit`.


Known issues:
- `./boot` only works from e.g. VT2 or via systemd (i.e. _not_ from VT1) as startx exports some variables that will make qubes-guid fail.
- The current implementation only allows the screenlocker to create its windows for a few seconds on startup and _not anymore_ afterwards. This is done for security reasons (dedicated screenlocker X), but can be changed easily.
- Not all screenlockers properly set exit codes (seems hard for e.g. `xscreenlocker`), but at least `xsecurelock` seems to do it; one can sometimes work around it by checking whether it correctly exited from other sources.
