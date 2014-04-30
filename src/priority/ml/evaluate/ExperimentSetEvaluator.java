package priority.ml.evaluate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class ExperimentSetEvaluator {

	private static void writeConstantPredictions(File trueVals, File dest, long constant) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(trueVals));
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		double doubleConstant = (double) constant;
		String line = in.readLine();
		while (line != null) {
			
			out.write(""+doubleConstant);
			out.newLine();
			out.flush();
			
			line = in.readLine();
		}
		out.close();
		in.close();
	}
	
	public static void writeConstantPredictionsForRuns(File root, int numRuns, String experimentLabel, long constant) throws IOException {
		for (int run = 1; run <= numRuns; run++) {
			File runFolder = new File(root, ""+run);
			
			File testVals = new File(runFolder, "test.csv");
			File outFile = new File(runFolder, experimentLabel + " results.csv");
			writeConstantPredictions(testVals, outFile, constant);
		}
	}
	
	private static Map<String,BufferedReader> openFiles(File runFolder, String[] predictionTypes) throws IOException{
		Map<String, BufferedReader> readers = new TreeMap<String, BufferedReader>();
		for(String predictionType: predictionTypes) {
			File predictions = new File(runFolder, predictionType + " results.csv");
			BufferedReader in = new BufferedReader(new FileReader(predictions));
			readers.put(predictionType, in);
		}
		return readers;
	}
	
	private static void close(Map<String, BufferedReader> ins) throws IOException {
		for (Entry<String, BufferedReader> entry : ins.entrySet()) {
			entry.getValue().close();
		}
	}
	
	private static boolean updateNextVal(Map<String, BufferedReader> ins, Map<String, Double> vals) throws IOException {
		boolean hasMore = true;
		boolean wasSet = false;
		
		for (Entry<String, BufferedReader> entry : ins.entrySet()) {
			String predictionType = entry.getKey();
			BufferedReader in = entry.getValue();
			
			String line = in.readLine();
			boolean notNull = line != null;
			if (notNull != hasMore && wasSet ) {
				throw new IOException("Unequal number of lines in files");
			}
			hasMore = notNull;
			wasSet = true;
			
			Double val = null;
			if (line != null && !line.equals("null")) {
				val = Double.parseDouble(line);
			}
			
			vals.put(predictionType, val);
		}
		
		return hasMore;
	}
	
	private static ResponseTimeEvaluator getEvaluator(Map<String, Map<String, ResponseTimeEvaluator>> evaluators,
			Map<String,ResponseTimeEvaluatorFactory> evaluatorFactories, String predictionType, String metric) {
		
		Map<String,ResponseTimeEvaluator> typeEvaluators = evaluators.get(predictionType);
		if(typeEvaluators == null) {
			typeEvaluators = new TreeMap<String, ResponseTimeEvaluator>();
			evaluators.put(predictionType, typeEvaluators);
		}
		
		ResponseTimeEvaluator evaluator = typeEvaluators.get(metric);
		if(evaluator == null) {
			ResponseTimeEvaluatorFactory factory = evaluatorFactories.get(metric);
			evaluator = factory.create();
			typeEvaluators.put(metric, evaluator);
		}
		
		return evaluator;
	}
	
	private static Double getTrueVal(String trueValLine){
		String[] split = trueValLine.split(",");
		return Double.parseDouble(split[2]);
	}
	
	public static Map<String, Map<String, SummaryStatistics>> evaluateForExperimentSet (File setRoot,
			int numRuns, String[] predictionTypes, Map<String,ResponseTimeEvaluatorFactory> evaluatorFactories ) throws IOException {
		
		Map<String, Map<String, ResponseTimeEvaluator>>  evaluators = new TreeMap<String, Map<String,ResponseTimeEvaluator>>();
		
		
		for(int run=1; run <= numRuns; run++) {
			
			File runFolder = new File(setRoot, ""+run);
			File trueValsFile  = new File(runFolder, "test.csv");
			
			BufferedReader trueValIn = new BufferedReader(new FileReader(trueValsFile));
			Map<String, BufferedReader> predictionIns = openFiles(runFolder, predictionTypes);
			
			String trueValLine = trueValIn.readLine();
			Map<String, Double> predictionVals = new TreeMap<String, Double>();
			updateNextVal(predictionIns, predictionVals);
			while (trueValLine != null) {
				Double trueVal = getTrueVal(trueValLine);
				
				for (String predictionType : predictionTypes) {
					
					Double predictonVal = predictionVals.get(predictionType);
					for (String metric : evaluatorFactories.keySet()) {
						ResponseTimeEvaluator evaluator = getEvaluator(evaluators, evaluatorFactories, predictionType, metric);
						evaluator.updateStats(run, trueVal, predictonVal);
					}
				}
				
				trueValLine = trueValIn.readLine();
				updateNextVal(predictionIns, predictionVals);
			}
			trueValIn.close();
			close(predictionIns);
		}
		
		
		Map<String, Map<String,SummaryStatistics>> retVal = new TreeMap<String, Map<String,SummaryStatistics>>();
		
		for (String predictionType : predictionTypes) {
			
			Map<String, SummaryStatistics> typeStats = retVal.get(predictionType);
			if (typeStats == null) {
				typeStats = new TreeMap<String, SummaryStatistics>();
				retVal.put(predictionType, typeStats);
			}
			for (String metric : evaluatorFactories.keySet()) {
				
				ResponseTimeEvaluator evaluator = getEvaluator(evaluators, evaluatorFactories, predictionType, metric);
				typeStats.put(metric, evaluator.getResults());
			}
		}
		
		
		return retVal;
	}
	
	private static void writeHeader(BufferedWriter out, Map<String, Map<String, SummaryStatistics>> stats) throws IOException {
		out.write(",");
		Set<String> metrics = stats.values().iterator().next().keySet();
		
		for (String metric : metrics) {
			out.write(metric + ",,");
		}
		out.newLine();

		out.write(",");
		for (int i = 0; i < metrics.size(); i++) {
			out.write("mean,stdev,");
		}
		out.newLine();
		out.flush();
	}
	
	private static void writeResults(File resultsFile, String[] predictionTypes,
			Map<String, Map<String, SummaryStatistics>> stats) throws IOException {
		
		BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
		writeHeader(out, stats);
		
		for (String predictionType: predictionTypes) {
			
			Map<String, SummaryStatistics> metricStats = stats.get(predictionType);
			
			out.write(predictionType);
			for(Entry<String, SummaryStatistics> metricEntry: metricStats.entrySet()) {
				SummaryStatistics metricVals = metricEntry.getValue();
				out.write(","+metricVals.getMean()+","+metricVals.getStandardDeviation());
			}
			out.newLine();
			out.flush();
		}
		out.close();
	}
	
	public static void computeAndWriteEvaluations(File rootFolder, File destFile, int numRuns, String[] predictionTypes,
			Map<String,ResponseTimeEvaluatorFactory> evaluatorFactories ) throws IOException {
		
		Map<String, Map<String, SummaryStatistics>> stats =
				evaluateForExperimentSet(rootFolder, numRuns, predictionTypes, evaluatorFactories);
		writeResults(destFile, predictionTypes, stats);
		
	}
}
