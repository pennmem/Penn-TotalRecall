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
import java.io.IOException;

import util.GiveMessage;
import util.OSPath;

import components.MyMenu;
import components.annotations.Annotation;
import components.annotations.AnnotationDisplay;
import components.annotations.AnnotationFileParser;

import control.CurAudio;

/**
 * Deletes an annotation that has already been committed to a temporary annotation file.
 * 
 * If the annotations is the last available, also deletes the temporary annotation file, which should at this point be empty.
 * 
 * @author Yuvi Masory
 */
public class DeleteAnnotationAction extends IdentifiedSingleAction {

	private int rowIndex;
	private Annotation annToDelete;

	/**
	 * Creates an <code>Action</code> that will delete the annotation matching the provided argument.
	 * 
	 * @param rowIndex
	 * @param annToDelete
	 */
	public DeleteAnnotationAction(int rowIndex) {
		this.rowIndex = rowIndex;
		this.annToDelete = AnnotationDisplay.getAnnotationsInOrder()[rowIndex];
	}

	/**
	 * Performs the action by calling {@link AnnotationFileParser#removeAnnotation(Annotation, File)}.
	 * 
	 * Warns on failure using dialogs.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	public void actionPerformed(ActionEvent e) {
		String curFileName = CurAudio.getCurrentAudioFileAbsolutePath();
		String desiredPath = OSPath.basename(curFileName) + "." + Constants.temporaryAnnotationFileExtension;
		File oFile = new File(desiredPath);
		
		boolean success = false;
		try {
			success = AnnotationFileParser.removeAnnotation(annToDelete, oFile);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			success = false;
		}
		if(success) {
			AnnotationDisplay.removeAnnotation(rowIndex);
			
			//no annotations left after removal, so delete file too
			if(AnnotationDisplay.getNumAnnotations() == 0) {
				if(oFile.delete() == false) {
					GiveMessage.errorMessage("Deletion of annotation successful, but could not remove temporary annotation file.");
				}
			}
		}
		else {
			GiveMessage.errorMessage("Deletion not successful. Files may be damaged. Check file system.");
		}
		
		MyMenu.updateActions();
	}

	/**
	 * The user can delete an annotation when audio is open and there is at least one annotation to the current file.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen() && AnnotationDisplay.getNumAnnotations() > 0) {
			setEnabled(true);
		}
		else {
			setEnabled(false);
		}
	}
}
