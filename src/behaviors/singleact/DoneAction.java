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

import info.Constants;
import info.SysInfo;

import java.awt.event.ActionEvent;
import java.io.File;

import util.GiveMessage;
import util.OSPath;

import components.audiofiles.AudioFile.AudioFilePathException;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Marks the current annotation file complete and then switches program state to reflect that no audio file is open.
 * 
 * Afterward sends update to all <code>UpdatingActions</code>.
 * 
 * @author Yuvi Masory
 *
 */
public class DoneAction extends IdentifiedSingleAction {

	public DoneAction() {
	}
	
	public void actionPerformed(ActionEvent e) {
		String curFileName = CurAudio.getCurrentAudioFileAbsolutePath();
		File tmpFile = new File(OSPath.basename(curFileName) + "." + Constants.temporaryAnnotationFileExtension);
		if(tmpFile.exists()) {
			File oFile = new File(OSPath.basename(tmpFile.getAbsolutePath()) + "." + Constants.completedAnnotationFileExtension);
			if(oFile.exists()) {
				GiveMessage.errorMessage("Output file already exists. You should not be able to reach this condition.");
				return;
			}
			else {
				if(!tmpFile.renameTo(oFile)) {
					GiveMessage.errorMessage("Operation failed.");
					return;
				}
				else {
					try {
						CurAudio.getMaster().getAudioFile().updateDoneStatus();
					} catch (AudioFilePathException e1) {
						e1.printStackTrace();
					}
					CurAudio.switchFile(null);
				}
			}
		}
		else {
			GiveMessage.errorMessage("You have not made any annotations yet.");
			return;
		}
	}
	
	/**
	 * A file can be marked done only if audio is open and not playing.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(SysInfo.sys.forceListen) {
				if(CurAudio.getListener().getGreatestProgress() < CurAudio.getMaster().durationInFrames() - 1) {
					setEnabled(false);
					return;
				}
			}
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING == false) {
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
