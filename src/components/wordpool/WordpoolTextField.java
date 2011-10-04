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

package components.wordpool;

import info.SysInfo;
import info.UserPrefs;

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import behaviors.multiact.AnnotateAction;

import components.MyFrame;

/**
 * Custom <code>JTextField</code> for entering annotations.
 * 
 * Includes features to aid in annotation speed and accuracy that were added to PyParse over the years.
 * 
 * @author Yuvi Masory
 */
public class WordpoolTextField extends JTextField implements KeyListener, FocusListener {

	private static WordpoolTextField instance;

	private String clipboard = "";

	private WordpoolTextField(){
		setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "annotate regular");
		getActionMap().put("annotate regular", new AnnotateAction(AnnotateAction.Mode.REGULAR));

		Set<AWTKeyStroke> keys = new HashSet<AWTKeyStroke>();
		keys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK, false));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);

		addKeyListener(this);

		//emacs key bindings
		if(UserPrefs.prefs.getBoolean(UserPrefs.useEmacs, UserPrefs.defaultUseEmacs)) {
			JTextComponent.KeyBinding[] newBindings = {
					new JTextComponent.KeyBinding(
							KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK, false),
							DefaultEditorKit.beginLineAction),
							new JTextComponent.KeyBinding(
									KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK, false),
									DefaultEditorKit.endLineAction),
									new JTextComponent.KeyBinding(
											KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK, false),
											DefaultEditorKit.backwardAction),
											new JTextComponent.KeyBinding(
													KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK, false),
													DefaultEditorKit.forwardAction),
													new JTextComponent.KeyBinding(
															KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK, false),
															DefaultEditorKit.deleteNextCharAction)};


			Keymap k = getKeymap();
			JTextComponent.loadKeymap(k, newBindings, getActions());
		}
		addFocusListener(this);
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SysInfo.sys.menuKey, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SysInfo.sys.menuKey, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SysInfo.sys.menuKey + InputEvent.SHIFT_DOWN_MASK, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SysInfo.sys.menuKey + InputEvent.SHIFT_DOWN_MASK, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "clear");
		getActionMap().put("clear", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setText("");
				MyFrame.getInstance().requestFocusInWindow();
			}
		});

	}

	@Override
	protected Document createDefaultModel() {
		return new WordpoolDocument();
	}

	public void keyPressed(KeyEvent e) {
		if(e.getModifiers() == 0) { // no modifiers
			if(e.getKeyCode() == KeyEvent.VK_TAB) {
				if(getText().length() == 0) {
					getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentAfter(
							getFocusCycleRootAncestor(), this).requestFocusInWindow();
					return;
				}
				WordpoolWord firstWord = WordpoolList.getFirstWord();
				if(firstWord == null) {
					return;
				}
				String candidate = firstWord.getText();
				if(candidate != null) {
					setText(candidate);
				}
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				e.consume();
			}
			if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				if(WordpoolList.getInstance().getModel().getSize() > 0) {
					WordpoolList.getInstance().requestFocusInWindow();
				}
			}
			//this assumes backspace will actually remove the previous element, but we'll make that bet
			//alternative is WordpoolDocument.removeUpdate(), but that is called by programmatic changes to textfield too
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { 
				if(getText().length() <= 1) {
					MyFrame.getInstance().requestFocusInWindow();
				}
			}
		}
		//emacs key bindings
		if(e.getModifiers() == InputEvent.CTRL_MASK) {
			if(e.getKeyCode() == KeyEvent.VK_K) {
				emacsKillLine();
			}
			else if (e.getKeyCode() == KeyEvent.VK_Y) {
				emacsYank();
			}
		}
	}

	private void emacsYank() {
		setText(getText() + clipboard);
	}

	private void emacsKillLine() {
		int pos = getCaretPosition();
		clipboard = getText().substring(pos, getText().length());
		setText(getText().substring(0, pos));
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}

	protected static WordpoolTextField getInstance() {
		if(instance == null) {
			instance = new WordpoolTextField();
		}
		return instance;
	}

	public static WordpoolTextField getFocusTraversalReference() {
		return getInstance();
	}

	public void focusGained(FocusEvent e) {
		setSelectionStart(0);
		setSelectionEnd(0);
		setCaretPosition(getText().length());
	}

	public void focusLost(FocusEvent e) {}
}
