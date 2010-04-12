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

package behaviors.multiact;

import info.UserPrefs;

import java.awt.event.ActionEvent;

import behaviors.singleact.ReplayLast200MillisAction;

import components.MyMenu;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * A combination of {@link behaviors.singleact.ReplayLast200MillisAction} and {@link behaviors.multiact.SeekAction}.
 * 
 * @author Yuvi Masory
 */
public class Last200PlusMoveAction extends IdentifiedMultiAction {

	public static enum Direction {BACKWARD, FORWARD};
	
	private ReplayLast200MillisAction replayer;
	
	private int shift;
	private Direction dir;
	
	
	public Last200PlusMoveAction(Direction dir) {
		super(dir);
		this.dir = dir;
		replayer = new ReplayLast200MillisAction();		
		shift = UserPrefs.getSmallShift();
		if(dir == Direction.BACKWARD) {
			shift *= -1;
		}
	}

	public void actionPerformed(ActionEvent e) {
		long curFrame = CurAudio.getAudioProgress();
		long frameShift = CurAudio.getMaster().millisToFrames(shift);
		long naivePosition = curFrame + frameShift;
		long frameLength = CurAudio.getMaster().durationInFrames();

		long finalPosition = naivePosition;

		if(naivePosition < 0) {
			finalPosition = 0;
		}
		else if(naivePosition > frameLength) {
			finalPosition = frameLength;
		}

		CurAudio.setAudioProgressWithoutUpdatingActions(finalPosition); //not using setAudioProgressAndUpdateActions() because we don't want to slow down start of playback
		CurAudio.getPlayer().queuePlayAt(finalPosition);

		replayer.actionPerformed(new ActionEvent(MyMenu.getInstance(), ActionEvent.ACTION_PERFORMED, null));

		MyMenu.updateActions();
	}

	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(false);
			}
			else {
				if(CurAudio.getAudioProgress() <= 0) {
					if(dir == Direction.FORWARD) {
						setEnabled(true);
					}	
					else {
						setEnabled(false);
					}
				}
				else if(CurAudio.getAudioProgress() == CurAudio.getMaster().durationInFrames() - 1) {
					if(dir == Direction.FORWARD) {
						setEnabled(false);
					}	
					else {
						setEnabled(true);
					}
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

	public void updateSeekAmount() {
		shift = UserPrefs.getSmallShift();
		if(dir == Direction.BACKWARD) {
			shift *= -1;
		}
	}
}
