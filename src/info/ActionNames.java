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

package info;

import java.util.HashMap;
import java.util.Map;

import util.ActionIdentification;
import behaviors.multiact.Last200PlusMoveAction;
import behaviors.multiact.SeekAction;
import behaviors.multiact.ToggleAnnotationsAction;
import behaviors.multiact.ZoomAction;
import behaviors.singleact.AboutAction;
import behaviors.singleact.CalibrateAction;
import behaviors.singleact.CheckUpdatesAction;
import behaviors.singleact.ContinueAnnotatingAction;
import behaviors.singleact.DeleteAnnotationAction;
import behaviors.singleact.DoneAction;
import behaviors.singleact.ExitAction;
import behaviors.singleact.KeyBindingsMessageAction;
import behaviors.singleact.OpenAudioLocationAction;
import behaviors.singleact.OpenWordpoolAction;
import behaviors.singleact.PlayPauseAction;
import behaviors.singleact.PreferencesAction;
import behaviors.singleact.ReplayLast200MillisAction;
import behaviors.singleact.ReplayLastPositionAction;
import behaviors.singleact.ReturnToLastPositionAction;
import behaviors.singleact.StopAction;
import behaviors.singleact.VisitTutorialSiteAction;

/**
 * Storage class that associates <code>Objects</code> with {@link util.ActionIdentification} objects.
 * 
 * Used by <code>IdentifiedMultiActions</code> and <code>IdentifiedSingleActions</code> to automatically associate
 * themselves with names and tooltips.
 * 
 * @author Yuvi Masory
 */
public class ActionNames {

	private static Map<Class<?>, ActionIdentification> generalMap; //do NOT access directly, use getGeneralMap()
	private static Map<Enum<?>, ActionIdentification> enumMap; //do NOT access directly, use getEnumMap()

	/**
	 * Accessor to map of object names.
	 * 
	 * <code>IdentifiedMultiActions</code> will pass their defining <code>Enum</code>.
	 * <code>IdentifiedSingleActions</code> will pass references to themselves.
	 * 
	 * @param o The map key
	 * @return The stored name of the object, or <code>null</code> if the object is not in the map
	 */
	public static String lookupName(Object o) {
		return lookup(o, true);
	}

	/**
	 * Accessor to the map of tool tips.
	 * 
	 * <code>IdentifiedMultiActions</code> will pass their defining <code>Enum</code>.
	 * <code>IdentifiedSingleActions</code> will pass references to themselves.
	 * 
	 * @param o The map key
	 * @return The stored tool tip of the object, or <code>null</code> if the object is not in the map
	 */
	public static String lookupToolTip(Object o) {
		return lookup(o, false);
	}

	/*
	 * Splits the map lookup between the Enum and non-Enum cases, and name/tooltip cases.
	 * 
	 * @param lookingUpName true if the lookup is for an action name, false if for tool tip
	 */
	private static String lookup(Object o, boolean lookingUpName) {
		ActionIdentification name;
		if(o instanceof Enum<?>) {
			name = getEnumMap().get(o);
		}
		else {
			name = getGeneralMap().get(o.getClass());
		}
		if(name == null) {
			return null;
		}
		else {
			if(lookingUpName) {
				return name.getActionName();
			}
			else {
				return name.getToolTip();
			}
		}
	}

