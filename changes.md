Change Log

#0.1.0
* Add fstack protector #136 @slawert

#0.0.9
* Support for Android TV devices #129  @deepakpk009
* Add additional dangerous apps packages #145 @Fi5t
* ~Add fstack protector #136 @slawert~ Note this was found in #170 to have not been applied and fixed in 0.1.0

#0.0.8

* Removed busybox from the default root checking methods
* Additional root app packages added
* Use PATH environment variable to find places su binaries might be hiding
* Updated sample app to AndroidX

#0.0.7

* Added a check to see if the native library is available to prevent crashing
* Automatic building of native binaries if NDK is present

#0.0.6

* more su directory checks
* fix crash when native lib load fails
* updated NDK build
* Added method to do root checks but ignoring the busybox due to false positives
* Allow setting of logging level
* UI tweeks to sample app
