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

/**
 * Represents a word that the annotator can select and commit to a particular timestamp in the audio file.
 * 
 * <code>WordpoolWords</code> come into existence in one of two ways.
 * The first occurs when a wordpool file is parsed for presentation in the <code>WordpoolDisplay</code>.
 * The second occurs when the user enters a word not in the <code>WordpoolDisplay</code> and commits it as an intrusion.
 * 
 * <code>The text of a <code>WordpoolWord</code> is always in all CAPS, with the conversion being performed by the constructor
 * if it has not already been done.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Yuvi Masory
 */
public class WordpoolWord implements Comparable<WordpoolWord> {

	private boolean isLst;
	
	private int num;
	
	private String myStr;

	/**
	 * Creates a new instance with the given parameters.
	 * 
	 * @param myStr The text of the word, will be converted to upper case
	 * @param num Index of the word in the file from which it was derived (0-based numbering), or -1 if not from any file (intrusion)
	 */
	public WordpoolWord(String myStr, int num) {
		this.num = num;
		this.myStr = myStr.toUpperCase();
		this.isLst = false;
	}

	/**
	 * Lst words are placed before general wordpool words. 
	 * Otherwise sorting is done alphabetically.
	 *
	 * {@inheritDoc}
	 */
	public int compareTo(WordpoolWord w) {
		if(this.equals(w)) {
			return 0;
		}
		else {
			if((w.isLst() && isLst()) || (!w.isLst() && !isLst())) {
				return getText().compareTo(w.getText()); 				
			}
			else {
				if(isLst()) {
					return -1;
				}
				else {
					return 1;
				}
			}
		}
	}
	
	/**
	 * This word is equal to another object if it too is a <code>WordpoolWord</code> and they share the same text.
 	 *
 	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if((o instanceof WordpoolWord) == false){
			return false;
		}
		else {
			
			WordpoolWord w = (WordpoolWord)o;
			return w.getText().equals(getText());
		}
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}
	
	/**
	 * Getter for the word's text
	 * 
	 * @return the word's text
	 */
	public String getText() {
		return myStr;
	}
	
	/**
	 * Getter for the word's lst status
	 * 
	 * @return <code>true</code> if the word also occurs in the current lst file.
	 */
	public boolean isLst() {
		return isLst;
	}
	
	/**
	 * Setter for the word's lst status.
	 * 
	 * @param isLst Whether the word is in the current lst file
	 */
	public void setLst(boolean isLst) {
		this.isLst = isLst;
	}

	/**
	 * Getter for the index of the word in its home file.
	 * 
	 * @return The index of the word in its home file, with 0-based numbering
	 */
	public int getNum() {
		return num;
	}
	
	@Override
	public String toString() {
		return myStr;
	}
}
