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

package behaviors.singleact;

import java.awt.event.ActionEvent;

import components.waveform.MyGlassPane;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Replays the last 200 milliseconds so the annotator can judge whether a word onset has been crossed. 
 * 
 * @author Yuvi Masory
 */
public class ReplayLast200MillisAction extends IdentifiedSingleAction {

	public static final int duration = 200;
	
	/**
	 * Performs the action by calling the corresponding <code>PrecisionPlayer</code> function.
	 * 
	 * As per <code>PrecisionPlayer</code>'s docs, this replay cannot be stopped once started.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	public void actionPerformed(ActionEvent e) {
		PrecisionPlayer player = CurAudio.getPlayer();
		player.setLoudness(CurAudio.getDesiredLoudness());
		
		long curFrame = CurAudio.getAudioProgress();
		long numFrames = CurAudio.getMaster().millisToFrames(duration);
		
		player.playShortInterval(curFrame - numFrames, curFrame - 1);
		MyGlassPane.getInstance().flashRectangle();
	}

	/**
	 * User can replay last 200 millis when audio is open, not playing, and not on the first frame.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			setEnabled(true);
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(false);
			}
			else {
				if(CurAudio.getAudioProgress() <= 0) {
					setEnabled(false);
				}
				else {
					setEnabled(true);
				}
			}
		}
		else {
			setEnabled(false);
		}
	}
}
