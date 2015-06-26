package com.scottyab.rootbeer;

/**
 * Created by mat on 19/06/15.
 */
public class RootBeerNative {

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
