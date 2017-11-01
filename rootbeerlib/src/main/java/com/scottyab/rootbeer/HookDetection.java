package com.scottyab.rootbeer;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.scottyab.rootbeer.util.QLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * Created by matthew on 01/11/17.
 *
 * This class was created from code published on Neil Bergman's excellent blog post found here
 * http://d3adend.org/blog/?p=589
 */

public class HookDetection {

    private final Context mContext;
    private boolean hookCheckingEnabled = false;
    private boolean hookingDetected = false;

    private static final String packageName = "com.scottyab.rootbeer";
    private static final String nativePackageName = "com.scottyab.rootbeer.RootBeerNative";

    public HookDetection(Context context) {
        mContext = context;
    }

    /**
     * Check for method hooking (can be called on multiple functions, and the the result checked with
     * @See(wasHookingDetected)
     */
    public void checkForHooking() {

        if (!hookCheckingEnabled) {
            return;
        }

        try {
            throw new Exception("blah");
        } catch (Exception e) {
            int zygoteInitCallCount = 0;
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                if (stackTraceElement.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
                        QLog.e("HookDetection: Substrate is active on the device.");
                        hookingDetected = true;
                    }
                }
                if (stackTraceElement.getClassName().equals("com.saurik.substrate.MS$2") &&
                        stackTraceElement.getMethodName().equals("invoked")) {
                    QLog.e("HookDetection: A method on the stack trace has been hooked using Substrate.");
                    hookingDetected = true;
                }
                if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                        stackTraceElement.getMethodName().equals("main")) {
                    QLog.e("HookDetection: Xposed is active on the device.");
                    hookingDetected = true;
                }
                if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                        stackTraceElement.getMethodName().equals("handleHookedMethod")) {
                    QLog.e("HookDetection: A method on the stack trace has been hooked using Xposed.");
                    hookingDetected = true;
                }

            }
        }
    }

    /**
     * Check to see if any methods are implemented as native when they should not be
     *
     * @return true is we've detected a method that is native but we don't think should be
     */
    public boolean detectIncorrectlyNativeMethods() {

        if (!hookCheckingEnabled) {
            return false;
        }

        boolean incorrectlyNativeMethodsFound = false;

        ApplicationInfo applicationInfo = mContext.getApplicationInfo();

        Set<String> classes = new HashSet<>();
        DexFile dex;
        try {
            dex = new DexFile(applicationInfo.sourceDir);
            Enumeration entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entry = (String) entries.nextElement();
                classes.add(entry);
            }
            dex.close();
        } catch (IOException e) {
            QLog.e("HookDetection: " + e.toString());
        }
        for (String className : classes) {
            if (className.startsWith(packageName)) {
                try {
                    Class clazz = HookDetection.class.forName(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (Modifier.isNative(method.getModifiers()) && !nativePackageName.equals(clazz.getCanonicalName())) {
                            QLog.e("HookDetection: Native function found (could be hooked by Substrate or Xposed): " + clazz.getCanonicalName() + "->" + method.getName());
                            incorrectlyNativeMethodsFound = true;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    QLog.e("HookDetection: " + e.toString());
                }
            }
        }

        return incorrectlyNativeMethodsFound;
    }

    public boolean detectSuspiciousLoadedSharedObjectsOrJars(){

        if (!hookCheckingEnabled) {
            return false;
        }

        boolean foundSuspiciousThings = false;

        try {
            Set<String> libraries = new HashSet<>();
            String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
            BufferedReader reader = new BufferedReader(new FileReader(mapsFilename));
            String line;
            while((line = reader.readLine()) != null) {
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    int n = line.lastIndexOf(" ");
                    libraries.add(line.substring(n + 1));
                }
            }
            for (String library : libraries) {
                if(library.contains("com.saurik.substrate")) {
                    QLog.e("HookDetection: Substrate shared object found: " + library);
                    foundSuspiciousThings = true;
                }
                if(library.contains("XposedBridge.jar")) {
                    QLog.e("HookDetection: Xposed JAR found: " + library);
                    foundSuspiciousThings = true;
                }
            }
            reader.close();
        }
        catch (Exception e) {
            QLog.e("HookDetection: "+e.toString());
        }

        return foundSuspiciousThings;
    }

    public boolean wasHookingDetected() {
        return hookingDetected;
    }

    public void setEnabled(boolean enabled) {
        this.hookCheckingEnabled = enabled;
    }
}
