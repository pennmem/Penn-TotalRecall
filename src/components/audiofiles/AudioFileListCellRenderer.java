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

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import control.CurAudio;




/**
 * A <code>DefaultListCellRenderer</code> whose appearance is determined by whether the {@link components.audiofiles.AudioFile} it is displaying is done being annotated or not.
 * 
 * <code>AudioFiles</code> that are done are displayed using a disabled <code>JComponent</code> with the program's strike-through <code>Font</code>.
 * <code>AudioFiles</code> that are incomplete are displayed using an enabled <code>JComponent</code> with the program's bold <code>Font</code>.
 * 
 * @author Yuvi Masory
 */
public class AudioFileListCellRenderer extends DefaultListCellRenderer {
	
	private final Font strikethrough;
	private final Font bold;
	private final Font plain;
	
	@SuppressWarnings("unchecked")
	public AudioFileListCellRenderer() {
		plain = getFont();
		Map attributes = plain.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		strikethrough = new Font(attributes);
		bold = plain.deriveFont(Font.BOLD);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if(((AudioFile)value).isDone()) {
			setEnabled(false);
			setFont(strikethrough);
		}
		else {
			if(CurAudio.audioOpen()) {
				if(((AudioFile)value).getAbsolutePath().equals(CurAudio.getCurrentAudioFileAbsolutePath())) {
					setFont(bold);
				}
				else {
					setFont(plain);
				}
			}
			else {
				setFont(plain);
			}
		}
		return this;
	}
}
