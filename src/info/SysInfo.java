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

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import control.Start;

/**
 * Collects system-specific information.
 * 
 * Values are determined only once, and then stored.
 * 
 * @author Yuvi Masory
 */
public class SysInfo {
	
	public static final SysInfo sys = new SysInfo();
	

	public final boolean isMacAny;
	public final boolean isMacOSX;
	public final boolean isLinux;
	public final boolean isGNOME;
	public final boolean isKDE;
	public final boolean isOpenJDK;
	public final boolean isWindowsAny;
	public final boolean isWindows7;
	public final boolean isSolaris;
	public final boolean isJava5;
	public final boolean isJava6;
	
	public final int menuKey;
	public final int chunkSizeInSeconds;
	public final int maxInterpolatedPixels;
	public final int jsInternalBufferSize;
	public final int jsExternalBufferSize;
	
	public final double interplationToleratedErrorZoneInSec;
	
	public final boolean useMnemonics;
	public final boolean useAWTFileChoosers;
	public final boolean useSheets;
	public final boolean launchedWithJWS;
	public final boolean preferDefaultJSMixerLine;
	public final boolean useMetalLAF;
	public final boolean mouseMode;
	public final boolean forceWatch;
	public final boolean bandpassFilter;
	public final boolean useAudioDataSmoothingForWaveform;
	public final boolean useWaveformImageDataSmoothing;	
	public final boolean interpolateFrames;	
	public final boolean nanoInterplation;
	public final boolean antiAliasWaveform;
	public final boolean pulseAudioSystem;
	public final boolean doubleDraw;
	
	
	public final String aboutMessage;
	public final String menuKeyString;
	public final String userHomeDir;
	public final String updateAddress;
	public final String preferencesString;


