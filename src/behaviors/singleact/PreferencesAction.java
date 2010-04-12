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

import java.awt.event.ActionEvent;


import components.preferences.PreferencesFrame;

/**
 * Launches the preferences window.
 * 
 * @author Yuvi Masory
 */
public class PreferencesAction extends IdentifiedSingleAction {
	
	private static PreferencesFrame prefs;

	public PreferencesAction() {
	}
	
	/**
	 * Performs the <code>Action</code> by setting the PreferencesFrame to visible.
	 * 
	 * If this is the first call, the PreferencesFrame and internal components will actually be created.
	 */
	public void actionPerformed(ActionEvent e) {
		if(prefs == null) {
			prefs = PreferencesFrame.getInstance();
			prefs.setVisible(true);
		}
		prefs.setVisible(true);
	}
	
	/**
	 * <code>PreferencesAction</code> is always enabled.
	 */
	@Override
	public void update() {}
}
