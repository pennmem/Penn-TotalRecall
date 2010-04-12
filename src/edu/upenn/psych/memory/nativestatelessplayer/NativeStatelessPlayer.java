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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.upenn.psych.memory.precisionplayer.PrecisionEvent;
import edu.upenn.psych.memory.precisionplayer.PrecisionEventLauncher;
import edu.upenn.psych.memory.precisionplayer.PrecisionListener;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

public class NativeStatelessPlayer implements PrecisionPlayer {
	
	private PrecisionPlayer.Status status;
	
	private List<PrecisionListener> listeners;
	
	private File audioFile;
	private long lastFrame;
	private long previousStartFrame;
	
	private NativeStatelessPlaybackThread mainThread;
	private NativeStatelessPlaybackThread shortThread;
	private final LibPennTotalRecall lib = LibPennTotalRecall.instance;
	

	/**
	 * Creates an new player, with status <code>BUSY</code>.
	 */
	public NativeStatelessPlayer() {
		status = PrecisionPlayer.Status.BUSY;
		listeners = new ArrayList<PrecisionListener>();
//		System.out.println("using: " + lib.getLibraryName() + ", revision " + lib.getLibraryRevisionNumber());
	}
	

	public void open(String fileName) throws FileNotFoundException,	IOException, UnsupportedAudioFileException {
		audioFile = new File(fileName);
		status = PrecisionPlayer.Status.READY;
		notifyEvent(PrecisionEvent.EventCode.OPENED, -1, null);
		
		AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = ais.getFormat();
		if(format.getChannels() > 1) {
			throw new UnsupportedAudioFileException(getClass() + " only supports mono audio at present");
		}
		if(format.getFrameSize() != 2) {
			throw new UnsupportedAudioFileException(getClass() + " only supports 16-bit audio at present");
		}
		lastFrame = ais.getFrameLength() - 1;
	}
	
	
	private void playAt(long startFrame, long endFrame, List<PrecisionListener> players) {
		try {
			if(startFrame < 0) {
				startFrame = 0;
			}
			if(endFrame <= startFrame) {
				System.err.println("endFrame cannot be <= startFrame (" + endFrame + ", " + startFrame + ")");
				return;
			}
			if(audioFile != null) {
				if((mainThread == null || mainThread.isAlive() == false) && (shortThread == null || shortThread.isAlive() == false)) {

					NativeStatelessPlaybackThread nThread = new NativeStatelessPlaybackThread(lib, this, audioFile, startFrame, endFrame, players);  

					if(players != null) {
						previousStartFrame = startFrame;
						mainThread = nThread;
						status = PrecisionPlayer.Status.PLAYING;
						mainThread.start();
						notifyEvent(PrecisionEvent.EventCode.PLAYING, startFrame, null);
					}
					else {
						shortThread = nThread;
						shortThread.start();
					}
				}
				else {
					System.err.println("I won't start another playback thread when one is already running");
				}
			}
			else {
				System.err.println("you must open() a player before calling a play function");
			}
		}
		catch(Throwable t) {
			notifyEvent(PrecisionEvent.EventCode.ERROR, -1, t.getMessage());
		}
	}

	
	public long stop() {
		try {
			if(audioFile != null && status == PrecisionPlayer.Status.PLAYING) {
				if(mainThread != null) {
					long framesPlayed = mainThread.stopPlayback();
					long absoluteFrame = previousStartFrame + framesPlayed;
					status = PrecisionPlayer.Status.READY;
					notifyEvent(PrecisionEvent.EventCode.STOPPED, absoluteFrame, null);
					return absoluteFrame;
				}
				else {
					return -1;
				}
			}
			else {
				return -1;
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		return -1;
	}

	









	/* simple overridable functions */

	
	public void playAt(long frame) throws IllegalArgumentException {
		playAt(frame, lastFrame);
	}
	
	public void kill() {
		stop();
	}

	public void playAt(long startFrame, long endFrame) throws IllegalArgumentException {
		playAt(startFrame, endFrame, listeners);
	}

	public void playShortInterval(long startFrame, long endFrame) throws IllegalArgumentException {
		playAt(startFrame, endFrame, null);
	}	

	public PrecisionPlayer.Status getStatus() {
		return status;
	}
	
	/**
	 * Adds a new listener to receive updates from this player.
	 * 
	 * @param listener The listener to receive events
	 */
	public void addListener(PrecisionListener listener) {
		listeners.add(listener);
	}
	
	/* default/empty but overridable implementations */
	
	
	/**
	 * Program currently has no volume slider, so just return false.
	 */
	public boolean isLoudnessControlSupported() {
		return false;
	}

	public int getLoudness() {
		return 100;
	}
	
	public void setLoudness(int loudness) {}
	public void queueShortInterval(long startFrame, long endFrame) {}
	public void queuePlayAt(long frame) {}
	
	
	/* custom methods */
	
	
	/**
	 * Launches notification in a new thread.
	 * 
	 * @param code The event code
	 * @param frame The audio frame of the event
	 */
	private void notifyEvent(PrecisionEvent.EventCode code, long frame, String errorMessage) {
		PrecisionEventLauncher trigger = new PrecisionEventLauncher(code, frame, errorMessage, listeners);
		trigger.start();
	}


	public void setStatus(Status status) {
		this.status = status;
	}
}
