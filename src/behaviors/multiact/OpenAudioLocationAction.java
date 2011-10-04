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

import info.Constants;
import info.SysInfo;
import info.UserPrefs;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import components.MyFrame;
import components.audiofiles.AudioFileDisplay;

/**
 * Presents a file chooser to the user and then adds the selected files to the {@link components.audiofiles.AudioFileDisplay}.
 * 
 * @author Yuvi Masory
 */
public class OpenAudioLocationAction extends IdentifiedMultiAction {

	public static enum SelectionMode {FILES_ONLY, DIRECTORIES_ONLY, FILES_AND_DIRECTORIES}

	private SelectionMode mode;

	public OpenAudioLocationAction(SelectionMode mode) {
		super(mode);
		this.mode = mode;
	}

	/**
	 * Performs <code>Action</code> by attempting to open the file chooser on the directory the last audio location selection was made in.
	 * Failing that, uses current directory.
	 * Afterwards adds the selected files and requests the list be sorted.
	 */
	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		String maybeLastPath = UserPrefs.prefs.get(UserPrefs.openLocationPath, SysInfo.sys.userHomeDir);
		if(new File(maybeLastPath).exists() == false) {
			maybeLastPath = SysInfo.sys.userHomeDir;
		}

		String path = null;
		if(mode != SelectionMode.FILES_AND_DIRECTORIES) {
			String title;
			if(mode == SelectionMode.DIRECTORIES_ONLY) {
				System.setProperty("apple.awt.fileDialogForDirectories", "true"); //exclusively directories then!
				title = "Open Audio Folder";
			}
			else {
				title = "Open Audio File";
			}				
			FileDialog fd = new FileDialog(MyFrame.getInstance(), title, FileDialog.LOAD);
			fd.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if(mode == SelectionMode.DIRECTORIES_ONLY) {
						return name == null;
					}
					else {
						for(String ext: Constants.audioFormatsLowerCase) {
							if(name.toLowerCase().endsWith(ext)) {
								return true;
							}
						}
						return false;
					}
				}				
			});
			fd.setDirectory(maybeLastPath);
			fd.setMode(FileDialog.LOAD);
			fd.setVisible(true);

			String dir = fd.getDirectory();
			String file = fd.getFile();
			if(dir != null && file != null) {
				path = fd.getDirectory() + fd.getFile();					
			}
			System.setProperty("apple.awt.fileDialogForDirectories", "false");

		}
		else {	
			JFileChooser jfc = new JFileChooser(maybeLastPath);
			jfc.setDialogTitle("Open Audio File or Folder");
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			jfc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) {
						return true;
					}
					else {
						for(String ext: Constants.audioFormatsLowerCase) {
							if(f.getName().toLowerCase().endsWith(ext)) {
								return true;
							}
						}
						return false;
					}
				}
				@Override
				public String getDescription() {
					return "Supported Audio Formats";
				}});

			int result = jfc.showOpenDialog(MyFrame.getInstance());
			if (result == JFileChooser.APPROVE_OPTION) {
				path = jfc.getSelectedFile().getPath();
			}			
		}		
		if(path != null) {
			UserPrefs.prefs.put(UserPrefs.openLocationPath, new File(path).getParentFile().getPath());
			File chosenFile = new File(path);
			if(chosenFile.isFile()) {
				AudioFileDisplay.addFilesIfSupported(new File[] {chosenFile});
			}
			else if(chosenFile.isDirectory()) {
				AudioFileDisplay.addFilesIfSupported(chosenFile.listFiles());
			}
		}
	}

	/**
	 * <code>OpenAudioLocationAction</code> is always enabled.
	 */
	@Override
	public void update() {}
}

