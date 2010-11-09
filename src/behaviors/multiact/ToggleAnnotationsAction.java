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
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


import javax.swing.Timer;

import util.GiveMessage;

import components.MyFrame;
import components.MyMenu;
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
	
	
	/**
	 * Defines the toggling direction of a <code>ToggleAnnotationAction</code> instance.
	 */
	public static enum Direction {FORWARD, BACKWARD};
	public static enum Action {PAN_BETWEEN, JUMP_BETWEEN, PAN_TO_FINISH}; 

	private Direction myDir;
	private Action myAction;
	

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
	 * Performs the panning, moving the audio position to the next/previous annotation.
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
		Annotation ann = findAnnotation(myDir, CurAudio.getMaster().framesToMillis(CurAudio.getAudioProgress()));
		// This could be used to check if the last annotation has been reached 
		int numOfAnnotations = AnnotationDisplay.getNumAnnotations();
				
		if(ann == null) {
			System.err.println("It should not have been possible to call " + getClass().getName() + ". Could not find matching annotation");
		}
		else {
			
			final long approxFrame = CurAudio.getMaster().millisToFrames(ann.getTime());
			final long curFrame = CurAudio.getAudioProgress();
			final long maxProgress = CurAudio.getListener().getGreatestProgress();
			final long lastFrame = CurAudio.getMaster().durationInFrames();
			
			if(approxFrame < 0 || approxFrame > CurAudio.getMaster().durationInFrames() - 1) {
				GiveMessage.errorMessage("The annotation I am toggling to isn't in range.\nPlease check annotation file for errors."); 
				return;
			} 
			
			if(maxProgress > approxFrame || curFrame < maxProgress){
				// Action performed is jump from One annotation to another 
					myAction = Action.JUMP_BETWEEN;
			}else if(maxProgress < approxFrame){
					// Action performed is pan from One annotation to another 
					myAction = Action.PAN_BETWEEN;
			}
			
			
			// If current frame and Frame number of the Last Annotation are the same then you pan to finish 
			if (curFrame ==CurAudio.getMaster().millisToFrames( 
										AnnotationDisplay.getAnnotationsInOrder()
														[numOfAnnotations -1].getTime())
														){
				//Not sure if this work , have not tested 
				myAction = Action.PAN_TO_FINISH;
			}
			
			if(myAction == Action.JUMP_BETWEEN){
				CurAudio.setAudioProgressAndUpdateActions(approxFrame);
				CurAudio.getPlayer().queuePlayAt(approxFrame);
				return;
			}else if(myAction == Action.PAN_BETWEEN){
				final Timer	timer = new Timer(20,null);
				MyFrame frameCopy = MyFrame.getInstance();
				frameCopy.addKeyListener(new KeyListener(){
					
					public void keyReleased(KeyEvent e){
						
					}
					
					public void keyTyped(KeyEvent e){
						
					}
					
					public void keyPressed(KeyEvent e){
							timer.stop();
							MyMenu.updateActions();
							CurAudio.getListener().offerGreatestProgress(curFrame);
							CurAudio.getPlayer().queuePlayAt(curFrame);
							return;
					}
				});
				 
				timer.addActionListener(new ActionListener() {
					private long panFrame = curFrame;
					public void actionPerformed(ActionEvent evt) {
						MyMenu.disableActions();
						if(myDir == Direction.FORWARD){
							if (panFrame >= approxFrame) {
								timer.stop();
								System.out.println(lastFrame);
								System.out.println(curFrame);
								CurAudio.setAudioProgressAndUpdateActions(approxFrame);
								CurAudio.getPlayer().queuePlayAt(approxFrame);
								CurAudio.getListener().offerGreatestProgress(approxFrame);
								MyMenu.updateActions();
								return;
							}
						CurAudio.setAudioProgressWithoutUpdatingActions(panFrame);
						panFrame += 4000;
						}
					}
				});
			timer.start();
			}
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


