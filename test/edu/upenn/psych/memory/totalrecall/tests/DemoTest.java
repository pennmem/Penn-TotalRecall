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

package edu.upenn.psych.memory.totalrecall.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import components.audiofiles.AudioFile;
import components.audiofiles.AudioFile.AudioFilePathException;

import control.AudioMaster;
import edu.upenn.psych.memory.precisionplayer.PrecisionEvent;
import edu.upenn.psych.memory.precisionplayer.PrecisionListener;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;
import edu.upenn.psych.memory.precisionplayer.PrecisionEvent.EventCode;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer.Status;

/**
* @author Apurva Jatakia
*/
public class DemoTest implements PrecisionListener {

	private PrecisionPlayer player;
	private static int updateCount;
	private long lastTime;
	private long lastFrame;
	private ArrayList<Long> framesArr;
	private ArrayList<Long> frameProgress;
	private ArrayList<Long> clockProgress;
	private ArrayList<Long> clockDiff;
	private Hashtable<Long, Integer> frameTable;
	private Hashtable<Long, Integer> frameProgressTable;
	private Hashtable<Long, Integer> clockProgresstable;
	private Hashtable<Long, Integer> clockDiffTable;
	private int[] counts = new int[100];
	private long startTime;
	private int i = 0;
	private boolean isNano = true;
	private AudioMaster master;
	boolean decreasing = false;
	final Logger logger;
	private String filePath;
	private int timeToPlay = 5000;
	File currentDir;

	public DemoTest() {
		logger = Logger.getLogger(DemoTest.class);
		SimpleLayout layout = new SimpleLayout();

		FileAppender appender = null;
		try {
			appender = new FileAppender(layout, "output1.txt", false);
		} catch (Exception e) {
		}

		logger.addAppender(appender);
		logger.setLevel(Level.DEBUG);

	}

