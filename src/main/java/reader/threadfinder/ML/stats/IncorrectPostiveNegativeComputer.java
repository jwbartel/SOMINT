package reader.threadfinder.ML.stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class IncorrectPostiveNegativeComputer {

	public static double[][] loadScores(File scoresFile) throws IOException{
		
		int iterations = 51;
		int predictionsPerIter = 0;
		
		
		BufferedReader in = new BufferedReader(new FileReader(scoresFile));
		
		//Find how big to make arrays
		String line = in.readLine();
		while(line != null){
			predictionsPerIter++;
			line =in.readLine();
		}
		in.close();

		
		
		double[][] scores = new double[iterations][predictionsPerIter];
		in = new BufferedReader(new FileReader(scoresFile));
		line = in.readLine();
		
		int predictionNum = 0;
		while(line != null){
			
			String[] scoreStrs = line.split(",");
			for(int iter = 0; iter<scoreStrs.length; iter++){
				scores[iter][predictionNum] = Double.parseDouble(scoreStrs[iter]);
			}

			line = in.readLine();
			
			predictionNum++;
		}
		
		return scores;
	}
	
	
	public static double[] getMissedPostiveRates(File labels, double[][] scores) throws IOException{
		
		int[] totals = new int[scores.length];
		int[] positives = new int[scores.length];
		int[] incorrectPositives = new int[scores.length];
		
		BufferedReader labelsIn = new BufferedReader(new FileReader(labels));
		
		String labelStr = labelsIn.readLine();
		int prediction = 0;
		while(labelStr != null){
		
			
			int label = Integer.parseInt(labelStr);
			for(int iter=0; iter<scores.length; iter++){
				double score = scores[iter][prediction];
				totals[iter]++;
				if(label > 0){
					positives[iter]++;
					if(score <= 0.5){
						incorrectPositives[iter]++;
					}
				}
			}
			
			labelStr = labelsIn.readLine();
			prediction++;
		}
		
		labelsIn.close();
		
		double[] missedPostiveRates = new double[scores.length];
		for(int iter=0; iter<missedPostiveRates.length; iter++){
			missedPostiveRates[iter] = ((double) incorrectPositives[iter])/((double) positives[iter]);
		}
		return missedPostiveRates;
	}
	
	public static double[] getMissedNegativeRates(File labels, double[][] scores) throws IOException{
		
		int[] negatives = new int[scores.length];
		int[] incorrectNegatives = new int[scores.length];
		
		BufferedReader labelsIn = new BufferedReader(new FileReader(labels));
		
		String labelStr = labelsIn.readLine();
		int prediction = 0;
		while(labelStr != null){
		
			
			int label = Integer.parseInt(labelStr);
			for(int iter=0; iter<scores.length; iter++){
				double score = scores[iter][prediction];
				if(label < 0){
					negatives[iter]++;
					if(score >= 0.5){
						incorrectNegatives[iter]++;
					}
				}
			}
			
			labelStr = labelsIn.readLine();
			prediction++;
		}
		
		labelsIn.close();
		
		double[] missedNegativeRates = new double[scores.length];
		for(int iter=0; iter<missedNegativeRates.length; iter++){
			missedNegativeRates[iter] = ((double) incorrectNegatives[iter])/((double) negatives[iter]);
		}
		return missedNegativeRates;
	}
	
	public static void writeMissedRates(File labelsAndScoresFolder, File positivesOutFile, File negativesOutFile) throws IOException {
		
		File[] files = labelsAndScoresFolder.listFiles();
		//BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		
		ArrayList<double[]> allMissedPositiveRates = new ArrayList<double[]>();
		ArrayList<double[]> allMissedNegativeRates = new ArrayList<double[]>();
		
		int maxNumIterations = 0;
		for(File file: files){
			String fileName = file.getName();
			if(fileName.startsWith("labels_run_")){
				
				String scoresFileName = "scores"+fileName.substring("labels".length());
				File  scoresFile = new File(labelsAndScoresFolder, scoresFileName);

				double[][] scores = loadScores(scoresFile);
				double[] missedPostiveRates = getMissedPostiveRates(file, scores);
				double[] missedNegativeRates = getMissedNegativeRates(file, scores);
				
				int numIterations = missedPostiveRates.length;
				if(maxNumIterations < numIterations){
					maxNumIterations = numIterations;
				}
				
				allMissedPositiveRates.add(missedPostiveRates);
				allMissedNegativeRates.add(missedNegativeRates);
				
			}
		}
		
		BufferedWriter positivesOut = new BufferedWriter(new FileWriter(positivesOutFile));
		BufferedWriter negativesOut = new BufferedWriter(new FileWriter(negativesOutFile));
		for(int iter=0; iter<maxNumIterations; iter++){
			
			for(int run=0; run<allMissedPositiveRates.size(); run++){
				
				double[] missedPostiveRates = allMissedPositiveRates.get(run);
				if(missedPostiveRates.length > iter){
					positivesOut.write(""+missedPostiveRates[iter]);
				}
				positivesOut.write(",");
				
				double[] missedNegativeRates = allMissedNegativeRates.get(run);
				if(missedNegativeRates.length > iter){
					negativesOut.write(""+missedNegativeRates[iter]);
				}
				negativesOut.write(",");
				
			}

			positivesOut.newLine();
			negativesOut.newLine();
			
		}
		
		positivesOut.flush();
		positivesOut.close();
		
		negativesOut.flush();
		negativesOut.close();
		
		//out.flush();
		//out.close();
		
	}
	
	public static void main(String[] args) throws IOException{
		File rootFolder = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\results\\truncated");
		File labelsAndScoresFolder = new File(rootFolder, "labels and scores");
		File missedPositivesFile = new File(rootFolder, "missed postives.csv");
		File missedNegativesFile = new File(rootFolder, "missed negatives.csv");
		writeMissedRates(labelsAndScoresFolder, missedPositivesFile, missedNegativesFile);
	}
	
}
