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

import info.UserPrefs;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * An <code>AbstractPreferenceDisplay</code> for choosing between one of two options.
 * 
 * It is easy to extend use this class to add a new preference chooser to support new features.
 * See {@link PreferencesFrame} for examples of this class in use.
 * 
 * This class automatically uses the correct {@link java.util.prefs.Preferences} object, in keeping with program policy.
 * 
 * @author Yuvi Masory
 */
public class BooleanPreference extends AbstractPreferenceDisplay {
	
	private boolean lastPref = false;
	
	private JRadioButton trueButton;
	private JRadioButton falseButton;
	
	private boolean defValue;
	
	private String prefKey;
	private String prefTitle;
	
	/**
	 * Creates a new <code>BooleanPreferences</code> hooked up to the provided Preferences object, key, and default value.
	 * 
	 * @param prefTitle The title of the preference, will be displayed graphically for the user
	 * @param prefKey The key for the <code>java.util.prefs.Preferences object</code>, should be stored in <code>info.UserPrefs</code>
	 * @param truePrefName The name of the option corresponding to <code>true</code>, will be displayed graphically for the user
	 * @param falsePrefName The name of the option corresponding to <code>false</code>, will be displayed graphically for the user
	 * @param defValue The default value, should be stored in <code>info.UserPrefs</code>
	 */
	protected BooleanPreference(String prefTitle, String prefKey, String truePrefName, String falsePrefName, boolean defValue) {
		super(prefTitle);
		this.prefKey = prefKey;
		this.prefTitle = prefTitle;
		this.defValue = defValue;
		trueButton = new JRadioButton(truePrefName);
		falseButton = new JRadioButton(falsePrefName);
		ButtonGroup group = new ButtonGroup();
		group.add(falseButton);
		group.add(trueButton);
		
		if(UserPrefs.prefs.getBoolean(prefKey, defValue) == false) {
			UserPrefs.prefs.putBoolean(prefKey, false);
			falseButton.setSelected(true);
			lastPref = false;
		}
		else {
			UserPrefs.prefs.putBoolean(prefKey, true);
			trueButton.setSelected(true);
			lastPref = true;
		}
		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(trueButton);
		radioPanel.add(falseButton);
		add(radioPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean save() {
		if(trueButton.isSelected()) {
			lastPref = true;
			UserPrefs.prefs.putBoolean(prefKey, true);
		}
		else {
			lastPref = false;
			UserPrefs.prefs.putBoolean(prefKey, false);
		}
		return true;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isChanged() {
		if(lastPref == false) {
			if(trueButton.isSelected()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if(trueButton.isSelected()) {
				return false;
			}
			else {
				return true;
			}
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void graphicallyRevert() {
		if(lastPref == true) {
			trueButton.setSelected(true);
		}
		else {
			falseButton.setSelected(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreDefault() {
		UserPrefs.prefs.putBoolean(prefKey, defValue);
		if(defValue == true) {
			trueButton.setSelected(true);
			lastPref = true;
		}
		else {
			falseButton.setSelected(true);
			lastPref = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getName() + ": " + prefTitle;
	}
}
