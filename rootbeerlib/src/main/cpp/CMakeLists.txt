cmake_minimum_required(VERSION 3.4.1)

add_library(toolChecker SHARED
        toolChecker.cpp)

target_link_libraries(toolChecker
        android
        log)
