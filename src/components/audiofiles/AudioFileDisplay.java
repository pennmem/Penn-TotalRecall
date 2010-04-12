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

package components.audiofiles;

import info.Constants;
import info.GUIConstants;
import info.MyShapes;
import info.UserPrefs;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import util.GiveMessage;

import components.MyFrame;
import components.audiofiles.AudioFile.AudioFilePathException;

import control.CurAudio;

/**
 * A custom interface component for displaying the available audio files to the user.
 * 
 * <p>Note: Access to this component from outside the package is limited to the public static methods provided in this class.
 * Code outside the package cannot and should not try to access the internal list, model, or other components directly.
 * 
 * @author Yuvi Masory
 */
public class AudioFileDisplay extends JScrollPane {

	private static final String title = "Audio Files";

	private static AudioFileDisplay instance;
	private static AudioFileList list;

	/**
	 * Creates a new instance of the component, initializing internal components, key bindings, listeners, and various aspects of appearance.
	 */
	private AudioFileDisplay() {
		list = AudioFileList.getInstance();
		getViewport().setView(list);

		setPreferredSize(GUIConstants.soundFileDisplayDimension);
		setMaximumSize(GUIConstants.soundFileDisplayDimension);
		
		setBorder(MyShapes.createMyUnfocusedTitledBorder(title));

		//overrides JScrollPane key bindings for the benefit of SeekAction's key bindings
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "none");

		//since AudioFileDisplay is a clickable area, we must write focus handling code for the event it is clicked on
		//this case is rare, since only a very small amount of this component is exposed (the area around the border title), the rest being obscured by the AudioFileList
		//JScrollPane passes focus to JList if focusable, and to the frame otherwise
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if(list.isFocusable()) {
					list.requestFocusInWindow();
				}
				else {
					MyFrame.getInstance().requestFocusInWindow();
				}
			}
		});
	}

	/**
	 * Adds files to the <code>AudioFileList</code>, but only if ones that are regular files with supported file extensions.
	 * 
	 * <code>AudioFiles</code> that don't exist, or are already displayed in this component are automatically filtered out, so this does not need to be checked in advance by the caller. 
	 * 
	 * @param files Candidate files to be added to the <code>AudioFileList</code>
	 * @return <code>true</code> if any of the files were ultimately added
	 */
	public static boolean addFilesIfSupported(File[] files) {
		ArrayList<AudioFile> supportedFiles = new ArrayList<AudioFile>();
		for(File f: files) {
			if(f.isFile()) { //this also filters files that don't actually exist, filtering of duplicate files is handled by the AudioFileListModel
				if(extensionSupported(f.getName())) {
					AudioFile af;
					try {
						af = new AudioFile(f.getAbsolutePath());
					} 
					catch (AudioFilePathException e) {
						e.printStackTrace();
						GiveMessage.errorMessage(e.getMessage());
						continue;
					}
					supportedFiles.add(af);
				}
			}
		}
		if(supportedFiles.size() > 0) {
			list.getModel().addElements(supportedFiles);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Singleton accessor.
	 * 
	 * @return The singleton <code>AudioFileDisplay</code>
	 */
	public static AudioFileDisplay getInstance() {
		if (instance == null) {
			instance = new AudioFileDisplay();
		}
		return instance;
	}
	
	/**
	 * Switches to the provided <code>File</code>, but only after asking the user for confirmation if the current user's preferences demand such a warning.
	 * 
	 * Keep in mind the user may decline to switch file.
	 * Also, if the provided file is already done being annotated, the switch will automatically be rejected without user being prompted.
	 * 
	 * @param file The file that may be switched to
	 * @return <code>true</code> iff the file switch took place
	 */
	protected static boolean askToSwitchFile(AudioFile file) {
		if(file.isDone()) {
			return false;
		}
		if(CurAudio.audioOpen()) {
			if(file.getAbsolutePath().equals(CurAudio.getCurrentAudioFileAbsolutePath())) {
				return true;
			}
			boolean shouldWarn = UserPrefs.prefs.getBoolean(UserPrefs.warnFileSwitch, UserPrefs.defaultWarnFileSwitch);
			if(shouldWarn) {
				JCheckBox checkbox = new JCheckBox(GUIConstants.dontShowAgainString);  
				String message = "Switch to file " + file.toString() + "?\nYour changes to the current file will not be lost.";  
				Object[] params = {message, checkbox};
				int response = JOptionPane.showConfirmDialog(MyFrame.getInstance(), params,
						GUIConstants.yesNoDialogTitle, JOptionPane.YES_NO_OPTION);
				boolean dontShow = checkbox.isSelected();
				if(dontShow && response != JOptionPane.CLOSED_OPTION) {
					UserPrefs.prefs.putBoolean(UserPrefs.warnFileSwitch, false);
				}
				if(response != JOptionPane.YES_OPTION) {
					return false;
				}
			}
		}
		CurAudio.switchFile(file);
		return true;
	}

	/**
	 * Decides whether a <code>File</code> is supported by comparing its extension to the collection of supported extensions in {@link Constants#audioFormatsLowerCase}, ignoring case.
	 * 
	 * @param name The name of the file, from <code>File.getName()</code>
	 * @return <code>true</code> iff the the <code>name</code> parameter ends with a supported extension
	 */
	private static boolean extensionSupported(String name) {
		for(String ext: Constants.audioFormatsLowerCase) {
			if(name.toLowerCase().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}
}
