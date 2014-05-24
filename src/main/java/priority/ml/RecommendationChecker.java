package priority.ml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import util.tools.io.CollectionIOAssist;

public class RecommendationChecker {

	private final File trainFile;
	private final File testFile;
	private final RecommendationBuilder modelBuilder;
	private final String type;

	public RecommendationChecker(File train, File test, RecommendationBuilder builder, String type) {
		this.trainFile = train;
		this.testFile = test;
		this.modelBuilder = builder;
		this.type = type;
	}

	public Map<String, Double> getResults() throws IOException, TasteException {
		Map<String, Double> results = new TreeMap<String, Double>();

		int count = 0;
		int numPredicted = 0;
		SummaryStatistics absoluteError = new SummaryStatistics();
		SummaryStatistics relativeError = new SummaryStatistics();
		SummaryStatistics scaledError = new SummaryStatistics();

		Recommender recommender = modelBuilder.buildRecommender(new FileDataModel(trainFile));

		Collection<String> testPoints = CollectionIOAssist.readCollection(testFile);
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(testFile.getParent(), type
				+ " results.csv")));
		for (String testPoint : testPoints) {
			count++;
			String[] dims = testPoint.split(",");

			long user = Long.parseLong(dims[0]);
			long item = Long.parseLong(dims[1]);
			double trueVal = Double.parseDouble(dims[2]);
			boolean predicted = false;

			try {
				Float prediction = Math.max(0, recommender.estimatePreference(user, item));
				if (prediction != Double.NaN) {
					if (trueVal > 0 && prediction > 0) {
						numPredicted++;
						out.write("" + prediction);
						out.newLine();
						out.flush();
						predicted = true;
						double absError = Math.abs(trueVal - prediction) / 1000 / 60;
						double relErr = Math.abs(trueVal - prediction) / trueVal;
						double scaledErr = Math.max(trueVal, prediction)
								/ Math.min(trueVal, prediction);
						absoluteError.addValue(absError);
						scaledError.addValue(scaledErr);
						relativeError.addValue(relErr);
					}
				}
			} catch (NoSuchUserException e) {
				System.err.println("Missing user: " + user);
			} catch (NoSuchItemException e) {
				System.err.println("Missing item: " + e.getMessage());
			}

			if (!predicted) {
				out.write("null");
				out.newLine();
				out.flush();
			}
		}
		out.flush();
		out.close();
		results.put("recall", ((double) numPredicted) / ((double) count));
		results.put("absolute error", absoluteError.getMean());
		results.put("relative error", relativeError.getMean());
		results.put("scaled error", scaledError.getMean());
		return results;

	}

	public static Map<String, ArrayList<Double>> experimentAcrossRuns(File runsRootFolder,
			RecommendationBuilder builder, String type) throws IOException, TasteException {
		File[] runsFolders = runsRootFolder.listFiles();

		ArrayList<Double> recalls = new ArrayList<Double>();
		ArrayList<Double> absoluteErrors = new ArrayList<Double>();
		ArrayList<Double> relativeErrors = new ArrayList<Double>();
		ArrayList<Double> scaledErrors = new ArrayList<Double>();

		for (File runFolder : runsFolders) {
			File trainFile = new File(runFolder, "train.csv");
			File testFile = new File(runFolder, "test.csv");

			RecommendationChecker checker = new RecommendationChecker(trainFile, testFile, builder,
					type);
			Map<String, Double> results = checker.getResults();
			recalls.add(results.get("recall"));
			absoluteErrors.add(results.get("absolute error"));
			relativeErrors.add(results.get("relative error"));
			scaledErrors.add(results.get("scaled error"));
		}

		Map<String, ArrayList<Double>> results = new TreeMap<String, ArrayList<Double>>();
		results.put("Recall", recalls);
		results.put("Absolute Error", absoluteErrors);
		results.put("Relative Error", relativeErrors);
		results.put("Scaled Error", scaledErrors);
		return results;
	}
}
