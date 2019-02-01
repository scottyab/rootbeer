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
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <pwd.h>
#include <grp.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/un.h>

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
 * Description: Parsing the mode_t structure
 *
 * Parameters: mode - mode_t structure, buf - Parsing result
 *
 *****************************************************************************/
void strmode(mode_t mode, char * buf) {
    const char chars[] = "rwxrwxrwx";

    for (size_t i = 0; i < 9; i++) {
        buf[i] = (mode & (1 << (8-i))) ? chars[i] : '-';
    }

    buf[9] = '\0';
}

/*****************************************************************************
 * Description: Check file stat
 *
 * Parameters: fname - filename to check
 *
 * Return value: 0 - non-existant / not visible, 1 - exists
 *
 *****************************************************************************/
extern int stat(const char *, struct stat *);
int checkFileStat(char *fname)
{
    int return_stat = 0;
    struct stat file_info = { 0 };
    struct passwd *my_passwd;
    struct group  *my_group;
    mode_t file_mode;

    if(!fname)
    {
        LOGD(">>>>> fname is NULL!!");
        return -1;
    }

    if ((return_stat = stat(fname, &file_info)) == -1)
    {
        LOGD(">>>>> stat() Failed!!");
        return -1;
    }

    file_mode = file_info.st_mode;
    LOGD(">>>>> File Name : %s\n", fname);
    printf(">>>>> =======================================\n");
    LOGD(">>>>> File Type : ");
    if (S_ISREG(file_mode))
    {
        LOGD(">>>>> Regular File\n");
    }
    else if (S_ISLNK(file_mode))
    {
        LOGD(">>>>> Symbolic Link\n");
    }
    else if (S_ISDIR(file_mode))
    {
        LOGD(">>>>> Directory\n");
    }
    else if (S_ISCHR(file_mode))
    {
        LOGD(">>>>> Terminal Device\n");
    }
    else if (S_ISBLK(file_mode))
    {
        LOGD(">>>>> Block Device\n");
    }
    else if (S_ISFIFO(file_mode))
    {
        LOGD(">>>>> FIFO\n");
    }
    else if (S_ISSOCK(file_mode))
    {
        LOGD(">>>>> Socket\n");
    }

    /*
    char buf[11] = { 0 };
    strmode(file_mode, buf);
    LOGD(">>>>> %04o is %s\n", file_mode, buf);
    my_passwd = getpwuid(file_info.st_uid);
    my_group  = getgrgid(file_info.st_gid);
    LOGD(">>>>> OWNER : %s\n", my_passwd->pw_name);
    LOGD(">>>>> GROUP : %s\n", my_group->gr_name);
    LOGD(">>>>> FILE SIZE IS : %d\n", (int)file_info.st_size);
    LOGD (">>>>> last read time:%d", file_info.st_atime);
    LOGD (">>>>> last modification time:%d", file_info.st_mtime);
    LOGD (">>>>> last state change time:%d", file_info.st_ctime);
    LOGD (">>>>> I / O block size:%d", file_info.st_blksize);
    LOGD (">>>>> allocated block size:%d", file_info.st_blocks);
    LOGD (">>>>> hardlinked files:%d", file_info.st_nlink);
    LOGD (">>>>> inode:%d", file_info.st_ino);
    LOGD (">>>>> bytes in regular file:%d", file_info.st_size);
    LOGD (">>>>> DEVICE NUMBER:%d", (int) file_info.st_dev);
    LOGD (">>>>> device number of special file:%d", (int) file_info.st_rdev);
    */

    return 1;
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
    int uds_detect_count = 0;
    int magisk_file_detect_count = 0;
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

            magisk_file_detect_count += checkFileStat("/dev/.magisk.unblock");

            magisk_file_detect_count += checkFileStat("/sbin/magiskinit");
            magisk_file_detect_count += checkFileStat("/sbin/magisk");
            magisk_file_detect_count += checkFileStat("/sbin/.magisk");

            magisk_file_detect_count += checkFileStat("/data/adb/magisk.img");
            magisk_file_detect_count += checkFileStat("/data/adb/magisk.db");
            magisk_file_detect_count += checkFileStat("/data/adb/.boot_count");
            magisk_file_detect_count += checkFileStat("/data/adb/magisk_simple");
            magisk_file_detect_count += checkFileStat("/data/adb/magisk");

            magisk_file_detect_count += checkFileStat("/cache/.disable_magisk");
            magisk_file_detect_count += checkFileStat("/cache/magisk.log");

            magisk_file_detect_count += checkFileStat("/init.magisk.rc");

            /*
            /overlay/sbin/magisk
            /data/adb/magisk/magisk.apk
            /data/adb/magisk_debug.log
            /data/adb/magisk_merge.img
            /dev/.magisk.patch.done
            /data/data/com.topjohnwu.magisk/install
            /data/user_de/0/com.topjohnwu.magisk/install
            */

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
                    int len = strlen(ptr);
                    if (len >= 32) {
                        // Magisk was detected.
                        LOGD("[Detect Magisk UnixDomainSocket] %s", ptr);

                        uds_detect_count++;
                    }
                }
            }
        }
    }

    if(uds_detect_count == 0 || magisk_file_detect_count == 0) {
        result = 0;
    } else {
        result = 1;
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
