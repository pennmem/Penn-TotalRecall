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

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * A <code>DefaultListCellRenderer</code> whose appearance is determined by whether the {@link components.wordpool.WordpoolList} it is displaying is done being annotated or not.
 * 
 * <code>WordpoolWords</code> from the audio file's lst file are displayed using the program's bold <code>Font</code>.
 * <code>WordpoolWords</code> from the general wordpool list are displayed using the program's plain <code>Font</code>.
 * 
 * @author Yuvi Masory
 */
public class WordpoolListCellRenderer extends DefaultListCellRenderer {

	private final Font boldFont;
	
	public WordpoolListCellRenderer() {
		boldFont = getFont().deriveFont(Font.BOLD);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if(((WordpoolWord)value).isLst()) {
			setFont(boldFont);
		}
		return this;
	}
}
