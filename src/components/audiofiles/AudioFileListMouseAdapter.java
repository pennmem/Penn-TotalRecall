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

package components.audiofiles;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseListener for the AudioFileList, used for launching popup context menus, and switching audio file via double-click.
 * 
 * @author Yuvi Masory
 */
public class AudioFileListMouseAdapter extends MouseAdapter {

	private AudioFileList list;

	/**
	 * Creates a mouse adapter that can act on the <code>AudioFileList</code> on whose behalf it is listening.
	 * 
	 * @param list The associated <code>AudioFileList</code> being listened to.
	 */
	protected AudioFileListMouseAdapter(AudioFileList list) {
		this.list = list;
	}

	/**
	 * Double clicks are used to switch the current audio file to the <code>AudioFile</code> clicked on, if that file isn't done.
	 * 
	 * @param e The <code>MouseEvent</code> provided by the action trigger
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		AudioFile file = getAssociatedFile(e);
		if(file == null) {				
			return; // event not on a File
		}
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			AudioFileDisplay.askToSwitchFile(file);
		}
	}

	/**
	 * Some platforms launch context menu on press, and some on release, so control is passed to {@link #evaluatePopup(MouseEvent)} for further consideration.
	 * 
	 * @param e The <code>MouseEvent</code> provided by the action trigger
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		evaluatePopup(e);
	}

	/**
	 * Some platforms launch context menu on press, and some on release, so control is passed to {@link #evaluatePopup(MouseEvent)} for further consideration.
	 * 
	 * @param e The <code>MouseEvent</code> provided by the action trigger
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		evaluatePopup(e);
	}

	/**
	 * Evaluates whether the mouse event is a popup trigger on this platform, and launches a popup context menu if appropriate.
	 * 
	 * @param e
	 */
	public void evaluatePopup(MouseEvent e) {
		if(e.isPopupTrigger()) {
			AudioFile file = getAssociatedFile(e);
			if(file == null) {				
				return; //event not on a File
			}
			AudioFilePopupMenu pop = new AudioFilePopupMenu(file, list.locationToIndex(e.getPoint()));
			pop.show(e.getComponent(), e.getX(), e.getY());			
		}
	}

	/**
	 * Utility method for determining which <code>AudioFile</code> received the event.
	 * 
	 * @param e The <code>MouseEvent</code> provided by the action trigger.
	 * @return The <code>AudioFile</code> that received the mouse event, or <code>null</code> if the event was not on an <code>AudioFile</code>.
	 */
	private AudioFile getAssociatedFile(MouseEvent e) {
		int index = list.locationToIndex(e.getPoint());
		if(index < 0) {
			return null; //event not on a File
		}
		AudioFile file = list.getModel().getElementAt(index);
		return file;
	}
}
