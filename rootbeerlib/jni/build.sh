#!/bin/bash
# build.sh uses the android ndk to compile and then copies the shared object into the correct place
# written by Matthew Rollings 2015
ndk-build 
cp ../libs/armeabi/libtool-checker.so ../src/main/jniLibs/armeabi/libtool-checker.so
cp ../libs/armeabi-v7a/libtool-checker.so ../src/main/jniLibs/armeabi-v7a/libtool-checker.so
cp ../libs/mips/libtool-checker.so ../src/main/jniLibs/mips/libtool-checker.so
cp ../libs/x86/libtool-checker.so ../src/main/jniLibs/x86/libtool-checker.so