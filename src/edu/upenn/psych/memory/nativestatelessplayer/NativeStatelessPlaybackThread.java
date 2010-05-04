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

import info.Constants;

import java.io.File;
import java.util.List;

import control.CurAudio;

import edu.upenn.psych.memory.precisionplayer.PrecisionEvent;
import edu.upenn.psych.memory.precisionplayer.PrecisionEventLauncher;
import edu.upenn.psych.memory.precisionplayer.PrecisionListener;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

public class NativeStatelessPlaybackThread extends Thread {
	
	private final long startFrame;
	private final long endFrame;
	private final List<PrecisionListener> listeners;
	private final NativeStatelessPlayer myPlayer;
	private final File audioFile;
	private LibPennTotalRecall myLib;
	
	private volatile boolean finish;
	
	protected NativeStatelessPlaybackThread(LibPennTotalRecall lib, NativeStatelessPlayer player, File file, long startFrame, long endFrame, List<PrecisionListener> listeners) {
		this.audioFile = file;
		this.listeners = listeners;
		this.startFrame = startFrame;
		this.endFrame = endFrame + CurAudio.getOffsetFrames();
		this.myPlayer = player;
		this.myLib = lib;
		this.finish = false;
	}

	@Override
	public void run() {
		try {
//			System.out.println(getClass().getName() + ": " + startFrame + " to " + endFrame);
			int returnCode = myLib.startPlayback(audioFile.getAbsolutePath(), startFrame, endFrame, (int) (CurAudio.getMaster().frameRate()));

			if(returnCode < 0) {
				myLib.stopPlayback();
				String message = "Unable to start playback.\n";
				switch(returnCode) {
				case(-2): 
					message += "No audio device found."; 
				break;
				case(-3): 
					message += "Unable to find or open file."; 
				break;
				case(-4):
					message += "Inconsistent state. Trying to repair";
				break;
				case(-5):
					message += "I/O error.";
				break;
				default: 
					String os = System.getProperty("os.name");
					if(os != null && os.toLowerCase().contains("linux")) {
						message += "\n" + Constants.programName + " prefers exclusive access to the sound system.\n" +
						"Please close all sound-emitting programs and web pages and try again.";
					}
					else {
						message += "Unspecified error."; 						
					}
					break;
				}
				if(listeners != null) {
					myPlayer.setStatus(PrecisionPlayer.Status.READY);
					PrecisionEventLauncher trigger = new PrecisionEventLauncher(PrecisionEvent.EventCode.ERROR, -1, message, listeners);
					trigger.start();
					return;
				}
			}



			while(finish == false) {
				long framesElapsed = myLib.streamPosition();				
				long curFrame = framesElapsed + startFrame;				
				if(curFrame >= endFrame) {
					if(curFrame > Integer.MAX_VALUE) {
						//this may be JNA number representation related, they warn not to use Java long
						System.err.println("applying FMOD last-frame-is-huge workaround");
						curFrame = endFrame;
					}
					else {
						stopPlayback();
					}
				}
				
				if(listeners != null) {
					if(framesElapsed > 0) {
						for(PrecisionListener ppl: listeners) {
							ppl.progress(curFrame);
						}
					}
				}
				if(myLib.playbackInProgress() == false) {
					break;
				}
				try {
					Thread.sleep(30);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(finish == false) {
				myLib.stopPlayback(); //this is EOM. we must still call stopPlayback() to close the native stream 
				if(listeners != null) {					
					//there is no way to guarantee the hearing frame at this line is actually the final frame
					//however, PrecisionPlayer requires EOM events report that they occur at the final frame, so we oblige
					myPlayer.setStatus(PrecisionPlayer.Status.READY);
					PrecisionEventLauncher trigger = new PrecisionEventLauncher(PrecisionEvent.EventCode.EOM, endFrame, null, listeners);
					trigger.start();
				}
			}
		}
		catch(Throwable t) {

			try {
				myLib.stopPlayback();
			}
			catch(Throwable t2) {
				t2.printStackTrace();
			}

			if(listeners != null) {
				myPlayer.setStatus(PrecisionPlayer.Status.READY);
				PrecisionEventLauncher trigger = new PrecisionEventLauncher(PrecisionEvent.EventCode.ERROR, -1, t.getMessage(), listeners);
				trigger.start();
				t.printStackTrace();
			}
		}

		if(myLib.playbackInProgress()) {
			try{
				Thread.sleep(500);
			}
			catch(InterruptedException e) {
				
			}
		}
	}
	
	protected long stopPlayback() {
		finish = true;		
		long stopFrame = myLib.stopPlayback();
		return stopFrame;
	}
}
