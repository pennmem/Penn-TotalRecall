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

import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import behaviors.singleact.DoneAction;

/**
 * A <code>JButton</code> hooked up to a {@link behaviors.singleact.DoneAction}.
 *  
 * @author Yuvi Masory
 */
public class DoneButton extends JButton {

	private static DoneButton instance;

	/**
	 * Creates a new instance, initializing the listeners and appearance.
	 */
	private DoneButton() {
		super(new DoneAction());
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "none");
	}

	/**
	 * Singleton accessor.
	 * 
	 * @return The singleton <code>DoneButton</code>
	 */
	public static DoneButton getInstance() {
		if (instance == null) {
			instance = new DoneButton();
		}
		return instance;
	}
}
