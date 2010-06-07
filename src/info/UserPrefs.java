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

package info;

import java.util.prefs.Preferences;

/**
 * Stores a user-specific Preferences, as well as preference names, for persistent storage of settings.
 * 
 * Names of preferences are stored here to prevent typos and to make the specific preferences easier
 * to find and document. The actual strings are unimportant.
 * 
 * @author Yuvi Masory
 */
public class UserPrefs {
	
	private static int curSmallShift = -1;
	private static int curMediumShift = -1;
	private static int curLargeShift = -1;
	
	/**
	 * A Preferences object that persistently stores a key-value tree for this user only.
	 * This is the only Preferences object that should ever be used.
	 */
	public static final Preferences prefs = Preferences.userNodeForPackage(UserPrefs.class);
	
	/**
	 * The x-coordinate of MyFrame's top left corner.
	 * Changed only at program exit.
	 * Must map to a non-negative integer.
	 */
	public static final String windowXLocation = "WINDOW_X";

	/**
	 * The y-coordinate of MyFrame's top left corner.
	 * Changed only at program exit.
	 * Must map to a non-negative integer.
	 */
	public static final String windowYLocation = "WINDOW_Y";
	
	/**
	 * The width of MyFrame.
	 * Changed only at program exit.
	 * Must map to a non-negative integer.
	 */
	public static final String windowWidth = "WINDOW_WIDTH";
	
	/**
	 * Whether MyFrame is maximized;
	 */
	public static final String windowMaximized = "WINDOW_MAXIMIZED";
	
	public static final boolean defaultWindowMaximized = false;

	/**
	 * Default value for windowWidth
	 * Must be an integer.
	 * Must be non-negative.
	 */
	public static final int defaultWindowWidth = 1000;
	
	/**
	 * The height of MyFrame.
	 * Changed only at program exit.
	 * Must map to a non-negative integer.
	 */
	public static final String windowHeight = "WINDOW_HEIGHT";
	
	/**
	 * Default value for windowHeight
	 * Must be non-negative.
	 */
	public static final int defaultWindowHeight = 500;
	
	/**
	 * The location of the split pane divider in MySplitPane.
	 * Changed only at program exit.
	 * Must map to a non-negative integer.
	 */
	public static final String dividerLocation = "DIVIDER_LOCATION";
	
	/**
	 * Whether or not this is the first time the program has run on this computer with this user.
	 */
	public static final String isFirstRun = "FIRST_RUN";

	
	
	
	
	/**
	 * The parent of the file/directory from which the wordpool file will be selected.
	 */
	public static final String openWordpoolPath = "OPEN_WORDPOOL_PATH";
	
	/**
	 * The parent of the file/directory from which the audio file/directory will be selected.
	 */
	public static final String openLocationPath = "OPEN_LOCATION_PATH";
	
	
	
	
	
	/**
	 * Whether or not to warn user on exit.
	 * Must map to boolean true or false.
	 */
	public static final String warnExit = "WARN_ON_EXIT";
	
	/**
	 * Default value for warnExit.
	 */
	public static final boolean defaultWarnExit = true;
	
	/**
	 * Whether or not to warn user when switching files.
	 * Must map to boolean true or false.
	 */
	public static final String warnFileSwitch = "WARN_FILE_SWITCH";
	
	/**
	 * Default value for warnFileSwitch.
	 */
	public static final boolean defaultWarnFileSwitch = true;
	
	/**
	 * Lower bound for the bandpass filter.
	 * Must map to a non-negative integer.
	 */
	public static final String minBandPass = "MIN_BAND_PASS";
	
	/**
	 * Default value for minBandPass.
	 * Must be non-negative.
	 */
	public static final int defaultMinBandPass = 1000; //phone company standard: 300, army standard: 400 (http://cnx.org/content/m15683/latest/), pyparse: 1000
	
	/**
	 * Upper bound for the bandpass filter.
	 * Must map to a non-negative integer.
	 */
	public static final String maxBandPass = "MAX_BAND_PASS";
	
	/**
	 * Default value for maxBandPass.
	 * Must be non-negative and not less than defaultMinBandPass.
	 */
	public static final int defaultMaxBandPass = 16000; //phone company standard: 3600, army standard: 2800 (http://cnx.org/content/m15683/latest/), pyparse: 16000
	

	/**
	 * Amount of a "large" shift, expressed in frames
	 */
	private static final String smallShift = "SMALL_SHIFT";
	public static final int defaultSmallShift = 5; //PyParse is 5
	/**
	 * Amount of a "medium" shift, expressed in frames
	 */
	private static final String mediumShift = "MEDIUM_SHIFT";
	public static final int defaultMediumShift = 50; //PyParse is 50	
	/**
	 * Amount of a "small" shift, expressed in frames
	 */
	private static final String largeShift = "LARGE_SHIFT";
	public static final int defaultLargeShift = 500; //PyParse is 1000
	
	public static int getSmallShift() {
		if(curSmallShift <= 0) {
			return prefs.getInt(smallShift, defaultSmallShift);
		}
		return curSmallShift;
	}
	
	public static void setSmallShift(int value) {
		prefs.putInt(smallShift, value);
	}
	
	public static int getMediumShift() {
		if(curMediumShift <= 0) {
			return prefs.getInt(mediumShift, defaultMediumShift);
		}
		return curMediumShift;
	}
	
	public static void setMediumShift(int value) {
		prefs.putInt(mediumShift, value);
	}
	
	public static int getLargeShift() {
		if(curLargeShift <= 0) {
			return prefs.getInt(largeShift, defaultLargeShift);
		}
		return curLargeShift;
	}
	
	public static void setLargeShift(int value) {
		prefs.putInt(largeShift, value);
	}
	
	
	public static final String useEmacs = "USE_EMACS";
	public static final boolean defaultUseEmacs = false;
	
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private UserPrefs() {
	}
}
