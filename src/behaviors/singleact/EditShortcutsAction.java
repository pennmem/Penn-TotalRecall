package behaviors.singleact;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import util.GUIUtils;

import components.ShortcutFrame;

public class EditShortcutsAction extends IdentifiedSingleAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		JFrame frame = ShortcutFrame.instance;
		frame.setLocation(GUIUtils.chooseLocation(frame));
		frame.setVisible(true);
	}
	
	@Override
	public void update() {}
}
;