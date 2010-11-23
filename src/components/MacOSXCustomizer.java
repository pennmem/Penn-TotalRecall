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

package components;

import info.SysInfo;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

import util.GiveMessage;
import behaviors.singleact.ExitAction;
import behaviors.singleact.PreferencesAction;

/**
 * Makes look and feel customizations for Mac OSX
 * 
 * @author Yuvi Masory
 */
public class MacOSXCustomizer {

	/**
	 * Takes control of Mac application menu behavior, moves menu bar to top of screen, adds dock badge.
	 * 
	 * If method returns false it is all but certain that no customizations were made.
	 * 
	 * @return <tt>true</tt> iff Mac Java extensions were found
	 */
	public static boolean customizeForMacOSX() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");		
		System.setProperty("apple.awt.textantialiasing", "on");
		System.setProperty("apple.awt.antialiasing", "on");
		System.setProperty("apple.awt.rendering", "quality");
	
		//also available:
		//http://developer.apple.com/mac/library/documentation/Java/Reference/Java_PropertiesRef/Articles/JavaSystemProperties.html
		//System.setProperty("apple.awt.graphics.UseQuartz", "true");
		
		try {
			//create Mac Java extensions customization object
			com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();

			//custom dock icon set using Jar Bundler program, but here in case of Java Web Start
			if(SysInfo.sys.launchedWithJWS) {
				try {
					macApp.setDockIconImage(ImageIO.read(MacOSXCustomizer.class.getResource("/images/headphones128.png")));
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}

			//take control of Mac application menu item
			macApp.addPreferencesMenuItem();
			macApp.setEnabledPreferencesMenu(true);
			macApp.addAboutMenuItem();
			macApp.setEnabledAboutMenu(true);
			
			//also available:
//			macApp.setDockMenu(PopupMenu);
//			macApp.setDockIconBadge(String);
			
			macApp.addApplicationListener(new com.apple.eawt.ApplicationAdapter(){
				@Override
				public void handleAbout(com.apple.eawt.ApplicationEvent e) {
					GiveMessage.infoMessage(SysInfo.sys.aboutMessage);
					e.setHandled(true);
				}

				@Override
				public void handlePreferences(com.apple.eawt.ApplicationEvent e) {
					new PreferencesAction().actionPerformed(new ActionEvent(MyFrame.getInstance(), ActionEvent.ACTION_PERFORMED, null, System.currentTimeMillis(), 0));
					e.setHandled(true);
				}

				@Override
				public void handleQuit(com.apple.eawt.ApplicationEvent e) {
					new ExitAction().actionPerformed(new ActionEvent(MyFrame.getInstance(), ActionEvent.ACTION_PERFORMED, null, System.currentTimeMillis(), 0));
					e.setHandled(true);
				}
				
//				@Override
//				public void handleOpenFile(com.apple.eawt.ApplicationEvent e) {
//					System.out.println("received: " + e.getFilename() + " from " + e.getSource());
////					e.setHandled(true);
//				}
				
//				@Override
//				public void handleOpenApplication(com.apple.eawt.ApplicationEvent e) {
//					System.out.println("open: " + e.getSource());
////					e.setHandled(true);
//				}
//				
//				@Override
//				//from double clicking icon in doc
//				public void handleReOpenApplication(com.apple.eawt.ApplicationEvent e) {
//					System.out.println("reopen: " + e.getSource());
////					e.setHandled(true);
//				}
			});
			
			return true;
		}
		catch(Throwable e) {
			//Mac Java extensions not available, despite the JVM identifying as MacOSX
			return false;
		}
	}





	/**
	 * Private constructor to prevent instantiation.
	 */
	private MacOSXCustomizer() {
	}
}
