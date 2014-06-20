package prediction.response.time;



public class LogNormalDistribution extends DistributionResponseTimePredictor{

	private double mu;
	private double sigma;
	
	public LogNormalDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
	}
	
	@Override
	public String getLabel() {
		return "LogNormal";
	}

	@Override
	public double getPrediction() {
	
		double x = getX();
		return Math.exp(mu + sigma*x);
	}

}
