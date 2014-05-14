package priority.ml.evaluate;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class WithinTimeEvaluator extends ResponseTimeEvaluator {
	
	public final static long SECOND = 1000;
	public final static long MINUTE = SECOND*60;
	public final static long FIVE_MINUTES = MINUTE*5;
	public final static long TWENTY_MINUTES = MINUTE*20;
	public final static long HOUR = MINUTE*60;
	public final static long DAY = HOUR*24;
	public final static long WEEK = DAY*7;

	final long limit;
	Map<Integer, Integer> predictionsWithinTime = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> requests = new TreeMap<Integer, Integer>();
	
	private static class WithinTimeEvaluatorFactory implements ResponseTimeEvaluatorFactory {
		final long limit;
		public WithinTimeEvaluatorFactory(long limit) {
			this.limit = limit;
		}
		
		@Override
		public ResponseTimeEvaluator create() {
			return new WithinTimeEvaluator(limit);
		}
	}
	
	public static ResponseTimeEvaluatorFactory factory(long limit) {
		return new WithinTimeEvaluatorFactory(limit);
	}
	
	public WithinTimeEvaluator(long limit) {
		this.limit = limit;
	}
	
	@Override
	public SummaryStatistics getResults() {
		SummaryStatistics stats = new SummaryStatistics();
		for(Integer run: requests.keySet()) {
			double percentage = ((double) predictionsWithinTime.get(run))/((double) requests.get(run));
			stats.addValue(percentage);
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
		if (prediction == null) {
			return;
		}
		
		increment(requests, run, 1);
		if (Math.abs(prediction - trueVal) > limit) {
			increment(predictionsWithinTime, run, 0);
		} else {
			increment(predictionsWithinTime, run, 1);
		}
	}

}
