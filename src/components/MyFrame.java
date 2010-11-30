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

import info.GUIConstants;
import info.SysInfo;

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import behaviors.singleact.ExitAction;

import components.waveform.MyGlassPane;
import components.wordpool.WordpoolDisplay;

import control.Start;


/**
 * Main window of the program.
 * 
 * <p>Every component in the frame (at any level of nesting) that can be clicked by the user (i.e., is not obscured) 
 * must handle focus-passing, see {@link MyFocusTraversalPolicy} for details.
 */
public class MyFrame extends JFrame implements KeyEventPostProcessor {

	private static MyFrame instance;

	private MyFrame() {
		setTitle(GUIConstants.defaultFrameTitle);
		setGlassPane(MyGlassPane.getInstance());
		MyGlassPane.getInstance().setVisible(true);
		setJMenuBar(MyMenu.getInstance());

		//force handling by  WindowListener below
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//handle clicking on the "x" mark to close the window
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				new ExitAction().actionPerformed(new ActionEvent(MyFrame.getInstance(), ActionEvent.ACTION_PERFORMED, null, System.currentTimeMillis(), 0));
			}
		});

		setContentPane(MySplitPane.getInstance());

		//accept drag and drop of directories and files
		new  FileDrop(this, new FileDropListener());

		//replace the java coffee cup icon on the top left of windows in such platforms as Windows and Linux
		if(SysInfo.sys.isWindows7) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					MyFrame.class.getResource("/images/headphones48.png"))); //bigger icon for windows 7's revamped task bar
		}
		else {
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					MyFrame.class.getResource("/images/headphones16.png")));
		}

		//this is default, but double checking because focusability is needed for MyFocusTraversalPolicy to be used
		setFocusable(true);

		//used to pass focus to text field when someone types outside of the field
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
		
//		getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
	}

	/**
	 * Singleton accessor.
	 * 
	 * @return The singleton <code>MyFrame</code>
	 */
	public static MyFrame getInstance() {
		if (instance == null) {
			instance = new MyFrame();
		}
		return instance;
	}

	/**
	 * This hears absolutely everything while the JVM has focus (includes preferences frame)
	 * 
	 * <p>Currently this class is used to pass focus to the {@link wordpool.WordpoolTextField} when the user starts typing while <code>MyFrame</code> is selected.
	 * Also used for focus debugging. If {@link Start#DEBUG_FOCUS} is <code>true</code> the focus owner will be printed every time a key is pressed.
	 * 
	 * <p>Please note that arrow keys do NOT generate key typed events, so they are never consumed
	 * Also note key typed events always have the location KeyEvent.KEY_LOCATION_UNKNOWN
	 * Because arrow keys don't generate KeyTyped events, InputMaps are never get them.
	 * They can be heard with KeyListeners, but those require focus, which is messy.
	 * 
	 * <p>Unfortunately there's really no way to tell if a KeyEvent is a press, release, or type event.
	 * We use a heuristic to avoid duplicate event handling.
	 */
	@SuppressWarnings("all")
	public boolean postProcessKeyEvent(KeyEvent e) {
		if(Start.DEBUG_FOCUS) {
			if(e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN) {
				System.out.println(getFocusOwner());
			}
		}
		//best attempt to restrict us to key_typed so we don't have duplicate events for key_pressed and key_released
		//unfortunately, there might be press/release events with undefined/unkown codes that this condition will accept
		if(e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN && e.getKeyCode() == KeyEvent.VK_UNDEFINED) {
			if(e.getModifiers() == 0) {
				if(Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar())) {
					if(getFocusOwner() != null) { //this is how we guarantee MyFrame is "focused" and not the PreferencesFrame. not 100% cross-platform since Solaris separates the focus notion from window selection/prominence
						WordpoolDisplay.switchToFocus(Character.toString(e.getKeyChar()));
					}
				}
			}
		}
		return false;
	}
}
