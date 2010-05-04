/*  
	This file is part of Penn TotalRecall <http://memory.psych.upenn.edu/TotalRecall>.

    TotalRecall is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 only.

    TotalRecall is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TotalRecall.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Header for a C library implementing libpenntotalrecall usable from Java (via Java Native Access) in Penn TotalRecall.
 *
 * Documentation can be found in Java binding interface edu.upenn.psych.memory.nativestatelessplayer.LibPennTotalRecall.java
 *
 * WARNING for FMOD implementations: streamPosition() must be called frequently in order to cause FMOD's system to update.
 *
 * Author: Yuvi Masory
 */
 
 /* guard against double inclusion */
#ifndef LIBPENNTOTALRECALL_H
#define LIBPENNTOTALRECALL_H

/* define true and false */
#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

/* macros for Windows compilation */
#ifdef _WIN32
#define EXPORT_DLL __declspec(dllexport)
#endif

/* expand Windows macros to the empty string for non-Windows compilation */
#ifndef EXPORT_DLL
#define EXPORT_DLL
#endif

/* prohibit C++ function decorations inc ase a C++ compiler is used */
#ifdef __cplusplus
extern "C" {
#endif

/* define library's exposed API */
EXPORT_DLL int startPlayback(char* filename, long long startFrame, long long endFrame, int frameRate);
EXPORT_DLL long long stopPlayback(void);
EXPORT_DLL long long streamPosition(void);
EXPORT_DLL int playbackInProgress(void);
EXPORT_DLL int getLibraryRevisionNumber(void);
EXPORT_DLL const char* getLibraryName(void);

#ifdef __cplusplus
}
#endif

#endif
