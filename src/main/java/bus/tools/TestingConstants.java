package bus.tools;

public class TestingConstants {

	public final static int[] PARTICIPANTS = {8, 10, 12, 13, 16, 17, 19, 21, 22, 23, 24, 25};
	public final static int SYNTHETIC_TESTS_PER_PARTICIPANT = 20;
	
	public final static double[] GRAPH_GROWTH_PROPORTIONS = {
			0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.6, 0.7, 0.8, 0.9
		};
	private final static double DEFAULT_THRESHOLD_INCREMENT = 1.0;
	private static Double thresholdIncrement = null;
	
	public static void setThresholdIncrement(double increment) {
		thresholdIncrement = increment;
	}
	
	public static double getThresholdIncrement() {
		if (thresholdIncrement != null) {
			return thresholdIncrement;
		}
		return DEFAULT_THRESHOLD_INCREMENT;
	}
}
