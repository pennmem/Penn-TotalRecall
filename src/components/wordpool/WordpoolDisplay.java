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

import info.GUIConstants;
import info.MyShapes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A custom interface component for displaying wordpool and lst words to the user and a text field in which to enter annotations.
 * 
 * The component supports tab auto-complete of words being entered in the text field, using words from the wordpool list display.
 * The user is forced to choose only words from the list, to do otherwise requires marking the word as an intrusion with a special keystroke.
 * The display is self-sorting, with lst words above regular wordpool words, and alphabetical otherwise.
 * 
 * <p>Note: Access to this component from outside the package is limited to the public static methods provided in this class.
 * Code outside the package cannot and should not try to access the internal list, model, or other components directly.
 * 
 * @author Yuvi Masory
 *
 */
public class WordpoolDisplay extends JPanel {

	private static final String title = "Wordpool";

	private static JTextField field;

	private static WordpoolDisplay instance;

	private static WordpoolScrollPane pane;

	/**
	 * Creates a new instance of the component, initializing internal components, listeners, and various aspects of appearance.
	 */
	private WordpoolDisplay() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		field = WordpoolTextField.getInstance();
		pane = new WordpoolScrollPane();

		add(field);
		add(pane);

		setPreferredSize(GUIConstants.wordpoolDisplayDimension);
		setMaximumSize(GUIConstants.wordpoolDisplayDimension);

		setBorder(MyShapes.createMyUnfocusedTitledBorder(title));

		//since WordpoolDisplay is a clickable area, we must write focus handling code for the event it is clicked on
		//this case is rare, since only a very small amount of this component is exposed (the area around the border title), 
		//the rest being obscured by the WordpoolList and WordpoolTextField
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				field.requestFocusInWindow();
			}
		});
	}

	/**
	 * Public accessor to the <code>WordpoolTextField</code>'s text.
	 * 
	 * @return WordpoolTextField.getInstance().getText()
	 */
	public static String getFieldText() {
		return WordpoolTextField.getInstance().getText();
	}

	/**
	 * Sets the <code>WordpoolTextField</code>'s text to the empty string.
	 */
	public static void clearText() {
		WordpoolTextField.getInstance().setText("");
	}
	
	public static void distinguishAsLst(List<WordpoolWord> lstWords) {
		WordpoolList.getInstance().getModel().distinguishAsLst(lstWords);
	}
	
	public static void undistinguishAllWords() {
		WordpoolList.getInstance().getModel().undistinguishAllWords();
	}

	/**
	 * Removes all <code>WordpoolWords</code> from the component, 
	 * whether or not they are present graphically or hidden (because of auto-complete filtering).
	 */
	public static void removeAllWords() {
		WordpoolList.getInstance().getModel().removeAllWords();
	}

	/**
	 * Adds the provided <code>WordpoolWords</code> to the component for display.
	 * 
	 * @param words
	 */
	public static void addWordpoolWords(List<WordpoolWord> words) {
		WordpoolList.getInstance().getModel().addElements(words);
	}

	/**
	 * Finds the alphabetically first <code>WordpoolWord</code> that matches the provided <code>String</code>.
	 * 
	 * @param str The <code>String</code> to be matched, often the contents of the <code>WordpoolTextField</code>
	 * @return The first alphabetical matching <code>WordpoolWord</code>, or <code>null</code> if there is no match
	 */
	public static WordpoolWord findMatchingWordpooWord(String str) {
		return WordpoolList.getInstance().getModel().findMatchingWordpoolWord(str);
	}

	/**
	 * Called by outside key listeners when the user types alphanumeric characters.
	 * The idea is to pass focus to the text field and enter the string programmatically that the user had started typing
	 * before the field had focus.
	 * This is a convenience feature so the user doesn't have to manually give the field focus to enter something.
	 * 
	 * Does nothing if the <code>WordpoolTextField</code> already has focus.
	 * 
	 * @param str The String the user typed, possibly before the <code>WordpoolTextField</code> had focus.
	 */
	public static void switchToFocus(String str) {
		if(field.hasFocus() == false) { //don't double-add strings that will be added by the field automatically!
			field.setText(field.getText() + str);
			field.requestFocusInWindow();
		}
	}
	
	public static void switchToFocusAndClobber(String str) {
		if(field.hasFocus() == false) { //don't double-add strings that will be added by the field automatically!
			field.setText(str);
			field.requestFocusInWindow();
		}
	}

	/**
	 * Singleton accessor
	 * 
	 * @return The singleton <code>WordpoolDisplay</code>
	 */
	public static WordpoolDisplay getInstance() {
		if (instance == null) {
			instance = new WordpoolDisplay();
		}
		return instance;
	}
	
	public static void setInputEnabled(boolean enabled) {
		WordpoolTextField.getInstance().setEnabled(enabled);
	}
}
