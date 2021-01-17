Proof of Concept for https://github.com/QubesOS/qubes-issues/issues/1917#issuecomment-739522026

1. Log in to e.g. VT2, run `./boot` there. It'll start an X server on VT3 and switch you to VT1. (**Note**: Running `./boot` from VT1 (where your main X server runs) tends to cause issues.)
2. Run `./lock` on e.g. VT1. You'll be switched to VT3 and cannot switch back. `xterm` is displayed as pseudo locker.
3. To unlock: `touch /tmp/unlock && exit`.
