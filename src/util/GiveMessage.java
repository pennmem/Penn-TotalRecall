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

package util;

import info.Constants;
import info.GUIConstants;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import behaviors.singleact.AboutAction;

import components.MyFrame;

/**
 * Utility class for launching dialogs with consistent appearance.
 * 
 * @author Yuvi Masory
 */
public class GiveMessage {

	/**
	 * Launches an error dialog with the provided message.
	 * 
	 * @param message The error message to display
	 */
	public static void errorMessage(String message) {
		JOptionPane.showMessageDialog(MyFrame.getInstance(),
				message, 
				GUIConstants.errorDialogTitle, 
				JOptionPane.ERROR_MESSAGE,
				new ImageIcon(AboutAction.class.getResource("/images/headphones48.png")));
	}

	/**
	 * Launches an info dialog with the provided message.
	 * 
	 * @param message The info message to display
	 */
	public static void infoMessage(String message) {
		JOptionPane.showMessageDialog(MyFrame.getInstance(),
				message, 
				Constants.programName, 
				JOptionPane.OK_OPTION, 
				new ImageIcon(AboutAction.class.getResource("/images/headphones48.png")));
	}

	public static String inputMessage(String message) {
		Object input = JOptionPane.showInputDialog(
				MyFrame.getInstance(), 
				message, 
				Constants.programName,
				JOptionPane.OK_CANCEL_OPTION,
				new ImageIcon(AboutAction.class.getResource("/images/headphones48.png")),
				null,
		"");
		if(input instanceof String) {
			return (String)input;
		}
		else {
			return null;
		}
	}
}
