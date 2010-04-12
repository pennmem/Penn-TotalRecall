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

package components.annotations;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import behaviors.singleact.DeleteAnnotationAction;


/**
 * Popup menu launched by right clicking on annotations. 
 * 
 * @author Yuvi Masory
 */
public class AnnotationTablePopupMenu extends JPopupMenu {

	protected AnnotationTablePopupMenu(Annotation annToDelete, int rowIndex, AnnotationTable table, String rowRepr) {
		super();
		JMenuItem fakeTitle = new JMenuItem(rowRepr + "...");
		fakeTitle.setEnabled(false);
		JMenuItem del = new JMenuItem(
				new DeleteAnnotationAction(rowIndex));
		add(fakeTitle);
		addSeparator();
		add(del);
	}
}
