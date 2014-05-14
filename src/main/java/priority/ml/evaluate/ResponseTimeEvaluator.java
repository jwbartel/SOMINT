package priority.ml.evaluate;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public abstract class ResponseTimeEvaluator {
	
	public final static Map<String, ResponseTimeEvaluatorFactory> ALL_EVALUATOR_FACTORIES = new TreeMap<String, ResponseTimeEvaluatorFactory>();
	
	public abstract SummaryStatistics getResults();
	
	public abstract void updateStats(int run, Double trueVal, Double prediction);
	
	
	static {
		ALL_EVALUATOR_FACTORIES.put("recall", RecallEvaluator.factory());
		ALL_EVALUATOR_FACTORIES.put("within 1 second", WithinTimeEvaluator.factory(WithinTimeEvaluator.SECOND));
		ALL_EVALUATOR_FACTORIES.put("within 1 minute", WithinTimeEvaluator.factory(WithinTimeEvaluator.MINUTE));
		ALL_EVALUATOR_FACTORIES.put("within 5 minutes", WithinTimeEvaluator.factory(WithinTimeEvaluator.FIVE_MINUTES));
		ALL_EVALUATOR_FACTORIES.put("within 20 minutes", WithinTimeEvaluator.factory(WithinTimeEvaluator.TWENTY_MINUTES));
		ALL_EVALUATOR_FACTORIES.put("within 1 hour", WithinTimeEvaluator.factory(WithinTimeEvaluator.HOUR));
		ALL_EVALUATOR_FACTORIES.put("within 1 day", WithinTimeEvaluator.factory(WithinTimeEvaluator.DAY));
		ALL_EVALUATOR_FACTORIES.put("within 1 week", WithinTimeEvaluator.factory(WithinTimeEvaluator.WEEK));
		ALL_EVALUATOR_FACTORIES.put("absolute error", AbsoluteErrorEvaluator.factory());
		ALL_EVALUATOR_FACTORIES.put("relative error", RelativeErrorEvaluator.factory());
		ALL_EVALUATOR_FACTORIES.put("scale error", ScaleErrorEvaluator.factory());
	}
	
}
