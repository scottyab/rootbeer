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



int checkForMagiskUDS()
{
    int detect_count = 0;
    int result = 0;

    // Magisk UDS Detection Method
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

            char *ptr = strtok(filename, "@");
            if(ptr) {
                if(strstr(ptr, "/")) {
                    ;
                } else if(strstr(ptr, " ")) {
                    ;
                } else if(strstr(ptr, ".")) {
                    ;
                } else {
                    if (strlen(ptr) >= 32) {
                        char temp[128] = { 0 };
                        sprintf(temp, "- [Method] Magisk 루팅이 의심됨!!\n%s\n", ptr);
                        LOGD("%s", temp);

                        detect_count++;
                    }
                }
            }
        }
    }

    if(detect_count == 0) {
        result = 0;
    }

    LOGD(">>>>>>>>>>> result: %d", result);

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

    if (binariesFound == 0) {
        return checkForMagiskUDS() > 0;
    }

    return binariesFound>0;
}
