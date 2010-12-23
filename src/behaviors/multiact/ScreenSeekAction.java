package behaviors.multiact;

import info.SysInfo;
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
					if(dir == Dir.BACKWARD) {
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
