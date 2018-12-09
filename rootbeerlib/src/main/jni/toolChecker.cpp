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
#define  LOG_TAG    "RootBeer"
#define  LOGD(...)  if (DEBUG) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__);
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__);

#define BUFSIZE 1024

/* Set to 1 to enable debug log traces. */
static int DEBUG = 1;

/*****************************************************************************
 * Description: Sets if we should log debug messages
 *
 * Parameters: env - Java environment pointer
 *      thiz - javaobject
 * 	bool - true to log debug messages
 *
 *****************************************************************************/
void Java_com_scottyab_rootbeer_RootBeerNative_setLogDebugMessages( JNIEnv* env, jobject thiz, jboolean debug)
{
  if (debug){
    DEBUG = 1;
  }
  else{
    DEBUG = 0;
  }
}


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
    if ((file = fopen(fname, "r")))
    {
        LOGD("LOOKING FOR BINARY: %s PRESENT!!!",fname);
        fclose(file);
        return 1;
    }
    LOGD("LOOKING FOR BINARY: %s Absent :(",fname);
    return 0;
}



/*****************************************************************************
 * Description: Check the Unix Domain Socket used by Magisk
 *
 * Parameters: none
 *
 * Return value: 0 - non-existant / not visible, 1 or more - exists
 *
 *****************************************************************************/
int Java_com_scottyab_rootbeer_RootBeerNative_checkForMagiskUDS( JNIEnv* env, jobject thiz )
{
    int detect_count = 0;
    int result = 0;

    // Magisk UDS(Unix Domain Socket) Detection Method.
    // The unix domain socket is typically used for local communications, ie IPC.
    // At least Android 8.0 can look up unix domain sockets.
    // You need to be sure that you can query the unix domain socket on Android 9.0 or later.
    FILE *fh = fopen("/proc/net/unix", "r");
    if (fh) {
        for (;;) {
            char filename[BUFSIZE] = {0};
            uint32_t a, b, c, d, e, f, g;
            int count = fscanf(fh, "%x: %u %u %u %u %u %u ",
                               &a, &b, &c, &d, &e, &f, &g);
            if (count == 0) {
                if (!fgets(filename, BUFSIZE, fh)) {
                    break;
                }
                continue;
            } else if (count == -1) {
                break;
            } else if (!fgets(filename, BUFSIZE, fh)) {
                break;
            }

            LOGD("%s", filename);

            // The name of the unix domain socket created by the daemon is prefixed with an @ symbol.
            char *ptr = strtok(filename, "@");
            if(ptr) {
                // On Android, the / character, space, and dot characters are the names of the normal unix domain sockets.
                if(strstr(ptr, "/")) {
                    ;
                } else if(strstr(ptr, " ")) {
                    ;
                } else if(strstr(ptr, ".")) {
                    ;
                } else { // Magisk replaces the name of the unix domain socket with a random string of 32 digits.
                    if (strlen(ptr) >= 32) {
                        // Magisk was detected.
                        LOGD("[Detect Magisk UnixDomainSocket] %s", ptr);

                        detect_count++;
                    }
                }
            }
        }
    }

    if(detect_count == 0) {
        result = 0;
    }

    return result;
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
int Java_com_scottyab_rootbeer_RootBeerNative_checkForRoot( JNIEnv* env, jobject thiz, jobjectArray pathsArray ) {

    int binariesFound = 0;

    int stringCount = (env)->GetArrayLength(pathsArray);

    for (int i = 0; i < stringCount; i++) {
        jstring string = (jstring) (env)->GetObjectArrayElement(pathsArray, i);
        const char *pathString = (env)->GetStringUTFChars(string, 0);

        binariesFound += exists(pathString);

        (env)->ReleaseStringUTFChars(string, pathString);
    }

    return binariesFound>0;
}
