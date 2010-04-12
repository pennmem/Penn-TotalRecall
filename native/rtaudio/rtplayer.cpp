/******************************************/
/*
  rtplayer.cpp
  by Yuvi Masory

  Ubuntu:
  to compile 32-bit you need lib32z1-dev and 32-bit lasound.so
  to compile with ALSA support you need libasound2-dev

  derived from:  	
  playraw.cpp
  by Gary P. Scavone, 2007

  Plays back mono signed 16-bit raw data.
*/
/******************************************/

#include "RtAudio.h"
#include <iostream>
#include <cstdlib>
#include <cstring>
#include <stdio.h>

typedef signed short  MY_TYPE;
#define FORMAT RTAUDIO_SINT16
#define SCALE  32767.0

// Platform-dependent sleep routines.
#if defined( __WINDOWS_ASIO__ ) || defined( __WINDOWS_DS__ )
  #include <windows.h>
  #define SLEEP( milliseconds ) Sleep( (DWORD) milliseconds ) 
#else // Unix variants
  #include <unistd.h>
  #define SLEEP( milliseconds ) usleep( (unsigned long) (milliseconds * 1000.0) )
#endif






int callback(void *outputBuffer, void *inputBuffer, unsigned int nBufferFrames,
		double streamTime, RtAudioStreamStatus status, void *data);





//sole RtAudio instance for duration of this instance of the library
RtAudio dac;

//playback state
FILE *audioFile;
signed long long lastByte = 0;

//version of library
const unsigned int revisionNumber = 0;
const int debugLevel = 0;

//audio info
const unsigned int samplerate = 44100;
const unsigned int bytesPerFrame = 2; //hard coded with #defines above
const unsigned int numChannels = 1;
const unsigned int firstChannel = 0;

//playback optimizations
const bool minimizeLatency = true;
unsigned int bufferFrames = 1024;






extern "C" long long streamPosition(void)
{
	if(dac.isStreamOpen() == false || dac.isStreamRunning() == false) {
		if (debugLevel > 0) {
			std::cout << "DEBUG streamPosition() called in EOM-only state" << std::endl;
		}
		return -1;
	}
	else {
		return dac.getStreamTime() * samplerate;
	}
}

extern "C" int playbackInProgress(void)
{
	return dac.isStreamRunning();
}

extern "C" long long stopPlayback(void)
{
	if(dac.isStreamOpen() == false) {
		std::cerr << "ERROR stopPlayback() called in inconsistent state" << std::endl;
		dac.closeStream();
		return -1;
	}

	if(dac.isStreamRunning() == false) {
		if (debugLevel > 0) {
			std::cout << "DEBUG stopPlayback() called in EOM-only state" << std::endl;
		}
		dac.closeStream();
		fclose(audioFile);
		return -1;
	}

	double elapsed = dac.getStreamTime() * samplerate;
	dac.abortStream();
	dac.closeStream();

	fclose(audioFile);

	if (dac.isStreamOpen() || dac.isStreamRunning()) {
		std::cerr << "ERROR stopPlayback() post-condition not holding" << std::endl;
	}

	return elapsed;
}

