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
import info.UserPrefs;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import util.GiveMessage;
import util.OSPath;

import components.MyFrame;
import components.wordpool.WordpoolDisplay;
import components.wordpool.WordpoolFileParser;
import components.wordpool.WordpoolWord;

import control.CurAudio;

/**
 * Presents a file chooser to the user and then adds words from the selected file to the {@link components.wordpool.WordpoolDisplay}.
 * 
 * @author Yuvi Masory
 */
public class OpenWordpoolAction extends IdentifiedSingleAction {

	public OpenWordpoolAction() {
	}

	public void actionPerformed(ActionEvent arg0) {
		String maybeLastPath = UserPrefs.prefs.get(UserPrefs.openWordpoolPath, SysInfo.sys.userHomeDir);
		if(new File(maybeLastPath).exists() == false) {
			maybeLastPath = SysInfo.sys.userHomeDir;
		}

		String title = "Open Wordpool File";
		String path = null;
		if(SysInfo.sys.useAWTFileChoosers) {
			FileDialog fd = new FileDialog(MyFrame.getInstance(), title);
			fd.setDirectory(maybeLastPath);
			fd.setFilenameFilter(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(Constants.wordpoolFileExtension);
				}				
			});
			fd.setVisible(true);
			path = fd.getDirectory() + fd.getFile();
		}
		else {
			JFileChooser jfc = new JFileChooser(maybeLastPath);
			jfc.setDialogTitle(title);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) {
						return true;
					}
					if(f.getName().toLowerCase().endsWith(Constants.wordpoolFileExtension)) {
						return true;
					}
					else {
						return false;
					}
				}
				@Override
				public String getDescription() {
					return "Text (.txt) Files";
				}
			});
			int result = jfc.showOpenDialog(MyFrame.getInstance());
			if (result == JFileChooser.APPROVE_OPTION) {
				path = jfc.getSelectedFile().getPath();
			}
		}

		if(path != null) {
			File chosenFile = new File(path);
			if(chosenFile.isFile()) {
				UserPrefs.prefs.put(UserPrefs.openWordpoolPath, new File(path).getParentFile().getPath());	
				switchWordpool(chosenFile);
			}
		}
	}

	/**
	 * <code>OpenWordpoolAction</code> is always enabled.
	 */
	@Override
	public void update() {}

	public static void switchWordpool(File file) {
		try {
			List<WordpoolWord> words = WordpoolFileParser.parse(file, false);
			WordpoolDisplay.removeAllWords();
			WordpoolDisplay.addWordpoolWords(words);

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
		}
		catch (IOException e) {
			e.printStackTrace();
			GiveMessage.errorMessage("Cannot process wordpool file!");			
		}
	}
}
