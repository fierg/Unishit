package u8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Wahrenkorbanalyse {

	// private List<Set<String>> transactions;

	private static int[][] transations = { { 1, 1, 1, 0, 0, 0 }, { 0, 1, 0, 0, 1, 1 }, { 1, 0, 1, 0, 1, 0 }, { 1, 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 1 }, { 0, 1, 0, 0, 1, 0 }, { 1, 1, 1, 0, 1, 1 },
			{ 1, 0, 0, 1, 0, 0 }, { 1, 0, 0, 0, 1, 1 } };


	public void getSubsetsForMinfreq(double minFreq){
		
		Set<String[]> passedSubsets = new HashSet<String[]>();
		Set<String[]> failedSubsets = new HashSet<String[]>();
		Set<String[]> trySubsets = new HashSet<String[]>();
		
		for (int[] set : transations) {
			
			
			
			
		}
		
		
		
	}
	
	private String getRealName(int position){
		switch (position) {
		case 0: return "A";
		case 1: return "B";
		case 2: return "C";
		case 3: return "D";
		case 4: return "E";
		case 5: return "F";
		default:
			throw new IllegalArgumentException();
		}
		
		
	}
	
}
