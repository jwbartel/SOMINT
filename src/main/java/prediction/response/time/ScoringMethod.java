package prediction.response.time;

import java.util.List;

public abstract class ScoringMethod {
	
	public abstract double score(List<Double> trueValues, List<Double> results);

	public static ScoringMethod percentWithinErrorThreshold(final double threshold) {
		return new ScoringMethod() {
			
			@Override
			public double score(List<Double> trueValues, List<Double> results) {
				int totalAcceptableResults = 0;
				int numWithinThreshold = 0;
				for (int i = 0; i < results.size(); i++) {
					Double trueValue = trueValues.get(i);
					Double result = results.get(i);
					if (result != null && trueValue != null && !trueValue.isInfinite()) {
						
						totalAcceptableResults++;
						double error = Math.abs(result - trueValue);
						if (error <= threshold) {
							numWithinThreshold++;
						}
					}
				}
				return ((double) numWithinThreshold) / ((double) totalAcceptableResults);
			}
		};
	}
	
	public static ScoringMethod rootMeanSquareError() {
		return new ScoringMethod() {
			
			@Override
			public double score(List<Double> trueValues, List<Double> results) {
				
				int totalAcceptableResults = 0;
				double meanSquareError = 0;
				
				for (int i = 0; i < results.size(); i++) {
					Double trueValue = trueValues.get(i);
					Double result = results.get(i);
					if (result != null && trueValue != null && !trueValue.isInfinite()
							&& result != Double.POSITIVE_INFINITY) {
						
						totalAcceptableResults++;
						meanSquareError += Math.pow(result-trueValue, 2.0);
					}
				}
				meanSquareError = meanSquareError/((double) totalAcceptableResults);
				return Math.sqrt(meanSquareError);
			}
		};
	}

	public static ScoringMethod coverage(){
		return new ScoringMethod() {
			
			@Override
			public double score(List<Double> trueValues, List<Double> results) {
				
				int totalAcceptableTrueValues = 0;
				int nonNullResults = 0;
				
				for (int i = 0; i < results.size(); i++) {
					Double trueValue = trueValues.get(i);
					Double result = results.get(i);
					if (trueValue != null && !trueValue.isInfinite()) {
						
						totalAcceptableTrueValues++;
						if (result != null) {
							nonNullResults++;
						}
					}
				}
				return ((double) nonNullResults) / ((double) totalAcceptableTrueValues);
			}
		};
	}
}
