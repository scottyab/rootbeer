/****************************************************************************
 * File:   rootChecker.cpp
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

// Socket and general headers
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <netdb.h>
#include <time.h>
#include <unistd.h>
#include <sys/stat.h>
/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> User Includes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/
#include "rootChecker.h"

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Defines <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

// LOGCAT
/* Set to 1 to enable debug log traces. */
#define DEBUG 1

/* Adjust this so the packet data in the log is readable */
#define TRACE_DATA_LINE_LENGTH      16

#define  LOG_TAG    "I2H-JNI"

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> Constant Macros <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

#define  LOGD(...)  if (DEBUG) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__);
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__);

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> Function Macros <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> Global Variables  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

/* JavaVM global variables to access java methods from C */
JavaVM *g_vm; 
jobject g_obj;
JNIEnv *g_t_env;

/****************************************************************************
 *>>>>>>>>>>>>>>>>>>>>>>>>>> Public Functions  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<*
 ****************************************************************************/

int exists(const char *fname)
{
    FILE *file;
    if (file = fopen(fname, "r"))
    {
      LOGE("LOOKING FOR BINRARY: %s PRESENT!!!",fname);
        fclose(file);
        return 1;
    }
      LOGE("LOOKING FOR BINRARY: %s Absent :(",fname);
    return 0;
}


/*****************************************************************************  
 * Description: Called when the JNI is intialised
 *
 * Parameters: vm - reference to the JavaVM
 *      reserved - 
 *
 * Return value: JNI version number
 *
 *****************************************************************************/
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    /* We save a vm as a global so that we can access attach new
     * threads created in C to the JavaVM in order that we can call
     * java methods from inside these threads.
     */
    g_vm=vm;
    
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}


/*****************************************************************************  
 * Description: Simple function to confirm JNI interface setup
 *
 * Parameters: env - Java environment pointer
 *      thiz - javaobject
 *
 * Return value: A string
 *
 *****************************************************************************/

jstring Java_com_scottyab_rootchecker_RootCheckNative_stringFromJNI( JNIEnv* env, jobject thiz )
{
    
    return (env)->NewStringUTF( "JNI communication test successful!");
}



/*****************************************************************************  
 * Description: Get NDK Build info
 *
 * Parameters: env - Java environment pointer
 *      thiz - javaobject
 *
 * Return value: A string
 *
 *****************************************************************************/

jstring Java_com_scottyab_rootchecker_RootCheckNative_getNDKVersionString( JNIEnv* env, jobject thiz )
{
  
    char ndkString[200];
    sprintf(ndkString, "%s %s", __TIME__ ,__DATE__);
    LOGD("%s",ndkString);

    return (env)->NewStringUTF( ndkString );
}


/*****************************************************************************  
 * Description: Get NDK Build info
 *
 * Parameters: env - Java environment pointer
 *      thiz - javaobject
 *
 * Return value: A string
 *
 *****************************************************************************/

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

int Java_com_scottyab_rootchecker_RootCheckNative_checkForRoot( JNIEnv* env, jobject thiz )
{
    
  int binariesFound = 0;
  for(int i = 0 ; i < 8; i ++){
    binariesFound+=exists(paths[i]);
  }

    return binariesFound>0;
}


