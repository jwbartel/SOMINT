package priority.ml.evaluate;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class ScaleErrorEvaluator extends ResponseTimeEvaluator {

	Map<Integer, SummaryStatistics> errors = new TreeMap<Integer, SummaryStatistics>();
	
	private static class ScaleErrorEvaluatorFactory implements ResponseTimeEvaluatorFactory {

		@Override
		public ResponseTimeEvaluator create() {
			return new ScaleErrorEvaluator();
		}
	}
	
	public static ResponseTimeEvaluatorFactory factory() {
		return new ScaleErrorEvaluatorFactory();
	}
	
	@Override
	public SummaryStatistics getResults() {
		SummaryStatistics stats = new SummaryStatistics();
		for(Integer run: errors.keySet()) {
			stats.addValue(errors.get(run).getMean());
		}
		return stats;
	}
	
	@Override
	public void updateStats(int run, Double trueVal, Double prediction) {
		if (prediction == null) {
			return;
		}
		
		SummaryStatistics runStats = errors.get(run);
		if(runStats == null) {
			runStats = new SummaryStatistics();
			errors.put(run, runStats);
		}
		runStats.addValue(
				((double)Math.max(trueVal, prediction))/((double)Math.min(trueVal, prediction)));
	}

}
