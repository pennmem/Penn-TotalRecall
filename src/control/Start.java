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

package control;

import info.Constants;
import info.SysInfo;
import info.UserPrefs;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import util.GiveMessage;
import behaviors.singleact.CheckUpdatesAction;

import components.MacOSXCustomizer;
import components.MyFocusTraversalPolicy;
import components.MyFrame;
import components.MyMenu;
import components.MySplitPane;

/**
 * Entry point class of the entire program.
 * 
 * @author Yuvi Masory
 */
//BUG in short interval playback the last frame or two reported is often a negative value which is a huge value through JNA, current workaround is kludge
//BUG jump in progress position when you click the waveform sometimes
//BUG audio playback slightly off in Windows, use sample.wav

//DO acceleration/deceleration to minimize instances of position correction from excessive stream/sysclock disparity, other sources of itnerpolation errors too
//DO latency offset correction feature for dev-mode at least

//IMPROVE FMOD self-stopping not releasing sound and error with Channel.getPosition() in streamPosition() submit to FMOD Wiki once resolved
//IMPROVE FMOD system is accessed from multiple threads, contra the API spec. This is an invitation for disaster.

//IMPROVE BUILD: msvc compiler warnings
//IMPROVE BUILD: use path + chdir for all platforms
//IMPROVE BUILD: add signing of .app to ant tasks
//IMPROVE emacs keybindings pref should take effect immediately
//IMPROVE examine bandpass lower cutoff for Mike
//IMPROVE waveform flutter on slower computers at chunk boundaries

//V2 post smoothness issue on javagaming.net
//V2 triangle/word overlaps
//V2 sparkle updater using rkuntz.org guide
//V2 8-bit & multi-channel support, aiff
//V2 more mouse support
//V2 uniform persistent preferences and keybindings w/ caching in memory, plus keybindings changer gui
//V2 stdout/stderr log
public class Start {
	private static boolean DEV_MODE;
	
	public static final boolean DEBUG_FOCUS = false;

	
	/**
	 * True entry point of program after Swing thread is created.
	 *
	 * First makes look and feel customizations for Mac OSX and other platforms, and then launches main program window.
	 * Creates and runs thread to check for updates.
	 */
	private Start() {
		System.out.println(Constants.programName + " current directory: " + new File(".").getAbsolutePath());
		System.out.println(Constants.programName + " detected architecture: " + System.getProperty("sun.arch.data.model"));
		if(SysInfo.sys.isMacOSX) {
			MacOSXCustomizer.customizeForMacOSX();
		}
		
		try {
			if(SysInfo.sys.useMetalLAF) {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		MyFrame.getInstance(); //creates all the components, so after this line everything is made, just not visible
		MyFrame.getInstance().setFocusTraversalPolicy(new MyFocusTraversalPolicy());
		MyMenu.updateActions(); //set up all action states before frame becomes visible but after all components tied to the actions are made		
		restoreFramePositionAndLayout();
		MyFrame.getInstance().setVisible(true);
		new CheckUpdatesAction(false).actionPerformed(new ActionEvent(MyFrame.getInstance(), ActionEvent.ACTION_PERFORMED, null));
		checkIfFirstRun();
		loadAndCheckCalibration();
	}

	/**
	 * Attempts to restore program window's frame size, position, and internal split pane divider location using saved values.
	 * 
	 * If saved values are not available, defaults are used.
	 */
	private void restoreFramePositionAndLayout() {		
		MyFrame frame = MyFrame.getInstance();
		
		if(UserPrefs.prefs.getBoolean(UserPrefs.windowMaximized, UserPrefs.defaultWindowMaximized)) {
			frame.setLocation(0, 0);
			frame.setBounds(0, 0, UserPrefs.defaultWindowWidth, UserPrefs.defaultWindowHeight);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		else {
			int lastX = 0;
			int lastY = 0;
			int lastWidth = 0;
			int lastHeight = 0;
			try {
				lastX = UserPrefs.prefs.getInt(UserPrefs.windowXLocation, 0); 
				lastY = UserPrefs.prefs.getInt(UserPrefs.windowYLocation, 0);
				lastWidth = UserPrefs.prefs.getInt(UserPrefs.windowWidth, UserPrefs.defaultWindowWidth);
				lastHeight = UserPrefs.prefs.getInt(UserPrefs.windowHeight, UserPrefs.defaultWindowHeight);
			}
			catch(NumberFormatException e) {
				lastX = 0;
				lastY = 0;
				lastWidth = 1000;
				lastHeight = 500;
			}
			frame.setLocation(lastX, lastY);
			frame.setBounds(new Rectangle(lastX, lastY, lastWidth, lastHeight));
		}

		int dividerLocation = 0;
		int halfway = UserPrefs.prefs.getInt(UserPrefs.windowHeight, UserPrefs.defaultWindowHeight)/2;
		dividerLocation = UserPrefs.prefs.getInt(UserPrefs.dividerLocation, halfway);
		MySplitPane.getInstance().setDividerLocation(dividerLocation);	
	}

	private void loadAndCheckCalibration() {
		int offset = UserPrefs.prefs.getInt(UserPrefs.audioOffsetFrames, UserPrefs.defaultAudioOffsetFrames);
		CurAudio.setOffsetFrames(Math.max(0, offset));
		if(offset < 1 && SysInfo.sys.isMacOSX == false) {
			String line1 = "Please calibrate your audio system by going to Controls --> Calibrate.";
			String line2 = "Failing to do so may result in substantially inaccurate annotations in Linux and Windows.";
			GiveMessage.infoMessage(line1 + "\n" + line2);
		}		
	}
	
	/**
	 * Presents user with first-run notification, if appropriate.
	 */
	private void checkIfFirstRun() {
		boolean firstRun = UserPrefs.prefs.getBoolean(UserPrefs.isFirstRun, true);
		if(firstRun) {
			String message = "Welcome to " + Constants.programName + " " + Constants.programVersion + "!" + "\n\n" +
			"Tutorials and help are available at " + Constants.tutorialSite + ".\n\n" +
			"Please report any bugs you encounter to " + Constants.maintainerEmail + ".";
			
			GiveMessage.infoMessage(message);
		}
		UserPrefs.prefs.putBoolean(UserPrefs.isFirstRun, false);
	}
	
	public static boolean developerMode() {
		return DEV_MODE;
	}

	/**
	 * Program entry point.
	 * Only used to create an object of this class running on the event dispatch thread, as per Java Swing policy.
	 *  
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		if(args.length > 0) {
			if(args[0].equals("-developer")) {
				DEV_MODE = true;
				System.out.println("Running " + Constants.programName + " in developer mode.");
			}
			else {
				DEV_MODE = false;
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Start();
			}
		});
	}
}
