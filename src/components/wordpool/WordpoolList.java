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

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * <code>JList</code> that stores available wordpool word for the annotating open audio file.
 * 
 * @author Yuvi Masory
 */
public class WordpoolList extends JList implements FocusListener, MouseListener, KeyListener {

	private static WordpoolListModel model;

	private static WordpoolList instance;

	WordpoolListCellRenderer render;

	private WordpoolList() {
		model = new WordpoolListModel();
		setModel(model);

		//set the cell renderer that will display lst words differently from regular words
		render = new WordpoolListCellRenderer();
		setCellRenderer(render);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.VERTICAL);

		//focus listener makes the the containing WordpoolDisplay look focused at the appropriate times
		addFocusListener(this);
		
		//normally JLists take focus on their own when clicked. however in this case we have made
		//the WordpoolList not focusable when it's empty
		//so in that case we give focus to the WordpoolTextField when the WordpoolList is clicked
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if(isFocusable()) {
					//automatically takes focus in this case
				}
				else {
					WordpoolTextField.getInstance().requestFocusInWindow();
				}
			}
		});
		
		addKeyListener(this);

		
		//clicking on wordpool words
		addMouseListener(this);
		
		//hitting enter can be used to enter a wordpool word to the text field
		//this code is duplicated in the mouse listener where double click has the same effect
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "insert_word");
		getActionMap().put("insert_word", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				Object[] objs = getSelectedValues();
				if(objs.length == 1) { //in case multiple selection mode is used in the future
					WordpoolWord selectedWord = (WordpoolWord)objs[0];
					WordpoolDisplay.switchToFocusAndClobber(selectedWord.getText());
				}
			}
		});
		
		//overrides JScrollPane key bindings for the benefit of SeekAction's key bindings
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK, false), "none");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK, false), "none");
	}

	/**
	 * Type-refined implementation that guarantees a <code>WordpoolListModel</code> instead of a <code>ListModel</code>.
	 * 
	 * @return The <code>WordpoolListModel</code> associated with the <code>WordpoolList</code>
	 */
	@Override
	public WordpoolListModel getModel() {
		return model;
	}
	
	/**
	 * Gets a reference to this object for use by a custom <code>FocusTraversalPolicy</code>.
	 * 
	 * <p>Unfortunately this requires a break from the encapsulation strategy of <code>WordpoolDisplay</code> containing all the <code>public</code> access.
	 * Please do NOT abuse this method to access the <code>WordpoolDisplay</code> for purposes other than those intended.
	 * Add new public features to <code>WordpoolDisplay</code> which can then use {@linkplain #getInstance()} as needed.
	 * 
	 * @return {@link #getInstance()}
	 */
	public static WordpoolList getFocusTraversalReference() {
		return getInstance();
	}

	/**
	 * Custom focusability condition that behaves in the default manner aside from rejecting focus when this <code>WordpoolList</code> has no elements.
	 * 
	 * @return Whether or nut this component should accept focus
	 */
	@Override
	public boolean isFocusable() {
		return(super.isFocusable() && model.getSize() > 0);
	}

	/**
	 * Handler for the event that this <code>WordpoolList</code> gains focus.
	 */
	public void focusGained(FocusEvent e) {
		int anchor = getAnchorSelectionIndex();
		if(anchor >= 0) {
			setSelectedIndex(anchor);	
		}
		else {
			setSelectedIndex(0);
		}
	}

	/**
	 * Handler for the event this <code>AudioFileList</code> loses focus.
	 * 
	 * Asks the containing <code>AudioFileDisplay</code> to stop looking focused.
	 */
	public void focusLost(FocusEvent e) {
		clearSelection();
	}

	/**
	 * 
	 * @return
	 */
	protected static WordpoolWord getFirstWord() {
		if(model.getSize() > 0) {
			return (WordpoolWord)model.getElementAt(0);
		}
		else {
			return null;
		}
	}

	/**
	 * Singleton accessor.
	 * 
	 * Many classes in this package require access to this object, so a singleton accessor strategy is used to avoid the need
	 * to pass every class a reference to this object.
	 * 
	 * @return The singleton <code>WordpoolList</code>
	 */
	protected static WordpoolList getInstance() {
		if(instance == null) {
			instance = new WordpoolList();
		}
		return instance;
	}

	/**
	 * On double click adds enters the clicked-on word to the text field.
	 * 
	 * @param e The MouseEvent provided by the trigger
	 */
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			int index = locationToIndex(e.getPoint());
			if(index >= 0) {
				WordpoolWord clickedWord = (WordpoolWord) model.getElementAt(index);
				WordpoolDisplay.switchToFocusAndClobber(clickedWord.getText());
			}
		}
	}

	/** Empty implementation. */
	public void mouseEntered(MouseEvent e) {}

	/** Empty implementation. */
	public void mouseExited(MouseEvent e) {}

	/** Empty implementation. */
	public void mousePressed(MouseEvent e) {}

	/** Empty implementation. */
	public void mouseReleased(MouseEvent e) {}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(getSelectedIndex() == 0) {
				WordpoolTextField.getInstance().requestFocusInWindow();
			}
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
}