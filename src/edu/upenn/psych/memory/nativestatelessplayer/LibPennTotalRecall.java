//    This file is part of Penn TotalRecall <http://memory.psych.upenn.edu/TotalRecall>.
//
//    TotalRecall is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, version 3 only.
//
//    TotalRecall is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with TotalRecall.  If not, see <http://www.gnu.org/licenses/>.

package edu.upenn.psych.memory.nativestatelessplayer;

import com.sun.jna.Native;


/**
 * Interface to libpenntotalrecall, being a the generic form for a C interface to a native shared library that can render audio on behalf of a <code>PrecisionPlayer</code>.
 * 
 * We use this approach instead of generating bindings for an entire native audio library since we will only use a handful of functions and wish to minimize latency and processing overhead.
 * 
 * WARNING if using FMOD implementation: streamPosition() must be called frequently in order to cause FMOD's system to update.
 * 
 * @author Yuvi Masory
 */
public final class LibPennTotalRecall {
	
	static {
        try {
            Native.register("penntotalrecall");
        }
        catch(Throwable t) {
            Native.register("penntotalrecall64");
        }
	}
	
	public static final LibPennTotalRecall instance = new LibPennTotalRecall();
	
	/**
	 * Tells native library to playback audio immediately.
	 * 
	 * Guarantees <code>playbackInProgress()</code> post-condition if no error.
	 * 
	 * 0 return value indicates playback successful.
	 * Negative return values indicate an error.
	 * Specifically:
	 * 		-1 - unspecified error
	 * 		-2 - no audio devices found
	 * 		-3 - unable to find or use file
	 * 		-4 - inconsistent state (e.g. <code>playbackInProgress()</code>)
	 * 
	 * @param canonicalPath File path
	 * @param startFrame First frame of audio in the file to render
	 * @param endFrame Last frame of audio in the file to render
	 * @param frameRate The framerate of the input source
	 * @return Return-code, see above
	 */
	public native int startPlayback(String canonicalPath, long startFrame, long endFrame, int frameRate);
	

	/**
	 * Tells native library to stop audio playback immediately.
	 * 
	 * Guarantees <code>playbackInProgress() == false</code> post-condition.
	 * 
	 * @return The hearing frame, relative to start frame, or -1 if audio not playing
	 */
	public native long stopPlayback();
	
	/**
	 * Asks the native library for the hearing frame.
	 * 
	 * @return The hearing frame, relative to start frame, or -1 if audio not playing
	 */
	public native long streamPosition();
	
	/**
	 * Asks the native library whether audio is currently being rendered.
	 */
	public native boolean playbackInProgress();
	
	/**
	 * Returns the version of the native library being used.
	 */
	public native int getLibraryRevisionNumber();
	
	/**
	 * Returns the name of the native library being used.
	 * 
	 * Intended to be meaningful, not necessarily the file path on the disk.
	 */
	public native String getLibraryName();
	
	
	private LibPennTotalRecall() {}
}
