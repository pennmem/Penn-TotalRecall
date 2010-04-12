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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

import behaviors.singleact.JumpToAnnotationAction;

/**
 * Mouse adapter for the <code>AnnotationTable</code>.
 * 
 * @author Yuvi Masory
 */
public class AnnotationTableMouseAdapter extends MouseAdapter {
	
	private AnnotationTable table;
	
	protected AnnotationTableMouseAdapter(AnnotationTable table){
		this.table = table;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			JumpToAnnotationAction jumpAct = new JumpToAnnotationAction();
			if((CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) == false) {
				//we are manually generating the event, so we must ourselves check the conditions
				jumpAct.actionPerformed(new ActionEvent(AnnotationTable.getInstance(), ActionEvent.ACTION_PERFORMED, null));
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		evaluatePopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		evaluatePopup(e);
	}
	
	public void evaluatePopup(MouseEvent e) {
		if(e.isPopupTrigger()) {
			int rIndex = table.rowAtPoint(e.getPoint());
			int cIndex = table.columnAtPoint(e.getPoint());
			if(rIndex < 0 || cIndex < 0) {
				return; // event not on an entry
			}
			String first = AnnotationTableCellRenderer.noDecimalsFormat.format(table.getValueAt(rIndex, 0));
			String second = table.getValueAt(rIndex, 1).toString();
			String third = table.getValueAt(rIndex, 2).toString();
			String rowRepr = first + " " + second + " " + third;
			Annotation annToDelete = AnnotationDisplay.getAnnotationsInOrder()[rIndex];
			AnnotationTablePopupMenu pop = new AnnotationTablePopupMenu(annToDelete, rIndex, table, rowRepr);
			pop.show(e.getComponent(), e.getX(), e.getY());		
		}
	}
}
