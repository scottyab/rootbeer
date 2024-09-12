# RootBeer ![app icon](./app/src/main/res/mipmap-xhdpi/ic_launcher_round.png)

[![RootBeer](https://www.appbrain.com/stats/libraries/shield/rootbeer.svg)](https://www.appbrain.com/stats/libraries/details/rootbeer/rootbeer)

A tasty root checker library and sample app. We've scoured the internets for different methods of answering that age old question... **Has this device got root?**

# Root checks
These are the current checks/tricks we are using to give an indication of root. 

**Java checks**

| Method Name                   | Description                                                                                                             | Limitations                                                                                          |
|-------------------------------|-------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `checkRootManagementApps`      | Checks if any apps for managing root access (like SuperSU or Magisk) are installed.                                     | May not detect newly developed or less popular root management apps.                                 |
| `checkPotentiallyDangerousApps`| Checks if any apps known for facilitating root access are installed.                                                    | Limited to a predefined list of apps; cannot detect custom or less-known dangerous apps.             |
| `checkRootCloakingApps`        | Detects apps that can cloak or hide root access from detection tools.                                                   | Root cloaking apps evolve quickly, potentially bypassing detection mechanisms.                       |
| `checkTestKeys`                | Verifies if the device's firmware is signed with Android's test keys, which it would be on AOSP or certain emluators.   | Only detects if test keys are used, and may miss rooted devices using production keys.               |
| `checkForDangerousProps`       | Checks for dangerous properties (`ro.debuggable` and `ro.secure`) that indicate this may not be a genuine Android device | Can be bypassed if properties are reset or hidden by advanced root cloaking techniques.              |
| `checkForBusyBoxBinary`        | Checks if the BusyBox binary is present, commonly used in rooted devices.                                               | Not all rooted devices use BusyBox, and some device manufacturers may leave busybox on the ROM.      |
| `checkForSuBinary`             | Checks for the presence of the `su` binary, typically used to elevate privileges.                                       | Su binaries may be renamed or hidden by root cloaking tools, bypassing detection.                    |
| `checkSuExists`                | Another check for the existence of the `su` binary, via 'which su'.                                                    | Same as `checkForSuBinary`, can be bypassed by renaming or hiding the binary.                        |
| `checkForRWSystem`             | Verifies if the `/system` partition is mounted as read-write, a sign of rooting.                                        | Some newer root methods do not require RW access to the `/system` partition (e.g., systemless root). |


**Native checks**

We call through to our native root checker to run some of its own checks. Native checks are typically harder to cloak, so some root cloak apps just block the loading of native libraries that contain certain keywords.

|  Method Name       | Description                                                                 | Limitations                     |
|--------------------|-----------------------------------------------------------------------------|---------------------------------|
| `checkForSuBinary` | Checks for the presence of the `su` binary, typically used to elevate privileges. | Same as Java `checkForSuBinary` |


## Disclaimer and limitations!

One way to think about root checking is that **root==god**, so there's no 100% guaranteed way to check for root! Please treat the results of this library as an *indication* of root together with other factors which will vary depending on your usecase rather than basing your whole security posture on the result.
A more robust solution would be to use [Google Play Integrity API](https://developer.android.com/google/play/integrity) this will verify requests from your app are coming from your unmodified app binary, installed by Google Play, running on a genuine Android device (i.e not rooted).

Rootbeer can be bypassed, there's several articles that illustrate how to do this. [here](https://medium.com/secarmalabs/bypassing-androids-rootbeer-library-part-1-a5f93918660d) one we found with a quick Google search.

### Background 

This started as an hackday project where we wanted to see if we could beat the current root cloakers at the time in 2015.
We love root! both [Scott](https://github.com/scottyab) and [Mat](https://github.com/stealthcopter) (the creators) own and use rooted devices (albeit not as daily driver) and can understand the frustration when apps add root checks and prevent you using their services. However this is not the responsibility of RootBeer please raise these concerns with the app that is implementing the Rootbeer root checks.

<img src="./art/rootbeerjesus.png" width=200 />


### Root cloakers

In **2015** we successfully tested Rootbeer and it flagged an indication of root when testing with the following root cloak apps. However Rootbeer was defeated when using a combination of the root cloakers activated at the same time.

Tested cloakers:

* [RootCloak Plus (Cydia)](https://play.google.com/store/apps/details?id=com.devadvance.rootcloakplus&hl=en_GB) requires [Cydia Substrate](http://play.google.com/store/apps/details?id=com.saurik.substrate)
* [RootCloak](http://repo.xposed.info/module/com.devadvance.rootcloak) - requires [Xposed Framework](http://repo.xposed.info/module/de.robv.android.xposed.installer)

## Usage

```java
RootBeer rootBeer = new RootBeer(context);
if (rootBeer.isRooted()) {
    //we found indication of root
} else {
    //we didn't find indication of root
}
```

You can also call each of the checks individually as the sample app does. It is advisable to call `isRooted()` from a background thread as it involves disk I/O.

### False positives

Manufacturers sometimes leave the busybox binary in production builds and this doesn't always mean that a device is rooted. We have removed the busybox check we used to include as standard in the isRooted() method to avoid these false positives.

If you want to detect the busybox binary in your app you can use `checkForBinary(BINARY_BUSYBOX)` to detect it alone or as part of the complete root detection method:

```java
rootBeer.isRootedWithBusyBoxCheck();
```

The following devices are known the have the busybox binary present on the stock rom:
* All OnePlus Devices
* Moto E
* OPPO R9m (ColorOS 3.0,Android 5.1,Android security patch January 5, 2018 )

### Dependency

Available on [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22rootbeer-lib%22), to include using Gradle just add the following:

```java
dependencies {
    implementation 'com.scottyab:rootbeer-lib:0.1.1'
}
```

Or use this [Jitpack.io link](https://jitpack.io/#scottyab/rootbeer/releases) - note prefer releases/tags rather than branches. 

### Building

The native library in this application will now be built via Gradle and the latest Android Studio without having to resort to the command line. However the .so files are also distributed in this repository for those who cannot compile using the NDK for some reason.

### Sample app

The sample app is published on Google Play to allow you to quickly and easily test the library. Enjoy! And please do feedback to us if your tests produce different results.

<a href="https://play.google.com/store/apps/details?id=com.scottyab.rootbeer.sample&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1"><img width="200" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" /></a>

<img width="200" alt="screenshot" src="./art/ss_got_root_fail.png">


## Contributing

There must be more root checks to make this more complete. If you have one please do send us a pull request.

### Thanks

* Kevin Kowalewski and others from this popular [StackOverflow post](https://stackoverflow.com/questions/1101380/determine-if-running-on-a-rooted-device?rq=1)
* Eric Gruber's - Android Root Detection Techniques [article](https://blog.netspi.com/android-root-detection-techniques/)


## Other libraries

If you dig this, you might like:

 * Tim Strazzere's [Anti emulator checks](https://github.com/strazzere/anti-emulator/) project
 * Scott Alexander-Bown's [SafetyNet Helper library](https://github.com/scottyab/safetynethelper) - coupled with server-side validation this is one of the best root detection approaches. See the [Google SafetyNet helper docs](https://developer.android.com/training/safetynet/index.html).

# Licence

Apache License, Version 2.0

    Copyright (C) 2015, Scott Alexander-Bown, Mat Rollings

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
