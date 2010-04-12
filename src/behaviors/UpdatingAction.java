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

package behaviors;

import javax.swing.AbstractAction;


/**
 * An AbstractAction that processes updates in program state.
 * 
 * <p>All AbstracActions in this program will indirectly inherit this class when they inherit (directly or indirectly)
 * IdentifiedSingleAction or IdentifiedMultiAction.
 *
 * <p>Inheriting this class forces the writer of an action to decide if there are times when the action should
 * be disabled or have its name changed. For example, StopAction can disable itself if audio is not playing.
 *
 * <p>Note that the update function only affects actions that are bound to action components like buttons.
 * If you manually generate the event by calling <code>actionPerformed(ActionEvent)</code> then you must first verify
 * the action's preconditions are met. The action will succeed whether or not it was enabled.
 *
 * @author Yuvi Masory
 */
public abstract class UpdatingAction extends AbstractAction {

	/**
	 * Informs the Action that the program's global state has changed in such a way that the Action may now want to enable/disable itself, or change something else.
	 * This method is called on every IdentifiedAction after many state changes, e.g. audio opening, audio playing, first annotation made, etc.
	 * If your IdentifiedAction requires an update() call at a state change that doesn't currently set updates, add a MyMenu.updateActions() call after that event takes place.
	 * 
	 * <p>Update code must be FAST, since it runs on the event dispatch thread.
	 */
	public abstract void update();
}
