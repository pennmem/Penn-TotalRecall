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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Custom <code>TableModel</code> for storing annotations of the open audio file.
 * 
 * @author Yuvi Masory
 */
public class AnnotationTableModel implements TableModel {
	
	private HashSet<TableModelListener> listeners;
	
	private ArrayList<Annotation> sortedAnns;
	
	//editing the table layout (e.g., adding a new column, switching the order of two columns) involves more than changing the next three lines
	//some of the methods below make assumptions about the number of columns and the Annotation methods they hook up to
	//doing this in a perfectly programmed worled would involve storing an array of Method objects
	private static final int columnCount = 3;
	private static final Class<?>[] columnClasses = new Class<?>[] {Double.class, String.class, Integer.class};
	private static final String[] columnNames = new String[] {"Time (ms)", "Word", "Word #"};
	
	private static final String colErr = "column index out of range";
	private static final String rowErr = "row index out of range";
	private static final String stateErr = "inconsistency in internal column handling";
	
	protected AnnotationTableModel() {
		if(columnCount != columnClasses.length || columnCount != columnNames.length) {
			throw new IllegalStateException(stateErr);
		}
		listeners = new HashSet<TableModelListener>();
		sortedAnns = new ArrayList<Annotation>();
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getRowCount() {
		return sortedAnns.size();
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex > columnClasses.length || columnIndex < 0) {
			throw new IllegalArgumentException(colErr);
		}
		return columnClasses[columnIndex];
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public String getColumnName(int columnIndex) {
		if(columnIndex > columnClasses.length || columnIndex < 0) {
			throw new IllegalArgumentException(colErr);
		}
		return columnNames[columnIndex];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex < 0 || rowIndex > sortedAnns.size()) {
			throw new IllegalArgumentException(rowErr);
		}
		if(columnIndex > columnCount - 1) {
			throw new IllegalArgumentException(colErr);
		}
		Annotation ann = sortedAnns.get(rowIndex);
		if(columnIndex == 0) {
			return ann.getTime();
		}
		if(columnIndex == 1) {
			return ann.getText();
		}
		if(columnIndex == 2) {
			return ann.getWordNum();
		}
		throw new IllegalStateException(stateErr);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException("setting table values not supported, use add/remove annotation methods");
	}
	
	protected Annotation getAnnotationAt(int rowIndex) {
		if(rowIndex < 0 || rowIndex > sortedAnns.size()) {
			throw new IllegalArgumentException(rowErr);
		}
		return sortedAnns.get(rowIndex);
	}

	protected Annotation[] toArray() {
		return sortedAnns.toArray(new Annotation[sortedAnns.size()]);
	}
	
	
	//adding duplicates is prevented by annotation-over deleting first annotation, performed in annotateaction
	protected void addElement(Annotation ann) {
		sortedAnns.add(ann);
		//then remove batch adding option below
		Collections.sort(sortedAnns);
		for(TableModelListener tml: listeners) {
			tml.tableChanged(new TableModelEvent(this));
		}
	}
	
	//duplicate adds are possible with this method
	protected void addElements(Iterable<Annotation> batch) {
		for(Annotation el: batch) {
			sortedAnns.add(el);
		}
		Collections.sort(sortedAnns);
		for(TableModelListener tml: listeners) {
			tml.tableChanged(new TableModelEvent(this));
		}
	}

	protected void removeElementAt(int index) {
		if(index < 0 || index > sortedAnns.size()) {
			throw new IllegalArgumentException(rowErr);
		}
		sortedAnns.remove(index);
		for(TableModelListener tml: listeners) {
			tml.tableChanged(new TableModelEvent(this, Math.min(index, sortedAnns.size()), sortedAnns.size()));
		}
	}

	protected void removeAllElements() {
		sortedAnns.clear();
	}

	public int size() {
		return sortedAnns.size();
	}
}
