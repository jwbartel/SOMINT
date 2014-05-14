package priority.ml.evaluate;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class RecallEvaluator extends ResponseTimeEvaluator {

	Map<Integer, Integer> predictions = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> requests = new TreeMap<Integer, Integer>();
	
	private static class RecallEvaluatorFactory implements ResponseTimeEvaluatorFactory {

		@Override
		public ResponseTimeEvaluator create() {
			return new RecallEvaluator();
		}
	}
	
	public static ResponseTimeEvaluatorFactory factory() {
		return new RecallEvaluatorFactory();
	}
	
	@Override
	public SummaryStatistics getResults() {
		SummaryStatistics stats = new SummaryStatistics();
		for(Integer run: requests.keySet()) {
			double recall = ((double) predictions.get(run))/((double) requests.get(run));
			stats.addValue(recall);
		}
		return stats;
	}

	private static void increment(Map<Integer,Integer> vals, int run, int incVal) {
		Integer val = vals.get(run);
		if (val == null) {
			val = 0;
		}
		vals.put(run, val + incVal);
	}
	
	@Override
	public void updateStats(int run, Double trueVal, Double prediction) {
		increment(requests, run, 1);
		if (prediction == null) {
			increment(predictions, run, 0);
		} else {
			increment(predictions, run, 1);
		}
	}

}
