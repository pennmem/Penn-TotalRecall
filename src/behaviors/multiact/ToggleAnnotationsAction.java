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

package behaviors.multiact;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import util.GiveMessage;

import components.MyFrame;
import components.annotations.Annotation;
import components.annotations.AnnotationDisplay;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Tries to move the audio position to the next/previous {@link components.annotations.Annotation}, relative to current audio position.
 * 
 * Afterward sends update to all <code>UpdatingActions</code>.
 * 
 * @author Yuvi Masory
 */
public class ToggleAnnotationsAction extends IdentifiedMultiAction {
	
	//This status indicates if the Toggle action has been completed
	int status =0;
	
	/**
	 * Defines the toggling direction of a <code>ToggleAnnotationAction</code> instance.
	 */
	public static enum Direction {FORWARD, BACKWARD};
	

	private Direction myDir;

	/**
	 * Create an action with the direction presets given by the provided <code>Enum</code>.
	 * 
	 * @param dir An <code>Enum</code> defined in this class which maps to the correct direction of toggling
	 * @see behaviors.multiact.IdentifiedMultiAction#IdentifiedMultiAction(Enum)
	 */
	public ToggleAnnotationsAction(Direction dir) {
		super(dir);
		this.myDir = dir;
	}

	/**
	 * Performs the toggling, moving the audio position to the next/previous annotation.
	 * 
	 * Afterward sends an update to all <code>UpdatingActions<code>.
	 *
	 * Since the waveform display autonomously decides when to paint itself, this action may not result in an instant visual change.
	 * 
	 * <p>Prints warnings if an appropriate Annotation could not be found, despite the action being enabled.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger
	 */
	public void actionPerformed(ActionEvent e) {
		//Reset Status to 0 
		status =0;
	
		
		Annotation ann = findAnnotation(myDir, CurAudio.getMaster().framesToMillis(CurAudio.getAudioProgress()));
		if(ann == null) {
			System.err.println("It should not have been possible to call " + getClass().getName() + ". Could not find matching annotation");
		}
		else {
			final long approxFrame = CurAudio.getMaster().millisToFrames(ann.getTime());
			final long curFrame = CurAudio.getAudioProgress();
			if(approxFrame < 0 || approxFrame > CurAudio.getMaster().durationInFrames() - 1) {
				GiveMessage.errorMessage("The annotation I am toggling to isn't in range.\nPlease check annotation file for errors."); 
				return;
			}
			final Timer	timer = new Timer(20,null); 
			timer.addActionListener(new ActionListener() {
		 	private long panFrame = curFrame;
		 	private long endFrame = approxFrame;
		 	public void actionPerformed(ActionEvent evt) {
	 		if(myDir == Direction.FORWARD){
	 			if (panFrame >= endFrame) {
		 			timer.stop();
		 			CurAudio.setAudioProgressAndUpdateActions(endFrame);
		 			CurAudio.getPlayer().queuePlayAt(endFrame);
			 		return;
			 		}
			 	CurAudio.setAudioProgressWithoutUpdatingActions(panFrame);
			 	panFrame += 4000;
	            }
		 	else if(myDir == Direction.BACKWARD){
	 			if (panFrame <= endFrame) {
	 				timer.stop();
	 				CurAudio.setAudioProgressAndUpdateActions(endFrame);
		 			CurAudio.getPlayer().queuePlayAt(endFrame);
	 				return;
	 			}
	 			CurAudio.setAudioProgressWithoutUpdatingActions(panFrame);
	 			panFrame -= 4000;
           		}
		 	}
		});
        timer.start();
		}
		MyFrame.getInstance().requestFocusInWindow();
	}
	
	
	/**
	 * A forward (backward) <code>ToggleAnnotationsAction</code> should be enabled only when audio is open, not playing, and when there is an annotation following (preceding) the current position.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(false);
			}
			else {
				double curTimeMillis = CurAudio.getMaster().framesToMillis(CurAudio.getAudioProgress());
				if(findAnnotation(myDir, curTimeMillis) != null) {
					setEnabled(true);
				}
				else {
					setEnabled(false);
				}
			}
		}
		else {
			setEnabled(false);
		}
	}

	/**
	 * Finds the next/previous <code>Annotation</code> relative to a certain audio position in milliseconds.
	 * 
	 * @param dir The direction of movement
	 * @param curTimeMillis The present time in milliseconds
	 * 
	 * @return In principle, the <code>Annotation</code> after/before <code>curTimeMillis</code>
	 */
	private Annotation findAnnotation(Direction dir, double curTimeMillis) {
		Annotation[] anns = AnnotationDisplay.getAnnotationsInOrder();
		if(myDir == Direction.FORWARD) {
			for(int i = 0; i < anns.length; i++) {
				if(anns[i].getTime() - curTimeMillis > 1) {
					return anns[i];
				}
			}
		}
		else {
			for(int i = anns.length - 1; i >= 0; i--) {
				if(curTimeMillis - anns[i].getTime() > 1) {
					return anns[i];
				}
			}
		}
		return null;
	}
}


