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
import info.GUIConstants;
import info.UserPrefs;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import components.MyFrame;
import components.MySplitPane;

import control.CurAudio;

/**
 * Exits the program in response to user request.
 * 
 * @author Yuvi Masory
 */
public class ExitAction extends IdentifiedSingleAction {
	
	public ExitAction() {
	}

	/**
	 * Performs exit, possibly querying user for confirmation if the inter-session preferences request such warning.
	 */
	public void actionPerformed(ActionEvent e) {
		boolean shouldWarn = UserPrefs.prefs.getBoolean(UserPrefs.warnExit, UserPrefs.defaultWarnExit);
		if(shouldWarn) {
			JCheckBox checkbox = new JCheckBox(GUIConstants.dontShowAgainString);  
			String message = "Are you sure you want to exit " + Constants.programName + "?";  
			Object[] params = {message, checkbox};
			int response = JOptionPane.showConfirmDialog(MyFrame.getInstance(), params,
					GUIConstants.yesNoDialogTitle, JOptionPane.YES_NO_OPTION);
			boolean dontShow = checkbox.isSelected();
			if(dontShow && response != JOptionPane.CLOSED_OPTION) {
				UserPrefs.prefs.putBoolean(UserPrefs.warnExit, false);
			}			
			switch(response) {
			case JOptionPane.YES_OPTION:
				doExit();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CLOSED_OPTION:
				break;
			} 
		}
		else {
			doExit();
		}
	}
	
	/**
	 * <code>ExitAction</code> is always enabled.
	 */
	@Override
	public void update() {}

	/*
	 * Exit is accomplished by stopping audio playback (to prevent a weird audio sound of Java closing mid-playback), and storing window and divider positions. 
	 */
	private static void doExit() {
		try { //just to make sure we exit
			if(CurAudio.audioOpen()) {
				CurAudio.getPlayer().stop();
			}
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
		
		Rectangle bounds = MyFrame.getInstance().getBounds();
		UserPrefs.prefs.putInt(UserPrefs.windowWidth, (int)bounds.getWidth());
		UserPrefs.prefs.putInt(UserPrefs.windowHeight, (int)bounds.getHeight());
		UserPrefs.prefs.putInt(UserPrefs.windowXLocation, (int)bounds.getX());
		UserPrefs.prefs.putInt(UserPrefs.windowYLocation, (int)bounds.getY());
		UserPrefs.prefs.putInt(UserPrefs.dividerLocation, MySplitPane.getInstance().getDividerLocation());
		UserPrefs.prefs.putBoolean(UserPrefs.windowMaximized, MyFrame.getInstance().getExtendedState() == JFrame.MAXIMIZED_BOTH);
		
		System.exit(0);
	}
}
