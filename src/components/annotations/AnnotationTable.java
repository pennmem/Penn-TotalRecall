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

import info.MyColors;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import behaviors.singleact.JumpToAnnotationAction;

/**
 * <code>JTable</code> that stores the annotations of the open audio file.
 * 
 * @author Yuvi Masory
 */
public class AnnotationTable extends JTable implements FocusListener {
	
	private static AnnotationTable instance;
	
	private static AnnotationTableModel model;
	
	private AnnotationTableCellRenderer render;

	private AnnotationTable() {
		model = new AnnotationTableModel();
		render = new AnnotationTableCellRenderer();
		JTableHeader header = getTableHeader();
		header.setReorderingAllowed(false);
		header.setResizingAllowed(true);
		header.setBorder(BorderFactory.createLineBorder(MyColors.annotationListHeaderBorderColor));
		setModel(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addMouseListener(new AnnotationTableMouseAdapter(this));
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return this;
			}
		});
		addFocusListener(this);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "jump to annotation");
		getActionMap().put("jump to annotation", new JumpToAnnotationAction());
		
		
	    InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "none");
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK), "none");
		
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>();
		forwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0, false));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>();
		backwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK, false));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK, false), "none");
	}

	@Override
	public boolean isFocusable() {
		return(super.isFocusable() && model.getRowCount() > 0);
	}

	@Override
	public AnnotationTableModel getModel() {
		return model;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight() {
        Component parent = getParent();
        if(parent instanceof javax.swing.JViewport) {
            return parent.getHeight() > getPreferredSize().height;
        }
        return false;
    }

	
	@Override
	public AnnotationTableCellRenderer getCellRenderer(int row, int col) {
		return render;
	}
	
	@Override
	public AnnotationTableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return render;
	}

	public void focusGained(FocusEvent e) {
		int anchor = getSelectionModel().getAnchorSelectionIndex();
		if(anchor >= 0) {
			changeSelection(anchor, 0, false, false);
			changeSelection(anchor, getModel().getColumnCount(), false, true);
		}
		else {
			changeSelection(0, 0, false, false);
			changeSelection(0, getModel().getColumnCount(), false, true);
		}
	}

	public void focusLost(FocusEvent e) {
		if(e.isTemporary() == false) {
			clearSelection();
		}
	}
	
	protected static AnnotationTable getInstance() {
		if(instance == null) {
			instance = new AnnotationTable();
		}
		return instance;
	}
	
	public static AnnotationTable getFocusTraversalReference() {
		return getInstance();
	}

	public static Annotation popSelectedAnnotation() {
		int[] rows = instance.getSelectedRows();
		if(rows.length == 1) {
			return model.getAnnotationAt(rows[0]);
		}
		else {
			return null;
		}
	}
}