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

package components.preferences;

import info.MyColors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;


/**
 * A <code>JPanel</code> that will display a preference chooser of some kind to the user.
 * 
 * <p>This inheritance strategy is used to minimize the amount of code that needs to be written for a new preference chooser, and to maximize consistency between preference choosers.
 * Spacing relative to the containing <code>PreferencesFrame</code> is taken care of here, and spacing is taken care of in <code>PreferencesFrame</code>.
 * An <code>AbstractPreferenceDisplay</code> will always take up all the space it can inside the <code>PreferencesFrame</code>, and is guaranteed at least its preferred size.
 * However, inheritors do need to take care of the resizing behavior of their <i>internal</i> components.
 * 
 * <p>When writing a new <code>AbstractPreferenceDisplay</code>, don't forget to consider case in which the relevant preference doesn't exist in the {@link java.util.prefs.Preferences} yet (first run).
 * By the time the chooser is realized the preference must already be written to the <code>Preferences</code> object, or {@link #isChanged()} will not behave correctly.
 * Inheritors should not consume VK_ESCAPE presses, as this keystroke is used by the <code>PreferencesFrame</code>.
 * 
 * <p>All preferences in this program should exclusively use <code>Preferences</code> object in {@link info.UserPrefs#prefs}, a user-specific preferences object.
 * System-wise preferences objects are not used as they require administrator access for persistence in some platforms.
 * 
 * @author Yuvi Masory
 * 
 * @see PreferencesFrame
 */
public abstract class AbstractPreferenceDisplay extends JPanel {

	/**
	 * Initializes some aspects of a new concrete <code>AbstractPreferenceDisplay</code> including spacing/layout and displayed title. 
	 * 
	 * @param prefName The name of the preference being chosen
	 */
	protected AbstractPreferenceDisplay(String prefName) {
		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		// outside invisible border for spacing
		Border outsideBorder = BorderFactory.createEmptyBorder(3, 5, 5, 3);
		
		// middle visible titled border
        
		Border blackline = BorderFactory.createLineBorder(MyColors.unfocusedColor);
		TitledBorder middleBorder = BorderFactory.createTitledBorder(blackline, prefName + ":");
		middleBorder.setTitleJustification(TitledBorder.LEADING);
		
		// inside invisible border for spacing
		Border insideBorder = BorderFactory.createEmptyBorder(5, 10, 10, 5);

		// combine the three borders into one compound border
		CompoundBorder insideCompoundBorder = BorderFactory.createCompoundBorder(middleBorder, insideBorder);
		CompoundBorder border = BorderFactory.createCompoundBorder(outsideBorder, insideCompoundBorder);
		setBorder(border);
	}
	
	/**
	 * Saves the preference the user has entered to the programs persistent <code>Preferences</code> object.
	 * 
	 * @return <code>true</code> if the preference was successfully set
	 * @throws BadPreferenceException If the preference the user has entered is badly formatted or unreasonable
	 */
	protected abstract boolean save() throws BadPreferenceException;
	
	/**
	 * Determines whether the the user has altered the displayed preference from the one saved in the program's <code>Preferences</code> object.
	 * 
	 * @return <code>true</code> iff the preference currently being graphically displayed is not the stored one
	 */
	protected abstract boolean isChanged();
	
	/**
	 * Restores the graphically displayed preference to the one that is currently stored in the program's <code>Preferences</code> object.
	 */
	protected abstract void graphicallyRevert();
	
	/**
	 * Restores both the saved value in the program's <code>Preferences</code> object, AND the graphically entered preference, the the factory default.
	 */
	protected abstract void restoreDefault();
}