	public void initialize() throws IOException {
		framesArr = new ArrayList<Long>();
		frameProgress = new ArrayList<Long>();
		clockProgress = new ArrayList<Long>();
		clockDiff = new ArrayList<Long>();
		frameTable = new Hashtable<Long, Integer>();
		frameProgressTable = new Hashtable<Long, Integer>();
		clockProgresstable = new Hashtable<Long, Integer>();
		clockDiffTable = new Hashtable<Long, Integer>();
		updateCount = 0;

		player = null;

		currentDir = new File(".");

		// System.out.println(getClass().getResource(filePath).toString());

		filePath = currentDir.getCanonicalPath() + "/0.wav";
		System.out.println(filePath);

		// System.out.println("Relative Path: " +
		// getClass().getResource("audioFiles/0.wav").getPath());

		try {
			AudioFile file = new AudioFile(filePath);
			master = new AudioMaster(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AudioFilePathException e) {
			e.printStackTrace();
		}

		player.addListener(this);
		player.setLoudness(100);
		if (isNano) {
			startTime = System.nanoTime();
		} else
			startTime = System.currentTimeMillis();

	}

	public void getPlayerStats() {

		try {
			initialize();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			player.open(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

		player.playAt(0);

	}

	public void testOpen() {
		boolean warningGenerated = false;
		try {
			initialize();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		if (!player.getStatus().equals(PrecisionPlayer.Status.BUSY)) {
			logger.warn("Status before opening the file:" + player.getStatus());
			warningGenerated = true;

		}

		// open two files one after the another
		try {
			player.open(filePath);

			// giving a missing file as input
			String fileNotFound = currentDir.getCanonicalPath() + "/hi.wav";
			player.open(fileNotFound);

		} catch (FileNotFoundException e) {
			String invalidFileFormat = "";
			try {
				invalidFileFormat = currentDir.getCanonicalPath()
						+ "/unsupported_m4a_audio.wav";
			} catch (IOException e3) {
				e3.printStackTrace();
			}
			try {
				player.open(invalidFileFormat);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				// calling the open method twice
				try {
					player.open(filePath);
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (UnsupportedAudioFileException e2) {
					e2.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

		if (!player.getStatus().equals(PrecisionPlayer.Status.BUSY)) {
			logger
					.info("The Open method is having a concurrent implementation");

		}

		if (!player.getStatus().equals(PrecisionPlayer.Status.READY)) {
			logger.warn("Status after opening the file: " + player.getStatus());
			warningGenerated = true;

		}

		player.playAt(0);
		// sleep the thread for 10 seconds
		try {
			Thread.sleep(timeToPlay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.stop();
		if (warningGenerated == true) {
			System.out
					.println("Test has generated some warnings. Please check the output.txt file for the warnings");
		} else {
			System.out.println("Test Passed");
		}

	}

	public void testPlatAt() {

		boolean warningGenerated = false;
		try {
			initialize();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// starting playback before file is opened
		Status status = player.getStatus();

		player.playAt(0);

		if (!player.getStatus().equals(status)) {
			logger
					.warn("The Status after Playat is called before file is opened changed from : "
							+ status + " to " + player.getStatus());
			warningGenerated = true;

		}

		try {
			player.open(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

		player.playAt(0);
		if (!player.getStatus().equals(PrecisionPlayer.Status.PLAYING)) {
			logger
					.warn("The Status after Playat is called after file is Opened: "
							+ player.getStatus());
			warningGenerated = true;

		}

		// calling the PlayAt again after the mainthread has already been
		// instantiated
		// Starting another playback thread when one is already running
		player.playAt(500);
		if (!player.getStatus().equals(PrecisionPlayer.Status.PLAYING)) {
			logger.warn("The Status after Playat is called twice: "
					+ player.getStatus());
			warningGenerated = true;

		}

		// sleep the thread for 10 seconds
		try {
			Thread.sleep(timeToPlay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long lastStopFrame = player.stop();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.playAt(lastStopFrame);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		player.stop();
		if (warningGenerated == true) {
			System.out
					.println("Test has generated some warnings. Please check the output.txt file for the warnings");
		} else {
			System.out.println("Test Passed");
		}

	}

	public void testStop() {
		boolean warningGenerated = false;
		try {
			initialize();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// calling the stop method before opening the file
		long ret = player.stop();
		if (ret != -1) {
			logger
					.warn("The Value returned by stop funtion when called before opening the file should be -1");
			warningGenerated = true;
		}

		try {
			player.open(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

		// calling stop method after opening the file but before playAt method
		// is called
		ret = player.stop();
		if (ret != -1) {
			logger
					.warn("The Value returned by stop funtion when called after opening the file but before playing should be -1");
			warningGenerated = true;
		}

		player.playAt(0);
		try {
			Thread.sleep(timeToPlay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// stop the playback to check what gets returned
		ret = player.stop();
		if (ret == -1) {
			logger
					.warn("The Value returned by stop function when called after the file is played should not be -1");
			warningGenerated = true;
		}

		ret = player.stop();
		if (ret != -1) {
			logger
					.warn("The Value returned by stop funtion when called twice back to back after playing should be -1");
			warningGenerated = true;
		}

		if (warningGenerated == true) {
			System.out
					.println("Test has generated some warnings. Please check the output.txt file for the warnings");
		} else {
			System.out.println("Test Passed");
		}

	}

	public void progress(long frames) {
		long tmpTime;
		long frameTime = (long) master.framesToMillis(frames);
		Long lClockDiff;
		long lastFrameTime = (long) master.framesToMillis(lastFrame);
		int factor;
		if (isNano) {
			tmpTime = System.nanoTime();
			lClockDiff = new Long((frameTime - lastFrameTime)
					- ((tmpTime - lastTime) / 1000000));
			factor = 1000000;
		} else {
			tmpTime = System.currentTimeMillis();
			lClockDiff = new Long((frameTime - lastFrameTime)
					- (tmpTime - lastTime));
			factor = 1;
		}
		if ((tmpTime - startTime) >= (factor * 1000)) {
			startTime = tmpTime;
			counts[i++] = updateCount;
			updateCount = 0;
		}

		clockDiff.add(lClockDiff);

		Long lframeProgress = new Long(frames - lastFrame);
		if (lframeProgress < 0) {
			decreasing = true;
		}
		frameProgress.add(lframeProgress);

		Long lClockProgress = new Long((tmpTime - lastTime) / factor);
		clockProgress.add(lClockProgress);

		Long lFrame = new Long(frames);
		framesArr.add(lFrame);
		if (!frameTable.containsKey(lFrame)) {
			frameTable.put(lFrame, new Integer(1));
		} else {
			int count = frameTable.get(lFrame);
			frameTable.put(lFrame, new Integer(count + 1));
		}

		if (!frameProgressTable.containsKey(lframeProgress)) {
			frameProgressTable.put(lframeProgress, new Integer(1));
		} else {
			int count = frameProgressTable.get(lframeProgress);
			frameProgressTable.put(lframeProgress, new Integer(count + 1));
		}

		if (!clockProgresstable.containsKey(lClockProgress)) {
			clockProgresstable.put(lClockProgress, new Integer(1));
		} else {
			int count = clockProgresstable.get(lClockProgress);
			clockProgresstable.put(lClockProgress, new Integer(count + 1));
		}

		if (!clockDiffTable.containsKey(lClockDiff)) {
			clockDiffTable.put(lClockDiff, new Integer(1));
		} else {
			int count = clockDiffTable.get(lClockDiff);
			clockDiffTable.put(lClockDiff, new Integer(count + 1));
		}

		updateCount++;

		lastFrame = frames;
		lastTime = tmpTime;
	}

	public void getStats(int[] data, String type) {
		int sum = 0;
		int count = 0;
		// int[] datasquare = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				// datasquare[i] = data[i] * data[i];
				sum += data[i];
				count++;
			}
		}
		// int[] data2 = new int[count];
		// for (int i = 0; i < data2.length; i++) {
		// data2[i] = data[i];
		//
		// }
		long mean = sum / count;
		System.out.println();
		System.out.println("Data for " + type);
		System.out.println("The Mean of the " + type + "is: " + mean);

		System.out.println("The Median of the " + type + "is " + median(data));
		int squareSum = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				squareSum += Math.pow((data[i] - mean), 2);
			}
		}
		double stdDev = Math.sqrt(squareSum / count);
		System.out.println("The Standard Deviation of the " + type + "is: "
				+ stdDev);

		System.out.println("The Variance of the " + type + "is: " + stdDev
				* stdDev);
	}

	public void getStats(long[] data, String type) {
		float sum = 0;
		float count = 0;
		long[] data2 = new long[data.length];
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				// datasquare[i] = data[i] * data[i];
				sum += data[i];
				count++;
				System.out.print(data[i] + ",");
				data2[i] = data[i];
			}
		}
		System.out.println();
		System.out.println("Count:" + count);
		float mean = sum / count;
		System.out.println();
		System.out.println("Data for " + type);
		System.out.println("The Mean of the " + type + "is: " + mean);
		System.out.println("The Median of the " + type + "is " + median(data2));
		double squareSum = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				squareSum += Math.pow((data[i] - mean), 2);
			}
		}
		double stdDev = Math.sqrt(squareSum / count);
		System.out.println("The Standard Deviation of the " + type + "is: "
				+ stdDev);

		System.out.println("The Variance of the " + type + "is: " + stdDev
				* stdDev);
	}

	public void stateUpdated(PrecisionEvent pe) {
		System.out.println(pe);

		PrecisionEvent.EventCode code = pe.getCode();
		if (code == EventCode.EOM) {
			System.out.println("Distinct frames and counts");
			print(frameTable, "Frames");
			System.out.println();
			if (decreasing) {

				System.out.println("The Frames were Decreasing");
			} else {
				System.out.println("The Frames were non-Decreasing");
			}
			System.out.println();
			System.out.println("Distinct frames Progress and counts");
			print(frameProgressTable, "Frame Progress");
			System.out.println();
			System.out.println("Distinct clock Progress and counts");
			print(clockProgresstable, "Clock Progress");
			System.out.println();
			System.out.println("The Clock Difference Values and Counts: ");
			print(clockDiffTable, "Clock Difference");
			getStats(counts, "No of Progress Updates");
			long[] frames = new long[framesArr.size()];
			int i = 0;
			Iterator<Long> iter = framesArr.iterator();
			while (iter.hasNext()) {
				Long l = iter.next();
				frames[i++] = l.longValue();
			}
			long[] frameProgressArr = new long[frameProgress.size()];
			i = 0;
			iter = frameProgress.iterator();
			while (iter.hasNext()) {
				Long l = iter.next();
				frameProgressArr[i++] = l.longValue();
			}
			long[] clockProgressArr = new long[clockProgress.size()];
			i = 0;
			iter = clockProgress.iterator();
			iter.next();
			while (iter.hasNext()) {
				Long l = iter.next();
				clockProgressArr[i++] = l.longValue();
			}

			long[] clockDiffArr = new long[clockDiff.size()];
			i = 0;
			iter = clockDiff.iterator();
			iter.next();
			while (iter.hasNext()) {
				Long l = iter.next();
				clockDiffArr[i++] = l.longValue();
			}

			System.out.println();
			getStats(frames, "Frames");
			System.out.println();
			getStats(frameProgressArr, "Frame Progress");
			System.out.println();
			getStats(clockProgressArr, "clock progress");
			System.out.println();
			getStats(clockDiffArr, "Clock Difference");
		}
	}

	public void print(Hashtable<Long, Integer> table, String type) {

		System.out.println();
		System.out.println(type + "		Count");
		Vector<Long> v = new Vector<Long>(table.keySet());
		Collections.sort(v);
		Iterator<Long> it = v.iterator();
		while (it.hasNext()) {
			Long key = it.next();
			if (key != 0) {
				System.out.println(key + "			" + table.get(key));
			}
		}
	}

	public static double median(int a[]) {
		int[] b = new int[a.length];
		System.arraycopy(a, 0, b, 0, b.length);
		Arrays.sort(b);

		if (a.length % 2 == 0) {
			return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
		} else {
			return b[b.length / 2];
		}
	}

	public static double median(long a[]) {
		long[] b = new long[a.length];
		System.arraycopy(a, 0, b, 0, b.length);
		Arrays.sort(b);

		if (a.length % 2 == 0) {
			return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
		} else {
			return b[b.length / 2];
		}
	}

}
