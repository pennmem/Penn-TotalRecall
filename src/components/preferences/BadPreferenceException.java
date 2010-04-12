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

package components.preferences;

/**
 * Exception thrown when an attempt is made to store in an ill-formatted or illegal preference.
 * 
 * @author Yuvi Masory
 */
public class BadPreferenceException extends Exception {
	
	private String prefName;

	/**
	 * Creates a new <code>BadPreferenceException</code>.
	 * 
	 * @param prefName The name of the preference associated with this exception
	 * @param message A message explaining why the preference is illegal
	 */
	protected BadPreferenceException(String prefName, String message) {
		super(message);
		this.prefName = prefName;
	}
	
	/**
	 * Getter for the name of the preference associated with this exception.
	 * 
	 * @return The name of the preference associated with this exception
	 */
	protected String getPrefName() {
		return prefName;
	}
}
