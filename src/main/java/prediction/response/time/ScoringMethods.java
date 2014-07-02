package prediction.response.time;

import java.util.List;

public class ScoringMethods {

	public static double rootMeanSquareError(List<Double> trueValues, List<Double> results) {
		
		int totalAcceptableResults = 0;
		double meanSquareError = 0;
		
		for (int i = 0; i < results.size(); i++) {
			Double trueValue = trueValues.get(i);
			Double result = results.get(i);
			if (result != null && trueValue != null && trueValue != 0 && trueValue != Double.POSITIVE_INFINITY
					&& result != Double.POSITIVE_INFINITY) {
				
				totalAcceptableResults++;
				meanSquareError += Math.pow(result-trueValue, 2.0);
			}
		}
		meanSquareError = meanSquareError/((double) totalAcceptableResults);
		return Math.sqrt(meanSquareError);
	}

	public static double coverage(List<Double> trueValues, List<Double> results) {
		
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
}
