/****************************************************************************
 * File:   toolChecker.cpp
 * Author: Matthew Rollings
 * Date:   19/06/2015
 *
 * Description : Root checking JNI NDK code
 *
 ****************************************************************************/

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>> System Includes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

// Android headers
#include <jni.h>
#include <android/log.h> 

// String / file headers
#include <string.h>
#include <stdio.h>

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> User Includes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/
#include "toolChecker.h"

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> Constant Macros <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

// LOGCAT
/* Set to 1 to enable debug log traces. */
#define DEBUG 1
#define  LOG_TAG    "RootBeer"
#define  LOGD(...)  if (DEBUG) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__);
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__);

const char *paths[8] = { 
  "/sbin/su", 
  "/system/bin/su", 
  "/system/xbin/su",
  "/data/local/xbin/su", 
  "/data/local/bin/su", 
  "/system/sd/xbin/su",
  "/system/bin/failsafe/su", 
  "/data/local/su"
  
};


/*****************************************************************************  
 * Description: Checks if a file exists
 *
 * Parameters: fname - filename to check
 *
 * Return value: 0 - non-existant / not visible, 1 - exists
 *
 *****************************************************************************/
int exists(const char *fname)
{
    FILE *file;
    if (file = fopen(fname, "r"))
    {
      LOGD("LOOKING FOR BINRARY: %s PRESENT!!!",fname);
        fclose(file);
        return 1;
    }
      LOGD("LOOKING FOR BINRARY: %s Absent :(",fname);
    return 0;
}




/*****************************************************************************  
 * Description: Checks for root binaries
 *
 * Parameters: env - Java environment pointer
 *      thiz - javaobject
 *
 * Return value: int number of su binaries found
 *
 *****************************************************************************/
int Java_com_scottyab_rootbeer_RootBeerNative_checkForRoot( JNIEnv* env, jobject thiz )
{
    
  int binariesFound = 0;
  for(int i = 0 ; i < 8; i ++){
    binariesFound+=exists(paths[i]);
  }

    return binariesFound>0;
}


