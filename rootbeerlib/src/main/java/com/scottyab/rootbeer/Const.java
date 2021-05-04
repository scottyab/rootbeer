package com.scottyab.rootbeer;

import java.util.ArrayList;
import java.util.Arrays;

final class Const {

    static final String BINARY_SU = "su";
    static final String BINARY_BUSYBOX = "busybox";

    private Const() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    static final String[] knownRootAppsPackages = {
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
    };

    public static final String[] knownDangerousAppsPackages = {
            "com.koushikdutta.rommanager",
            "com.koushikdutta.rommanager.license",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine",
            "com.ramdroid.appquarantinepro",
            "com.android.vending.billing.InAppBillingService.COIN",
            "com.android.vending.billing.InAppBillingService.LUCK",
            "com.chelpus.luckypatcher",
            "com.blackmartalpha",
            "org.blackmart.market",
            "com.allinone.free",
            "com.repodroid.app",
            "org.creeplays.hack",
            "com.baseappfull.fwd",
            "com.zmapp",
            "com.dv.marketmod.installer",
            "org.mobilism.android",
            "com.android.wp.net.log",
            "com.android.camera.update",
            "cc.madkite.freedom",
            "com.solohsu.android.edxp.manager",
            "org.meowcat.edxposed.manager",
            "com.xmodgame",
            "com.cih.game_cih",
            "com.charles.lpoqasert",
            "catch_.me_.if_.you_.can_"
    };

    public static final String[] knownRootCloakingPackages = {
            "com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.zachspong.temprootremovejb",
            "com.amphoras.hidemyroot",
            "com.amphoras.hidemyrootadfree",
            "com.formyhm.hiderootPremium",
            "com.formyhm.hideroot"
    };

    // These must end with a /
    private static final String[] suPaths = {
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/su/bin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/",
            "/cache/",
            "/data/",
            "/dev/"
    };


    static final String[] pathsThatShouldNotBeWritable = {
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            "/sbin",
            "/etc",
            //"/sys",
            //"/proc",
            //"/dev"
    };

    /**
     * Get a list of paths to check for binaries
     *
     * @return List of paths to check, using a combination of a static list and those paths
     * listed in the PATH environment variable.
     */
    static String[] getPaths() {
        ArrayList<String> paths = new ArrayList<>(Arrays.asList(suPaths));

        String sysPaths = System.getenv("PATH");

        // If we can't get the path variable just return the static paths
        if (sysPaths == null || "".equals(sysPaths)) {
            return paths.toArray(new String[0]);
        }

        for (String path : sysPaths.split(":")) {

            if (!path.endsWith("/")) {
                path = path + '/';
            }

            if (!paths.contains(path)) {
                paths.add(path);
            }
        }

        return paths.toArray(new String[0]);
    }

}
