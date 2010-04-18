package components;

import javax.swing.JFrame;

public class CalibrationFrame extends JFrame {
	
	private static CalibrationFrame instance;
	
	private CalibrationFrame() {
		setTitle("Calibration");
		setSize(300, 300);
	}
	
	public static CalibrationFrame getInstance() {
		if(instance == null) {
			instance = new CalibrationFrame();
		}
		return instance;
	}
}
