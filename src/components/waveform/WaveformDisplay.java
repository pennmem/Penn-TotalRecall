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
import info.MyShapes;
import info.SysInfo;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;

import components.MyFrame;
import components.annotations.Annotation;
import components.annotations.AnnotationDisplay;
import components.waveform.WaveformBuffer.WaveformChunk;

import control.CurAudio;
import edu.upenn.psych.memory.precisionplayer.PrecisionPlayer;

/**
 * This WaveformDisplay is totally autonomous except for changes of zoom factor.
 * 
 * Keep in mind that events other than the repaint timer going off can cause repaints.
 * 
 * @author Yuvi Masory
 */
public class WaveformDisplay extends JComponent {

	private final DecimalFormat secFormat = new DecimalFormat("0.000s");

	private final int REFRESH_DELAY = 20; //people prefer 20 over 30

	private Timer refreshTimer;

	private int pixelsPerSecond;
	
	private volatile boolean chunkInProgress;
	
	private static volatile int progressBarXPos;
	
	private long refreshFrame;
	private int refreshWidth;
	private int refreshHeight;
	private WaveformChunk previousRefreshChunk;
	private WaveformChunk curRefreshChunk;
	private WaveformChunk nextRefreshChunk;
	
	private static WaveformDisplay instance;

	private WaveformDisplay() {
		setOpaque(true);
		setBackground(MyColors.waveformBackground);
		setUI(new ComponentUI() {}); //a little bit of magic so the JComponent will draw the background color without subclassing to a JPanel
		pixelsPerSecond = GUIConstants.zoomlessPixelsPerSecond;
		refreshFrame = -1;
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				MyFrame.getInstance().requestFocusInWindow();
			}
		});
		if(SysInfo.sys.mouseMode) {
			addMouseListener(new WaveformMouseAdapter(this));
			addMouseMotionListener(new WaveformMouseAdapter(this));
		}
	}

	public static WaveformDisplay getInstance() {
		if (instance == null) {
			instance = new WaveformDisplay();
		}
		return instance;
	}

	public static int height() {
		return instance.getHeight();
	}

	public static void zoomX(boolean in) {
		if(in) {
			instance.pixelsPerSecond += GUIConstants.xZoomAmount;
		}
		else {
			if(instance.pixelsPerSecond >= GUIConstants.xZoomAmount + 1) {
				instance.pixelsPerSecond -= GUIConstants.xZoomAmount;
			}
		}
	}
	
	public void startRefreshes() {
		ActionListener refresher = new RefreshListener();
		refreshTimer = new Timer(REFRESH_DELAY, refresher);
		refreshTimer.start();
	}

	public void stopRefreshes() {
		if(refreshTimer != null) {
			refreshTimer.stop();
			curRefreshChunk = null;
			previousRefreshChunk = null;
			nextRefreshChunk = null;
			repaint();
		}
	}


	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	
	
	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //just so the default background color is painted
		
		if(refreshTimer == null || curRefreshChunk == null || refreshTimer.isRunning() == false) {
			//draw reference line
			g.setColor(MyColors.waveformReferenceLineColor);
			g.drawLine(0, getHeight()/2, getWidth() - 1, getHeight()/2);
			
			//draw bottom border
			g.setColor(MyColors.unfocusedColor);
			g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
			return;
		}
		chunkInProgress = false;
		
		//draw buffered waveform image
		int curChunkXPos = frameToComponentX(CurAudio.firstFrameOfChunk(curRefreshChunk.getNum()));
		g.drawImage(curRefreshChunk.getImage(), curChunkXPos, 0, null);
		
		if(previousRefreshChunk != null) {
			g.drawImage(previousRefreshChunk.getImage(), curChunkXPos - curRefreshChunk.getImage().getWidth(null), 0, null);
		}
		else {
			if(curRefreshChunk.getNum() != 0) {
				chunkInProgress = true;
			}
		}
		if(nextRefreshChunk != null) {
			g.drawImage(nextRefreshChunk.getImage(), curChunkXPos + curRefreshChunk.getImage().getWidth(null), 0, null);
		}
		else {
			if(curRefreshChunk.getNum() != CurAudio.lastChunkNum()) {
				chunkInProgress = true;
			}
		}

		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHints(MyShapes.getRenderingHints());
		
		//draw current time
		g2d.drawString(secFormat.format(CurAudio.getMaster().framesToSec(refreshFrame)), 10, 20);
		
		//draw annotations
		Annotation[] anns = AnnotationDisplay.getAnnotationsInOrder();
		for(int i = 0; i < anns.length; i++) {
			double time = anns[i].getTime();
			int xPos = frameToComponentX(CurAudio.getMaster().millisToFrames(time));
			if(xPos < 0) {
				continue;
			}
			if(xPos > refreshWidth) {
				break;
			}
			String text = anns[i].getText();
			g2d.setColor(MyColors.annotationLineColor);
			g2d.drawLine(xPos, 0, xPos, getHeight() - 1);
			g2d.setColor(MyColors.annotationTextColor);
			g2d.drawString(text, xPos + 5, 40);
		}
		
		//find progress bar position
		progressBarXPos = frameToComponentX(refreshFrame);
		if(progressBarXPos < 0) {
			System.err.println("bad val " + progressBarXPos + "/" + (getWidth() - 1));
		}
		else if(progressBarXPos > getWidth() - 1) {
			if(refreshWidth == getWidth()) {
				if(SysInfo.sys.interpolateFrames == false || Math.abs(refreshFrame - CurAudio.getMaster().durationInFrames()) > CurAudio.getMaster().secondsToFrames(SysInfo.sys.interplationToleratedErrorZoneInSec)) {
					System.err.println("bad val " + progressBarXPos + "/" + (getWidth() - 1));
				}
			}
			progressBarXPos = getWidth() - 1;
		}

		//accent selected annotation
		boolean foundOverlap = false;
		if(CurAudio.getPlayer().getStatus() != PrecisionPlayer.Status.PLAYING) {
			for(int i = 0; i < anns.length; i++) {
				int annX = WaveformDisplay.frameToDisplayXPixel(CurAudio.getMaster().millisToFrames(anns[i].getTime()));
				if(progressBarXPos == annX) {
					foundOverlap = true;
					g2d.setPaintMode();
					g2d.setColor(MyColors.annotationAccentColor);
					g2d.drawLine(progressBarXPos, 0, progressBarXPos, refreshHeight - 1);
					int[] xCoordinates = {progressBarXPos - 20, progressBarXPos - 1, progressBarXPos + 2, progressBarXPos + 20};
					int[] yCoordinates = {0, 20, 20, 0};
					g2d.fillPolygon(xCoordinates, yCoordinates, xCoordinates.length);
					yCoordinates = new int[] {refreshHeight - 1, refreshHeight - 21, refreshHeight - 21, refreshHeight - 1};
					g2d.fillPolygon(xCoordinates, yCoordinates, xCoordinates.length);
					break;
				}					
			}
		}
		
		//draw progress bar
		if(foundOverlap == false) {
			Stroke originalStroke = g2d.getStroke();
			g2d.setStroke(MyShapes.getProgressBarStroke());
			g2d.setXORMode(MyColors.waveformBackground);
			
			g2d.setColor(MyColors.progressBarColor);
			g2d.drawLine(progressBarXPos, 0, progressBarXPos, getHeight() - 1);
			
			g2d.setPaintMode();
			g2d.setStroke(originalStroke);
		}
		
		//draw bottom border
		g2d.setColor(MyColors.unfocusedColor);
		g2d.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);

	}

	
	private int frameToComponentX(long frame) {
		int absoluteX = absoluteX(frame);
		int absoluteCurX = absoluteX(refreshFrame);
		
		int offset = refreshWidth/2 - absoluteCurX;
		if(offset > 0) { //first half window of audio is adjusted
			offset = 0; 
		}
		else { //last half window of audio is adjusted
			int absoluteLength = -1 * (int)Math.ceil(GUIConstants.zoomlessPixelsPerSecond * CurAudio.getMaster().durationInSeconds());
			if((-absoluteLength) <= refreshWidth) {
				offset = 0;
			}
			else {
				offset = Math.max(offset, absoluteLength + refreshWidth);
			}
		}
		return absoluteX + offset;
	}
	
	private int absoluteX(long frame) {
		return (int) (GUIConstants.zoomlessPixelsPerSecond * CurAudio.getMaster().framesToSec(frame));
	}
	
	public static int frameToAbsoluteXPixel(long frame) {
		if(CurAudio.audioOpen()) {
			return instance.absoluteX(frame);
		}
		throw new IllegalStateException("audio not open");
	}
	
	public static int frameToDisplayXPixel(long frame) {
		if(CurAudio.audioOpen()) {
			return instance.frameToComponentX(frame);
		}
		throw new IllegalStateException("audio not open");
	}
	
	public static int displayXPixelToFrame(int xPix) {
		if(CurAudio.audioOpen()) {
			return (int) (instance.refreshFrame + (xPix - progressBarXPos) * ((1./GUIConstants.zoomlessPixelsPerSecond) * CurAudio.getMaster().frameRate()));
		}
		throw new IllegalStateException("audio not open");
	}

	public static int getProgressBarXPos() {
		return progressBarXPos;
	}


	//one RefreshListener per file, guaranteed
	protected final class RefreshListener implements ActionListener {
		private final long maxFramesError;
		private final long lastFrame;
		
		private long bufferedFrame;
		private int bufferedWidth;
		private int bufferedHeight;
		private int bufferedNumAnns;
		
		private boolean wasPlaying;
		private long lastTime;

		protected RefreshListener() {
			lastFrame = CurAudio.getMaster().durationInFrames() - 1;
			maxFramesError = CurAudio.getMaster().secondsToFrames(1) / GUIConstants.zoomlessPixelsPerSecond * SysInfo.sys.maxInterpolatedPixels;
			bufferedFrame = -1;
			bufferedWidth = -1;
			bufferedHeight = -1;
			bufferedNumAnns = -1;
			wasPlaying = false;
			lastTime = 0;
		}

		
		public final void actionPerformed(ActionEvent evt) {
			long realRefreshFrame = CurAudio.getAudioProgress();	
			refreshWidth = getWidth();
			refreshHeight = getHeight();		
			int chunkNum = CurAudio.lookupChunkNum(realRefreshFrame);
			int numAnns = AnnotationDisplay.getNumAnnotations();
			boolean isPlaying = CurAudio.getPlayer().getStatus() == PrecisionPlayer.Status.PLAYING;
			
			if(SysInfo.sys.interpolateFrames) {
				long curTime;
				if(SysInfo.sys.nanoInterplation) {
					curTime = System.nanoTime();	
				}
				else {
					curTime = System.currentTimeMillis();
				}
				if(isPlaying && wasPlaying) {
					long changeMillis = curTime - lastTime;
					if(SysInfo.sys.nanoInterplation) {
						refreshFrame += CurAudio.getMaster().nanosToFrames(changeMillis);
					}
					else {
						refreshFrame += CurAudio.getMaster().millisToFrames(changeMillis);
					}
					if(refreshFrame > lastFrame) {
						refreshFrame = lastFrame;
					}
					if(Math.abs(refreshFrame - realRefreshFrame) > maxFramesError) {
						if(SysInfo.sys.interpolateFrames == false || Math.abs(refreshFrame - lastFrame) > CurAudio.getMaster().secondsToFrames(SysInfo.sys.interplationToleratedErrorZoneInSec)) {
							System.err.println("interpolation error greater than " + SysInfo.sys.maxInterpolatedPixels + " pixels: " + Math.abs(refreshFrame - realRefreshFrame) + " (frames)");
							refreshFrame = realRefreshFrame;
						}
					}				
				}
				else {
					refreshFrame = realRefreshFrame;
				}
				lastTime = curTime;
			}
			else {
				refreshFrame = realRefreshFrame;
			}

			if(chunkInProgress == false && refreshFrame == bufferedFrame && bufferedWidth == refreshWidth && bufferedHeight == refreshHeight && bufferedNumAnns == numAnns) {
				return;
			}

			WaveformChunk[] chunks = WaveformBuffer.getWaveformChunks();
			if(chunks == null) { //occurs only while WaveformBuffer's constructor is being run
				return;
			}
			if(chunks[chunkNum] == null) {
				return;
			}			
			curRefreshChunk = chunks[chunkNum];			
			if(chunkNum > 0) {
				previousRefreshChunk = chunks[chunkNum - 1];				
			}
			if(chunkNum < chunks.length - 1) {
				nextRefreshChunk = chunks[chunkNum + 1];				
			}
			

			wasPlaying = isPlaying;
			bufferedFrame = realRefreshFrame;
			bufferedWidth = refreshWidth;
			bufferedHeight = curRefreshChunk.getImage().getHeight(null);
			bufferedNumAnns = AnnotationDisplay.getNumAnnotations();
			
			repaint();
		}
	};
}
