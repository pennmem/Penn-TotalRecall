package components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import util.GUIUtils;

public class CalibrationFrame extends JFrame {
	
	private static CalibrationFrame instance;
	
	private CalibrationFrame() {
		setTitle("Calibration");
		setSize(600, 300);
		setLocation(GUIUtils.chooseLocation(this));
		
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setForeground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));		
		setContentPane(panel);

		JLabel label = new JLabel("Instructions");
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTrack(true);
		slider.setSnapToTicks(true);
		
		JButton jbOk = new JButton("OK");
		JButton jbCancel = new JButton("Cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(jbOk);
		buttonPanel.add(jbCancel);
		
		panel.add(label);		
		panel.add(Box.createVerticalGlue());
		panel.add(slider);
		panel.add(buttonPanel);
	}
	
	public static CalibrationFrame getInstance() {
		if(instance == null) {
			instance = new CalibrationFrame();
		}
		return instance;
	}
}
