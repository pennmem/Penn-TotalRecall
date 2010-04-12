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

import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * Simple <code>JScrollPane</code> container for the <code>WordpoolList</code>.
 * Nearly the same as a default <code>JScrollPane</code>.
 * 
 * @author Yuvi Masory
 */
public class WordpoolScrollPane extends JScrollPane {
	
	private static WordpoolList list;
	
	/**
	 * Creates a new <code>WordpoolScrollPane</code>, initializing the view to <code>WordpoolList</code> and key bindings.
	 */
	protected WordpoolScrollPane() {
		setOpaque(false);
		list = WordpoolList.getInstance();
		getViewport().setView(list);
		
		//overrides JScrollPane key bindings for the benefit of SeekAction's key bindings
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "none");
	}
}
