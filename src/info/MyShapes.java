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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Central location for <code>Strokes</code>, <code>Borders</code>, and <code>RenderingHints</code> in the GUI.
 * 
 * Objects are created on the first call of their getter.
 * 
 * @author Yuvi Masory, Arka M
 */
public class MyShapes {

	private static BasicStroke progressBarStroke;

	private static RenderingHints renderHints;
	
		
	//Strokes
	/**
	 * Getter for the <code>Stroke</code> used by the selection bar.
	 * 
	 * Creates the <code>Stroke</code> on the first call.
	 * 
	 * @return The <code>Stroke</code> for the selection bar
	 */
	public static BasicStroke getProgressBarStroke(){
		if (progressBarStroke == null){
			progressBarStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0,
					new float[] {10.0f, 3.0f}, 0);	// float array specifies 10 pixels on, 3 off
		}
		return progressBarStroke;
	}
	
	
	
	
	
	
	//Borders
	
	/**
	 * Creates a titled border whose border color is the program's standard for lack of programmatic non-LAF focus hints.
	 * 
	 * @param title The border title
	 * @return The constructed <code>Border</code>
	 */
	public static TitledBorder createMyUnfocusedTitledBorder(String title) {
		return createMyTitledBorder(title, MyColors.unfocusedColor);
	}
	
	/**
	 * Creates a titled border with the provided title and border color.
	 * 
	 * Uses {@link #getBorderTitleFont()} for title <code>Font</code>.
	 * 
	 * @param title The title
	 * @param c The color for the border line
	 * @return The constructed border
	 */
	private static TitledBorder createMyTitledBorder(String title, Color c) {
		Border blackline = BorderFactory.createLineBorder(c);
		TitledBorder border = BorderFactory.createTitledBorder(
				blackline, title);
		border.setTitleJustification(TitledBorder.CENTER);
		return border;
	}
	
	
	
	
	
	
	//RenderingHints

	/**
	 * Getter for a <code>RenderingHints</code> object that makes Fonts and Lines more attractive by turning on anti-aliasing and high-quality rendering.
	 * 
	 * Creates the <code>RenderingHints</code> on the first call.
	 * 
	 * @return The <code>RenderingHints</code> object with attractive settings on
	 */
	public static RenderingHints getRenderingHints() {
		if(renderHints == null) {
			renderHints = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
		return renderHints;
	}

	
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private MyShapes() {
	}
}
