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

package util;

/**
 * Convenience class for storing an AbstractAction's name and tooltip.
 * 
 * Instances stored by maps in the info package. 
 * Helpful for automatically initializing IdentifiedActions. 
 * 
 * @author Yuvi Masory
 */
public class ActionIdentification {
	
	private String actionName;
	private String toolTip;
	
	/**
	 * Simple constructor passed properties of an AbstractAction.
	 * 
	 * @param actionName Destined to be an Action.NAME
	 * @param toolTip Destined to be an Action.SHORT_DESCRIPTION
	 */
	public ActionIdentification(String actionName, String toolTip) {
		this.actionName = actionName;
		this.toolTip = toolTip;
	}
	
	/**
	 * Getter for an AbstractAction's name
	 * 
	 * @return An Action.NAME value
	 */
	public String getActionName() {
		return actionName;
	}
	
	/**
	 * Getter for an AbstractAction's toolTip
	 * 
	 * @return An Action.SHORT_DESCRIPTION value
	 */
	public String getToolTip() {
		return toolTip;
	}
}