package com.scottyab.rootbeer;

import android.content.Context;
import android.content.pm.PackageManager;

import com.scottyab.rootbeer.util.QLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by scottab on 19/06/2015.
 */
public class RootBeer {

    final Context mContext;

    public RootBeer(Context context) {
        mContext = context;
    }


    public boolean detectThreats() {
        boolean rootManagement = detectRootManagementApps();
        boolean potentiallyDangerousApps = detectPotentiallyDangerousApps();
        boolean suBinary = checkForBinary("su");
        boolean busyboxBinary = checkForBinary("busybox");
        boolean dangerousProps = checkForDangerousProps();
        boolean rwSystem = checkForRWSystem();
        boolean testKeys = detectTestKeys();
        boolean testSuExists = checkSuExists();

        boolean result = rootManagement || potentiallyDangerousApps || suBinary
                || busyboxBinary || dangerousProps || rwSystem || testKeys || testSuExists;

        return result;
    }

    public boolean detectTestKeys() {
        String buildTags = android.os.Build.TAGS;

        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }

    public boolean detectRootManagementApps() {

        boolean result = false;

        PackageManager pm = mContext.getPackageManager();

        for (String packageName : Const.knownRootAppsPackages) {
            try {
                // Root app detected
                pm.getPackageInfo(packageName, 0);
                QLog.e(packageName + " ROOT management app detected!");
                result = true;
            } catch (PackageManager.NameNotFoundException e) {
                // Exception thrown, package is not installed into the system
                continue;
            }
        }

        return result;
    }

    public boolean detectPotentiallyDangerousApps() {



        boolean result = false;

        PackageManager pm = mContext.getPackageManager();

        for (String packageName : Const.knownDangerousAppsPackages) {
            try {
                // app detected
                pm.getPackageInfo(packageName, 0);
                QLog.e(packageName + " potentially dangerous app detected!");
                result = true;
            } catch (PackageManager.NameNotFoundException e) {
                // Exception thrown, package is not installed into the system
                continue;
            }
        }

        return result;
    }

    public boolean detectRootCloakingApps() {

        boolean result = false;


        PackageManager pm = mContext.getPackageManager();

        for (String packageName : Const.knownRootCloakingPackages) {
            try {
                // Root app detected
                pm.getPackageInfo(packageName, 0);
                QLog.e(packageName + " ROOT Cloaking app detected!");
                result = true;
            } catch (PackageManager.NameNotFoundException e) {
                // Exception thrown, package is not installed into the system
                continue;
            }
        }

        return result;
    }


    public boolean checkForSuBinary(){
        return checkForBinary("su");
    }

    public boolean checkForBusyBoxBinary(){
        return checkForBinary("busybox");
    }

    public boolean checkForBinary(String filename) {

        String[] pathsArray = { "/sbin/", "/system/bin/", "/system/xbin/",
                "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/" };

        boolean result = false;

        for (String path : pathsArray) {
            String completePath = path + filename;
            File f = new File(completePath);
            boolean fileExists = f.exists();
            if (fileExists) {
                QLog.v(completePath + " binary detected!");
                result = true;
            }
        }

        return result;
    }

    private String[] propsReader() {
        InputStream inputstream = null;
        try {
            inputstream = Runtime.getRuntime().exec("getprop").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String propval = "";
        try {

            propval = new Scanner(inputstream).useDelimiter("\\A").next();

        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return propval.split("\n");
    }

    private String[] mountReader() {
        InputStream inputstream = null;
        try {
            inputstream = Runtime.getRuntime().exec("mount").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String propval = "";
        try {

            propval = new Scanner(inputstream).useDelimiter("\\A").next();

        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return propval.split("\n");
    }

    public boolean checkForDangerousProps() {

        final Map<String, String> dangerousProps = new HashMap<String, String>();
            dangerousProps.put("ro.debuggable", "1");
            dangerousProps.put("ro.secure", "0");

        boolean result = false;

        String[] lines = propsReader();
        for (String line : lines) {
            for (String key : dangerousProps.keySet()) {
                if (line.contains(key)) {
                    String badValue = dangerousProps.get(key);
                    badValue = "[" + badValue + "]";
                    if (line.contains(badValue)) {
                        QLog.v(key + " = " + badValue + " detected!");
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public boolean checkForRWSystem() {

        boolean result = false;

        String[] lines = mountReader();

        for (String line : lines) {
            if (line.contains("/system")) {
                if (line.contains(" rw,")) {
                    QLog.v("System partition mounted with rw permissions!");
                    result = true;
                    break;
                }
            }
        }

        return result;
    }


    public boolean checkSuExists() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    //untested
    public static boolean isSelinuxFlagInEnabled() {
        String selinux = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            selinux = (String) get.invoke(c, "ro.build.selinux");
        } catch (Exception ignored) {
        }

        return "1".equals(selinux) ? true : false;
    }


    public boolean checkForRootNative() {
        RootBeerNative rootBeerNative = new RootBeerNative();
        boolean nativeRoot = rootBeerNative.checkForRoot() > 0;
        return nativeRoot;
    }

}