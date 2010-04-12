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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;

import util.LoopIterator;

import components.annotations.AnnotationTable;
import components.audiofiles.AudioFileList;
import components.wordpool.WordpoolList;
import components.wordpool.WordpoolTextField;

/**
 * A custom <code>FocusTraversalPolicy</code> for this program, including documentation on general focus guidelines of the program.
 * 
 * <p>To speed up the annotation process we generally want users to be able to use the program using only the keyboard.
 * One exception is the adding of audio files. For that you need to drag files onto the program with a mouse, or use
 * the mouse on the file chooser.
 * 
 * <p>Any component in <code>MyFrame</code> that can be clicked by the user MUST handle focus passing.
 * The simplest way to do this is to add an anonymous mouse listener to a clickable component that calls
 * {@link javax.swing.JComponent#requestFocusInWindow()} on whatever component it wants to pass focus to.
 * Focus should always be in one of (1) <code>MyFrame</code>, (2) <code>VolumeSliderDisplay.VolumeSlider</code>,
 * (3) <code>AudioFileList</code>, (4) <code>WordpoolTextField</code>, (5) <code>WordpoolList</code>, (6) <code>AnnotationTable</code>.
 * Other <code>JComponents</code> should choose the above components to pass focus to when clicked on.
 * For example, the <code>ControlPanel</code> gives focus to <code>MyFrame</code>, and the mute button gives focus to the volume slider.
 * 
 * <p>Focusable components are traversed in the order given above, looping back from the last component to the first.
 * 
 * <p>Please keep the spreadsheet in /dev updated with changes to the focus subsystem.
 * 
 * @author Yuvi Masory
 *
 */
public class MyFocusTraversalPolicy extends FocusTraversalPolicy {
	
	private static final String genericFailureMessage = 
		"can't find a focus-appropriate component to give focus to";

	//these are components that can take focus, in the order of focus traversal desired
	//must have at least one element to avoid ArrayIndexOutOfBoundsException
	private static final Component[] focusLoop = new Component[]{
		MyFrame.getInstance(),
		AudioFileList.getFocusTraversalReference(),
		WordpoolTextField.getFocusTraversalReference(),
		WordpoolList.getFocusTraversalReference(),
		AnnotationTable.getFocusTraversalReference(),
		DoneButton.getInstance()
	};

	/**
	 * Returns the next component in the focus traversal loop.
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponentAfter(Container aContainer, Component aComponent) {
		return getNextComponent(aComponent, true);
	}

	/**
	 * Returns the previous component in the focus traversal loop.
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponentBefore(Container aContainer, Component aComponent) {
		return getNextComponent(aComponent, false);
	}

	/**
	 * Returns the first component in the focus traversal list.
	 * {@inheritDoc}
	 */
	@Override
	public Component getDefaultComponent(Container aContainer) {
		return focusLoop[0];
	}

	/**
	 * Returns the first component in the focus traversal list.
	 * {@inheritDoc}
	 */
	@Override
	public Component getInitialComponent(Window window) {
		return focusLoop[0];
	}

	/**
	 * Returns the first component in the focus traversal list.
	 * {@inheritDoc}
	 */
	@Override
	public Component getFirstComponent(Container aContainer) {
		return focusLoop[0];
	}

	/**
	 * Returns the last component in the focus traversal list.
	 * {@inheritDoc}
	 */
	@Override
	public Component getLastComponent(Container aContainer) {
		return focusLoop[focusLoop.length - 1];
	}
	
	/**
	 * Handles the job of finding the next/previous component in the loop by using a {@link util.LoopIterator}.
	 * Makes sure that the next component in the focus traversal cycle is actually eligible for focus (i.e., enabled,
	 * visible, focusable).
	 * 
	 * @param aComponent The base component whose successor/predecessor is to be found
	 * @param forward <code>true</code> iff the direction of traversal is forward
	 * @return The next focus-eligible component in the provided direction
	 */
	private Component getNextComponent(Component aComponent, boolean forward) {
		int componentIndex = -1;
		for(int i = 0; i < focusLoop.length; i++) {
			Component fc = focusLoop[i];
			if(fc != aComponent) {
				continue;
			}
			else {
				componentIndex = i;
				break;
			}
		}
		if(componentIndex < 0) {
			System.err.println("can't find  the next focus component because I don't recognize the current one: " + aComponent);			
		}
		LoopIterator<Component> li = new LoopIterator<Component>(focusLoop, componentIndex, forward);
		if(li.hasNext()) {
			li.next();
			while(li.hasNext()) {
				Component c = li.next();
				//these are the three conditions for a component to be eligible for focus
				if(c.isEnabled() && c.isVisible() && c.isFocusable()) {
					return c;
				}
			}
			System.err.println(genericFailureMessage);
			return null;
		}
		else {
			System.err.println(genericFailureMessage);
			return null;
		}
	}
}
