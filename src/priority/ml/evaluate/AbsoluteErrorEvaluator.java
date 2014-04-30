package priority.ml.evaluate;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class AbsoluteErrorEvaluator extends ResponseTimeEvaluator {

	Map<Integer, SummaryStatistics> errors = new TreeMap<Integer, SummaryStatistics>();
	
	private static class AbsoluteErrorEvaluatorFactory implements ResponseTimeEvaluatorFactory {

		@Override
		public ResponseTimeEvaluator create() {
			return new AbsoluteErrorEvaluator();
		}
	}
	
	public static ResponseTimeEvaluatorFactory factory() {
		return new AbsoluteErrorEvaluatorFactory();
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
		runStats.addValue(Math.abs((double) (trueVal - prediction)));
	}

}
