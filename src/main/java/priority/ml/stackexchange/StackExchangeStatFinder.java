package priority.ml.stackexchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import priority.ml.InteractionDataPoint;

public class StackExchangeStatFinder {

	File rootFolder;
	
	public StackExchangeStatFinder(String rootFolder) {
		this.rootFolder = new File(rootFolder);
	}
	
	public SummaryStatistics getThreadSizes() throws IOException {
		SummaryStatistics retVal = new SummaryStatistics();
		File threadsFile = new File(rootFolder, "threads.csv");
		BufferedReader in = new BufferedReader(new FileReader(threadsFile));
		String line = in.readLine();
		while(line != null) {
			String threadSizeStr = line.substring(line.lastIndexOf(',')+1,line.length()-1);
			int threadSize = Integer.parseInt(threadSizeStr);
			if (threadSize >= 2) retVal.addValue(threadSize);
			line = in.readLine();
		}
		in.close();
		return retVal;
	}
	
	public SummaryStatistics getResponseTimes() throws IOException {
		SummaryStatistics retVal = new SummaryStatistics();
		File interactionsFolder = new File(rootFolder, "interactions");
		File[] interactions = interactionsFolder.listFiles();
		for(File interaction: interactions) {
			FileInputStream is = new FileInputStream(interaction);
			InteractionDataPoint interactionPoint = StackExchangeDataPointRetriever.getInteractionDataPoint(is);
			
			Long timeSinceLast = interactionPoint.getTimeSinceLastInteraction();
			if(timeSinceLast != null) retVal.addValue(timeSinceLast);
			is.close();
		}
		return retVal;
	}
	
	public static void main(String[] args) throws IOException {
		String root = "C:\\Users\\Jacob\\Workspaces\\recipientprediction\\Email Predictions\\data\\Jacob\\StackOverflow\\precomputes";
		StackExchangeStatFinder finder = new StackExchangeStatFinder(root);
		SummaryStatistics threadSizes = finder.getThreadSizes();
		System.out.println("Average thread size:"+threadSizes.getMean());
		
		SummaryStatistics responseTimes = finder.getResponseTimes();
		System.out.println("Average response time:"+ (responseTimes.getMean()/1000/3600/24) + " days");
	}
}
