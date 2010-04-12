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

/**
 * Specification for the listener of <code>PrecisionPlayer</code> notifications.
 * 
 * @author Yuvi Masory
 */
public interface PrecisionListener {

	/**
	 * Indicates that main playback has reached the provided frame.
	 * 
	 * <p>There is no guarantee as to how often progress notifications will come.
	 * Since many applications give visual indications of audio progress, implementations should seek to give enough
	 * progress notifications to support a satisfying video framerate, ideally ~30 notifications per second.
	 * 
	 * <p>This notification is given in the same thread as main playback.
	 * If handlers take too long to run, playback may be disturbed.
	 */
	public void progress(long frames);

	/**
	 * Indicates that one of the <code>PrecisionEvents</code> has occurred in main playback. 
	 * 
	 * <p>See <code>PrecisionEvent</code> class for documentation.
	 * 
	 * <p>This notification is given in an independent thread.
	 * Handlers may execute lengthy code without disturbing main playback.
	 */
	public void stateUpdated(PrecisionEvent pe);
}