	private SysInfo() {
		//was the program launched with Java Web Start?
		String jwsVal = System.getProperty("deployment.version");
		if(jwsVal == null) {
			launchedWithJWS = false;
		}
		else {
			launchedWithJWS = true;
		}
		
		String jreVersion = System.getProperty("java.runtime.version");
		if(jreVersion == null) {
			isJava5 = false;
			isJava6 = false;
		}
		else {
			if(jreVersion.startsWith("1.5")) {
				isJava5 = true;
				isJava6 = false;
			}
			else if(jreVersion.startsWith("1.6")) {
				isJava6 = true;
				isJava5 = false;
			}
			else {
				System.err.println("I don't understand what version of Java you are running");
				isJava5 = false;
				isJava6 = false;
			}
		}
		
		
		
		//determine current operating system
		String osName = System.getProperty("os.name").toLowerCase();				
		if(osName == null) {
			isSolaris = isMacOSX = isMacAny = isLinux =	isWindowsAny = isWindows7 = isOpenJDK = false;
		}
		else {
			if(osName.contains("windows 7")) {
				isWindowsAny = true;
				isWindows7 = true;
				isSolaris = isLinux = isMacOSX = isMacAny = isOpenJDK = false;
			}
			else if(osName.contains("win")) {
				isWindowsAny = true;
				isSolaris = isWindows7 = isLinux = isMacOSX = isMacAny = isOpenJDK = false;
			}
			else if(osName.contains("linux")) {
				isLinux = true;
				isSolaris = isWindowsAny = isWindows7 = isMacOSX = isMacAny = false;
				String vmName = System.getProperty("java.vm.name");
				if(vmName == null) {
					isOpenJDK = false;
				}
				else {
					isOpenJDK = vmName.toLowerCase().contains("openjdk");
				}
			}
			else if(osName.contains("mac os x")) {
				isMacOSX = true;
				isMacAny = true;
				isSolaris = isWindowsAny = isWindows7 = isLinux = isOpenJDK = false;
			}
			else if(osName.contains("mac")) {
				isMacAny = true;
				isSolaris = isWindowsAny = isWindows7 = isMacOSX = isLinux = isOpenJDK = false;
			}
			else if(osName.contains("solaris")) {
				isSolaris = true;
				isOpenJDK = isMacOSX = isMacAny = isLinux = isWindows7 = isWindowsAny = false;
			}
			else {
				isSolaris = isMacAny = isWindowsAny = isWindows7 = isMacOSX = isLinux = isOpenJDK = false;
				System.err.println("cannot recognize your operating system");
			}
		}
		
		
		if(isLinux && isJava5) {
			if(runningCompiz()) {
				System.err.println("Compiz detected, exiting.");
				JOptionPane.showMessageDialog(
						null,
						Constants.programName + " has detected you are running Compiz and Java 5, which are incompatible.\n" +
						"Please either upgrade to Java 6 or turn off Compiz.\n" +
						"In Ubuntu you can turn off Compiz through System -> Preferences -> Appearance -> Visual Effects -> None\n",
						GUIConstants.errorDialogTitle, 
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		
		
		
		if(isOpenJDK) { //workaround to possible openjdk bug in Graphics.drawImage()
			doubleDraw = true;
		}
		else {
			doubleDraw = false;
		}
		
		
		
		
		//detect GNOME/KDE in Linux
		if(isLinux) {
			String desktopVar = System.getenv("DESKTOP_SESSION").toLowerCase();
			if(desktopVar == null) {
				isGNOME = false;
				isKDE = false;
			}
			else if(desktopVar.contains("gnome")) {
				isGNOME = true;
				isKDE = false;
			}
			else if(desktopVar.contains("kde")) {
				isKDE = true;
				isGNOME = false;
			}
			else {
				isKDE = false;
				isGNOME = false;
			}
		}
		else {
			isGNOME = false;
			isKDE = false;
		}
		
		
		
		
		
		//what is the user's home directory?
		String homeVal = System.getProperty("user.home");
		if(homeVal == null) {
			String curDir = null;
			try {
				curDir = new File(".").getCanonicalPath();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			if(curDir == null) {
				userHomeDir = "";
			}
			else {
				userHomeDir = curDir;
			}
		}
		else {
			userHomeDir = homeVal;
		}
		
		
		
		
		//what is the correct update file location?
		if(isMacOSX) {
			updateAddress = "http://memory.psych.upenn.edu/files/software/TotalRecall/version_files/mac_version.txt";
		}
		else if(isLinux) {
			updateAddress = "http://memory.psych.upenn.edu/files/software/TotalRecall/version_files/linux_version.txt";
		}
		else {
			updateAddress = "http://memory.psych.upenn.edu/files/software/TotalRecall/version_files/windows_version.txt";
		}
		
		
		
		
		
		//generate string displayed for "About this Program"		
		aboutMessage = 
		Constants.programName + " v" + Constants.programVersion + "\n" +
		"Author: " + Constants.authorString + "\n" +
		"Maintainer: " + Constants.maintainerEmail + "\n\n" +
		"Released by:" + "\n" +
		Constants.orgName + "\n" +
		Constants.orgAffiliationName + "\n" +
		Constants.orgHomepage + "\n\n" +
		"License: " + Constants.license + "\n" +
		Constants.licenseSite;
		
		
		
		

		//modifier key for menu actions, and its name
		menuKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();		
		switch(menuKey) {
			case(Event.CTRL_MASK): menuKeyString = "Control"; break;
			case(KeyEvent.META_MASK): menuKeyString = (isLinux ? "Meta" : "Command"); break;
			case(KeyEvent.ALT_MASK): menuKeyString = (isMacAny ? "Option" : "Alt"); break;
			case(KeyEvent.SHIFT_MASK): menuKeyString = "Shift"; break;
			default: menuKeyString = "MenuKey";
		}
		
		
		
		
		
		//customize appearance
		if(isMacOSX) {
			useAWTFileChoosers = true;
			useSheets = false;
			useMetalLAF = false;
			preferencesString = "Preferences";
			useMnemonics = false;
		}
		else if(isMacAny) {
			useAWTFileChoosers = false;
			useSheets = false;
			useMetalLAF = false;
			preferencesString = "Preferences";
			useMnemonics = false;
		}
		else if(isWindowsAny) {
			useAWTFileChoosers = false;
			useSheets = false;
			useMetalLAF = false;
			preferencesString = "Options";
			useMnemonics = true;
		}
		else if(isLinux) {
			useAWTFileChoosers = false;
			useSheets = false;
			preferencesString = "Preferences";
			useMnemonics = true;
			
			if(isJava5) {
				useMetalLAF = true;
			}
			else {
				useMetalLAF = false;
			}
			
//			//the Swing imitation of ClearLooks LAF doesn't draw menu item borders correctly, so use Java LAF "Metal" instead
//			if(isLinux && usingClearlooks()) {
//				useMetalLAF = true;
//			}
//			else {
//				useMetalLAF = false;
//			}
		}
		else {
			useAWTFileChoosers = false;
			useSheets = false;
			useMetalLAF = false;
			preferencesString = "Preferences";
			useMnemonics = false;
		}
		

		
		
		
		
		//check for JS mixers
		boolean pulseAudioDefined = false;
		try {
			Class.forName("org.classpath.icedtea.pulseaudio.PulseAudioSourceDataLine");
			pulseAudioDefined = true;
		} 
		catch (Throwable t) {
		}
		pulseAudioSystem = pulseAudioDefined;
		
		
		
		
		
		
		
		//audio settings
		if(isMacOSX) {
			preferDefaultJSMixerLine = true;
			jsInternalBufferSize = 1024 * 3;
			jsExternalBufferSize = 1024 * 3;
			interpolateFrames = true;
			maxInterpolatedPixels = 10;
			interplationToleratedErrorZoneInSec = 0.25;
			nanoInterplation = false;
		}
		else if(isLinux) {
			preferDefaultJSMixerLine = true;
			jsInternalBufferSize = -1;
			jsExternalBufferSize = 1024 * 3;
			interpolateFrames = true;
			maxInterpolatedPixels = 15;
			interplationToleratedErrorZoneInSec = 0.25;
			nanoInterplation = false;
			
		}
		//ideally an ordered collection of contingencies in order of preference
		else if(isWindowsAny){ //Windows settings will serve as defaults
			preferDefaultJSMixerLine = true; //very bizarre, but if you explicitly request a MixerSourceLine (same class as you get automatically), audio is horrible
			jsInternalBufferSize = 1024 * 3;
			jsExternalBufferSize = 1024 * 3;
			interpolateFrames = true;
			maxInterpolatedPixels = 30;
			interplationToleratedErrorZoneInSec = 0.25;
			nanoInterplation = true;			
		}
		else {
			preferDefaultJSMixerLine = true;
			jsInternalBufferSize = -1;
			jsExternalBufferSize = 1024 * 3;
			interpolateFrames = true;
			maxInterpolatedPixels = Integer.MAX_VALUE;
			interplationToleratedErrorZoneInSec = 0.25;
			nanoInterplation = true;		
		}

		
		
		//performance optimiziations
		chunkSizeInSeconds = (int)Math.ceil(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / GUIConstants.zoomlessPixelsPerSecond);
		
		//annotation optimizations
		mouseMode = true;
		if(Start.developerMode()) {
			forceWatch = false;
		}
		else {
			forceWatch = true;
		}
		
		//pretty waveform 
		bandpassFilter = true; //essential for making words discernable
		useAudioDataSmoothingForWaveform = true; //essential for thickening the waveform
		useWaveformImageDataSmoothing = true; //prettier but blockier
		antiAliasWaveform = false; //no preference for it
	}
	
	
	private boolean runningCompiz() {
		try {
			File ps = new File("/bin/ps");
			ProcessBuilder pb = new ProcessBuilder(ps.getAbsolutePath(), "-A");
			Process psProc = pb.start();
			psProc.waitFor();

			// read the output of ps                                                                                                            
			BufferedReader br = new BufferedReader(new InputStreamReader(psProc.getInputStream()));
			boolean runningCompiz = false;
			String line = null;
			while((line=br.readLine()) != null) {
				line = line.toLowerCase();
				if ((line.endsWith("compiz")) || (line.endsWith("compiz.real"))) {
					runningCompiz = true;
					break;
				}
			}			
			return runningCompiz;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}                                                                                                                  

	
	
	
	
	@SuppressWarnings("unused")
	private boolean usingClearlooks() {
		try {
			File gconf = new File("/usr/bin/gconftool-2");
			if(gconf.exists() == false) {
				return false;
			}
			ProcessBuilder pb = new ProcessBuilder(gconf.getAbsolutePath(), "-g", "/desktop/gnome/interface/gtk_theme");
			Process psProc = pb.start();
			psProc.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(psProc.getInputStream()));
			boolean clearlooks = false;
			String line = null;
			while((line=br.readLine()) != null) {                                                       
				if ((line.toLowerCase().contains("clearlooks"))) {
					clearlooks = true;
					break;
				}
			}
			return clearlooks;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
