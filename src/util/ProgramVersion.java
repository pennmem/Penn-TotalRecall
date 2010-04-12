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

package util;

import info.Constants;

import java.util.Scanner;

/**
 * Represents a version number of this program. Useful for its <code>equals()</code> and <code>compareTo()</code> methods.
 * 
 * @author Yuvi Masory
 */
public class ProgramVersion implements Comparable<ProgramVersion> {

	private int majorNumber;
	private int minorNumber;
	
	private ProgramVersion(String repr) {
		Scanner sc = new Scanner(repr).useDelimiter(Constants.programVersionDelimiter);
		majorNumber = -1;
		minorNumber = - 1;
		if(sc.hasNextInt()) {
			majorNumber = sc.nextInt();
			if(sc.hasNextInt()) {
				minorNumber = sc.nextInt();
			}
		}
	}
	
	public int getMajorNumber() {
		return majorNumber;
	}
	
	public int getMinorNumber() {
		return minorNumber;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ProgramVersion == false) {
			return false;
		}
		ProgramVersion otherVersion = (ProgramVersion)o;
		return otherVersion.getMajorNumber() == getMajorNumber() && otherVersion.getMinorNumber() == getMinorNumber();
	}

	public int compareTo(ProgramVersion otherVersion) {
		if(this.equals(otherVersion)) {
			return 0;
		}
		if(otherVersion.getMajorNumber() > this.getMajorNumber()) {
			return -1;
		}
		else if(otherVersion.getMajorNumber() < this.getMajorNumber()) {
			return 1;
		}
		else if(otherVersion.getMinorNumber() > this.getMinorNumber()) {
			return -1;
		}
		else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		return Integer.toString(majorNumber).hashCode() + Integer.toString(minorNumber).hashCode();
	}
	
	public static ProgramVersion getCurrentVersionNumber() {
		return new ProgramVersion(Constants.programVersion);
	}
	
	public static ProgramVersion getSavedVersionNumber(String repr) {
		if(validateVersionString(repr)) {
			return new ProgramVersion(repr);
		}
		else {
			throw new IllegalArgumentException("not a valid version string");			
		}
	}
	
	private static boolean validateVersionString(String version) {
		Scanner sc = new Scanner(version).useDelimiter(Constants.programVersionDelimiter);
		if(sc.hasNextInt()) {
			sc.nextInt();
			if(sc.hasNextInt()) {
				sc.nextInt();
				return true;
			}
		}
		return false;
	}
}
