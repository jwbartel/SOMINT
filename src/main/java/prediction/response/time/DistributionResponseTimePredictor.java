package prediction.response.time;

import java.util.Random;

public abstract class DistributionResponseTimePredictor {

	protected final Random rand;
	
	public DistributionResponseTimePredictor() {
		this.rand = new Random();
	}
	
	protected double getX() {
		return rand.nextGaussian();
	}
	
	public abstract String getLabel();
	public abstract double getPrediction();
}
