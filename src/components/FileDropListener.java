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

package components;

import info.Constants;

import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;

import util.OSPath;

import behaviors.singleact.OpenWordpoolAction;

import components.audiofiles.AudioFileDisplay;
import components.wordpool.WordpoolDisplay;
import components.wordpool.WordpoolFileParser;
import control.CurAudio;

/**
 * A <code>FileDrop.Listener</code> that catches directories and folders dropped on <code>MyFrame</code>, 
 * adding the appropriate files to the <code>AudioFileDisplay</code>.
 * 
 * @author Yuvi Masory
 */
public class FileDropListener implements FileDrop.Listener{

	/**
	 * Handles drag and drop of audio files or a wordpool document.
	 * 
	 * <p>For each directory dropped, adds the directory's normal files to the file batch.
	 * Adds each file dropped to the batch.
	 * Finally, adds the whole batch to the <code>AudioFileDisplay</code>. using {@link AudioFileDisplay#addFilesIfSupported(File[])}.
	 * 
	 * <p>Files are added in a batch, instead of one at a time, to the <code>AudioFileDisplay</code> in keeping with that classes policies
	 * on sorting optimization.
	 * 
	 * <p>Note that all files described above are given to the <code>AudioFileDisplay</code>, which sorts out which ones are actually the correct format, etc.
	 * 
	 * @param files The <code>Files</code> that were dropped
	 * @param evt The <code>DropTargetDropEvent</code> provided by the trigger
	 */
	public void filesDropped(File[] files, DropTargetDropEvent evt) {
		boolean somethingAccepted = false;
		if(files.length > 0) {
			boolean wordpoolFound = false;
			if(files.length == 1) { //check for wordpool file
				if(files[0].getName().toLowerCase().endsWith(Constants.wordpoolFileExtension)) {
					OpenWordpoolAction.switchWordpool(files[0]);
					
					if(CurAudio.audioOpen()) {
						File lstFile = new File(OSPath.basename(CurAudio.getCurrentAudioFileAbsolutePath()) + "." + Constants.lstFileExtension);
						if(lstFile.exists()) {
							try {
								WordpoolDisplay.distinguishAsLst(WordpoolFileParser.parse(lstFile, true));
							} 
							catch(IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					somethingAccepted = true;
					wordpoolFound = true;
				}
			}
			if(wordpoolFound == false) { //check for audio files
				for(File f: files) {
					if(f.isFile()) {
						if(AudioFileDisplay.addFilesIfSupported(new File[] {f})) {
							somethingAccepted = true;
						}
					}
					else if(f.isDirectory()) {
						if(AudioFileDisplay.addFilesIfSupported(f.listFiles())) {
							somethingAccepted = true;
						}
					}
				}
			}
		}
		evt.getDropTargetContext().dropComplete(somethingAccepted);		
	}
}
