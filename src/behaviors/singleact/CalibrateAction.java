package behaviors.singleact;

import java.awt.event.ActionEvent;

import components.CalibrationFrame;

public class CalibrateAction extends IdentifiedSingleAction {

	public void actionPerformed(ActionEvent e) {
			CalibrationFrame.getInstance().setVisible(true);
	}	
	
	@Override
	public void update() {
	}
}
