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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import edu.upenn.psych.memory.precisionplayer.PrecisionEvent;
import edu.upenn.psych.memory.precisionplayer.PrecisionListener;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
* @author Apurva Jatakia
*/
public class PlayPauseTest implements PrecisionListener {
	
	private final int sleepMillis = 1000;

	private volatile PrecisionPlayer player;
	
	private int frame = 0; 
	
	public PlayPauseTest() {
		player = null;
		try {
			player.open("/Users/yuvi/Desktop/audio/0.wav");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		player.addListener(this);
		player.setLoudness(100);

		Timer t = new Timer(sleepMillis, new PlayPausePerformer());
		t.start();
		while(true) {
			try {
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void progress(long frames) {
	}

	public void stateUpdated(PrecisionEvent pe) {
	}
	
	private class PlayPausePerformer implements ActionListener {		
		public void actionPerformed(ActionEvent e) {
			player.stop();
			player.setLoudness(100);
			player.playAt(frame += 10000);
		}
	}
}
