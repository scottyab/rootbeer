package com.scottyab.rootbeer;

/**
 * Created by mat on 19/06/15.
 */
public final class Const {

    private Const() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static final String[] knownRootAppsPackages = {
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",

    };

    public static final String[] knownDangerousAppsPackages = {
            "com.koushikdutta.rommanager",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine"
    };

    public static final String[] knownRootCloakingPackages = {
            "com.devadvance.rootcloak",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.devadvance.rootcloakplus",
            "com.zachspong.temprootremovejb",
            "com.amphoras.hidemyroot",
            "com.formyhm.hideroot"
    };

    public static final String[] suPaths ={
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/"
    };


    public static final String[] pathsThatShouldNotBeWrtiable = {
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            //"/sys",
            "/sbin",
            "/etc",
            //"/proc",
            //"/dev"
    };



}
