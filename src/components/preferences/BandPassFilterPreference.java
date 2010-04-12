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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * An <code>AbstractPreferenceDisplay</code> for choosing the range of frequencies to bandpass filter.
 * 
 * Reasonable inputs (i.e. non-negative integer frequencies with max >= min) are enforced. 
 * 
 * @author Yuvi Masory
 */
public class BandPassFilterPreference extends AbstractPreferenceDisplay {

	private static final int DEFAULT_MIN = UserPrefs.defaultMinBandPass;
	private static final int DEFAULT_MAX = UserPrefs.defaultMaxBandPass;

	private int lastMinVal;
	private int lastMaxVal;

	//by the time the input verifier is made in the constructor, 
	//minField's getText() must return a string parseable to
	//an integer >= maxField's
	private JTextField minField;
	private JTextField maxField;

	private String prefTitle;

	/**
	 * Creates a new <code>BandPasFilterPreference</code> with the provided title.
	 * 
	 * @param prefTitle The title of the preference, will be displayed graphically for the user
	 */
	protected BandPassFilterPreference(String prefTitle) {
		super(prefTitle);
		this.prefTitle = prefTitle;
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JPanel minPanel = new JPanel();
		minPanel.setLayout(new BoxLayout(minPanel, BoxLayout.X_AXIS));
		minPanel.add(new JLabel("Min (Hz):"));

		int storedMin = UserPrefs.prefs.getInt(UserPrefs.minBandPass, DEFAULT_MIN);
		UserPrefs.prefs.putInt(UserPrefs.minBandPass, storedMin);
		lastMinVal = storedMin;
		minField = new JTextField(Integer.toString(storedMin));

		minField.setMaximumSize(new Dimension(75, Integer.MAX_VALUE));
		minPanel.add(minField);
		JPanel maxPanel = new JPanel();
		maxPanel.setLayout(new BoxLayout(maxPanel, BoxLayout.X_AXIS));
		maxPanel.add(new JLabel("Max (Hz):"));


		int storedMax = UserPrefs.prefs.getInt(UserPrefs.maxBandPass, DEFAULT_MAX);
		UserPrefs.prefs.putInt(UserPrefs.maxBandPass, storedMax);
		lastMaxVal = storedMax;
		maxField = new JTextField(Integer.toString(storedMax));

		//set the input verifier, which will correct bad inputs when the user tries to move focus away
		NondecreasingPositiveIntegerVerifier verifier = new NondecreasingPositiveIntegerVerifier(minField, maxField);
		minField.setInputVerifier(verifier);
		maxField.setInputVerifier(verifier);

		maxField.setMaximumSize(new Dimension(75, Integer.MAX_VALUE));
		maxPanel.add(maxField);
		panel.add(minPanel);
		panel.add(maxPanel);
		add(panel);
	}

	/**
	 * Rejects invalid inputs (see class-level docs) by throwing <code>BadPreferenceException</code>.
	 * This check is redundant to the input verifier's job.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected boolean save() throws BadPreferenceException {
		int minVal = 0;
		int maxVal = 0;
		try {
			minVal = Integer.parseInt(minField.getText());
			maxVal = Integer.parseInt(maxField.getText());
		}
		catch(NumberFormatException e) {
			throw new BadPreferenceException(prefTitle, "Input must be an integer.");
		}
		if(minVal > maxVal) {
			throw new BadPreferenceException(prefTitle, "Minimum value must be less than maximum value.");
		}
		else {
			lastMinVal = minVal;
			minField.setText(Integer.toString(minVal));
			UserPrefs.prefs.putInt(UserPrefs.minBandPass, minVal);		
			lastMaxVal = maxVal;
			maxField.setText(Integer.toString(maxVal));
			UserPrefs.prefs.putInt(UserPrefs.maxBandPass, maxVal);
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isChanged() {
		try {
			if(Integer.parseInt(minField.getText()) != lastMinVal){
				return true;
			}
			if(Integer.parseInt(maxField.getText()) != lastMaxVal) {
				return true;
			}
		}
		catch(NumberFormatException e) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void graphicallyRevert() {
		maxField.setText(Integer.toString(lastMaxVal));
		minField.setText(Integer.toString(lastMinVal));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void restoreDefault() {
		UserPrefs.prefs.putInt(UserPrefs.minBandPass, DEFAULT_MIN);
		minField.setText(Integer.toString(DEFAULT_MIN));
		lastMinVal = DEFAULT_MIN;
		UserPrefs.prefs.putInt(UserPrefs.maxBandPass, DEFAULT_MAX);
		maxField.setText(Integer.toString(DEFAULT_MAX));
		lastMaxVal = DEFAULT_MAX;
	}

	/**
	 * Custom <code>InputVerifier</code> that guarantees one <code>JTextField</code> displays a positive integer >= than the other one.
	 * 
	 */
	private class NondecreasingPositiveIntegerVerifier extends InputVerifier {

		JTextField minComp;
		JTextField maxComp;

		/**
		 * Creates a new <code>NondecreasingPositiveIntegerVerifier</code> with the provided <code>JTextFields</code>.
		 * 
		 * @param minComp The <code>JTextField</code> displaying the smaller (or equal) integer
		 * @param maxComp The <code>JTextField</code> displaying the larger (or equal) integer
		 * @throws IllegalArgumentException If the inputs' texts do not currently meet the verifier's standards
		 */
		private NondecreasingPositiveIntegerVerifier(JTextField minComp, JTextField maxComp) {
			this.minComp = minComp;
			this.maxComp = maxComp;
			if(verify(minComp) == false || verify(maxComp) == false) {
				throw new IllegalArgumentException("provided components not in verifiable state");
			}
		}

		/**
		 * In this implementation we give a beep and return the value to the previous one if the field's text is found invalid by {{@link #verify(JComponent)}.
		 * {@inheritDoc}
		 */
		@Override
		public boolean shouldYieldFocus(JComponent input) {
			if(input != minComp && input != maxComp) {
				throw new IllegalArgumentException("unrecognized component: not min or max component given to constructor");
			}
			boolean goodInput = verify(input);
			if(goodInput == false) {
				if(input == minComp) {
					minComp.setText(Integer.toString(lastMinVal));
					if(verify(maxComp) == false) {
						System.err.println("input verifier internal assumption failed");
					}
				}
				else {
					maxComp.setText(Integer.toString(lastMaxVal));
					if(verify(minComp) == false) {
						System.err.println("input verifier internal assumption failed");						
					}
				}
				Toolkit.getDefaultToolkit().beep();
			}
			return goodInput;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean verify(JComponent input) {
			if(input != minComp && input != maxComp) {
				throw new IllegalArgumentException("unrecognized component: not min or max component given to constructor");
			}
			int minNum = 0;
			int maxNum = 0;
			try {
				minNum = Integer.parseInt(minComp.getText());
				maxNum = Integer.parseInt(maxComp.getText());
			}
			catch(NumberFormatException e) {
				return false;
			}
			if(minNum < 0 || maxNum < 0) {
				return false;
			}
			if(minNum > maxNum) {
				return false;
			}
			return true;
		}		
	}
}