extern "C" int startPlayback(char* path, long startFrame, long endFrame)
{
	if (debugLevel > 0) {
		std::cout << "DEBUG startPlaybac() 1: startPlayback called" << std::endl;
	}

	//should never be called with stream open, so sanity check
	if(dac.isStreamOpen() || dac.isStreamRunning()) {
		std::cerr << "ERROR startPlayback() called in inconsistent state" << std::endl;
		dac.closeStream();
		return -4;
	}

	if (debugLevel > 0) {
		std::cout << "DEBUG startPlayback() 2: sanity check passed" << std::endl;
	}

//	std::cout << "using API: " << dac.getCurrentApi();

	//are there any devices we can access? if so let's just use the default
	if (dac.getDeviceCount() < 1) {
		std::cerr << "ERROR startPlayback() no audio devices found";
		return -2;
	}

	if (debugLevel > 0) {
		std::cout << "DEBUG startPlayback() 3: audio devices found" << std::endl;
	}

	//create wrapper struct around file pointer
	audioFile = fopen(path, "rb");
	if (audioFile == NULL) {
		std::cerr << "ERROR startPlayback() unable to find or open file";
		return -3;
	}

	if (debugLevel > 0) {
		std::cout << "DEBUG startPlayback() 4: able to open device" << std::endl;
	}

	//skip forward in file
	unsigned int toSkip = startFrame * bytesPerFrame;
	int success = fseek(audioFile, toSkip, SEEK_SET);
	if (success != 0) {
		std::cerr << "ERROR startPlayback() skip unsuccessful" << std::endl;
		fclose(audioFile);
		return -3;
	}
	lastByte = endFrame * bytesPerFrame - 1;

	if (debugLevel > 0) {
		std::cout << "DEBUG startPlayback() 5: skip successful" << std::endl;
	}

	//playback stream parameters
	RtAudio::StreamParameters oParams;
	oParams.deviceId = dac.getDefaultOutputDevice();
	oParams.nChannels = numChannels;
	oParams.firstChannel = firstChannel;

	//playback stream options
	RtAudio::StreamOptions oOptions;
	if (minimizeLatency) {
		oOptions.flags = RTAUDIO_MINIMIZE_LATENCY;
	}

	if (debugLevel > 0) {
		std::cout << "DEBUG startPlayback() 6: stream options set" << std::endl;
	}

	try {
		dac.openStream(&oParams, NULL, FORMAT, samplerate, &bufferFrames, &callback, (void *)audioFile, &oOptions);
		if (debugLevel > 0) {
			std::cout << "DEBUG startPlayback() 7: stream opened" << std::endl;
		}
		dac.startStream();
		if (debugLevel > 0) {
			std::cout << "DEBUG startPlayback() 8: stream started" << std::endl;
		}
	}
	catch (RtError& e) {
		std::cerr << "ERROR startPlayback() " << e.getMessage() << std::endl;
		fclose(audioFile);
		dac.closeStream();
		return -1;
	}

	if (dac.isStreamOpen() == false || dac.isStreamRunning() == false) {
		std::cerr << "ERROR startPlayback() post-condition not holding" << std::endl;
	}

	if(debugLevel > 0) {
		std::cout << "DEBUG startPlayback() 9: post-conditions hold, falling off" << std::endl;
	}

	return 0;
}

int callback(void *outputBuffer, void *inputBuffer, unsigned int nBufferFrames, double streamTime, RtAudioStreamStatus status, void *data)
{
	if (debugLevel > 1) {
		std::cout << "DEBUG callback() called" << std::endl;
	}

	if(status) {
		std::cerr << "ERROR callback() underrun detected" << std::endl;
	}
	int pos = ftell(audioFile);
	if(pos < 0) {
		std::cerr << "ERROR callback() ftell() exceptional return value" << std::endl;
		return 2;
	}
	else {
		if(pos >= lastByte) {
			if (debugLevel > 0) {
				std::cout << "DEBUG callback() native EOM" << std::endl;
			}
			return 1;
		}
	}
	unsigned int count = fread(outputBuffer, numChannels * sizeof(MY_TYPE), nBufferFrames, audioFile);
	if (count < nBufferFrames) {
		unsigned int bytes = (nBufferFrames - count) * numChannels * sizeof(MY_TYPE);
		unsigned int startByte = count * numChannels * sizeof(MY_TYPE);
		memset((char *)(outputBuffer)+startByte, 0, bytes);

		if (debugLevel > 1) {
			std::cout << "DEBUG callback() count < nBufferFrames falling off" << std::endl;
		}

		return 1;
	}

	if (debugLevel > 1) {
		std::cout << "DEBUG callback() normal falling off" << std::endl;
	}

	return 0;
}

extern "C" long rt_getStreamLatency(void)
{
	try {
		return dac.getStreamLatency();
	}
	catch(RtError& e) {
		std::cerr << "ERROR: rt_getStreamLatency() queried at illegal time" << std::endl;
		return 0;
	}
}


extern "C" int getRevisionNumber(void)
{
	return revisionNumber;
}
