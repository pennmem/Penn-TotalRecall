package components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import util.GUIUtils;

public class CalibrationFrame extends JFrame {
	
	private static CalibrationFrame instance;
	
	private CalibrationFrame() {
		setTitle("Calibration");
		setSize(300, 300);
		setLocation(GUIUtils.chooseLocation(this));
		
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setForeground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));		
		setContentPane(panel);

		JLabel label = new JLabel("Instructions");
		
		JSpinner spinner = new JSpinner();
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		SpinnerNumberModel model = new SpinnerNumberModel();
		model.setStepSize(1);
		model.setMinimum(0);
		model.setMaximum(100);
		spinner.setModel(model);
		
		JButton jbOk = new JButton("OK");
		JButton jbCancel = new JButton("Cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(jbOk);
		buttonPanel.add(jbCancel);
		
		panel.add(label);		
		panel.add(Box.createVerticalGlue());
		panel.add(spinner);
		panel.add(buttonPanel);
	}
	
	public static CalibrationFrame getInstance() {
		if(instance == null) {
			instance = new CalibrationFrame();
		}
		return instance;
	}
}
