# RootBeer ![image](./app/src/main/res/mipmap-xhdpi/ic_launcher.png)

A tasty root checker library and sample app. We've scoured the internets for different methods of answering that age old question... **Has this device got root?**

# Root checks
These are the current checks/tricks we are using to give an indication of root.

**Java checks**

* CheckRootManagementApps
* CheckPotentiallyDangerousApps
* CheckRootCloakingApps
* CheckTestKeys
* checkForDangerousProps
* checkForBusyBoxBinary
* checkForSuBinary
* checkSuExists
* checkForRWSystem

**Native checks**

We call through to our native root checker to run some of its own checks. Native checks are typically harder to cloak, so some root cloak apps just block the loading of native libraries that contain certain keywords.

* checkForSuBinary


## Disclaimer and limitations!

We love root! both [Scott](https://github.com/scottyab) and [Mat](https://github.com/stealthcopter) (the main contributors) use rooted devices. But we appreciate sometimes you might want to have a indication your app is running on a rooted handset. Plus we wanted to see if we could beat the root cloakers. So that's what this library gives you, an *indication* of root.

Remember **root==god**, so there's no 100% way to check for root.

<img src="./art/rootbeerjesus.png" width=200 />


### Root cloakers
We've tested the Rootbeer lib and it shows an indication of root when testing with the following root cloak apps. However Rootbeer is defeated when using a combination of the root cloakers activated at the same time.

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

You can also call each of the checks individually as the sample app does.

### False positives

Note that sometimes the `isRooted()` method can return a false positive. This is often because the manufacturer of the device rom has left the busybox binary. This alone doesn't mean that the device is rooted, if you wish to avoid this but still use a convenience method to you can use the following:

```java
rootBeer.isRootedWithoutBusyBoxCheck()
```

The following devices are known the have the busybox binary present on the stock rom:
* All OnePlus Devices
* Moto E
* OPPO R9m (ColorOS 3.0,Android 5.1,Android security patch January 5, 2018 )

### Dependency

Available on [maven central](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22rootbeer-lib%22), to include using Gradle just add the following:

```java
dependencies {
    implementation 'com.scottyab:rootbeer-lib:0.0.7'
}
```

Or use this [Jitpack.io link](https://jitpack.io/#scottyab/rootbeer)

### Building

The native library in this application will now be built via Gradle and the latest Android Studio without having to resort to the command line. However the .so files are also distributed in this repository for those who cannot compile using the NDK for some reason.

### Sample app

The sample app is published on Google play to allow you to quickly and easier test the library. Enjoy! And please do feedback to us if your tests produce different results.


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
 * Scott Alexander-Bown's [SafetyNet Helper library](https://github.com/scottyab/safetynethelper) - coupled with server side validation this is one of the best root detection approaches. See the [Google SafetyNet helper docs](https://developer.android.com/training/safetynet/index.html).

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
