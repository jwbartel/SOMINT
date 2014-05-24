package util.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class FacebookStatsFinder {
	
	static int[] accounts = {8, 10, 12, 13, 16, 17, 19, 21, 22, 23, 24, 25};
	
	private final File idealFolder;
	private DescriptiveStatistics idealSizeStats;
	private DescriptiveStatistics numIdealStats;
	
	
	public FacebookStatsFinder(File idealFolder) throws IOException {
		this.idealFolder = idealFolder;
		readIdeals();
	}
	
	private void readIdeals() throws IOException {
		idealSizeStats = new DescriptiveStatistics();
		numIdealStats = new DescriptiveStatistics();
		
		for(int account: accounts) {
			File accountIdealFile = new File(idealFolder, ""+account+"_ideal.txt");
			BufferedReader in = new BufferedReader(new FileReader(accountIdealFile));
			ArrayList<String> currMembers = new ArrayList<String>();
			int accountIdealLists = 0;
			in.readLine();
			String line = in.readLine();
			
			while (line != null) {
				
				if (line.length() == 0) {
					idealSizeStats.addValue(currMembers.size());
					currMembers = new ArrayList<String>();
					accountIdealLists++;
					line = in.readLine();
				} else {
					currMembers.add(line);
				}
				
				line = in.readLine();
			}
			numIdealStats.addValue(accountIdealLists);
		}
	}
	
	private static void printSummary(DescriptiveStatistics stats, String title) {
		System.out.println("============" + title + "============");
		System.out.println("Mean: " + stats.getMean());
		System.out.println("STDEV: " + stats.getStandardDeviation());
		System.out.println("Min: " + stats.getMin());
		System.out.println("Median: " + stats.getPercentile(50.0));
		System.out.println("Max: " + stats.getMax());
	}
	
	public static void main(String[] args) throws IOException {
		FacebookStatsFinder finder = new FacebookStatsFinder(new File("C:\\Users\\Jacob\\Workspaces\\Research Data\\Facebook data\\ideal"));
		printSummary(finder.numIdealStats, "Number of ideal friend lists");
		printSummary(finder.idealSizeStats, "Size of ideal friend lists");
	}
}
