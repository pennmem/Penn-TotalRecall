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

package components.waveform;

import info.GUIConstants;
import info.MyColors;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import behaviors.singleact.ReplayLast200MillisAction;

/**
 * Application glass pane, used for drawing mouse feedback.
 * 
 * @author Yuvi Masory
 */
public class MyGlassPane extends JComponent {

	private static MyGlassPane instance;

	private final AlphaComposite composite;

	private volatile boolean highlightMode;
	private volatile Point highlightSource;
	private volatile Point highlightDest;
	private volatile Rectangle highlightRect;
	
	
	private Timer timer;

	private volatile boolean flashMode;

	private int flashRectangleXPos;
	private int flashRectangleWidth;
	
	private final int flashWidth = (int)(GUIConstants.zoomlessPixelsPerSecond * (ReplayLast200MillisAction.duration / (double)1000));

	private MyGlassPane() {
		composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F);
		flashMode = false;		
		highlightMode = false;
		highlightSource = new Point();
		highlightDest = new Point();
		highlightRect = new Rectangle();
	}

	@Override
	protected void paintComponent(Graphics g) { 
		if(highlightMode) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setComposite(composite);
			g2d.setColor(MyColors.mouseHighlightColor);
			g2d.fillRect((int)highlightRect.getX(), (int)highlightRect.getY(), (int)highlightRect.getWidth(), (int)highlightRect.getHeight());
		}
		else if(flashMode) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setComposite(composite);
			g2d.setColor(MyColors.replay200MillisFlashColor);
			int yPos = (int)SwingUtilities.convertPoint(WaveformDisplay.getInstance(), -1, 0, this).getY();
			g2d.fillRect(flashRectangleXPos, yPos, flashRectangleWidth, WaveformDisplay.height());
		}
		else {
			setVisible(false);
		}
	}
	
	public void setHighlightMode(boolean flag) {
		highlightMode = flag;
		setVisible(flag);
	}
	
	public void setHighlightSource(Point sourcePoint, Component sourceComp) {
		highlightSource = SwingUtilities.convertPoint(sourceComp, sourcePoint, this);
	}
	
	public void setHighlightDest(Point destPoint, Component sourceComp) {
		highlightDest = SwingUtilities.convertPoint(sourceComp, destPoint, this);
		udpateHighlightRect();
	}
	
	private void udpateHighlightRect() {
		int xSource = (int) (highlightSource.getX() < highlightDest.getX() ? highlightSource.getX() : highlightDest.getX());
		int ySource = (int) SwingUtilities.convertPoint(WaveformDisplay.getInstance(), 0, 0, this).getY();
		int width = (int) Math.abs(highlightSource.getX() - highlightDest.getX());
		int height = WaveformDisplay.height();
		Rectangle naiveBounds = new Rectangle(xSource, ySource, width, height);
		Rectangle waveformBounds = SwingUtilities.convertRectangle(WaveformDisplay.getInstance(), WaveformDisplay.getInstance().getVisibleRect(), this);
		highlightRect = naiveBounds.intersection(waveformBounds);
	}

	public int[] getHighlightBounds() {
		return new int[] {(int) highlightSource.getX(), (int) highlightDest.getX()};
	}

	public boolean isHighlightMode() {
		return highlightMode;
	}
	

	
	


	public void flashRectangle() {
		if((timer != null && timer.isRunning()) == false) {
			this.flashRectangleXPos = (int)SwingUtilities.convertPoint(WaveformDisplay.getInstance(), WaveformDisplay.getProgressBarXPos() - flashWidth, -1, this).getX();
			this.flashRectangleWidth = flashWidth;
			flashMode = true;
			setVisible(true);
			repaint();
			timer = new Timer(ReplayLast200MillisAction.duration, new StopFlashListener());
			timer.setRepeats(false);
			timer.start();
		}
	}
	
	private final class StopFlashListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			flashMode = false;
			repaint();
		}		
	}
	
	
	
	public static MyGlassPane getInstance() {
		if(instance == null) {
			instance = new MyGlassPane();
		}
		return instance;
	}
}
