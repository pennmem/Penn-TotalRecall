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

/**
 * Central location for many kinds of constants other than those used by GUI components.
 * 
 * Constants used directly by GUI components are stored in GUIConstants.
 * 
 * @author Yuvi Masory
 */
public class Constants {
	
	
	
	
	//program info
	
	/**
	 * This program's name.
	 */
	public static final String programName = "Penn TotalRecall";
	
	/**
	 * This program's version number.
	 * Should be of the form x.y where x and y are integers, of any number of digits.
	 * Given two versions x1^y1, and x2^y2 (where ^ is some delimiter), the former is considered newer if (x1 > x2) or if (x1 = x2 and y1 > y2).
	 * Otherwise the latter is considered newer.
	 * 
	 * Must use delimiter found in <code>programVersionDelimiter</code>.
	 */
	public static final String programVersion = "0.00";
	
	/**
	 * Regex delimiter used in programVersion String, given in the form usable by the Pattern class.
	 * Must be delimiter used in <code>programVersion</code>.
	 */
	public static final String programVersionDelimiter = "\\.";
	
	/**
	 * List of all authors, ordered chronologically by date of involvement, separated by commas.
	 */
	public static final String authorString = "Yuvi Masory";
	
	/**
	 * Maintainer, to whom bugs are reported.
	 */
	public static final String maintainerEmail = "memory-software@psych.upenn.edu";
	
	/**
	 * Organization distributing the program.
	 */
	public static final String orgName = "Computational Memory Lab";
	
	/**
	 * Web address of homepage of organization distributing program.
	 */
	public static final String orgHomepage = "http://memory.psych.upenn.edu";
	
	/**
	 * Affiliation of organization distributing the program.
	 */
	public static final String orgAffiliationName = "University of Pennsylvania";
	
	/**
	 * Web address of program download webpage.
	 */
	public static final String downloadSite = "http://memory.psych.upenn.edu/TotalRecall";
	
	/**
	 * Web address of program tutorial webpage.
	 */
	public static final String tutorialSite = "http://memory.psych.upenn.edu/TotalRecall";
	
	/**
	 * Name of license under which code written for this program is released.
	 */
	public static final String license = "GNU General Public License v3";
	
	/**
	 * Web address of full text of license.
	 */
	public static final String licenseSite = "http://www.gnu.org/licenses/gpl-3.0.txt";
	
	
	
	
	
	//file and I/O constants
	
	/**
	 * Extension of files containing candidate words for a particular sound file.
	 */
	public static final String lstFileExtension = "lst";
	
	/**
	 * Extension of completed annotation files.
	 */
	public static final String completedAnnotationFileExtension = "ann";
	
	/**
	 * Extension of incomplete annotation files.
	 */
	public static final String temporaryAnnotationFileExtension = "tmp";
	
	/**
	 * Extension of temporary file used in editing an annotation file.
	 */
	public static final String deletionTempFileExtension = "del";
	
	public static final String wordpoolFileExtension = "txt";
	
	/**
	 * List of extensions of supported audio file formats, all in lower case.
	 * The program does not guarantee that every sound file with one of these extension is supported.
	 */
	public static final String[] audioFormatsLowerCase = {"wav", "wave"};

	/**
	 * String used by annotators to mark sound intrusions that aren't a word.
	 */
	public static final String intrusionSoundString = "<>";
	
	/**
	 * Regex that separates entries in a line of the annotation files.
	 */
	public static final String annotationFileDelimiter = "\t";
	
	public static final String inlineCommentIndicator = "#";
	
	public static final String propertyPairOpenBrace = "{";
	
	public static final String propertyPairCloseBrace = "}";
	
	public static final String commentStart = "#";
	
	public static final String headerStartLine = commentStart + "Begin Header. [Do not edit before this line. Never edit with an instance of the program open.]";

	public static int timeout = 15000;
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Constants(){
	}
}
