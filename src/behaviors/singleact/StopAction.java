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

import components.MyMenu;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Stops audio playback.
 * 
 * @author Yuvi Masory
 */
public class StopAction extends IdentifiedSingleAction {

	/**
	 * Performs the action, without saving the stopped position.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		boolean currentlyPlaying = CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING;
		CurAudio.getPlayer().stop();
		CurAudio.setAudioProgressWithoutUpdatingActions(0);
		if(currentlyPlaying == false) {
			MyMenu.updateActions();
		}
	}

	/**
	 * The user can stop audio when audio is open, playing, and not on the first frame.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(true);
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
