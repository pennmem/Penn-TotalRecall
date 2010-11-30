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

import java.awt.event.ActionEvent;

import components.waveform.WaveformDisplay;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Zooms the waveform display in/out.
 * 
 * It only changes the pixelsPerSecond value that the waveform display will utilize on its next repaint.
 * 
 * @author Yuvi Masory
 */
public class ZoomAction extends IdentifiedMultiAction {

	/**
	 * Defines the zoom direction of a <code>ZoomAction</code> instance.
	 */
	public static enum Direction {IN, OUT};
	
	private Direction dir;
	
	public ZoomAction(Direction dir) {
		super(dir);
		this.dir = dir;
	}

	/**
	 * Performs the zoom, increasing/decreasing the pixelsPerSecond by calling {@link components.waveform.WaveformDisplay#zoomX(boolean)}. 
	 * 
	 * Since the waveform display autonomously decides when to paint itself, this action may not result in an instant visual change.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if(dir == Direction.IN) {
			WaveformDisplay.zoomX(true);
		}
		else {
			WaveformDisplay.zoomX(false);
		}
	}
	
	/**
	 * Zooming is enabled only when audio is open and not playing.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
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
}
