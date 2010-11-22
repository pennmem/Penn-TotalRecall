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

import components.MySplitPane;
import components.annotations.Annotation;
import components.annotations.AnnotationDisplay;
import components.waveform.WaveformDisplay;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

public class DeleteSelectedAnnotationAction extends IdentifiedSingleAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		long curFrame = CurAudio.getAudioProgress();
		int progX = WaveformDisplay.frameToAbsoluteXPixel(curFrame);

		Annotation[] anns = AnnotationDisplay.getAnnotationsInOrder();
		for(int i = 0; i < anns.length; i++) {
			int annX = WaveformDisplay.frameToAbsoluteXPixel(CurAudio.getMaster().millisToFrames(anns[i].getTime()));
			if(progX == annX) {
				new DeleteAnnotationAction(i).actionPerformed(
						new ActionEvent(MySplitPane.getInstance(), ActionEvent.ACTION_PERFORMED, null));
				return;
			}
		}
	}

	@Override
	public void update() {
		if(CurAudio.audioOpen() && CurAudio.getPlayer().getStatus() != PrecisionPlayer.Status.PLAYING) {
			setEnabled(true);
		}
		else {
			setEnabled(false);
		}
	}
}