	/*
	 * Fills up map on first call with stored values, returns a reference for later calls.
	 * 
	 * @return The general map between class objects and ActionIdentifications
	 */
	private static Map<Class<?>, ActionIdentification> getGeneralMap() {
		if(generalMap == null) {
			generalMap = new HashMap<Class<?>, ActionIdentification>();
			generalMap.put(
					AboutAction.class, 
					new ActionIdentification("About", null));
			generalMap.put(
					ContinueAnnotatingAction.class, 
					new ActionIdentification("Continue Editing", null));
			generalMap.put(
					CheckUpdatesAction.class, 
					new ActionIdentification("Check For Updates", null));
			generalMap.put(
					DeleteAnnotationAction.class,
					new ActionIdentification("Delete Annotation", null));
			generalMap.put(
					DoneAction.class, 
					new ActionIdentification("Mark Complete", "Mark Annotation File Complete"));
			generalMap.put(
					ExitAction.class, 
					new ActionIdentification("Exit", null));
			generalMap.put(
					OpenAudioLocationAction.class, 
					new ActionIdentification("Add Audio Files...", "Select File or Folder"));
			generalMap.put(
					OpenWordpoolAction.class, 
					new ActionIdentification("Select Wordpool...", "Select Text File Containing Words in Audio File"));
			generalMap.put(
					PreferencesAction.class,
					new ActionIdentification(SysInfo.sys.preferencesString + "...", null));
			generalMap.put(
					PlayPauseAction.class, 
					new ActionIdentification("Play/Pause", null));
			generalMap.put(
					StopAction.class, 
					new ActionIdentification("Go to Start", null));
			generalMap.put(
					ReplayLast200MillisAction.class, 
					new ActionIdentification("Replay Last 200 ms", null));
			generalMap.put(
					VisitTutorialSiteAction.class, 
					new ActionIdentification("Program Homepage", Constants.tutorialSite));
			generalMap.put(
					ReturnToLastPositionAction.class,
					new ActionIdentification("Undo Play", "Return to the position prior to hitting play"));
			generalMap.put(
					KeyBindingsMessageAction.class,
					new ActionIdentification("Key Bindings", null));
			generalMap.put(
					ReplayLastPositionAction.class,
					new ActionIdentification("Replay", null));
			generalMap.put(
					CalibrateAction.class,
					new ActionIdentification("Calibrate...", null));
		}
		return generalMap;
	}

	/*
	 * Fills up map on first call with stored values, returns a reference for later calls.
	 * 
	 * @return The map between Enum objects and ActionIdentifications
	 */
	private static Map<Enum<?>, ActionIdentification> getEnumMap() {
		if(enumMap == null) {
			final String f = "Forward ";
			final String b = "Backward ";
			final String small = "Small Amount";
			final String medium = "Medium Amount";
			final String large = "Large Amount";
			enumMap = new HashMap<Enum<?>, ActionIdentification>();
			enumMap.put(
					SeekAction.SeekAmount.FORWARD_SMALL, 
					new ActionIdentification(f + small, null));
			enumMap.put(
					SeekAction.SeekAmount.BACKWARD_SMALL, 
					new ActionIdentification(b + small, null));
			enumMap.put(
					SeekAction.SeekAmount.FORWARD_MEDIUM, 
					new ActionIdentification(f + medium, null));
			enumMap.put(
					SeekAction.SeekAmount.BACKWARD_MEDIUM, 
					new ActionIdentification(b + medium, null));
			enumMap.put(
					SeekAction.SeekAmount.FORWARD_LARGE, 
					new ActionIdentification(f + large, null));
			enumMap.put(
					SeekAction.SeekAmount.BACKWARD_LARGE, 
					new ActionIdentification(b + large, null));
			enumMap.put(
					ToggleAnnotationsAction.Direction.FORWARD, 
					new ActionIdentification("Toggle Next Annotation", null));
			enumMap.put(
					ToggleAnnotationsAction.Direction.BACKWARD, 
					new ActionIdentification("Toggle Previous Annotation", null));
			enumMap.put(
					ZoomAction.Direction.IN, 
					new ActionIdentification("Zoom In", null));
			enumMap.put(
					ZoomAction.Direction.OUT, 
					new ActionIdentification("Zoom Out", null));
			enumMap.put(
					Last200PlusMoveAction.Direction.BACKWARD,
					new ActionIdentification(b + small + " then Replay Last 200 ms", null));
			enumMap.put(
					Last200PlusMoveAction.Direction.FORWARD,
					new ActionIdentification(f + small + " then Replay Last 200 ms", null));
		}
		return enumMap;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ActionNames() {
	}
}
