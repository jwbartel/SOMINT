package reader.threadfinder.newsgroups.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import reader.threadfinder.IteratedThreadStatistics;
import reader.threadfinder.ThreadStatistics;

public class GaussianFinder {

	public static Collection<Double> findExtrapolatedGaussianDistrib(Collection<Double> values, Double expectedMean){
		Collection<Double> retVal = new ArrayList<Double>(values);
		
		for(Double value: values){
			retVal.add((value - expectedMean)*(-1.0) + expectedMean);
		}
		
		return retVal;
	}
	
	public static Collection<Double> findGaussianThreadLength(Map<Integer, ThreadStatistics> finalThreadStatistics){
		
		Double minLength = null;
		Collection<Double> lengths = new ArrayList<Double>();
		
		for(ThreadStatistics stats: finalThreadStatistics.values()){
			if(stats.getNumMessages() > 1){
				if(minLength == null || stats.getNumMessages() < minLength){
					minLength = (double) stats.getNumMessages();
				}
				lengths.add((double) stats.getNumMessages());
			}
		}
		
		return findExtrapolatedGaussianDistrib(lengths, minLength);
	}
	
	public static Collection<Double> findGaussianTotalThreadTime(Map<Integer, ThreadStatistics> finalThreadStatistics){
		
		Double minTotalTime = null;
		Collection<Double> totalTimes = new ArrayList<Double>();
		
		for(ThreadStatistics stats: finalThreadStatistics.values()){
			if(stats.getNumMessages() > 1){
				if(minTotalTime == null || stats.getTotalTime() < minTotalTime){
					minTotalTime = (double) stats.getTotalTime();
				}
				totalTimes.add((double) stats.getTotalTime());
			}
		}
		
		return findExtrapolatedGaussianDistrib(totalTimes, minTotalTime);
	}
	
	public static Collection<Double> findGaussianTimeBetweenThreads(ArrayList<IteratedThreadStatistics> iteratedThreadStatistics){
		
		Double minTime = null;
		Collection<Double> times = new ArrayList<Double>();
		
		Set<Integer> seenThreads = new HashSet<Integer>();
		Set<Long> creationDates = new TreeSet<Long>();
		
		for(IteratedThreadStatistics iterStats: iteratedThreadStatistics){
			if(!seenThreads.contains(iterStats.getThreadID()) && iterStats.getStatistics().getNumMessages() > 1 && iterStats.getStatistics().getTimeLastMessage() != null){
				long startTime = iterStats.getStatistics().getTimeLastMessage().getTime() - iterStats.getStatistics().getTotalTime();
				creationDates.add(startTime);
				seenThreads.add(iterStats.getThreadID());
			}
		}
		
		Long prevCreationDate = null;
		for(Long creationDate: creationDates){
			if(prevCreationDate != null){
				double time = ((double) (creationDate - prevCreationDate));
				if(minTime == null || minTime > time){
					minTime = time;
				}
				times.add(time);
			}
			
			prevCreationDate = creationDate;
		}
		
		return findExtrapolatedGaussianDistrib(times, minTime);
	}
	
	public static void writeDistribution(Collection<Double> distrib, File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		for(Double val: distrib){
			out.write(""+val);
			out.newLine();
		}
		out.flush();
		out.close();
	}
}
