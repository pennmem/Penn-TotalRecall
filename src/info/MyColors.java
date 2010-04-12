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

import java.awt.Color;

/**
 * Central location for colors in the GUI.
 * 
 * @author Yuvi Masory
 */
public class MyColors {
	
	//focus colors
	
	/**
	 * Border color for component groups that are looking focused programmatically (not be the LAF).
	 */
	public static final Color focusedColor = new Color(120, 172, 221); //mac push button focused color, better than SystemColor.controlHighlight for other OSes
	
	/**
	 * Border color for component groups that are not currently looking focused programmatically.
	 */
	public static final Color unfocusedColor = new Color(180, 180, 180); //Mac finder uses (180, 180, 180) for diving areas
	
	
	
	
	//component colors

	/**
	 * Color of border of header of annotation list.
	 */
	public static final Color annotationListHeaderBorderColor = unfocusedColor;	
	
	/**
	 * Color of waveform of first channel of audio.
	 */
	public static final Color firstChannelWaveformColor = Color.BLACK;
	
	/**
	 * Color of background behind waveform.
	 */
	public static final Color waveformBackground = Color.WHITE;
	
	
	
	

	//text colors
	
	/**
	 * Color of time scale text on waveform.
	 */
	public static final Color waveformScaleTextColor = Color.BLACK;
	
	/**
	 * Color of annotation text on waveform.
	 */
	public static final Color annotationTextColor = Color.BLACK;

	
	
	
	
	//line/bar colors
	
	/**
	 * Color of vertical reference line that indicates on the waveform the current position of the audio playback.
	 */
	public static final Color progressBarColor = Color.BLACK;
	
	/**
	 * Color of horizontal reference line cutting through waveform.
	 */
	public static final Color waveformReferenceLineColor = Color.BLACK;
	
	/**
	 * Color of vertical reference lines and associated strings that indicate time scale of waveform.
	 */
	public static final Color waveformScaleLineColor = new Color(226, 224, 131); //PyParse color (226, 224, 131)
	
	public static final Color annotationAccentColor = Color.BLUE;
	
	/**
	 * Color of vertical reference lines and associated strings that indicate on he waveform annotations.
	 */
	public static final Color annotationLineColor = Color.RED; //PyParse color (255, 64, 255)
	
	public static final Color replay200MillisFlashColor = new Color(120, 172, 221); //mac push button focused color, better than SystemColor.controlHighlight for other OSes
	
	public static final Color mouseHighlightColor = new Color(120, 172, 221); //mac push button focused color, better than SystemColor.controlHighlight for other OSes
//available in WindowsLAF: UIManager.getDefaults().getColor("textHighlight");
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private MyColors() {
	}
}
