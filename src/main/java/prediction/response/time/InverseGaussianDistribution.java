package prediction.response.time;


/*
 * Samples from an Inverse Gaussian Distribution based on the method described in
 * 
 * Michael, John R., William R. Schucany and Roy W. Haas. Generating Random Variates
 * Using Transformations with Multiple Roots. The American Statistician, Vol. 30,
 * No. 2 (May, 1976), pp. 88-90
 */

public class InverseGaussianDistribution extends DistributionResponseTimePredictor{

	private double mu;
	private double lambda;
	
	public InverseGaussianDistribution(double mu, double lambda) {
		this.mu = mu;
		this.lambda = lambda;
	}
	
	@Override
	public String getLabel() {
		return "Inverse Gaussian";
	}

	@Override
	public double getPrediction() {
	
		double x = getX();
		double chiSquare = Math.pow(x, 2);
		
		double root1 = mu
				+ ((Math.pow(mu, 2)*chiSquare)/(2*lambda))
				- (mu/(2*lambda))*Math.sqrt(
						4*mu*lambda*chiSquare
						+ Math.pow(mu, 2)*Math.pow(chiSquare, 2));
		
		double root2 = Math.pow(mu, 2)/root1;
		
		double probRoot1 = mu/(mu+root1);
		double randVal = rand.nextDouble();
		if (randVal <= probRoot1) {
			return root1;
		} else {
			return root2;
		}
	}

}
