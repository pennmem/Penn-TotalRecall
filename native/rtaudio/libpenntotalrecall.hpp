/*
 * Header for a C++ library implementing libpenntotalrecall usable from Java in Penn TotalRecall.
 *
 * Documentation can be found in Java binding interface edu.upenn.psych.memory.nativestatelessplayer.LibPennTotalRecall.java
 *
 * Author: Yuvi Masory
 */

extern "C" int startPlayback(char* filename, long long startFrame, long long endFrame);

extern "C" long long stopPlayback(void);

extern "C" long long streamPosition(void);

extern "C" int playbackInProgress(void);

extern "C" int getLibraryRevisionNumber(void);

extern "C" const char * getLibraryName(void);
