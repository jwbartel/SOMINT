package snml.rule.superfeature.model.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorization;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;

import snml.dataconvert.mahout.MahoutDataSet;

public class MahoutALSWRModelRule extends MahoutFactorizerModelRule {

	int numFeatures;
	double lambda;
	int numIterations;

	public MahoutALSWRModelRule(String featureName, int numFeatures,
			double lambda, int numIterations) {
		super(featureName);
		this.numFeatures = numFeatures;
		this.lambda = lambda;
		this.numIterations = numIterations;
	}

	@Override
	public void save(String modelFilePath) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(String modelFilePath) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected Factorization initializeFactorization(MahoutDataSet trainingSet) {
		try {
			Factorizer factorizer = new ALSWRFactorizer(trainingSet.getDataSet(),
					numFeatures, lambda, numIterations);
			return factorizer.factorize();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
