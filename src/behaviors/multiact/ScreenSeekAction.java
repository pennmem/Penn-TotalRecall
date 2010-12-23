package behaviors.multiact;

import info.GUIConstants;
import info.SysInfo;

import java.awt.event.ActionEvent;

import components.MyFrame;
import components.waveform.WaveformDisplay;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

public class ScreenSeekAction extends IdentifiedMultiAction {

	public static enum Dir {FORWARD, BACKWARD}

	private Dir dir;
	
	public ScreenSeekAction(Dir dir) {
		super(dir);
		this.dir = dir;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		
		int shift = (int) (((double)WaveformDisplay.getInstance().getWidth() / (double)GUIConstants.zoomlessPixelsPerSecond) * 1000);
		shift -= shift/5;
		if(dir == Dir.BACKWARD) {
			shift *= -1;
		}
		
		long curFrame = CurAudio.getAudioProgress();
		long frameShift = CurAudio.getMaster().millisToFrames(shift);
		long naivePosition = curFrame + frameShift;
		long frameLength = CurAudio.getMaster().durationInFrames();

		long finalPosition = naivePosition;

		if(naivePosition < 0) {
			finalPosition = 0;
		}
		else if(naivePosition >= frameLength) {
			finalPosition = frameLength - 1;
		}
		if(SysInfo.sys.forceListen) {
			finalPosition = Math.min(finalPosition, CurAudio.getListener().getGreatestProgress());
		}

		CurAudio.setAudioProgressAndUpdateActions(finalPosition);
		CurAudio.getPlayer().queuePlayAt(finalPosition);
		MyFrame.getInstance().requestFocusInWindow();
	}
	
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(false);
			}
			else {
				boolean canSkipForward;
				if(SysInfo.sys.forceListen) {
					canSkipForward = CurAudio.getAudioProgress() < CurAudio.getListener().getGreatestProgress();
				}
				else {
					canSkipForward = true;
				}
				if(CurAudio.getAudioProgress() <= 0) {
					if(canSkipForward && (dir == Dir.FORWARD)) {
						setEnabled(true);
					}	
					else {
						setEnabled(false);
					}
				}
				else if(CurAudio.getAudioProgress() == CurAudio.getMaster().durationInFrames() - 1) {
					if(dir == Dir.FORWARD) {
						setEnabled(false);
					}	
					else {
						setEnabled(true);
					}
				}
				else {
					if(dir == Dir.FORWARD) {
						setEnabled(canSkipForward);
					}
					else {
						setEnabled(true);
					}
				}
			}
		}
		else {
			setEnabled(false);
		}
	}
}
