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

import info.GUIConstants;
import info.MyShapes;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import components.MyFrame;

/**
 * A custom interface component for displaying committed annotations to the user.
 * 
 * @author Yuvi Masory
 */
public class AnnotationDisplay extends JScrollPane {
	
	private static final String title = "Annotations";
	
	private static AnnotationDisplay instance;
	private static AnnotationTable table;

	/**
	 * Creates a new instance of the component, initializing internal components, key bindings, listeners, 
	 * and various aspects of appearance.
	 */
	private AnnotationDisplay() {		
		table = AnnotationTable.getInstance();
		getViewport().setView(table);
		setPreferredSize(GUIConstants.annotationDisplayDimension);
		setMaximumSize(GUIConstants.annotationDisplayDimension);
		
		setBorder(MyShapes.createMyUnfocusedTitledBorder(title));
		
		//since AnnotationDisplay is a clickable area, we must write focus handling code for the event it is clicked on
		//passes focus to the table if it is focusable (not empty), otherwise giving focus to the frame
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if(table.isFocusable()) {
					table.requestFocusInWindow();
				}
				else {
					MyFrame.getInstance().requestFocusInWindow();
				}
			}
		});
		
		//overrides JScrollPane key bindings for the benefit of SeekAction's key bindings
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "none");
	}
	
	
	public static Annotation[] getAnnotationsInOrder() {
		return table.getModel().toArray();
	}
	
	public static void addAnnotation(Annotation ann) {
		if(ann == null) {
			throw new IllegalArgumentException("annotation/s cannot be null");
		}
		table.getModel().addElement(ann);
	}
	
	public static void addAnnotations(Iterable<Annotation> anns) {
		if(anns == null) {
			throw new IllegalArgumentException("annotations cannot be null");
		}
		table.getModel().addElements(anns);
	}
	
	public static void removeAnnotation(int rowIndex) {
		table.getModel().removeElementAt(rowIndex);
	}
	
	public static void removeAllAnnotations() {
		table.getModel().removeAllElements();
	}

	public static AnnotationDisplay getInstance() {
		if (instance == null) {
			instance = new AnnotationDisplay();
		}
		return instance;
	}


	public static int getNumAnnotations() {
		return table.getModel().size();
	}
}
