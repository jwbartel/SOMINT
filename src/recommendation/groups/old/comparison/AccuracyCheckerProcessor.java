package recommendation.groups.old.comparison;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class AccuracyCheckerProcessor {
	
	static Map<Double,Integer> countOfCosts = new HashMap<Double, Integer>();
	static ArrayList<Integer> missedIdealsCounts = new ArrayList<Integer>();
	static ArrayList<Integer> predictedGroups = new ArrayList<Integer>();
	
	static class PosCostPairing implements Comparable<PosCostPairing>{
		int pos;
		Double cost;
		
		public PosCostPairing(int pos, double cost){
			this.pos = pos;
			this.cost = cost;
		}
		
		@Override
		public int compareTo(PosCostPairing arg0) {
			int compare = cost.compareTo(arg0.cost);
			if(compare == 0)
				return new Integer(pos).compareTo(arg0.pos);
			return compare;
		}
		
		public String toString(){
			return "("+pos+", "+cost+")";
		}
		
	}

	public static void processHybridFiles(File folder) throws IOException{
		
		countOfCosts.clear();
		missedIdealsCounts.clear();
		predictedGroups.clear();
		
		File[] files = folder.listFiles();
		Arrays.sort(files);
		
		for(int i=0; i<files.length; i++){
			
			BufferedReader in = new BufferedReader(new FileReader(files[i]));
			
			String line = in.readLine();
			while(line != null){
				
				if(line.equals("~~~~~~~~~~~~~~~~ AFTER JOIN ~~~~~~~~~~~~~~~~~")){
					line =  in.readLine();
					
					while(line != null && !line.equals("Recommended v Ideal: Cost Matrix")){
						line = in.readLine();
					}
					
					String names = in.readLine();
					String[] namesSplit = names.split(",");
					line = in.readLine();
					int groupCount = 0;
					if(line != null){
						String[] split = line.split(",");
						
						ArrayList<double[]> costMatrixObj = new ArrayList<double[]>();
						int width = split.length-1;
						while(line != null){
							split = line.split(",");
							groupCount++;
							double[] currValues = new double[width];
							
							for(int j=1; j<width+1; j++){
								if(!split[j].equals(" ")){
									currValues[j-1] = Double.parseDouble(split[j]);
								}else{
									currValues[j-1] = -1;
								}
							}
							costMatrixObj.add(currValues);
							line = in.readLine();
						}
						predictedGroups.add(groupCount);
						
						double[][] empty = new double[0][0];
						
						double[][] costMatrix = costMatrixObj.toArray(empty);
						getCountOfCosts(costMatrix, width);
						
					}
				}
				
				line = in.readLine();
			}
		
		}
		
		writeDataToFile(new File(folder, "summary.csv"));
	}
	
	public static void processLCMAFiles(File folder) throws IOException{
		
		countOfCosts.clear();
		missedIdealsCounts.clear();
		predictedGroups.clear();
		
		File[] files = folder.listFiles();
		Arrays.sort(files);
		
		for(int i=0; i<files.length; i++){
			
			BufferedReader in = new BufferedReader(new FileReader(files[i]));
			
			String line = in.readLine();
			while(line != null){
				
				if(line.equals("Recommended v Ideal: Cost Matrix")){
					line = in.readLine();
					
					
					String names = in.readLine();
					String[] namesSplit = names.split(",");
					line = in.readLine();
					if(line != null){
						int groupCount = 0;
						String[] split = line.split(",");
						
						ArrayList<double[]> costMatrixObj = new ArrayList<double[]>();
						int width = split.length-1;
						while(line != null){
							split = line.split(",");
							groupCount++;
							double[] currValues = new double[width];
							
							for(int j=1; j<width+1; j++){
								if(!split[j].equals(" ")){
									currValues[j-1] = Double.parseDouble(split[j]);
								}else{
									currValues[j-1] = -1;
								}
							}
							costMatrixObj.add(currValues);
							line = in.readLine();
						}
						predictedGroups.add(groupCount);
						
						double[][] empty = new double[0][0];
						
						double[][] costMatrix = costMatrixObj.toArray(empty);
						getCountOfCosts(costMatrix, width);
						
					}
				}
				
				line = in.readLine();
			}
		
		}
		
		writeDataToFile(new File(folder, "summary.csv"));
	}
	
	static void writeDataToFile(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		out.write("Costs,");
		Iterator<Double> costsIter = countOfCosts.keySet().iterator();
		int printCount = 0;
		while(costsIter.hasNext()){
			double cost = costsIter.next();
			int count = countOfCosts.get(cost);
			
			for(int i=0; i<count; i++){
				out.write(""+cost+",");
				printCount++;
			}
		}
		ArrayList<String> statLines = new ArrayList<String>();
		out.newLine();
		statLines.add("AVG,=AVERAGE(B1:"+getColumn(printCount)+"1)");
		out.newLine();
		statLines.add("STDEV,=STDEV(B1:"+getColumn(printCount)+"1)");
		out.newLine();
		
		printCount = 0;
		out.write("Missed ideals,");
		for(int i=0; i<missedIdealsCounts.size(); i++){
			out.write(""+missedIdealsCounts.get(i)+",");
			printCount++;
		}
		out.newLine();
		statLines.add("AVG,=AVERAGE(B4:"+getColumn(printCount)+"4)");
		out.newLine();
		statLines.add("STDEV,=STDEV(B4:"+getColumn(printCount)+"4)");
		out.newLine();
		
		printCount = 0;
		out.write("Predicted Groups,");
		for(int i=0; i < predictedGroups.size(); i++){
			out.write(""+predictedGroups.get(i)+",");
			printCount++;
		}
		out.newLine();
		statLines.add("AVG,=AVERAGE(B7:"+getColumn(printCount)+"7)");
		out.newLine();
		statLines.add("STDEV,=STDEV(B7:"+getColumn(printCount)+"7)");
		out.newLine();
		
		for(int i=0; i<statLines.size(); i++){
			out.write(statLines.get(i));
			out.newLine();
		}
		
		out.flush();
		out.close();
	}
	
	static String getColumn(int printCount){
		if(printCount == 0){
			return "B";
		}
		
		String result = ""+(char)('A'+(printCount)%26);
		
		printCount = printCount/26;
		while(printCount > 0){
			result = ""+(char)('A'+(printCount-1)%26)+result;
			printCount /= 26;
		}
		
		
		return result;
	}
	
	
	static void getCountOfCosts(double[][] costMatrix, int numIdeals){
		
		ArrayList<Integer> usedIdeals = new ArrayList<Integer>();
		ArrayList<Integer> usedPredictions = new ArrayList<Integer>();
		
		Map<Integer, Set<PosCostPairing>> idealToTopCost = new HashMap<Integer, Set<PosCostPairing>>();
		Map<Integer, Set<PosCostPairing>> predictionToTopCost = new HashMap<Integer, Set<PosCostPairing>>();
		
		
		for(int prediction=0; prediction<costMatrix.length; prediction++){
			if(usedPredictions.contains(prediction)) continue;
			
			for(int ideal=0; ideal < costMatrix[prediction].length; ideal++){
				if(usedIdeals.contains(ideal)) continue;
				
				double cost = costMatrix[prediction][ideal];
				if(cost == -1) continue;
				
				
				Set<PosCostPairing> idealPairings = idealToTopCost.get(ideal);
				if(idealPairings == null){
					idealPairings  = new TreeSet<PosCostPairing>();
					idealToTopCost.put(ideal, idealPairings);
				}
				idealPairings.add(new PosCostPairing(prediction, cost));
				
				Set<PosCostPairing> predictionPairings = predictionToTopCost.get(prediction);
				if(predictionPairings == null){
					predictionPairings  = new TreeSet<PosCostPairing>();
					predictionToTopCost.put(prediction, predictionPairings);
				}
				predictionPairings.add(new PosCostPairing(ideal, cost));
			}
			
		}
		
		while(true){
			Iterator<Integer> predictions = predictionToTopCost.keySet().iterator();
			

			
			Map<Integer, PosCostPairing> minPredictionPairings = null;
			double minCost = -1;

			
			//Find the lowest cost scores amongst the predictions
			while(predictions.hasNext()){
				
				int prediction = predictions.next();
				if(usedPredictions.contains(prediction)) continue;
				Set<PosCostPairing> pairings = predictionToTopCost.get(prediction);
				Iterator<PosCostPairing> pairingsIter = pairings.iterator();
				while(pairingsIter.hasNext()){
					PosCostPairing predictionPairing = pairingsIter.next();
					
					if(usedIdeals.contains(predictionPairing.pos)){
						//Remove any scores for ideals that have already been matched
						pairings.remove(predictionPairing);
						pairingsIter = pairings.iterator();
						continue;
						
					}
					
					if(minCost == -1 || minCost > predictionPairing.cost){
						//found a new minimum cost
						minPredictionPairings = new TreeMap<Integer, PosCostPairing>();
						minCost = predictionPairing.cost;
					}
					
					if(minCost == predictionPairing.cost){
						//The min cost is the current cost
						minPredictionPairings.put(prediction, predictionPairing);
					}else{
						//The min cost is less than the current cost
						break;
					}
					
				}
			}
			
			if(minPredictionPairings == null || minPredictionPairings.size() == 0) break;
			
			Iterator<Integer> predictionsIter = minPredictionPairings.keySet().iterator();
			while(predictionsIter.hasNext()){
				int prediction = predictionsIter.next();
				
				if(usedPredictions.contains(prediction)) continue;
				PosCostPairing pairing = minPredictionPairings.get(prediction);
				int ideal = pairing.pos;
				if(usedIdeals.contains(ideal)) continue;
				
				if(!idealToTopCost.containsKey(ideal)) continue;
				
				Set<PosCostPairing> idealPairingsSet = idealToTopCost.get(ideal);
				Iterator<PosCostPairing> idealPairingsIter = idealPairingsSet.iterator();
				while(idealPairingsIter.hasNext()){
					PosCostPairing idealPairing = idealPairingsIter.next();
					if(usedPredictions.contains(idealPairing.pos)){
						idealPairingsSet.remove(idealPairing);
						idealPairingsIter = idealPairingsSet.iterator();
						continue;
					}
					
					if(idealPairing.pos == prediction){
						usedIdeals.add(ideal);
						usedPredictions.add(prediction);
						incrementCostCount(pairing.cost);
					}
					break;
				}
				
			}
		}
		
		missedIdealsCounts.add(numIdeals-usedIdeals.size());
		
		System.out.println(countOfCosts);
	}
	
	static void incrementCostCount(double cost){
		Integer oldCount = countOfCosts.get(cost);
		if(oldCount == null){
			oldCount = 1;
		}else{
			oldCount++;
		}
		
		countOfCosts.put(cost, oldCount);
	}
	
	public static void main(String[] args) throws IOException{
		
		//processHybridFiles(new File("data/Jacob/Stats/seedless/CompareResults/Hybrid/"));
		
		File lcmaFolder = new File("data/Jacob/Stats/seedless/CompareResults/LCMA/");
		File[] thresholdFolders = lcmaFolder.listFiles();
		Arrays.sort(thresholdFolders);
		
		for(int i=0; i<thresholdFolders.length; i++){
			File thresholdFolder = thresholdFolders[i];
			if(thresholdFolder.isDirectory()){
				processLCMAFiles(thresholdFolder);
			}
		}
	}
}
