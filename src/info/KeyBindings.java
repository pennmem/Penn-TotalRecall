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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;

import behaviors.multiact.Last200PlusMoveAction;
import behaviors.multiact.ScreenSeekAction;
import behaviors.multiact.SeekAction;
import behaviors.multiact.ToggleAnnotationsAction;
import behaviors.multiact.ZoomAction;
import behaviors.singleact.DoneAction;
import behaviors.singleact.ExitAction;
import behaviors.singleact.OpenAudioLocationAction;
import behaviors.singleact.OpenWordpoolAction;
import behaviors.singleact.PlayPauseAction;
import behaviors.singleact.ReplayLast200MillisAction;
import behaviors.singleact.ReplayLastPositionAction;
import behaviors.singleact.ReturnToLastPositionAction;
import behaviors.singleact.StopAction;

/**
 * Storage class that associates <code>Objects</code> with <code>KeyStroke</code> objects.
 * 
 * Used by <code>IdentifiedMultiActions</code> and <code>IdentifiedSingleActions</code> to automatically associate
 * themselves with key bindings.
 * 
 * @author Yuvi Masory
 */
public class KeyBindings {

	//do NOT explicitly use ctrl, that may be the same as menu on some systems
	private static final int MENU = SysInfo.sys.menuKey;
	private static final int SHIFT = InputEvent.SHIFT_DOWN_MASK;
	private static final int ALT = InputEvent.ALT_DOWN_MASK;
	private static final int NONE = 0;

	private static Map<Class<?>, KeyStroke> generalMap; //do NOT access directly, use getGeneralMap()
	private static Map<Enum<?>, KeyStroke> enumMap; //do NOT access directly, use getEnumMap()

	/**
	 * Accessor to map of key bindings.
	 * 
	 * <code>IdentifiedMultiActions</code> will pass their defining <code>Enum</code>.
	 * <code>IdentifiedSingleActions</code> will pass references to themselves.
	 * 
	 * @param o The map key
	 * @return The key binding value, or <code>null</code> if the object is not in the map
	 */
	public static KeyStroke lookupBinding(Object o) {		
		if(o instanceof Enum<?>) {
			return getEnumMap().get(o);
		}
		else {
			return getGeneralMap().get(o.getClass());
		}
	}

	/*
	 * Fills up map on first call with stored values, returns a reference for later calls.
	 * 
	 * @return The general map between class objects and KeyStrokes
	 */
	private static Map<Class<?>, KeyStroke> getGeneralMap() {
		if(generalMap == null) {
			generalMap = new HashMap<Class<?>, KeyStroke>();
			generalMap.put(DoneAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_D, MENU + SHIFT, false));
			generalMap.put(ExitAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU, false));
			generalMap.put(OpenAudioLocationAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU, false));
			generalMap.put(OpenWordpoolAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU + SHIFT, false));
			generalMap.put(PlayPauseAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, NONE, false));
//			generalMap.put(PreferencesAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_K, MENU, false));
			generalMap.put(ReplayLast200MillisAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_Z, MENU, false));
			generalMap.put(StopAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU, false));
			generalMap.put(ReturnToLastPositionAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_L, MENU, false));
			generalMap.put(ReplayLastPositionAction.class, KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU, false));
		}
		return generalMap;
	}

	/*
	 * Fills up map on first call with stored values, returns a reference for later calls.
	 * 
	 * @return The map between Enum objects and KeyStrokes
	 */
	private static Map<Enum<?>, KeyStroke> getEnumMap() {
		if(enumMap == null) {
			enumMap = new HashMap<Enum<?>, KeyStroke>();
			enumMap.put(SeekAction.SeekAmount.FORWARD_SMALL, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, NONE, false));
			enumMap.put(SeekAction.SeekAmount.BACKWARD_SMALL, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, NONE, false));
			enumMap.put(SeekAction.SeekAmount.FORWARD_MEDIUM, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, MENU, false));
			enumMap.put(SeekAction.SeekAmount.BACKWARD_MEDIUM, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, MENU, false));
			enumMap.put(SeekAction.SeekAmount.FORWARD_LARGE, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, MENU + SHIFT, false));
			enumMap.put(SeekAction.SeekAmount.BACKWARD_LARGE, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, MENU + SHIFT, false));
			enumMap.put(ToggleAnnotationsAction.Direction.FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SHIFT, false));
			enumMap.put(ToggleAnnotationsAction.Direction.BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SHIFT, false));
			enumMap.put(ZoomAction.Direction.IN, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, MENU, false));
			enumMap.put(ZoomAction.Direction.OUT, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, MENU, false));
			enumMap.put(Last200PlusMoveAction.Direction.BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ALT, false));
			enumMap.put(Last200PlusMoveAction.Direction.FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ALT, false));
			enumMap.put(ScreenSeekAction.Dir.FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_M, MENU, false));
			enumMap.put(ScreenSeekAction.Dir.BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU, false));
		}
		return enumMap;
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private KeyBindings() {
	}
}
