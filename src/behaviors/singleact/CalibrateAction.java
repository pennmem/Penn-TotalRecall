package behaviors.singleact;

import java.awt.event.ActionEvent;

import components.CalibrationFrame;

public class CalibrateAction extends IdentifiedSingleAction {

	public void actionPerformed(ActionEvent e) {
		CalibrationFrame frame = CalibrationFrame.getInstance();
		frame.setVisible(true);		
	}	
	
	@Override
	public void update() {
	}
}
