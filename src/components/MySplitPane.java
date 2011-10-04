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

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import behaviors.multiact.AnnotateAction;
import behaviors.singleact.DeleteSelectedAnnotationAction;
import behaviors.singleact.PlayPauseAction;

import components.waveform.WaveformDisplay;

import control.XActionManager;

/**
 * A custom <code>JSplitPane</code> that serves as the content pane to <code>MyFrame</code>.
 * Splits the program's interface between the waveform area above, and the control area below. 
 * 
 * @author Yuvi Masory
 */
public class MySplitPane extends JSplitPane {
	
	private static MySplitPane instance;

	/**
	 * Creates a new instance of the component, initializing internal components, key bindings, listeners, and various aspects of appearance.
	 */
	private MySplitPane() {
		super(JSplitPane.VERTICAL_SPLIT, WaveformDisplay.getInstance(), ControlPanel.getInstance());		
        
        setOneTouchExpandable(false); //we don't want to make it easy to totally lost view of one of the components, both are essential
        setContinuousLayout(true); //in general we want this true when audio is open, and closed otherwise, due to expense of generated repaints
        setResizeWeight(0.5);
        
		//overrides MySplitPane key bindings for the benefit of SeekAction's key bindings and to prevent accidental movement of the divider
	    InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "none");
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "none");
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
	    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
	    

        DeleteSelectedAnnotationAction deleteAction = new DeleteSelectedAnnotationAction();
        InputMap deleteActionMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        String DELETE_ACTION_KEY = "delete action";
        deleteActionMap.put(XActionManager.lookup(deleteAction, null), DELETE_ACTION_KEY);
	    getActionMap().put(DELETE_ACTION_KEY, deleteAction);
	    XActionManager.registerInputMap(deleteAction, null, DELETE_ACTION_KEY, deleteActionMap);

	    getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
                                       0,
                                       false), "play");

	    getActionMap().put("play", new PlayPauseAction(false));

        AnnotateAction intrusionAction = new AnnotateAction(AnnotateAction.Mode.INTRUSION);
		InputMap intrusionInputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		String ANNOTATE_INTRUSION_KEY = "annotate intrusion";
		Enum<?> intrusionEnum = AnnotateAction.Mode.INTRUSION;
		KeyStroke intrusionKey = XActionManager.lookup(intrusionAction, intrusionEnum);
		intrusionInputMap.put(intrusionKey, ANNOTATE_INTRUSION_KEY);
		getActionMap().put(ANNOTATE_INTRUSION_KEY, intrusionAction);
		XActionManager.registerInputMap(intrusionAction, intrusionEnum, ANNOTATE_INTRUSION_KEY, intrusionInputMap);
		

		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0, false), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, false), "none");
	}
	
	/**
	 * Singleton accessor.
	 * 
	 * @return The singleton <code>MySplitPane</code>
	 */
    public static MySplitPane getInstance() {
        if (instance == null) {
            instance = new MySplitPane();
        }
        return instance;
    }
}
