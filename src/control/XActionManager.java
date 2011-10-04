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

package control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import scala.Option;

import behaviors.UpdatingAction;
import edu.upenn.psych.memory.shortcutmanager.Shortcut;
import edu.upenn.psych.memory.shortcutmanager.XAction;
import edu.upenn.psych.memory.shortcutmanager.XActionListener;


/**
 * Storage class that associates <code>Objects</code> with {@link util.ActionIdentification} and <code>KeyStroke</code> objects.
 * 
 * Used by <code>IdentifiedMultiActions</code> and <code>IdentifiedSingleActions</code> to automatically associate
 * themselves with names, tooltips, and shortcuts.
 * 
 * @author Yuvi Masory
 */
public class XActionManager {
	
	public static class Pair<T, U> {
		
		final T t;
		final U u;
		
		public Pair(T t, U u) {
			this.t = t;
			this.u = u;
		}
	}
	
	private XActionManager() {}
	
	private static HashMap<String, Set<UpdatingAction>> listenersMap = new HashMap<String, Set<UpdatingAction>>();
	private static HashMap<String, XAction> xactionsMap = new HashMap<String, XAction>();
	private static HashMap<String, ArrayList<Pair<Object, InputMap>>> inputMapMap = new HashMap<String, ArrayList<Pair<Object, InputMap>>>();

	public static XActionListener listener = new XActionListener() {
		@Override
		public void xActionUpdated(XAction xact, Option<Shortcut> old) {
			String id = xact.id();
			xactionsMap.put(id, xact);
			update(xact.id(), old);
		}
	};
	
	public static KeyStroke lookup(UpdatingAction action, Enum<?> e) {
		String id = makeId(action, e);
		return lookup(id);
	}
	
	public static KeyStroke lookup(String id) {
		XAction xact = xactionsMap.get(id);
		if(xact != null) {
			Shortcut shorty = xact.javaShortcut();
			if(shorty != null) {
				return shorty.stroke();
			}
		}
		return null;
	}
	
	private static String makeId(UpdatingAction action, Enum<?> e) {
		String id = action.getClass().getName();
		if(e != null) {
			String eName = e.name();
			String[] parts = e.getClass().toString().split("\\$");
			String eClass = parts[parts.length - 1];
			id += "-" + eClass + "." + eName;
		}
		return id;
	}
	
	public static void registerInputMap(UpdatingAction action, Enum<?> e, String mapKey, InputMap map) {
		String id = makeId(action, e);
		if(inputMapMap.get(id) == null) {
			inputMapMap.put(id, new ArrayList<Pair<Object, InputMap>>());
		}
		ArrayList<Pair<Object, InputMap>> pairs = inputMapMap.get(id);
		pairs.add(new Pair<Object, InputMap>(mapKey, map));
	}
	
	public static void registerAction(UpdatingAction action, Enum<?> e) {
		String id = makeId(action, e);
		if(listenersMap.get(id) == null) {
			listenersMap.put(id, new HashSet<UpdatingAction>());
		}
		listenersMap.get(id).add(action);
		update(id, null);
	}
		
	private static void update(String id, Option<Shortcut> old) {
		Set<UpdatingAction> actions = listenersMap.get(id);
		XAction xact = xactionsMap.get(id);
		if(xact != null) {
			Shortcut shorty = xact.javaShortcut();
			KeyStroke stroke = shorty == null ? null : shorty.stroke();
			if (actions != null) {
				for(UpdatingAction action: actions) {
					action.putValue(Action.NAME, xact.name());
					String tooltip = xact.javaTooltip();
					action.putValue(Action.SHORT_DESCRIPTION, tooltip);
					action.putValue(Action.ACCELERATOR_KEY, stroke);
				}
			}
			ArrayList<Pair<Object, InputMap>> inputMapPairs = inputMapMap.get(id);
			if(inputMapPairs != null) {
				for(Pair<Object, InputMap> pair: inputMapPairs) {
					InputMap inputMap = pair.u;
					Shortcut oldShortcut;
					if(old == null) {
						oldShortcut = null;
					}
					else {
						oldShortcut = old.isDefined() ? old.get() : null;
					}
					KeyStroke oldStroke = oldShortcut == null ? null : oldShortcut.stroke();
					Object inputMapKey = pair.t;
					if(oldStroke != null) {
						inputMap.remove(oldStroke);
					}
					inputMap.put(stroke, inputMapKey);
				}
			}
		}
	}
}
