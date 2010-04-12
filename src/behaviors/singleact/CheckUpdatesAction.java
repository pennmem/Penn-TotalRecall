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

package behaviors.singleact;

import java.awt.event.ActionEvent;

import util.CheckUpdatesThread;

/**
 * Launches a {@link util.CheckUpdatesThread}.
 * 
 * @author Yuvi Masory
 */
public class CheckUpdatesAction extends IdentifiedSingleAction {

	private boolean informEitherWay;
	
	/**
	 * Creates an instance of the <code>Action</code>.
	 * 
	 * @param informEitherWay Whether or not to inform the user if an update is NOT available
	 */
	public CheckUpdatesAction(boolean informEitherWay) {
		this.informEitherWay = informEitherWay;
	}

	/**
	 * Performs the <code>Action</code> by creating and launching a CheckUpdatesThread.
	 */
	public void actionPerformed(ActionEvent e) {
		new Thread(new CheckUpdatesThread(informEitherWay)).start();
	}
	
	/**
	 * <code>CheckUpdatesAction</code> is always enabled.
	 */
	@Override
	public void update() {}
}
