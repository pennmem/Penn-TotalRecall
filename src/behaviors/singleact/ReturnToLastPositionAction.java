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

public class ReturnToLastPositionAction extends IdentifiedSingleAction {

	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		long pos = CurAudio.popLastPlayPos();
		CurAudio.setAudioProgressAndUpdateActions(pos);
		CurAudio.getPlayer().queuePlayAt(pos);
		MyFrame.getInstance().requestFocusInWindow();
	}

	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.hasLastPlayPos()) {
				if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
					setEnabled(false);
				}
				else {
					setEnabled(true);
				}
			}
			else {
				setEnabled(false);
			}
		}
		else {
			setEnabled(false);
		}
	}
}
