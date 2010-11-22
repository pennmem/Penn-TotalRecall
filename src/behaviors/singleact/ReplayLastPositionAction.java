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

public class ReplayLastPositionAction extends IdentifiedSingleAction {
	
	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		PrecisionPlayer player = CurAudio.getPlayer();		
		if(player.getStatus() == PrecisionPlayer.Status.PLAYING) {
			player.stop();
		}
		new ReturnToLastPositionAction().actionPerformed(new ActionEvent(MyMenu.getInstance(), ActionEvent.ACTION_PERFORMED, null));
		new PlayPauseAction(false).actionPerformed(new ActionEvent(MyMenu.getInstance(), ActionEvent.ACTION_PERFORMED, null));
	}

	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.hasLastPlayPos()) {
				setEnabled(true);
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
