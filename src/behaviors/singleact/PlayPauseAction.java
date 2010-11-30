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

import components.MyFrame;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Plays or "pauses" audio.
 * 
 * Remember that a "pause" is a normal stop as far as the <code>PrecisionPlayer</code> is concerned.
 * The program however remembers the stop position for future resumption.
 * 
 * @author Yuvi Masory
 */
public class PlayPauseAction extends IdentifiedSingleAction {
	
	private static final String playText = "Play";
	private static final String pauseText = "Pause";
	
	private boolean isDummy;
	
	/**
	 * Dummy actions don't actually perform the action, they are used only for the benefit
	 * of visual representation of the action for a JMenuItem.
	 * 
	 * This workaround is necessary to prevent visual jumps in the waveform at every pause.
	 * This may be related to a bug report I am submitting to Oracle regarding events moving through
	 * the queue very slowly when associated with a menu item. 
	 * 
	 * @param dummy <code>false</code> iff this object will actually perform its action 
	 */
	public PlayPauseAction(boolean dummy) {
		isDummy = dummy;
	}
	
	/**
	 * Performs action by starting/stopping <code>PrecisionPlayer</code> and storing the frame in
	 * <code>CurAudio</code> class as appropriate.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if(isDummy) {
			return;
		}
		PrecisionPlayer player = CurAudio.getPlayer();
		player.setLoudness(CurAudio.getDesiredLoudness());
		if(player.getStatus() == PrecisionPlayer.Status.PLAYING) { //PAUSE
			long frame = player.stop();
			CurAudio.setAudioProgressWithoutUpdatingActions(frame);
			long numFrames = CurAudio.getMaster().millisToFrames(200);			
			player.queueShortInterval(frame - numFrames, frame - 1);
			player.queuePlayAt(frame);
		}
		else { //PLAY/RESUME	
			long pos = CurAudio.getAudioProgress();
			player.playAt(pos);
			CurAudio.pushPlayPos(pos);
		}
		MyFrame.getInstance().requestFocusInWindow();
	}

	/**
	 * Play/pause is enabled when audio is open, not playing, and not on the final frame.
	 * 
	 * The "pause" label is used when audio is playing.
	 * The "play" label is used otherwise, whether or not the action is enabled.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getAudioProgress() == CurAudio.getMaster().durationInFrames() - 1) {
				setEnabled(false);				
			}
			else {
				setEnabled(true);
			}
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				putValue(NAME, pauseText);
			}
			else {
				putValue(NAME, playText);
			}
		}
		else {
			putValue(NAME, playText);
			setEnabled(false);
		}
	}
}
