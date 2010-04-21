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
 * Implementation of libpenntotalrecall using FMOD.
 *
 * Author: Yuvi Masory
 *
 * WARNING streamPosition() must be called frequently in order to cause FMOD's system to update.
 *
 * See Apache Ant build.xml that came with this project to see which compilers this has been tested on.
 */
 

#include "../inc/libpenntotalrecall.h"

#include "../inc/wincompat.h"
#include "../inc/fmod.h"
#include "../inc/fmod_errors.h"

#include <stdio.h>
//needed on Windows to get memset()
#include <memory.h>

//library info
const unsigned int revisionNumber = 1;
const char* libName = "FMOD implementation of LibPennTotalRecall";

//playback state
FMOD_SYSTEM *fmsystem = NULL;
FMOD_SOUND *sound = NULL;
FMOD_CHANNEL *channel = NULL;
FMOD_CREATESOUNDEXINFO soundInfo;
int lastStartFrame = 0;


static void printError(FMOD_RESULT result);
//static FMOD_RESULT F_CALLBACK soundEndCallback(FMOD_CHANNEL *channel, FMOD_CHANNEL_CALLBACKTYPE type, void *commanddata1, void *commanddata2);



EXPORT_DLL int startPlayback(char* filename, long long startFrame, long long endFrame)
{
    unsigned int hiclock = 0, loclock = 0;
	FMOD_RESULT result = FMOD_OK;

	if (fmsystem != NULL || sound != NULL || channel != NULL || lastStartFrame != 0) {
		fprintf(stderr, "startPlayback() called in inconsistent state, trying to correct\n");
		stopPlayback();
		return -4;
	}

    if (startFrame < 0) {
      fprintf(stderr, "startPlayback() given a negative startFrame (%lld)! Correcting to 0\n", startFrame);
      startFrame = 0;
    }

    if (endFrame <= startFrame) {
    	fprintf(stderr, "startPlayback() given an endFrame (%lld) <= startFrame (%lld)", endFrame, startFrame);
		stopPlayback();
		return -1;
    }

	lastStartFrame = startFrame;

	result = FMOD_System_Create(&fmsystem);
	if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::System_Create() in startPlayback()\n");
		printError(result);
		stopPlayback();
		return -1;
	}

	result = FMOD_System_Init(fmsystem, 32, FMOD_INIT_NORMAL, NULL);
	if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::System.init() in startPlayback()\n");
		printError(result);
		stopPlayback();
		return -1;
	}

	memset(&soundInfo, 0, sizeof(FMOD_CREATESOUNDEXINFO));
	soundInfo.cbsize = sizeof(FMOD_CREATESOUNDEXINFO);
	soundInfo.initialseekposition = startFrame;
	soundInfo.initialseekpostype = FMOD_TIMEUNIT_PCM;

	result = FMOD_System_CreateSound(fmsystem, filename, FMOD_SOFTWARE | FMOD_CREATESTREAM | FMOD_LOOP_OFF, &soundInfo, &sound);
	if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::System.createSound() in startPlayback()\n");
		printError(result);
		stopPlayback();
		return -3;
	}

	result = FMOD_System_PlaySound(fmsystem, FMOD_CHANNEL_FREE, sound, TRUE, &channel);
	if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::System.playSound() in startPlayback()\n");
		printError(result);
		stopPlayback();
		return -1;
	}

	/*
	result = FMOD_Channel_SetCallback(channel, soundEndCallback);
	if ((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN)) {
		fprintf(stderr, "exceptional return value for FMOD::System.setCallback() in startPlayback()\n");
		printError(result);
		return -1;
	}
	*/

    FMOD_System_GetDSPClock(fmsystem, &hiclock, &loclock); 
    FMOD_64BIT_ADD(hiclock, loclock, 0, endFrame - startFrame);
    result = FMOD_Channel_SetDelay(channel, FMOD_DELAYTYPE_DSPCLOCK_END, hiclock, loclock);
	if (result != FMOD_OK) {
        fprintf(stderr, "exceptional return value for FMOD::Chanel.setDelay() in startPlayback()\n");
        printError(result);
		stopPlayback();
        return -1;
    }

	result = FMOD_Channel_SetVolume(channel, 1);
	if ((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN)) {
		fprintf(stderr, "exceptional return value for FMOD::Channel.setVolume() in startPlayback()\n");
		printError(result);
		stopPlayback();
		return -1;
	}

	result = FMOD_Channel_SetPaused(channel, FALSE);
	if ((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN)) {
		fprintf(stderr, "exceptional return value for FMOD::Channel.setPaused() in startPlayback()\n");
		printError(result);
		stopPlayback();
		return -1;
	}

	FMOD_System_Update(fmsystem);

    return 0;
}

EXPORT_DLL long long stopPlayback(void)
{
	FMOD_RESULT result = FMOD_OK;
	long long toReturn;

	if (fmsystem == NULL || sound == NULL || channel == NULL) {
		fprintf(stderr, "stopPlayback() called in inconsistent state\n");
	}

	toReturn = streamPosition();

    if(sound != NULL) {
      result = FMOD_Sound_Release(sound);

      if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::Sound.release() in stopPlayback()\n");
		printError(result);
		return -1;
      }
    }

    if(fmsystem != NULL) {
      result = FMOD_System_Close(fmsystem);
      if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::System.close() in stopPlayback()\n");
		printError(result);
		return -1;
      }
    }

    if(fmsystem != NULL) {
      result = FMOD_System_Release(fmsystem);
      if (result != FMOD_OK) {
		fprintf(stderr, "exceptional return value for FMOD::System.release() in stopPlayback()\n");
		printError(result);
		return -1;
      }
    }

	fmsystem = NULL;
	sound = NULL;
	channel = NULL;
	lastStartFrame = 0;

	return toReturn - lastStartFrame;
}

EXPORT_DLL long long streamPosition(void)
{
	FMOD_RESULT result = FMOD_OK;
	unsigned int frames = 0;

	if (fmsystem == NULL || sound == NULL || channel == NULL) {
		fprintf(stderr, "streamPosition() called in inconsistent state\n");
		return -1;
	}

	FMOD_System_Update(fmsystem);

	result = FMOD_Channel_GetPosition(channel, &frames, FMOD_TIMEUNIT_PCM);
	if ((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN)) {
		fprintf(stderr, "exceptional return value for FMOD::Channel.getPosition() in streamPosition()\n");
		printError(result);
		return -1;
	}

	return frames - lastStartFrame;
}

EXPORT_DLL int playbackInProgress(void)
{
	FMOD_RESULT result = FMOD_OK;
	int playing = 0;

	if (channel == NULL) {
		return 0;
	}

	result = FMOD_Channel_IsPlaying(channel, &playing);
	if ((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN)) {
		fprintf(stderr, "exceptional return value for FMOD::Channel.isPlaying() in playbackInProgress()\n");
		printError(result);
	}

	return playing;
}

EXPORT_DLL int getLibraryRevisionNumber(void)
{
	return revisionNumber;
}

EXPORT_DLL const char* getLibraryName(void)
{
	return libName;
}

static void printError(FMOD_RESULT result)
{
    fprintf(stderr, "FMOD error: (%d) %s\n", result, FMOD_ErrorString(result));
}

/*
static FMOD_RESULT F_CALLBACK soundEndCallback(FMOD_CHANNEL *channel, FMOD_CHANNEL_CALLBACKTYPE type, void *commanddata1, void *commanddata2)
{
	if (type ==  FMOD_CHANNEL_CALLBACKTYPE_END) {
		stopPlayback();
		fprintf(stderr, "STOP\n");
	}
	return FMOD_OK;
}
*/
