#v0.0.8

* Removed busybox from the default root checking methods
* Additional root app packages added
* Use PATH environment variable to find places su binaries might be hiding
* Updated sample app to AndroidX

#v0.0.7

* Added a check to see if the native libary is avaliable to prevent crashing
* Automatic building of native binaries if NDK is present

#v0.0.6

* more su directory checks
* fix crash when native lib load fails
* updated NDK build
* Added method to do root checks but ignoring the busybox due to false positives
* Allow setting of logging level
* UI tweeks to sample app
