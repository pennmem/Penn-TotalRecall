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

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import components.MyMenu;

/**
 * Preference for the sizes of forward/backward jumps of <code>SeekActions</code> and <code>Last200PlusMoveActions</code>.
 * 
 * Does not require restart or reload of audio file.
 * 
 * @author Yuvi Masory
 */
public class SeekSizePreference extends AbstractPreferenceDisplay {

	private static final int minVal = 1;
	private static final int step = 5; 
	
	private JSpinner spinner;
	
	private int defValue;
	
	public enum ShiftSize {SMALL_SHIFT, MEDIUM_SHIFT, LARGE_SHIFT};
	private ShiftSize size;
	
	protected SeekSizePreference(String title, ShiftSize size) {
		super(title);
		this.size = size;
		switch(size) {
			case SMALL_SHIFT: defValue = UserPrefs.defaultSmallShift; break;
			case MEDIUM_SHIFT: defValue = UserPrefs.defaultMediumShift; break;
			case LARGE_SHIFT: defValue = UserPrefs.defaultLargeShift; break;
		}
		spinner = new JSpinner();
		SpinnerNumberModel model = new SpinnerNumberModel();
		model.setStepSize(step);
		model.setMinimum(minVal);
		model.setMaximum(Integer.MAX_VALUE);
		

		model.setValue(getCurrentVal());
		spinner.setModel(model);
		
		add(spinner);
	}
	
	private int getCurrentVal() {
		switch(size) {
			case SMALL_SHIFT: return UserPrefs.getSmallShift();
			case MEDIUM_SHIFT: return UserPrefs.getMediumShift();
			case LARGE_SHIFT: return UserPrefs.getLargeShift();
		}
		return minVal;
	}

	@Override
	protected void graphicallyRevert() {
		spinner.setValue(getCurrentVal());
	}

	@Override
	protected boolean isChanged() {
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();
		String curContents = editor.getTextField().getText();
		DecimalFormat format = editor.getFormat();
		Number num = null;
		try {
			num = format.parse(curContents);
		} 
		catch (ParseException e) {
		}
		if(num == null) {
			return true;
		}
		else {
			return num.intValue() != getCurrentVal();
		}
	}

	@Override
	protected void restoreDefault() {
		spinner.setValue(defValue);
		saveVal(defValue);
		MyMenu.updateSeekActions();
	}

	@Override
	protected boolean save() throws BadPreferenceException {
		saveVal((Integer)spinner.getValue());
		MyMenu.updateSeekActions();
		return true;
	}
	
	private void saveVal(int nVal) {
		switch(size) {
			case SMALL_SHIFT: UserPrefs.setSmallShift(nVal); break;
			case MEDIUM_SHIFT: UserPrefs.setMediumShift(nVal); break;
			case LARGE_SHIFT: UserPrefs.setLargeShift(nVal); break;
		}
	}
}
