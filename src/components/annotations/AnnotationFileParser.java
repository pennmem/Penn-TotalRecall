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

package components.annotations;

import info.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.OSPath;

/**
 * Handles manipulations of annotation files, e.g. adding and removing annotations.
 * 
 * @author Yuvi Masory
 */
public class AnnotationFileParser {

	private static final Pattern delimiter = Pattern.compile(Constants.annotationFileDelimiter);

	private static final Pattern commentPattern = Pattern.compile("(.*)" + Constants.inlineCommentIndicator + ".*");

	/**
	 * Private constructor to prevent instantiation.
	 */
	private AnnotationFileParser() {
	}



	private static Annotation parseLine(String line) {
		Scanner sc = new Scanner(line).useDelimiter(delimiter);
		if(sc.hasNextDouble()) {
			double time = sc.nextDouble();
			if(sc.hasNextInt()) {
				int wordNum = sc.nextInt();
				if(sc.hasNext()) {
					String text = sc.next().toUpperCase();
					return new Annotation(time, wordNum, text);
				}
			}
		}
		return null;
	}




	private static String makeLine(Annotation ann) {
		return ann.getTime() + delimiter.toString() + ann.getWordNum() + delimiter.toString() + ann.getText();
	}




	/**
	 * Parses <code>Annotations</code> from a <code>File</code>.
	 * 
	 * Proceeds line by line, parsing at most one <code>Annotation</code> per line.
	 * 
	 * @param file The file to be parsed
	 * @return A <code>List</code> of <code>Annotations</code> from the file, or <code>null</code> if the file does not exist or causes an <code>IOException</code>
	 */
	public static List<Annotation> parse(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		ArrayList<Annotation> anns = new ArrayList<Annotation>();
		String line;
		try {
			int lineNum = 1;
			line = br.readLine();
			while(line != null) {
				Matcher m = commentPattern.matcher(line);
				if(m.matches()) {
					line = m.group(1);
				}
				if(line.length() > 0) {
					Annotation ann = parseLine(line);	
					if(ann != null) {
						anns.add(ann);
					}
					else {
						Matcher whiteSpace = Pattern.compile("\\s*").matcher(line);
						if(whiteSpace.matches() == false) {
							System.err.println("line #" + lineNum + " unparseable: " + line);
						}
					}
				}
				line = br.readLine();
				lineNum++;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return anns;
		}
		return anns;
	}




	public static boolean removeAnnotation(Annotation annToDelete, File oFile) throws IOException {
		File nFile = new File(OSPath.basename(oFile.getAbsolutePath()) + "." + Constants.deletionTempFileExtension);
		BufferedReader reader = null;
		BufferedWriter writer = null;
		reader = new BufferedReader(new FileReader(oFile));
		writer = new BufferedWriter(new FileWriter(nFile));

		boolean foundTarget = false;
		String curLine;
		while((curLine = reader.readLine()) != null) {
			Annotation curLineAnn = parseLine(curLine);
			if(curLineAnn != null && curLineAnn.equals(annToDelete)) {
				if(foundTarget) {
					System.err.println("duplicate match?: " + curLine);
				}
				else {
					foundTarget = true;
				}
			}
			else {
				writer.write(curLine + "\n");
			}
		}
		writer.close();
		reader.close();

		if(oFile.delete() == false) {
			throw new IOException("could not delete old file");
		}
		if(nFile.renameTo(oFile) == false) {
			throw new IOException("could not rename temp deletion file to normal temp file");
		}
		return foundTarget;
	}


	public static void appendAnnotation(Annotation ann, File oFile) throws IOException {
		ArrayList<String> inLines = new ArrayList<String>();
		ArrayList<String> outLines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(oFile));
		String curLine;
		while((curLine = reader.readLine()) != null) {
			inLines.add(curLine);
		}
		String lineToAdd = makeLine(ann);
		boolean foundPosition = false;
		for(int i = 0; i < inLines.size(); i++) {
			if(foundPosition == false) {
				Annotation curAnn = parseLine(inLines.get(i));
				if(curAnn != null) {
					if(ann.getTime() < curAnn.getTime()) {
						outLines.add(lineToAdd);
						foundPosition = true;
					}
				}
			}
			outLines.add(inLines.get(i));
		}
		if(foundPosition == false) {
			outLines.add(lineToAdd);
		}
		reader.close();

		File nFile = new File(OSPath.basename(oFile.getAbsolutePath()) + "." + Constants.deletionTempFileExtension);
		BufferedWriter writer = new BufferedWriter(new FileWriter(nFile));
		for(int i = 0; i < outLines.size(); i++) {
			writer.write(outLines.get(i) + "\n");
		}
		writer.close();		
		oFile.delete();		
		if(nFile.renameTo(oFile) == false) {
			throw new IOException("could not rename temp deletion file to normal temp file");
		}
	}
	

