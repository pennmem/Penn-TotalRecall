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


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import control.CurAudio;

import behaviors.singleact.ContinueAnnotatingAction;





/**
 * <code>JPopupMenu</code> that presents user with actions for manipulating the <code>AudioFileList</code>.
 * 
 * Different options are presented depending on the file state and the file/s the menu is being launched on.
 * 
 * @author Yuvi Masory
 */
public class AudioFilePopupMenu extends JPopupMenu {

	/**
	 * Constructs a popup menu with options appropriate for the provided file.
	 * Possible options include marking the file incomplete, or removing it from the list.
	 * The popup menu will have the <code>file</code> parameter as its title, regardless of whether the LAF officially supports <code>JPopupMenu</code> titles.
	 * 
	 * @param file The <code>AudioFile</code> on whose behalf the menu is being offered
	 * @param index The index of <code>file</code> in its <code>AudioFileList</code>
	 */
	protected AudioFilePopupMenu(AudioFile file, final int index) {
		super();

		//most, if not all LAFs do not support JPopupMenu titles
		//to simulate a title we add a disabled JMenuItem
		JMenuItem fakeTitle = new JMenuItem(file.getName() + "...");
		fakeTitle.setEnabled(false);

		JMenuItem cont = new JMenuItem(
				new ContinueAnnotatingAction(file));
		if(file.isDone() == false) {
			cont.setEnabled(false);
		}
		JMenuItem del = new JMenuItem(
				new AbstractAction(){
					public void actionPerformed(ActionEvent e) {
						AudioFileList.getInstance().getModel().removeElementAt(index);
					}
				});
		del.setText("Remove from List");
		if(CurAudio.audioOpen()) {
			if(CurAudio.getCurrentAudioFileAbsolutePath().equals(file.getAbsolutePath())) {
				del.setEnabled(false);
			}
		}

		add(fakeTitle);
		addSeparator();
		add(cont);
		add(del);
	}
}
