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

package components.audiofiles;

import info.Constants;

import java.io.File;
import java.util.HashSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.OSPath;

/**
 * A <code>File</code> that represents an audio file for the purpose of representation in the <code>AudioFileDisplay</code>.
 * 
 * <p><code>AudioFiles</code> keep track of whether they are done being annotated or not ("completion status"), and provide file system sanity checks that guarantee the <code>AudioFile</code>'s directory does not contain both temporary and final annotation files for this <code>AudioFile</code>.
 * Please note that the audio file, temporary annotation file, and final annotation file must be in the same directory and must share the same filename up to file extension. 
 * 
 * <p>NOTE: This class does NOT represent the actual audio data.
 * For that, see {@link control.AudioMaster}.
 * 
 * @author Yuvi Masory
 */
public class AudioFile extends File {
	
	private HashSet<ChangeListener> listeners; //notified when completion status changes

	private boolean done; //whether or not the AudioFile is done being annotated.

	/**
	 * Creates a new <code>AudioFile</code> from the given path.
	 * 
	 * <p>Automatically determines if the file is done being annotated, using the presence sister annotation files in the same directory to judge.
	 * An AudioFile is either done or not done, so this constructor enforces the requirement that the new <code>AudioFile</code>'s directory can't contain both temporary and final annotation files.
	 * 
	 * @param pathname The path of the file to be created
	 * @throws AudioFilePathException If the new file's directory contains both temporary and final annotation files
	 */
	public AudioFile(String pathname) throws AudioFilePathException {		
		super(pathname);
		listeners = new HashSet<ChangeListener>();
		updateDoneStatus();
	}
	
	/**
	 * Provide a shorter String representation of this object, for the benefit of the graphical display of the object in the <code>AudioFileList</code>.
	 * 
	 * @return The <code>AudioFile</code>'s name, using the inherited {@link java.io.File#getName()}. 
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Getter for the <code>AudioFile</code>'s completion status.
	 * 
	 * @return <code>true</code> iff this <code>AudioFile</code> is done being annotated.
	 */
	public boolean isDone() {
		return done;
	}
	
	/**
	 * Determines how <code>AudioFiles</code> are sorted.
	 * 
	 * <p>The following three rules are applied, in order if precedence:
	 * <OL>
	 * 	<LI> <code>AudioFiles</code> come before other types of <code>Objects</code>.
	 * 	<LI> <code>AudioFiles</code> that are still incomplete come before those that are already done.
	 * 	<LI> <code>AudioFiles</code> sort by alphabetical order.
	 * </OL>
	 */
	@Override
	public int compareTo(File f) {
		if(f instanceof AudioFile == false) {
			return 1;
		}
		else {
			AudioFile ff = (AudioFile)f;
			if((ff.isDone() && isDone()) || (ff.isDone() == false && isDone() == false)) {
				return toString().compareTo(ff.toString());
			}
			else {
				if(isDone()){
					return 1;
				}
				else {
					return -1;
				}
			}
		}
	}
	
	/**
	 * Finds hash value based on file path.
	 * 
	 * Returns <code>getAbsolutePath().hashCode()</code>.
	 */
	@Override
	public int hashCode() {
		return getAbsolutePath().hashCode();
	}
	
	/**
	 * Two <code>AudioFiles</code> are equal if they have the same absolute path.
	 * 
	 * @return <code>true</code> iff <code>o</code> is an <code>AudioFile</code> with the same absolute path
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof AudioFile) {
			if(((AudioFile)o).getAbsolutePath().equals(getAbsolutePath())) {
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * Sets the <code>done</done> field by finding if a temporary annotation file or a final annotation file is present in the same directory as the this <code>File</code>.
	 * Informs listeners if the completion status changes.
	 * 
	 * @throws AudioFilePathException If both the temporary and final annotation files are present
	 */
	//it's okay that the listeners update loop is entered even if this call came from the constructor
	//since there's no constructor that takes in ChangeListeners, that loop will iterate zero times
	public void updateDoneStatus() throws AudioFilePathException {
		boolean savedStatus = done;
		boolean updatedStatus = savedStatus;
		boolean annFileExists = false;
		boolean tmpFileExists = false;
		File annFile = new File(OSPath.basename(getAbsolutePath()) + "." + Constants.completedAnnotationFileExtension);
		File tmpFile = new File(OSPath.basename(getAbsolutePath()) + "." + Constants.temporaryAnnotationFileExtension);
		if(annFile.exists()) {
			annFileExists = true;
			updatedStatus = true;
		}
		if(tmpFile.exists()) {
			tmpFileExists = true;
			updatedStatus = false;
		}
		if(annFileExists && tmpFileExists) {
			throw new AudioFilePathException(
					"Both exist, so I don't know if I'm completed or not:\n" +
					 annFile.getPath() + "\n" +
					 tmpFile.getPath());
		}
		done = updatedStatus;
		if(savedStatus != done) {
			for(ChangeListener listener: listeners) {
				listener.stateChanged(new ChangeEvent(this));
			}
		}
	}
	
	/**
	 * Adds a <code>ChangeListener</code> to be notified of updates in this <code>AudioFile</code>'s completion status.
	 * 
	 * @param listen The <code>ChangeListener</code> to be added.
	 */
	public void addChangeListener(ChangeListener listen) {
		listeners.add(listen);
	}
	
	/**
	 * Removes all the <code>ChangeListeners</code> registered to receive updates from this <code>AudioFile</code>.
	 */
	public void removeAllChangeListeners() {
		for(Object o: listeners.toArray()) {
			listeners.remove(o);
		}
	}
	
	/**
	 * Exception thrown when this <code>AudioFile</code>'s directory contains both temporary and final annotation files for this <code>AudioFile</code>. 
	 */
	public class AudioFilePathException extends Exception {
		private AudioFilePathException(String str) {
			super(str);
		}
	}
}
