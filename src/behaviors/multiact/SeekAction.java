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

import info.SysInfo;
import info.UserPrefs;

import java.awt.event.ActionEvent;
import java.util.Map;

import components.MyFrame;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Sets the audio position forward/backward one of several pre-defined amounts, in response to user request.
 * 
 * Afterward sends update to all <code>UpdatingActions</code>.
 * 
 * @author Yuvi Masory
 */
public class SeekAction extends IdentifiedMultiAction{

	/**
	 * Defines the seek direction and magnitude of a <code>SeekAction</code> instance.
	 */
	public static enum SeekAmount {FORWARD_SMALL, BACKWARD_SMALL, FORWARD_MEDIUM, BACKWARD_MEDIUM, FORWARD_LARGE, BACKWARD_LARGE};

	private int shift;

	private static Map<SeekAmount, Integer> timeMap;

	private SeekAmount amount;

	/**
	 * Create an action with the direction and amount presets given by the provided <code>Enum</code>.
	 *
	 * Since the waveform display autonomously decides when to paint itself, this action may not result in an instant visual change.
	 * 
	 * @param amount An <code>Enum</code> defined in this class which the class maps to the correct direction and magnitude of the seek.
	 * @see behaviors.multiact.IdentifiedMultiAction#IdentifiedMultiAction(Enum)
	 */
	public SeekAction(SeekAmount amount) {
		super(amount);
		this.amount = amount;
		updateSeekAmount();
	}

	/**
	 * Performs the <code>SeekAction</code>, intelligently boundaries to make sure the player isn't taken outside of the audio data.
	 * 
	 * Afterward sends an update to all <code>UpdatingActions</code>.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	public void actionPerformed(ActionEvent e) {
		long curFrame = CurAudio.getAudioProgress();
		long frameShift = CurAudio.getMaster().millisToFrames(shift);
		long naivePosition = curFrame + frameShift;
		long frameLength = CurAudio.getMaster().durationInFrames();

		long finalPosition = naivePosition;

		if(naivePosition < 0) {
			finalPosition = 0;
		}
		else if(naivePosition >= frameLength) {
			finalPosition = frameLength - 1;
		}
		if(SysInfo.sys.forceListen) {
			finalPosition = Math.min(finalPosition, CurAudio.getListener().getGreatestProgress());
		}

		CurAudio.setAudioProgressAndUpdateActions(finalPosition);
		CurAudio.getPlayer().queuePlayAt(finalPosition);
		MyFrame.getInstance().requestFocusInWindow();
	}

	/**
	 * A forward (backward) <code>SeekAction</code> should be enabled only when audio is open, not playing, and not at the end (beginning) of the audio.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(false);
			}
			else {
				boolean canSkipForward;
				if(SysInfo.sys.forceListen) {
					canSkipForward = CurAudio.getAudioProgress() < CurAudio.getListener().getGreatestProgress();
				}
				else {
					canSkipForward = true;
				}
				if(CurAudio.getAudioProgress() <= 0) {
					if(canSkipForward && (amount == SeekAmount.FORWARD_SMALL || amount == SeekAmount.FORWARD_MEDIUM || amount == SeekAmount.FORWARD_LARGE)) {
						setEnabled(true);
					}	
					else {
						setEnabled(false);
					}
				}
				else if(CurAudio.getAudioProgress() == CurAudio.getMaster().durationInFrames() - 1) {
					if(amount == SeekAmount.FORWARD_SMALL || amount == SeekAmount.FORWARD_MEDIUM || amount == SeekAmount.FORWARD_LARGE) {
						setEnabled(false);
					}	
					else {
						setEnabled(true);
					}
				}
				else {
					if(amount == SeekAmount.FORWARD_SMALL || amount == SeekAmount.FORWARD_MEDIUM || amount == SeekAmount.FORWARD_LARGE) {
						setEnabled(canSkipForward);
					}
					else {
						setEnabled(true);
					}
				}
			}
		}
		else {
			setEnabled(false);
		}
	}

	/**
	 * Queries the map associating the behavior-defining enum of this class and its associated (positive or negative) integer seek amount.
	 * 
	 * However, unlike direct access to the map, this method returns 0 instead of <code>null</code> for unknown keys.
	 * 
	 * @param sa The <code>Enum</code> defining this SeekAction's behavior
	 * @return The integer shift corresponding to <code>sa</code>, or 0 if <code>sa</code> is not in the map.
	 */
	public int lookup(SeekAmount sa) {
		Integer shift = timeMap.get(sa);
		if(shift == null) {
			return 0;
		}
		else {
			return shift;
		}
	}

	/**
	 * Getter for the <code>Enum</code> defining this <code>Action</code>'s behavior, can be converted into an integer seek amount using {@link #lookup(SeekAmount)}.
	 * 
	 * @return The <code>Enum</code> defining this <code>Action</code>'s behavior.
	 */
	public SeekAction.SeekAmount getAmount() {
		return amount;
	}

	public void updateSeekAmount() {
		switch(amount) {
			case FORWARD_SMALL: shift = UserPrefs.getSmallShift(); break;
			case BACKWARD_SMALL: shift = UserPrefs.getSmallShift() * -1; break;
			case FORWARD_MEDIUM: shift = UserPrefs.getMediumShift(); break;
			case BACKWARD_MEDIUM: shift = UserPrefs.getMediumShift() * -1; break;
			case FORWARD_LARGE: shift = UserPrefs.getLargeShift(); break;
			case BACKWARD_LARGE: shift = UserPrefs.getLargeShift() * -1; break;
		}
	}
}
