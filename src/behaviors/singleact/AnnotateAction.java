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

package behaviors.singleact;

import info.Constants;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import util.GiveMessage;
import util.OSPath;
import behaviors.UpdatingAction;

import components.MyFrame;
import components.MyMenu;
import components.annotations.Annotation;
import components.annotations.AnnotationDisplay;
import components.annotations.AnnotationFileParser;
import components.waveform.WaveformDisplay;
import components.wordpool.WordpoolDisplay;
import components.wordpool.WordpoolWord;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * Commits a user's annotation, updating the annotation file and program window as appropriate.
 * 
 * @author Yuvi Masory
 */
public class AnnotateAction extends IdentifiedSingleAction {

	private boolean isIntrusion;

	/**
	 * Create an <code>Action</code> corresponding to an intrusion or a normal annotation.
	 * 
	 * @param isIntrusion Whether the annotations committed by this <code>Action</code> are intrusions
	 */
	public AnnotateAction(boolean isIntrusion) {
		this.isIntrusion = isIntrusion;
	}
	
	private String obfuscate(String in) {
		return in;
	}

	/**
	 * Performs the <code>AnnotationAction</code> by appending the word in the text field to the temporary annotations file.
	 * 
	 * @param e The <code>ActionEvent</code> provided by the trigger.
	 */
	@Override	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		//do nothing if no audio file is open
		if(CurAudio.audioOpen() == false) { 
			WordpoolDisplay.clearText();
			return;
		}
		
		//retrieve time associated with annotation
		double time = CurAudio.getMaster().framesToMillis(CurAudio.getAudioProgress());
		
		//retrieve text associated with annotation, possibly the intrusion string
		String text = WordpoolDisplay.getFieldText(); 
		if(text.length() == 0) {
			if(isIntrusion) {
				text = Constants.intrusionSoundString;
			}
			else {
				return;
			}
		}
		
		//find whether the text matches a wordpool entry, so we can find the wordpool number of the annotation text
		WordpoolWord match = WordpoolDisplay.findMatchingWordpooWord(text);
		if(match == null) {
			if(isIntrusion == false) { //words not from the wordpool must be marked as intrusions
				return;
			}
			match = new WordpoolWord(text, -1);
		}


		//append the new annotation to the end of the temporary annotation file
		String curFileName = CurAudio.getCurrentAudioFileAbsolutePath();
		File oFile = new File(OSPath.basename(curFileName) + "." + Constants.temporaryAnnotationFileExtension);		

		if(oFile.exists() == false) {		
			try {
				oFile.createNewFile();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
				GiveMessage.errorMessage("Could not create " + Constants.temporaryAnnotationFileExtension + " file.");
			}		
		}
		if(oFile.exists()) {
			//check for header
			try {
				if(AnnotationFileParser.headerExists(oFile) == false) {

					String annotatorName = MyMenu.getAnnotator();
					if(annotatorName == null) {
						annotatorName = GiveMessage.inputMessage("Please enter your name:");
						if(annotatorName == null || annotatorName.equals("")) {
							GiveMessage.errorMessage("Cannot commit annotation without name.");
							return;
						}
					}
					MyMenu.setAnnotator(annotatorName);

					AnnotationFileParser.prependHeader(oFile, annotatorName);
				}

				if(UpdatingAction.getStamps().size() > 0) {
					ArrayList<ArrayList<Long>> spans = new ArrayList<ArrayList<Long>>();
					
					Long[] stamps = UpdatingAction.getStamps().toArray(new Long[] {});
					Arrays.sort(stamps);

					long start = 0L;
					long end = 0L;
					for(long stamp: stamps) {
						if(stamp - end > 15000) {
							if(start > 0 && end > start) {
								ArrayList<Long> nSpan = new ArrayList<Long>();
								nSpan.add(start);
								nSpan.add(end);
								spans.add(nSpan);
							}
							start = stamp;
							end = stamp;
						}
						else {
							end = stamp;
						}
					}
					if(start > 0 && end > start) {
						ArrayList<Long> nSpan = new ArrayList<Long>();
						nSpan.add(start);
						nSpan.add(end);
						spans.add(nSpan);
					}
					
					UpdatingAction.getStamps().clear();
					
					for(ArrayList<Long> span: spans) {
						String toWrite = "Span: " + span.get(0) + "-" + span.get(1);
						AnnotationFileParser.addField(oFile, obfuscate(toWrite));
					}
				}


				Annotation ann = new Annotation(time, match.getNum(), match.getText());

				//check if we are annotating the same position as an existing annotation, if so delete
				new DeleteSelectedAnnotationAction().actionPerformed(
						new ActionEvent(WordpoolDisplay.getInstance(), ActionEvent.ACTION_PERFORMED, null, System.currentTimeMillis(), 0));
				WaveformDisplay.getInstance().repaint();
				
				//file may no longer exist after deletion
				if(oFile.exists() == false) {
					if(oFile.createNewFile()) {
						String annotatorName = GiveMessage.inputMessage("Please enter your name:");
						if(annotatorName == null || annotatorName.equals("")) {
							GiveMessage.errorMessage("Cannot commit annotation without name.");
							return;
						}
						if(AnnotationFileParser.headerExists(oFile) == false) {
							AnnotationFileParser.prependHeader(oFile, annotatorName);
						}
					}
					else {
						throw new IOException("Could not re-create file.");
					}	
				}
				
				
				//add a new annotation object, and clear the field
				AnnotationFileParser.appendAnnotation(ann, oFile);
				AnnotationDisplay.addAnnotation(ann);
				WordpoolDisplay.clearText();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
				GiveMessage.errorMessage("Error comitting annotation! Check files for damage.");
			}
		}
		
	    //return focus to the frame after annotation, for the sake of action key bindings
	    MyFrame.getInstance().requestFocusInWindow();
	    MyMenu.updateActions();
	}
	

	/**
	 * <code>AnnotateActions</code> are enabled anytime audio is open and not playing.
	 */
	@Override
	public void update() {
		if(CurAudio.audioOpen()) {
			if(CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING) {
				setEnabled(false);
			}
			else {
				setEnabled(true);
			}
		}
		else {
			setEnabled(false);
			WordpoolDisplay.clearText();
		}
	}
}
