package prediction.response.time.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import prediction.features.messages.SecondsToFirstResponseRule;
import prediction.features.messages.ThreadSetProperties;
import prediction.response.time.ScoringMethods;
import snml.dataimport.ThreadData;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.mahout.MahoutALSWRModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class ALSWRCollaborativeFilterResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		extends
		MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> {
	
	private int numIterations;
	private int[] possibleNumFeatures;
	private double[] possibleLambdas;
	
	private int chosenNumFeatures;
	private double chosenLambda;
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final int numFeatures,
					final double lambda,
					final int numIterations,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> createCollaborativeFilteringPredictor(
					IBasicFeatureRule userFeature,
					IBasicFeatureRule itemFeature,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				return new ALSWRCollaborativeFilterResponseTimePredictor<>(
						title, userFeature, itemFeature, numFeatures, lambda,
						numIterations, threadsProperties);
			}
		};
	}
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final int[] possibleNumFeatures,
					final double[] possibleLambdas,
					final int numIterations,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> createCollaborativeFilteringPredictor(
					IBasicFeatureRule userFeature,
					IBasicFeatureRule itemFeature,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				try {
					return new ALSWRCollaborativeFilterResponseTimePredictor<>(
							title, userFeature, itemFeature, possibleNumFeatures, possibleLambdas,
							numIterations, threadsProperties);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}

	public ALSWRCollaborativeFilterResponseTimePredictor(
			String title,
			IBasicFeatureRule userFeatureRule,
			IBasicFeatureRule itemFeatureRule,
			int numFeatures,
			double lambda,
			int numIterations,
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
		super(title, userFeatureRule, itemFeatureRule, threadsProperties);
		
		possibleNumFeatures = new int[1];
		possibleNumFeatures[0] = numFeatures;
		
		possibleLambdas = new double[1];
		possibleLambdas[0] = lambda;
		
		this.numIterations = numIterations;
		snmlModel = new MahoutALSWRModelRule("responseTime", numFeatures, lambda, numIterations);
	}

	/**
	 * Creates a Response Time Predictor.  By default, the number of features is the first value in possibleNumFeatures and the lambda is the first value in possibleLambdas
	 * @throws Exception 
	 */
	public ALSWRCollaborativeFilterResponseTimePredictor(
			String title,
			IBasicFeatureRule userFeatureRule,
			IBasicFeatureRule itemFeatureRule,
			int[] possibleNumFeatures,
			double[] possibleLambdas,
			int numIterations,
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) throws Exception {
		super(title, userFeatureRule, itemFeatureRule, threadsProperties);
		
		if (possibleNumFeatures == null || possibleNumFeatures.length == 0
				|| possibleLambdas == null || possibleLambdas.length == 0) {
			throw new Exception(
					"Must specify at least on possible lambda and number of features");
		}
		
		this.possibleNumFeatures = possibleNumFeatures;
		this.possibleLambdas = possibleLambdas;
		
		this.numIterations = numIterations;
		snmlModel = new MahoutALSWRModelRule("responseTime", possibleNumFeatures[0], possibleLambdas[0], numIterations);
	}

	@Override
	public void validate(Collection<ThreadType> validationSet) throws Exception {
		if (possibleNumFeatures.length > 1 || possibleLambdas.length > 1) {

			double bestLambda = 0;
			int bestNumFeatures = 0;
			double bestRMSE = Double.POSITIVE_INFINITY;
			double bestCoverage = Double.POSITIVE_INFINITY;

			for (int i = 0; i < possibleNumFeatures.length; i++) {
				for (int j = 0; j < possibleLambdas.length; j++) {

					chosenNumFeatures = possibleNumFeatures[i];
					chosenLambda = possibleLambdas[j];
					extractor = null;

					snmlModel = new MahoutALSWRModelRule("responseTime",
							chosenNumFeatures, chosenLambda, numIterations);
					train();

					IBasicFeatureRule responseTimeFeature = new SecondsToFirstResponseRule(
							"responseTime");
					List<Double> trueTimes = new ArrayList<>();
					List<Double> predictedTimes = new ArrayList<>();
					for (ThreadType thread : validationSet) {
						ThreadData threadData = extractor
								.extractThreadDataItem(thread);
						trueTimes.add((Double) responseTimeFeature
								.extract(threadData));
						predictedTimes
								.add(this.predictResponseTime(thread).minResponseTime);
					}

					double rmse = ScoringMethods.rootMeanSquareError(trueTimes,
							predictedTimes);
					double coverage = ScoringMethods.coverage(trueTimes,
							predictedTimes);

					boolean overwrite = (rmse < bestRMSE || (rmse == bestRMSE && coverage > bestCoverage));
					if (overwrite) {
						bestNumFeatures = chosenNumFeatures;
						bestLambda = chosenLambda;
						bestRMSE = rmse;
						bestCoverage = coverage;
					}
				}
			}

			if (bestLambda != chosenLambda
					|| bestNumFeatures != chosenNumFeatures) {
				chosenLambda = bestLambda;
				chosenNumFeatures = bestNumFeatures;

				snmlModel = new MahoutALSWRModelRule("responseTime",
						chosenNumFeatures, chosenLambda, numIterations);
				train();
			}

		} else {
			chosenNumFeatures = possibleNumFeatures[0];
			chosenLambda = possibleLambdas[0];
			train();
		}
	}

	@Override
	public String getModelInfo() throws Exception {
		return "ALS-WR collaborative filtering(" + chosenNumFeatures + " features, lambda="+chosenLambda + ")";
	}

}