	public static boolean headerExists(File oFile) {
		if(oFile.exists()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(oFile));
				String firstLine = br.readLine();
				br.close();
				if(firstLine != null) {
					return firstLine.contains(Constants.headerStartLine);
				}
				else {
					return false;
				}
			} 	
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		else {
			return false;
		}
	}
	


	public static void prependHeader(File oFile, String annotatorName) throws IOException {		
		if(oFile.exists() == false) {
			throw new FileNotFoundException(oFile + " not found");
		}
		
		File tmpFile = new File(oFile.getAbsolutePath() + "." + Constants.deletionTempFileExtension);
		
		BufferedWriter fw = new BufferedWriter(new FileWriter(tmpFile));
		
		fw.write(Constants.headerStartLine + "\n");
		fw.write(Constants.commentStart + "Annotator: " + annotatorName + "\n");
		
		Date date = new Date();
		String utcString = DateFormat.getDateInstance(DateFormat.LONG).format(date) + Constants.annotationFileDelimiter + DateFormat.getTimeInstance(DateFormat.LONG).format(date);
		fw.write(Constants.commentStart + "UTC Locally Formatted: " +  utcString + "\n");
		
		fw.write(Constants.commentStart + "UNIX: " + System.currentTimeMillis()/1000 + "\n");
		
		fw.write(Constants.commentStart + "Program Version: " + Constants.programVersion + "\n");
		
		String[] osPropertyStrings = {"os.name", "os.arch", "user.name", "user.country", "user.language"};
		writePropertyLine("OS Properties", osPropertyStrings, fw);		
		String[] javaPropertyStrings = {
				"java.runtime.version", "java.specification.name", "java.specification.vendor", "java.specification.version", "java.vendor", "java.version",
				"java.vm.name", "java.vm.specification.name", "java.vm.specification.vendor", "java.vm.specification.version", "java.vm.vendor", "java.vm.version"};
		writePropertyLine("Java Properties", javaPropertyStrings, fw);

		fw.write("\n");
		
		BufferedReader br = new BufferedReader(new FileReader(oFile));
		String curLine;
		while((curLine = br.readLine()) != null) {
			fw.write(curLine + "\n");
		}
		br.close();
		
		fw.close();		
		
		if(oFile.delete() == false) {
			throw new IOException("could not delete old file");
		}
		if(tmpFile.renameTo(oFile) == false) {
			throw new IOException("could not rename temp deletion file to normal temp file");
		}

	}
	
	private static void writePropertyLine(String name, String[] properties, BufferedWriter fw) throws IOException {
		fw.write(Constants.commentStart + name + ": ");
		for(String prop: properties) {
			String propVal = System.getProperty(prop);
			if(propVal != null) {
				if(propVal.contains(Constants.propertyPairOpenBrace) || propVal.contains(Constants.propertyPairCloseBrace) || propVal.contains(Constants.annotationFileDelimiter)) {
					System.err.println("cannot store property value: " + propVal + " because it contains a reserved character");
					continue;
				}
			}
			else {
				System.err.println("no such property: " + prop);
				continue;
			}
			fw.write(Constants.propertyPairOpenBrace);
			fw.write(prop);
			fw.write(Constants.annotationFileDelimiter);
			fw.write(propVal);
			fw.write(Constants.propertyPairCloseBrace);
		}
		fw.write("\n");
	}
}
