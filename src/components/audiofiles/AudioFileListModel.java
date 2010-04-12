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

import java.util.HashSet;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import util.MyCollection;




/**
 * A very simple <code>ListModel</code> that guarantees elements remain sorted and without repetitions.
 * 
 * The sorting strategy anticipates that any time many files are added consecutively, they will be added as a batch. 
 * This corresponds to the user opening a directory with <code>OpenAudioLocationAction</code> or dragging many files (or a directory) onto the application.
 * To encourage fast sorting, no method is provided for adding one element individually.
 * 
 * @author Yuvi Masory
 *
 */
// This class assumes that the ListDataListener (often javax.swing.plaf.basic.BasicListUI$Handler by default), 
// will repaint the AudioFileList after ListDataEvents>.
public class AudioFileListModel implements ListModel, ChangeListener {

	private MyCollection<AudioFile> collection;
	private HashSet<ListDataListener> listeners;

	/**
	 * Creates a new <code>AudioFileListModel</code>.
	 */
	protected AudioFileListModel() {
		listeners = new HashSet<ListDataListener>();
		collection = new MyCollection<AudioFile>();
	}

	/**
	 * {@inheritDoc}
	 */
	public AudioFile getElementAt(int index) {
		if(index < 0 || index >= collection.size()) {
			return null;
		}
		return collection.get(index);
	}

	/**
	 * Removes the element at the provided index.
	 * 
	 * Since removing elements cannot make a sorted list unsorted, no sorting is performed after the removal.
	 * However, all registered listeners are notified of a <code>ListDataEvent.INTERVAL_REMOVED</code> event.
	 * 
	 * @param index The index of the element to be removed
	 */
	public void removeElementAt(int index) {
		if(index < 0 || index >= collection.size()) {
			throw new IllegalArgumentException("index not in file set: " + index);
		}
		collection.linearRemoveAt(index).removeAllChangeListeners();
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	/**
	 * Adds files to the list, skipping <code>AudioFiles</code> already in the list.
	 * 
	 * Sets this <code>AudioFileListModel</code> to listen to completion status changes in any files added.
	 * After all files are added, the list is re-sorted using {@link java.util.Collections#sort(List)}.
	 * 
	 * @param files An iterable collection of the files to add
	 */
	public void addElements(Iterable<AudioFile> files) {
		for(AudioFile file: files) {
			file.addChangeListener(this);
			collection.add(file);
		}
		collection.sort();
		//we fire a CONTENTS_CHANGED event instead of INTERVAL_ADDED, because after sorting there is no guarantee that a clean interval is all that has been changed
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	/**
	 * Finds the number of <code>AudioFiles</code> in the model.
	 * 
	 * @return The size of the list containing the <code>AudioFiles</code> 
	 */
	public int getSize() {
		return collection.size();
	}

	/**
	 * Handler for changes of completion status in <code>AudioFiles</code>.
	 * 
	 * Re-sorts the data using {@link java.util.Collections#sort(List)} and requests a repaint.
	 */
	public void stateChanged(ChangeEvent e) {
		collection.sort();
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
}
