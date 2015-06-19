package com.scottyab.rootchecker;

/**
 * Created by mat on 19/06/15.
 */
public class RootCheckNative {

    /****************************************************************************
     *>>>>>>>>>>>>>>>>>>>>>>>>> Static Libraries   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
     ****************************************************************************/

    /**
     * Loads the C/C++ libraries statically
     *
     */
    static {
        System.loadLibrary("tool-checker");
    }



    /****************************************************************************
     *>>>>>>>>>>>>>>>>>>>>>>>>> Imported Functions <<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
     ****************************************************************************/

    public native String stringFromJNI();
    public native String getNDKVersionString();
    public native int checkForRoot();

}
