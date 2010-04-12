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

import info.SysInfo;

import java.awt.event.ActionEvent;

import util.GiveMessage;

/**
 * Displays information about the program to the user
 * 
 * @author Yuvi Masory
 */
public class AboutAction extends IdentifiedSingleAction {

	public AboutAction() {
	}

	/**
	 * Performs the action using a dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		GiveMessage.infoMessage(SysInfo.sys.aboutMessage);
	}

	/**
	 * <code>AboutAction</code> is always enabled.
	 */
	@Override
	public void update() {}
}
