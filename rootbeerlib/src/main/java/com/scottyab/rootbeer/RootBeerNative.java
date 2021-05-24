package com.scottyab.rootbeer;

import com.scottyab.rootbeer.util.QLog;

public class RootBeerNative {

    private static boolean libraryLoaded = false;

    /**
     * Loads the C/C++ libraries statically
     */
    static {
        try {
            System.loadLibrary("toolChecker");
            libraryLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            QLog.e(e);
        }
    }

    public boolean wasNativeLibraryLoaded() {
        return libraryLoaded;
    }

    public native int checkForRoot(Object[] pathArray);

    public native int setLogDebugMessages(boolean logDebugMessages);

}
