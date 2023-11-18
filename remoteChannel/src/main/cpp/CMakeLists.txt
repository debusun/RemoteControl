# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)
set(CMAKE_SYSTEM_NAME Android)
message(STATUS "CMAKE_SYSTEM_VERSION (before build) = ${CMAKE_SYSTEM_VERSION}")
# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("DDSHelloWorld")
set(CMAKE_ANDROID_API_MIN <25>)
set(CMAKE_ANDROID_API <25>)
#设置API level和cdr路径
set(CMAKE_SYSTEM_VERSION 25) # API level
set(LOCAL_FASTCDR_PATH "~/Fast-DDS/install/")
message(STATUS "CMAKE_SYSTEM_VERSION (before build) = ${CMAKE_SYSTEM_VERSION}")
include_directories(${LOCAL_FASTCDR_PATH}/include)
link_directories(${LOCAL_FASTCDR_PATH}/lib)

if(NOT fastrtps_FOUND)
    #    find_package(fastrtps 2.12 REQUIRED)
    include_directories(/home/bcy/Fast-DDS/install/share/fastrtps/cmake)
endif()
include_directories(/home/bcy/Fast-DDS/install/include/fastdds)

# Set C++11
include(CheckCXXCompilerFlag)
if(CMAKE_COMPILER_IS_GNUCXX OR CMAKE_COMPILER_IS_CLANG OR
        CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    check_cxx_compiler_flag(-std=c++11 SUPPORTS_CXX11)
    if(SUPPORTS_CXX11)
        add_compile_options(-std=c++11)
    else()
        message(FATAL_ERROR "Compiler doesn't support C++11")
    endif()
endif()

message(STATUS "Configuring HelloWorld publisher/subscriber example...")
file(GLOB DDS_REMOTE_CONTROL_SOURCES_CXX "src/*.cxx")
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
#add_library(${CMAKE_PROJECT_NAME} SHARED
#        # List C/C++ source files with relative paths to this CMakeLists.txt.
#        testdds.cpp)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.

add_library(RemoteControlServer SHARED src/RemoteControlServer.cpp ${DDS_REMOTE_CONTROL_SOURCES_CXX})
target_link_libraries(RemoteControlServer fastrtps fastcdr)
target_link_libraries(RemoteControlServer log)

add_library(RemoteControlClient SHARED src/RemoteControlClient.cpp ${DDS_REMOTE_CONTROL_SOURCES_CXX})
target_link_libraries(RemoteControlClient fastrtps fastcdr)
target_link_libraries(RemoteControlClient log)

message(STATUS "CMAKE_SYSTEM_VERSION (before build) = ${CMAKE_SYSTEM_VERSION}")