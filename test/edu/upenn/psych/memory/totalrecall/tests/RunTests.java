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

package edu.upenn.psych.memory.totalrecall.tests;

import java.util.Scanner;

/**
* @author Apurva Jatakia
*/
public class RunTests {

	public static void main(String[] args) {
		DemoTest test = new DemoTest();
		OpenTest opentest = new OpenTest();
		Thread thread = new Thread(opentest);
		int response;
		do {
			System.out.println("Options");
			System.out.println("1. Test Open");
			System.out.println("2. Test PlayAt");
			System.out.println("3. Test Stop");
			System.out.println("4. Get Statistics");
			System.out.println("5. Test the OpenAL Player");
			System.out.println("Enter your choice:");
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			switch (choice) {
			case 1:
				test.testOpen();
				break;
			case 2:
				test.testPlatAt();
				break;
			case 3:
				test.testStop();
				break;
			case 4:
				test.getPlayerStats();
				break;
			case 5:
				thread.start();
				break;
			default:
				break;
			}

			System.out.println("Do You want to continue:(y(1)/n(2))");
			response = scan.nextInt();
		} while (response== 1);

		System.out
				.println("Thank you for testing. Check the output log file generated.");
//		new PlayPauseTest();
	}
}
