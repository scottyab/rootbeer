#!/bin/bash
# build.sh uses the android ndk to compile and then copies the shared object into the correct place
# written by Matthew Rollings 2015
ndk-build 
cp ../libs/armeabi/libtool-checker.so ../src/main/jniLibs/armeabi-v7a/libtool-checker.so