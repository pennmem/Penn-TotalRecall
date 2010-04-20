package components;

import info.Constants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import util.GUIUtils;
import util.GiveMessage;
import edu.upenn.psych.memory.nativestatelessplayer.NativeStatelessPlayer;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

public class CalibrationFrame extends JFrame {
	
	private PrecisionPlayer player;
	private boolean audioError = false;
	
	private static CalibrationFrame instance;
	
	private CalibrationFrame() {
		setSize(600, 300);
		setTitle("Audio Calibration");
		setLocation(GUIUtils.chooseLocation(this));
		
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setForeground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));		
		setContentPane(panel);
		
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
		textPanel.add(Box.createHorizontalGlue());
		textPanel.add(new JLabel(getInstructions()));
		textPanel.add(Box.createHorizontalGlue());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton playButton = new JButton();
		
		String tmpDirPath = System.getProperty("java.io.tmpdir");
		String tmpFileName = Long.toString(System.nanoTime()) + "_penntotalrecall.wav";
		File extractedBeep = new File(tmpDirPath, tmpFileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(extractedBeep);
		} 
		catch (IOException e) {
			e.printStackTrace();
			notifyFailure();
			return;
		}
		InputStream in = this.getClass().getResourceAsStream("/beep200-300ms.wav");
		if(in == null) {
			System.err.println("could not find resource");
			notifyFailure();
			return;
		}
		try {
			byte[] buffer = new byte[4096];
			int numRead = 0;
			while((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
			notifyFailure();
			return;
		}
		finally {
			if(out != null) {
				try {
					out.close();
				}
				catch(IOException e) {}
			}
			if(in != null) {
				try {
					in.close();
				}
				catch(IOException e) {}
			}
		}
		
		
		System.out.println("extracting beep to: " + extractedBeep.getAbsolutePath());
		
		try {
			player = new NativeStatelessPlayer();
			player.open(extractedBeep.getAbsolutePath());
		}
		catch(Throwable t) {
			audioError = true;
			t.printStackTrace();
		}
		playButton.setAction(new PlaySampleAction());
		buttonPanel.add(playButton);
		
		
		JPanel sliderPanel = new JPanel();
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTrack(true);
		slider.setSnapToTicks(true);
		sliderPanel.add(slider);
		sliderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
		sliderPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, (int)slider.getPreferredSize().getHeight() + 20));
				
		panel.add(textPanel);
		panel.add(Box.createVerticalGlue());
		panel.add(buttonPanel);
		panel.add(sliderPanel);
	}
	
	private void notifyFailure() {
		GiveMessage.errorMessage("Error initializing audio calibration system.");
	}

	private String getInstructions() {
		StringBuffer buff = new StringBuffer();
		buff.append("<html>");
		buff.append("<p>To calibrate your audio system, please move the slider to the smallest value for which you hear a sound when you hit the Play button.</p>");
		buff.append("<br>");
		buff.append("<p>For example, if you hear nothing at all when the slider is on 5, but hear a tiny blip when the slider is on 6, choose 6.</p>");		
		buff.append("</html>");
		return buff.toString();
	}
	
	public static CalibrationFrame getInstance() {
		if(instance == null) {
			instance = new CalibrationFrame();
		}
		return instance;
	}
	
	private class PlaySampleAction extends AbstractAction {
		
		public PlaySampleAction() {
			putValue(NAME, "Play");
		}
		
		public void actionPerformed(ActionEvent e) {
			if(audioError != true) {
				player.playShortInterval(0, 44100);
			}
			else {
				GiveMessage.errorMessage("Cannot load audio system.\nYou may need to reinstall " + Constants.programName + ".");
				CalibrationFrame.this.toFront();
			}
		}
	}
}
