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

package components.wordpool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import util.MyCollection;

/**
 * Custom list model for the <code>WordpoolList</code>.
 * 
 * @author Yuvi Masory
 */
//This class assumes that the ListDataListener (often javax.swing.plaf.basic.BasicListUI$Handler by default), 
//will repaint the WordpoolList after ListDataEvents>.
public class WordpoolListModel implements ListModel {

	private MyCollection<WordpoolWord> collection;

	private HashSet<ListDataListener> listeners;
	private HashSet<WordpoolWord> hiddenWords;

	public WordpoolListModel() {
		super();
		collection = new MyCollection<WordpoolWord>();
		hiddenWords = new HashSet<WordpoolWord>();
		listeners = new HashSet<ListDataListener>();
	}

	public Object getElementAt(int index) {
		if(index < 0 || index >= collection.size()) {
			throw new IllegalArgumentException("index not in wordpool list: " + index);
		}
		return collection.get(index);
	}

	public int getSize() {
		return collection.size();
	}

	public void addElements(Iterable<WordpoolWord> words) {
		WordpoolDisplay.clearText();		
		for(WordpoolWord w: words) {
			if(w.getNum() < 0) {
				System.err.println("adding wordpool words with negative line numbers is not allowed");
				continue;
			}
			if(collection.contains(w) || hiddenWords.contains(w)) {
				continue;
			}
			else {
				collection.add(w);			
			}
		}
		collection.sort();

		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	protected void removeAllWords() {
		hiddenWords.clear();
		collection.clear();
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}




	protected void distinguishAsLst(List<WordpoolWord> lstWords) {
		for(WordpoolWord w: hiddenWords) {
			for(WordpoolWord lst: lstWords) {
				if(lst.equals(w)) {
					w.setLst(true);
				}
			}
		}
		for(WordpoolWord w: collection) {
			for(WordpoolWord lst: lstWords) {
				if(lst.equals(w)) {
					w.setLst(true);
				}
			}
		}
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		collection.sort();
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	protected void undistinguishAllWords() {
		for(WordpoolWord w: hiddenWords) {
			w.setLst(false);
		}
		for(WordpoolWord w: collection) {
			w.setLst(false);
		}
		collection.sort();
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	protected void hideWordsStartingWith(String prefix) {
		ArrayList<WordpoolWord> toRemove = new ArrayList<WordpoolWord>();
		for(int i = 0; i < collection.size(); i++) {
			WordpoolWord w = collection.get(i);
			if(w.getText().toUpperCase().startsWith(prefix.toUpperCase()) == false) {
				toRemove.add(w);
				hiddenWords.add(w);
			}
		}
		collection.linearRemove(toRemove);

		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	protected void restoreWordsStartingWith(String prefix) {
		for(Object o: hiddenWords.toArray()) {
			WordpoolWord w = (WordpoolWord)o;
			if(w.getText().toUpperCase().startsWith(prefix.toUpperCase())) {
				collection.add(w);
				hiddenWords.remove(w);
			}
		}

		collection.sort();

		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, collection.size());
		for(ListDataListener ldl: listeners) {
			ldl.contentsChanged(e);
		}
	}

	protected WordpoolWord findMatchingWordpoolWord(String str) {
		for(int i = 0; i < collection.size(); i++) {
			WordpoolWord w = collection.get(i);
			if(w.getText().equals(str)) {
				return w;
			}
		}
		return null;
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
