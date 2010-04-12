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

package components.wordpool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parser for the wordpool files.
 * 
 * Parses both wordpool documents and the narrower word list documents for one audio file (called LST files in PyParse).
 * Words must contain letters or the parser will skip them.
 * The word consists of the entire line it is found on.
 * Indexes are relative to the list of words considered words by this parser, not line numbers or any other standard.
 * 
 * @author Yuvi Masory
 *
 */
public class WordpoolFileParser {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private WordpoolFileParser() {
	}

	/**
	 * Parses the wordpool file, traversing it line by line.
	 * 
	 * @param file The file to be parsed
	 * @return A List containing the WordpoolWords in the same order they appear in the list
	 * @throws IOException In the event of i/o problems while reading the File
	 */
	public static List<WordpoolWord> parse(File file, boolean suppressLineNumbers) throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(file));
		ArrayList<WordpoolWord> words = new ArrayList<WordpoolWord>();
		String line;
		int lineNum = 0;
		line = br.readLine();
		while(line != null) {
			lineNum++; //PyParse goes 1-indexes the wordpool words and goes by line num not word num
			Matcher whiteSpace = Pattern.compile("\\s*").matcher(line);
			if(whiteSpace.matches()) {
				System.err.println("line #" + lineNum + " not a valid wordpool word: " + line);
			}
			else {
				line = line.trim();
				if(suppressLineNumbers == false) {
					words.add(new WordpoolWord(line, lineNum));	
				}
				else {
					words.add(new WordpoolWord(line, -1));						
				}
			}
			line = br.readLine();
		}
		return words;
	}
}
