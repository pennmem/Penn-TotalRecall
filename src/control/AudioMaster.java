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

package control;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import components.audiofiles.AudioFile;

/**
 * JavaSound distributes useful information about a file among sever classes in a way that can
 * 	be annoying if  you don't have the API memorized. This class stores and determines everything
 * 	the program needs to know about an audio file.
 * 
 * <p>
 * Audio checking policy: <code>AudioMaster</code> constructor throws exceptions concerning Java Sound's inability to handle
 * a file, as well as exceptions for this program's inability to handle a format. No other checking
 * needs to be conducted after an AudioMaster is successfully created. No other methods should
 * throw compatibility exceptions, but should instead suppress them with try/catch blocks if required by Java.
 * 
 * <p>
 * Sample and frame rates are guaranteed the same. AudioMaster constructor rejects audio for which 
 * they are different.
 * 
 * <p>
 * The current <code>AudioMaster</code> should be used to perform any and all math conversions
 * related to the open audio file.
 * 
 * @author Yuvi Masory
 *
 */
public class AudioMaster {	

	private static final int BITS_PER_BYTE = 8;

	// from constructor
	private AudioFile audioFile;

	// from AudioInputStream
	private long numSampleFrames;

	// from AudioFormat
	private boolean isBigEndian;
	private int numChannels;
	private int sampleSizeInBits;
	private int frameSizeInBits;
	private float framesPerSecond;
	private float sampleRate;
	private AudioFormat.Encoding encoding;

	// computed
	private double durationInSeconds;

	/**
	 * Incompatible file types are rejected, forcing error handling on whoever is instantiating the class.
	 * 
	 * @param audioFile the file containing the audio
	 * @throws FileNotFoundException
	 * @throws UnsupportedAudioFileException If Java Sound or some other part of this program can't handle the file
	 * @throws IOException
	 */
	public AudioMaster(AudioFile audioFile) throws FileNotFoundException, UnsupportedAudioFileException, IOException {
		this.audioFile = audioFile;

		// open an AudioInputStream
		AudioInputStream aiStream = AudioSystem.getAudioInputStream(
				new BufferedInputStream (
						new FileInputStream(audioFile)));
		
		if(aiStream.getFormat().getFrameSize() != 2) {
			throw new UnsupportedAudioFileException("only 16-bit audio is supported");
		}
		if(aiStream.getFormat().getChannels() != 1) {
			throw new UnsupportedAudioFileException("only mono audio is supported");
		}
		

		// grab info from AudioInputStream
		numSampleFrames = aiStream.getFrameLength();
		AudioFormat format = aiStream.getFormat();

		// grab info from AudioFormat
		numChannels = format.getChannels();
		framesPerSecond = format.getFrameRate();
		sampleRate = format.getSampleRate();
		sampleSizeInBits = format.getSampleSizeInBits();
		isBigEndian = format.isBigEndian();
		encoding = format.getEncoding();

		// computed
		durationInSeconds = (double)numSampleFrames / (double)framesPerSecond;
		frameSizeInBits = sampleSizeInBits * numChannels;
		
//		printInfo();

		if(sampleSizeInBits != 16) {
			throw new UnsupportedAudioFileException("Only 16 bit encodings supported.");
		}
		if(framesPerSecond != sampleRate) {
			//this is just a sanity check. in uncompressed audio sample and frame rates should be the same
			throw new UnsupportedAudioFileException("Frame rate doesn't equal sample rate. Unsupported.");
		}

	}


	@SuppressWarnings("unused")
	private void printInfo() {
		System.out.println();
		System.out.println("-- AudioMaster --");
		System.out.println("file name: " + audioFile.getName());
		System.out.println("encoding: " + encoding);
		System.out.println("number of channels: " + numChannels);
		System.out.println("bigEndian: " + isBigEndian);
		System.out.println("sample size in bits: " + sampleSizeInBits);
		System.out.println("frames per second: " + framesPerSecond);
		System.out.println("sample rate: " + sampleRate);
		System.out.println("num frames: " + numSampleFrames);
		System.out.println("predicted duration: " + durationInSeconds() + " seconds");
		System.out.println();
	}

	public double durationInSeconds() {
		return durationInSeconds;
	}

	public long durationInFrames() {
		return numSampleFrames;
	}

	public int numChannels() {
		return numChannels;
	}
	
	public double frameRate() {
		return framesPerSecond;
	}

	public int sampleSizeInBits() {
		return sampleSizeInBits;
	}
	
	public int sampleSizeInBytes() {
		return sampleSizeInBits/BITS_PER_BYTE;
	}

	public int frameSizeInBits() {
		return frameSizeInBits;
	}
	
	public int frameSizeInBytes() {
		return frameSizeInBits/BITS_PER_BYTE;
	}
	
	public long millisToFrames(long millis) {
		return millisToFrames((double)millis);
	}


	public long millisToFrames(double millis) {
		double sec = millis/1000.;
		return (long) (sampleRate * sec);
	}


	public long nanosToFrames(long millis) {
		return millisToFrames(((double)millis)/1000000);
	}


	public double framesToMillis(long frames) {
		return (framesToSec(frames) * 1000);
	}


	public double framesToSec(long frames) {
		return frames * (1/((double)sampleRate));
	}

	public long framesToBytes(long frames) {
		return frames * (frameSizeInBits/BITS_PER_BYTE);
	}

	public long secondsToFrames(double seconds) {
		return (long)(sampleRate * seconds);
	}
	
	public AudioFile getAudioFile() {
		return audioFile;
	}
}
