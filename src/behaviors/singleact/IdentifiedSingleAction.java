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

package behaviors.singleact;

import info.ActionNames;
import info.KeyBindings;

import behaviors.UpdatingAction;

import components.MyMenu;

/**
 * An UpdatingAction that associates itself with a name, tool tip, and key binding.
 * 
 * <p>All AbstracActions in this program should inherit IdentifiedSingleAction or IdentifiedMultiAction, either directly or indirectly.
 *
 * <p>Inheriting this class allows for the central storage of names, tool tips, and key bindings in the classes
 * Info.ActionNames and Info.KeyBindings.
 *
 * @author Yuvi Masory
 */
public abstract class IdentifiedSingleAction extends UpdatingAction {

	/**
	 * Creates the action, automatically associating it with the name, tool tip, and accelerator key stored in Info.ActionName and Info.KeyBindings.
	 */
	public IdentifiedSingleAction() {
		putValue(NAME, ActionNames.lookupName(this));
		putValue(SHORT_DESCRIPTION, ActionNames.lookupToolTip(this));
		putValue(ACCELERATOR_KEY, KeyBindings.lookupBinding(this));
		MyMenu.registerAction(this);
	}
}
