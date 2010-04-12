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

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * The MVC "view" of a cell of the <code>AnnotationTable</code>.
 * 
 * @author Yuvi Masory
 */
public class AnnotationTableCellRenderer extends DefaultTableCellRenderer {

	protected static final DecimalFormat noDecimalsFormat = new DecimalFormat("0");
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object
			value, boolean isSelected,
			boolean hasFocus, int
			row, int column) {

		JLabel renderedLabel = (JLabel) super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		renderedLabel.setHorizontalAlignment(SwingConstants.LEADING);
		return renderedLabel;
	}
	
	@Override
	protected void setValue(Object value) {
		if(value != null) {
			setText((value instanceof Double) ? noDecimalsFormat.format(value) : value.toString());
		}
		else {
			setText("");
		}
	}
}
