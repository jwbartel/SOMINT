package recommendation.groups.evolution.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MeanAndSTDEVCollector {

	static final String listSizesFile = "list sizes.csv";
	static final String emptySeedsFile = "empty seeds.csv";
	static final String normalizedManualEffortFile = "normalized manual effort.csv";
	static final String leastEffortDepthFile = "least effort depth.csv";
	static final String normalizedLeastEffortFile = "normalized least effort.csv";
	
	static double[] percentages = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.6, 0.7, 0.8, 0.9};
	
	static Map<Double, Integer> percentPosMap = new HashMap<Double, Integer>();
	
	Map<Double, ArrayList<Double>> listSizesMap = new TreeMap<Double, ArrayList<Double>>();
	Map<Double, ArrayList<Double>> emptySeedsMap = new TreeMap<Double, ArrayList<Double>>();
	Map<Double, ArrayList<Double>> normalizedManualEffortMap = new TreeMap<Double, ArrayList<Double>>();
	Map<Double, ArrayList<Double>> leastEffortDepthsMap = new TreeMap<Double, ArrayList<Double>>();
	Map<Double, ArrayList<Double>> normalizedLeastsEffortMap = new TreeMap<Double, ArrayList<Double>>();

	
	public static void buildPercentPosMap(){
		percentPosMap.clear();		
		for(int percentagesPos=0; percentagesPos<percentages.length; percentagesPos++){
						
			double percentage = percentages[percentagesPos];
			percentPosMap.put(percentage, percentagesPos);
		}
	}	
	
	public void collectForFolder(String folder, String outfolder) throws IOException{
		collectForFolder(new File(folder), new File(outfolder));
	}
	
	public void collectForFolder(File folder, File outFolder) throws IOException{
		buildPercentPosMap();
		
		double[] deletionRates = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.90};
		double[] errors = {0.0, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50, 0.55, 0.60, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00};
		
		String effortHeader = "deletion rate,error,";
		for(int i=0; i<percentages.length; i++){
			effortHeader += ","+percentages[i];
		}
		writeHeader(effortHeader, new File(outFolder, listSizesFile));
		writeHeader(effortHeader, new File(outFolder, emptySeedsFile));
		writeHeader(effortHeader, new File(outFolder, normalizedManualEffortFile));
		writeHeader(effortHeader, new File(outFolder, leastEffortDepthFile));
		writeHeader(effortHeader, new File(outFolder, normalizedLeastEffortFile));
		
		for(int deletionPos=0; deletionPos<deletionRates.length; deletionPos++){
			double deletionRate = deletionRates[deletionPos];
			
			for(int errorPos =0; errorPos < errors.length; errorPos++ ){
				double error = errors[errorPos];
				
				File file = new File(folder,"efforts "+deletionRate+"deletion "+error+"effort .csv");
				collectForFile(file, outFolder);
			}
		}
	}
	
	public void collectForFile(File file, File outFolder) throws IOException{
		listSizesMap.clear();
		emptySeedsMap.clear();
		leastEffortDepthsMap.clear();
		normalizedLeastsEffortMap.clear();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		double deletionRate = 0.0;
		double error = 0.0;
		
		
		String line = in.readLine();
		line = in.readLine();
		while(line != null){
			String[] split = line.split(",");
			
			
			deletionRate = Double.parseDouble(split[1]);
			error = Double.parseDouble(split[2]);
			double percentNew = Double.parseDouble(split[3]);
			double intendedSize = Integer.parseInt(split[4]);
			double manualEffort = Integer.parseInt(split[5]); 
			double listSize = Integer.parseInt(split[6]);
			double depthOfLeastEffort = Integer.parseInt(split[7]);
			double leastEffort = Integer.parseInt(split[8]);
			
			ArrayList<Double> listSizes = listSizesMap.get(percentNew);
			if(listSizes == null){
				listSizes = new ArrayList<Double>();
				listSizesMap.put(percentNew, listSizes);
			}
			listSizes.add(listSize);
			
			ArrayList<Double> emptySeeds = emptySeedsMap.get(percentNew);
			if(emptySeeds == null){
				emptySeeds = new ArrayList<Double>();
				emptySeedsMap.put(percentNew, emptySeeds);
			}
			double emptySeedValue = (manualEffort == intendedSize)? 1: 0;
			emptySeeds.add(emptySeedValue);
			
			ArrayList<Double> normalizedManualEfforts = normalizedManualEffortMap.get(percentNew);
			if(normalizedManualEfforts == null){
				normalizedManualEfforts = new ArrayList<Double>();
				normalizedManualEffortMap.put(percentNew, normalizedManualEfforts);
			}
			double normalizedManualEffort = getNormalizedManualEffort(intendedSize, manualEffort);
			normalizedManualEfforts.add(normalizedManualEffort);
			
			ArrayList<Double> leastEffortDepths = leastEffortDepthsMap.get(percentNew);
			if(leastEffortDepths == null){
				leastEffortDepths = new ArrayList<Double>();
				leastEffortDepthsMap.put(percentNew, leastEffortDepths);
			}
			if(depthOfLeastEffort != -1){
				leastEffortDepths.add(depthOfLeastEffort);
			}
			
			ArrayList<Double> normalizedLeastEfforts = normalizedLeastsEffortMap.get(percentNew);
			if(normalizedLeastEfforts == null){
				normalizedLeastEfforts = new ArrayList<Double>();
				normalizedLeastsEffortMap.put(percentNew, normalizedLeastEfforts);
			}
			double normalizedLeastEffort = getNormalizedLeastEffort(intendedSize, manualEffort, leastEffort);
			normalizedLeastEfforts.add(normalizedLeastEffort); 
					
			line = in.readLine();
		}
		
		double[] listSizeAverages = new double[percentages.length];
		double[] listSizeSTDEVs = new double[percentages.length];
		
		double[] emptySeedAverages = new double[percentages.length];
		double[] emptySeedSTDEVs = new double[percentages.length];
		
		double[] normalizedManualEffortAverages = new double[percentages.length];
		double[] normalizedManualEffortSTDEVs = new double[percentages.length];
		
		double[] depthLeastEffortAverages = new double[percentages.length];
		double[] depthLeastEffortSTDEVs = new double[percentages.length];
		
		double[] normalizedLeastEffortAverages = new double[percentages.length];
		double[] normalizedLeastEffortSTDEVs = new double[percentages.length];
		
		for(int i=0; i<percentages.length; i++){
			double percentNew = percentages[i];
			
			double[] listSizeVals = getMeanAndSTDEV(listSizesMap.get(percentNew));
			listSizeAverages[i] = listSizeVals[0];
			listSizeSTDEVs[i] = listSizeVals[1];
			
			double[] emptySeedVals = getMeanAndSTDEV(emptySeedsMap.get(percentNew));
			emptySeedAverages[i] = emptySeedVals[0];
			emptySeedSTDEVs[i] = emptySeedVals[1];
			
			double[] normalizedManualEffortVals = getMeanAndSTDEV(normalizedManualEffortMap.get(percentNew));
			normalizedManualEffortAverages[i] = normalizedManualEffortVals[0];
			normalizedManualEffortSTDEVs[i] = normalizedManualEffortVals[1];
			
			double[] depthLeastEffortVals = getMeanAndSTDEV(leastEffortDepthsMap.get(percentNew));
			depthLeastEffortAverages[i] = depthLeastEffortVals[0];
			depthLeastEffortSTDEVs[i] = depthLeastEffortVals[1];
			
			double[] normalizedLeastEffortVals = getMeanAndSTDEV(normalizedLeastsEffortMap.get(percentNew));
			normalizedLeastEffortAverages[i] = normalizedLeastEffortVals[0];
			normalizedLeastEffortSTDEVs[i] = normalizedLeastEffortVals[1];
			
		}
		
		writeEffortValues(deletionRate, error, listSizeAverages, listSizeSTDEVs, new File(outFolder, listSizesFile));
		writeEffortValues(deletionRate, error, emptySeedAverages, emptySeedSTDEVs, new File(outFolder, emptySeedsFile));
		writeEffortValues(deletionRate, error, normalizedManualEffortAverages, normalizedManualEffortSTDEVs, new File(outFolder, normalizedManualEffortFile));
		writeEffortValues(deletionRate, error, depthLeastEffortAverages, depthLeastEffortSTDEVs, new File(outFolder, leastEffortDepthFile));
		writeEffortValues(deletionRate, error, normalizedLeastEffortAverages, normalizedLeastEffortSTDEVs, new File(outFolder, normalizedLeastEffortFile));
		
		
		
	}
	
	public double getNormalizedManualEffort(double intendedGroupSize, double manualEffort){
		return manualEffort/intendedGroupSize;
	}
	
	public double getNormalizedLeastEffort(double intendedGroupSize, double manualEffort, double leastEffort){
		if(leastEffort > 0){
			if(manualEffort == 0){
				return 1.0 / intendedGroupSize;
			}else{
				return leastEffort / intendedGroupSize;
			}
		}else if(leastEffort < 0){
			return getNormalizedManualEffort(intendedGroupSize, manualEffort);
		}else{
			return leastEffort / intendedGroupSize;
		}
	}
	
	public static void writeHeader(String header, File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		out.write(header);
		out.newLine();
		out.flush();
		out.close();
	}
	
	public static void writeEffortValues(double deletionRate, double error, double[] averages, double[] stdevs, File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest, true));
		
		String meanLine = ""+deletionRate+","+error;//+","+"AVG";
		String stdevLine = ",,STDEV";
		for(int i=0; i<averages.length; i++){
			meanLine += "," + averages[i];
			stdevLine += "," + stdevs[i];
		}
		
		out.write(meanLine);
		out.newLine();
		//out.write(stdevLine);
		//out.newLine();
		out.flush();
		out.close();
	}
	
	
	public static double[] getMeanAndSTDEV(ArrayList<Double> values){
		
		double total = 0;
		for(int i=0; i<values.size(); i++){
			total += values.get(i);
		}
		
		double mean = (values.size()>0)?total/values.size():0;
		
		
		double stdDevNumerator = 0;
		for(int i=0; i<values.size(); i++){
			stdDevNumerator += Math.pow(values.get(i)-mean, 2);
		}
		double stdev = Math.sqrt(stdDevNumerator/values.size());
		
		double[] vals = {mean, stdev};
		return vals;
	}
	
	
	public static void main(String[] args) throws IOException{
		
		MeanAndSTDEVCollector collector = new MeanAndSTDEVCollector();
		collector.collectForFolder("data/Jacob/Stats/maintenance/adjustedMatching/data","data/Jacob/Stats/maintenance/adjustedMatching/analysis");
	}
}
