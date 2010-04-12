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

package edu.upenn.psych.memory.precisionplayer;

import java.util.List;

/**
 * Convenicne thread launcher for <code>PrecisionEvents</code> since the spec requires they be sent in a different thread than audio playback.
 * 
 * @author Yuvi Masory
 */
public class PrecisionEventLauncher extends Thread {

	private long position;
	private PrecisionEvent.EventCode code;
	private List<PrecisionListener> listeners;
	private String errorMessage;

	/**
	 * Prepares a launcher thread with the provided parameters.
	 * 
	 * @param code The code of the <code>PrecisionEvent</code>
	 * @param position The frame at which the event occurs
	 * @param listeners The listeners to be notified of the event
	 */
	public PrecisionEventLauncher(PrecisionEvent.EventCode code, long position, String errorMessage, List<PrecisionListener> listeners) {
		super();
		this.position = position;
		this.code = code;
		this.listeners = listeners;
		this.errorMessage = errorMessage;
	}

	/**
	 * Notifies registered listeners of the event.
	 */
	@Override
	public void run() {
		if(listeners != null) {
			for(PrecisionListener lis: listeners) {
				PrecisionEvent event = new PrecisionEvent(code, position, errorMessage);
				lis.stateUpdated(event);
			}

		}
	}
}
