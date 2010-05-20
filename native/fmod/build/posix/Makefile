# Makefile for FMOD shared library used by Penn TotalRecall
# Compiles libpenntotalrecall.c
#
# Supports Linux 32/64-bit, Mac OSX 32/64-bit Intel/PPC
# For Windows support see Visual Studio project in ../windows
#
# Author: Yuvi Masory
#
# Distributed under the terms of the GNU General Public License, version 3.
# See LICENSE file
#
# Note: This Makefile assumes a very particular directory structure. Be careful
# changing the directory structure from what is in the repository!
# The unusual choice of build target location, and the lack of a clean option,
# have to do with Ant integration.
#
# C compilers tested:
#	i686-apple-darwin10-gcc-4.2.1 (GCC) 4.2.1 (Apple Inc. build 5646) (dot 1)
#	gcc (Ubuntu 4.4.1-4ubuntu9) 4.4.1
#   gcc (Ubuntu 4.4.3-4ubuntu5) 4.4.3

###############################
# Set variables
###############################
PLATFORM = $(shell uname)
SOURCE = ../../src/libpenntotalrecall.c
GENERAL_CFLAGS = -Wall -O3 -std=c99 -pedantic
BUILD_DIR = ../../../../tmp/
INSTALL_DIR = /usr/lib/

###############################
# Linux
###############################
ifeq ($(PLATFORM), Linux)

SHARED_OPT = -shared
COMMON = -pthread -fPIC
PLATFORM_CFLAGS = $(COMMON) -m32
PLATFORM_CFLAGS64 = $(COMMON) -m64

BUILD_NAME = libpenntotalrecall.so
LIBRARIES = -lfmodex-4.28.09 -L../../lib/linux32/
FMOD_LIB = ../../lib/linux32/libfmodex-*.so

LIBRARIES64 = -lfmodex64-4.28.09 -L../../lib/linux64/
FMOD_LIB64 = ../../lib/linux64/libfmodex64-*.so
BUILD_NAME64 = libpenntotalrecall64.so

endif

###############################
# OSX
###############################
ifeq ($(PLATFORM), Darwin)

BUILD_NAME = libpenntotalrecall.dylib
LIBRARIES = -lfmodex -L../../lib/osx/
FMOD_LIB = ../../lib/osx/libfmodex.dylib
SHARED_OPT = -dynamiclib
PLATFORM_CFLAGS = -arch i386 -arch ppc -arch x86_64 -mmacosx-version-min=10.5 -isysroot /Developer/SDKs/MacOSX10.5.sdk

endif

###############################
# All Platforms
###############################
all: $(PLATFORM)_all

Darwin_all:
	 mkdir -p ../../../../tmp
	 gcc $(SHARED_OPT) $(GENERAL_CFLAGS) $(PLATFORM_CFLAGS) -o $(BUILD_DIR)$(BUILD_NAME) $(SOURCE) $(LIBRARIES)

Linux_all: Darwin_all
	 gcc $(SHARED_OPT) $(GENERAL_CFLAGS) $(PLATFORM_CFLAGS64) -o $(BUILD_DIR)$(BUILD_NAME64) $(SOURCE) $(LIBRARIES64)


install: $(PLATFORM)_install

Darwin_install:
	sudo mv $(BUILD_DIR)$(BUILD_NAME) $(INSTALL_DIR)
	sudo chmod ugo+rx $(BUILD_DIR)$(BUILD_NAME)
	sudo cp -p $(FMOD_LIB) $(INSTALL_DIR)

Linux_install: Darwin_install
	sudo mv $(BUILD_DIR)$(BUILD_NAME64) $(INSTALL_DIR)
	sudo chmod ugo+rx $(BUILD_DIR)$(BUILD_NAME64)
	sudo cp -p $(FMOD_LIB64) $(INSTALL_DIR)


uninstall: $(PLATFORM)_uninstall

Darwin_uninstall:
	sudo rm -f $(INSTALL_DIR)$(BUILD_NAME)

Linux_uninstall: Darwin_uninstall
	sudo rm -f $(INSTALL_DIR)$(BUILD_NAME64)
