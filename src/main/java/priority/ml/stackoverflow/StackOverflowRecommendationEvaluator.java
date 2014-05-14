package priority.ml.stackoverflow;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import priority.ml.evaluate.ExperimentSetEvaluator;
import priority.ml.evaluate.ResponseTimeEvaluator;
import priority.ml.evaluate.ResponseTimeEvaluatorFactory;
import priority.ml.evaluate.WithinTimeEvaluator;

public class StackOverflowRecommendationEvaluator {

	public void writeConstantPrediction(long time) {
		
	}
	
	public static void evaluateAndwriteAcrossExperiments(File rootFolder, String[] experimentTypes, int numRuns, 
			String[] predictionTypes, Map<String,ResponseTimeEvaluatorFactory> evaluatorFactories ) throws IOException {
		
		for(String experimentType : experimentTypes) {

			
			File resultsFile = new File(rootFolder, experimentType + " results.csv");
			File experimentRoot = new File(new File(rootFolder, experimentType), "earliest times");
			

//			ExperimentSetEvaluator.writeConstantPredictionsForRuns(experimentRoot, numRuns,"20MinPrediction", WithinTimeEvaluator.TWENTY_MINUTES);
//			ExperimentSetEvaluator.writeConstantPredictionsForRuns(experimentRoot, numRuns,"10MinPrediction", WithinTimeEvaluator.TWENTY_MINUTES/2);
//			ExperimentSetEvaluator.writeConstantPredictionsForRuns(experimentRoot, numRuns,"05MinPrediction", WithinTimeEvaluator.FIVE_MINUTES);
			
			ExperimentSetEvaluator.computeAndWriteEvaluations(experimentRoot, resultsFile, numRuns, predictionTypes, evaluatorFactories);
		}
	}
	
	public static void main(String[] args) throws IOException {
	
		File rootFolder = new File("D:\\Stack Overflow Data");
		String[] experimentTypes = {"owner and tag", "owner and word", "tag and word", "owner and tag_word"};
		int numRuns = 10;
		String[] predictionTypes = {
				"Random", "05MinPrediction", "10MinPrediction", "20MinPrediction", "Euclidean", "PearsonCorrelation", "SlopeOne"};
		
		Map<String,ResponseTimeEvaluatorFactory> evaluatorFactories = ResponseTimeEvaluator.ALL_EVALUATOR_FACTORIES;
		evaluateAndwriteAcrossExperiments(rootFolder, experimentTypes, numRuns, predictionTypes, evaluatorFactories);
	}
}
