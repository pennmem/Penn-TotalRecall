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

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;

import util.GiveMessage;
import util.OSPath;

import components.audiofiles.AudioFile;
import components.audiofiles.AudioFile.AudioFilePathException;

/**
 * Reopens a file which was already done being annotated.
 * 
 * @author Yuvi Masory
 */
public class ContinueAnnotatingAction extends IdentifiedSingleAction {

	private AudioFile myAudioFile;

	/**
	 * Creates the <code>Action</code> action for the provided <code>File</code>.
	 * 
	 * @param f The audio file whose corresponding annotation file will be reopened
	 */
	public ContinueAnnotatingAction(AudioFile f) {
		if(f == null) {
			throw new IllegalArgumentException("file cannot be null");
		}
		this.putValue(Action.NAME, "Continue Editing");
		myAudioFile = f;
	}
	
	/**
	 * Performs the <code>Action</code> by changing a permanent annotation file into a temporary one.
	 * 
	 * That only involves changing the file extensions.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if(myAudioFile.isDone() == false) {
			System.err.println("it should not have been possible to call ContinueAnnotatingAction on an incomplete file");
			return;
		}
		File tmpFile = new File(OSPath.basename(myAudioFile.getAbsolutePath()) + "." + Constants.temporaryAnnotationFileExtension);
		File doneFile = new File(OSPath.basename(myAudioFile.getAbsolutePath()) + "." + Constants.completedAnnotationFileExtension);
		if(tmpFile.exists() == true) {
			System.err.println(Constants.temporaryAnnotationFileExtension + " file already exists.\nThis should not happen.");
			return;
		}
		if(doneFile.exists() == false) {
			System.err.println("Can't find annotation file, to re-open");
			return;
		}
		if(doneFile.renameTo(tmpFile)) {
 			try {
				myAudioFile.updateDoneStatus();
			} 
 			catch (AudioFilePathException e1) {
 				//should not be possible to enter this condition after above checking, so we're not going to specially handle the exception
				e1.printStackTrace(); 
			}
			return;
		}
		else {
			GiveMessage.errorMessage("Could not re-open file for annotation.");
			return;
		}
	}

	/**
	 * A <code>ContinueAnnotationAction</code> is always enabled.
	 */
	@Override
	public void update() {}
}
