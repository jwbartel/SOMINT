package priority.ml.stackoverflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import priority.ml.EuclideanRecommendationBuilder;
import priority.ml.PearsonCorrelationRecommendationBuilder;
import priority.ml.RandomRecommendationBuilder;
import priority.ml.RecommendationBuilder;
import priority.ml.RecommendationChecker;
import priority.ml.SlopeOneRecommendationBuilder;
import reader.threadfinder.stackoverflow.tools.Pair;
import bus.tools.io.CollectionIOAssist;
import bus.tools.io.CsvCollectionValueWriter;
import bus.tools.io.DoubleValueWriter;
import bus.tools.io.MapIOAssist;

public class StackOverflowRecommendationChecker {

	final static String[] metrics = { "Recall", "Absolute Error", "Relative Error", "Scaled Error" };

	private final static Map<String, RecommendationBuilder> builders = new TreeMap<String, RecommendationBuilder>();

	static {
		builders.put("Random", new RandomRecommendationBuilder());
		builders.put("SlopeOne", new SlopeOneRecommendationBuilder());
		builders.put("Euclidean", new EuclideanRecommendationBuilder());
		builders.put("PearsonCorrelation", new PearsonCorrelationRecommendationBuilder());

//		builders.put("LogLikelihood", new LogLikelihoodRecommendationBuilder());
	}

	private static void printHeaders(int numRuns, BufferedWriter out) throws IOException {
		out.write("Run,");
		for (int run = 1; run <= numRuns; run++) {
			out.write(run + ",");
		}
		out.newLine();
		out.flush();
	}

	public static void printResults(BufferedWriter out, String type, ArrayList<Double> results)
			throws IOException {
		CsvCollectionValueWriter<Double> valueWriter = new CsvCollectionValueWriter<Double>(
				new DoubleValueWriter());
		out.write(type + ",");
		out.write(valueWriter.writeVal(results));
		out.newLine();
		out.flush();
	}

	private static void writeSubsetOfData(File from, File to, int length) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(from));
		BufferedWriter out = new BufferedWriter(new FileWriter(to));
		String line = in.readLine();
		for (int i = 0; i < length && line != null; i++) {

			out.write(line);
			out.newLine();
			out.flush();

			line = in.readLine();
		}
		in.close();
		out.close();
	}

	public static void printResults(File folder, Map<String, Map<Integer, Long>> results)
			throws IOException {

		if (!folder.exists()) {
			folder.mkdirs();
		}

		for (Entry<String, Map<Integer, Long>> entry : results.entrySet()) {
			String type = entry.getKey();
			Map<Integer, Long> typeResults = entry.getValue();

			File outFile = new File(folder, type + ".csv");
			MapIOAssist.writeMap(outFile, typeResults);
		}
	}

	public static void printTimeToTrainResults(File outputDirectory, File fullDataFile,
			int maxLength, int stepSize) throws IOException, TasteException {
		Map<String, Map<Integer, Long>> results = getTimeToTrainResults(fullDataFile, maxLength,
				stepSize);

		File timeResultsDirectory = new File(outputDirectory, "time to train");
		printResults(timeResultsDirectory, results);
	}

	public static void printTimeToPredictResults(File outputDirectory, File fullDataFile,
			File testFile, int maxTrainLength, int trainStepSize, int numToTest)
			throws IOException, TasteException {
		Map<String, Map<Integer, Long>> results = getTimeToPredictResults(fullDataFile, testFile,
				maxTrainLength, trainStepSize, numToTest);

		File timeResultsDirectory = new File(outputDirectory, "time to predict");
		printResults(timeResultsDirectory, results);
	}

	public static Map<String, Map<Integer, Long>> getTimeToTrainResults(File fullDataFile,
			int maxLength, int stepSize) throws IOException, TasteException {
		Map<String, Map<Integer, Long>> results = new TreeMap<String, Map<Integer, Long>>();

		File subsetDataFile = new File(fullDataFile.getParent(),
				"subset of data for testing timing.csv");
		for (Entry<String, RecommendationBuilder> entry : builders.entrySet()) {
			String type = entry.getKey();
			RecommendationBuilder builder = entry.getValue();

			Map<Integer, Long> typeResults = new TreeMap<Integer, Long>();
			for (int length = 1; length < maxLength; length += stepSize) {

				writeSubsetOfData(fullDataFile, subsetDataFile, length);
				DataModel model = new FileDataModel(subsetDataFile);
				long start = System.currentTimeMillis();
				builder.buildRecommender(model);
				long totalTime = System.currentTimeMillis() - start;
				typeResults.put(length, totalTime);
			}
			results.put(type, typeResults);
		}
		return results;
	}

	public static Collection<Pair<Integer, Integer>> getTestPoints(File testFile, int numToTest)
			throws IOException {
		ArrayList<String> testPointStrs = new ArrayList<String>(
				CollectionIOAssist.readCollection(testFile));
		Random rand = new Random();
		while (testPointStrs.size() > numToTest) {
			int pos = rand.nextInt(testPointStrs.size());
			testPointStrs.remove(pos);
		}

		Collection<Pair<Integer, Integer>> testPoints = new TreeSet<Pair<Integer, Integer>>();
		for (String testPointStr : testPointStrs) {
			String[] splitPair = testPointStr.split(",");
			Pair<Integer, Integer> testPoint = new Pair<Integer, Integer>(
					Integer.parseInt(splitPair[0]), Integer.parseInt(splitPair[1]));
			testPoints.add(testPoint);
		}
		return testPoints;
	}

	public static Map<String, Map<Integer, Long>> getTimeToPredictResults(File fullDataFile,
			File testFile, int maxTrainLength, int trainStepSize, int numToTest)
			throws IOException, TasteException {
		Map<String, Map<Integer, Long>> results = new TreeMap<String, Map<Integer, Long>>();

		Collection<Pair<Integer, Integer>> testPoints = getTestPoints(testFile, numToTest);

		File subsetDataFile = new File(fullDataFile.getParent(),
				"subset of data for testing timing.csv");
		for (Entry<String, RecommendationBuilder> entry : builders.entrySet()) {
			String type = entry.getKey();
			RecommendationBuilder builder = entry.getValue();

			Map<Integer, Long> typeResults = new TreeMap<Integer, Long>();
			for (int length = 1; length < maxTrainLength; length += trainStepSize) {

				writeSubsetOfData(fullDataFile, subsetDataFile, length);
				DataModel model = new FileDataModel(subsetDataFile);
				Recommender recommender = builder.buildRecommender(model);
				long start = System.currentTimeMillis();
				for (Pair<Integer, Integer> testPoint : testPoints) {
					try {
						recommender.estimatePreference(testPoint.getFirstVal(),
								testPoint.getSecondVal());
					} catch (NoSuchUserException e) {

					} catch (NoSuchItemException e) {

					}
				}
				long totalTime = System.currentTimeMillis() - start;
				typeResults.put(length, totalTime);
			}
			results.put(type, typeResults);
		}
		return results;
	}

	private static SummaryStatistics buildResult(ArrayList<Double> runResults) {
		SummaryStatistics stats = new SummaryStatistics();
		for (Double val : runResults) {
			stats.addValue(val);
		}
		return stats;
	}

	public static Map<String, Map<String, SummaryStatistics>> checkResults(File runsRootFolder,
			int numRuns) throws IOException, TasteException {

		Map<String, Map<String, SummaryStatistics>> overallResults = new TreeMap<String, Map<String, SummaryStatistics>>();

		for (Entry<String, RecommendationBuilder> entry : builders.entrySet()) {
			String type = entry.getKey();
			RecommendationBuilder builder = entry.getValue();

			Map<String, SummaryStatistics> recommenderResults = new TreeMap<String, SummaryStatistics>();

			Map<String, ArrayList<Double>> results = RecommendationChecker.experimentAcrossRuns(
					runsRootFolder, builder, type);

			for (String metric : metrics) {

				recommenderResults.put(metric, buildResult(results.get(metric)));
			}
			overallResults.put(type, recommenderResults);
		}

		return overallResults;
	}

	private static void writeHeader(BufferedWriter out) throws IOException {
		out.write(",");
		for (String metric : metrics) {
			out.write(metric + ",,");
		}
		out.newLine();

		out.write(",");
		for (int i = 0; i < metrics.length; i++) {
			out.write("mean,stdev,");
		}
		out.newLine();
		out.flush();
	}

	public static void printAllRecommendationAccuracyResults(File rootFolder, File outputDirectory,
			int numRuns) throws IOException, TasteException {

		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		String[] pairTypes = {/*
							 * "owner and tag", "owner and word",
							 * "tag and word",
							 */
		"reduced owner and tag_word" };

		for (String pairType : pairTypes) {

			File typeRoot = new File(rootFolder, pairType);
			for (File runRootFolder : typeRoot.listFiles()) {
				if (runRootFolder.getName().startsWith("accepted")) {
					continue;
				}

				BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputDirectory,
						runRootFolder.getName() + " " + pairType + " results.csv")));
				writeHeader(out);

				Map<String, Map<String, SummaryStatistics>> overallResults = checkResults(
						runRootFolder, numRuns);
				for (Entry<String, Map<String, SummaryStatistics>> entry : overallResults
						.entrySet()) {

					String recommender = entry.getKey();
					Map<String, SummaryStatistics> results = entry.getValue();
					out.write(recommender + ",");
					for (String metric : metrics) {
						SummaryStatistics stats = results.get(metric);
						out.write(stats.getMean() + ",");
						out.write(stats.getStandardDeviation() + ",");
					}
					out.newLine();
					out.flush();
				}
				out.close();
			}
		}
	}

	public static void main(String[] args) throws IOException, TasteException {
		File rootFolder = new File("D:\\Stack Overflow data\\experiment sets");
		File outFolder = new File("D:\\Stack Overflow data\\experiment results");
		printAllRecommendationAccuracyResults(rootFolder, outFolder, 10);

//		File timingFolder = new File(
//				"D:\\Stack Overflow data\\experiment sets\\tag and word\\accepted times\\1");
//		File trainForTiming = new File(timingFolder, "train.csv");
//		File testForTiming = new File(timingFolder, "test.csv");
//		int maxTrainingSize = 50001;
//		int trainingStepSize = 500;
//		int numToTest = 100;
//		printTimeToTrainResults(outFolder, trainForTiming, maxTrainingSize, trainingStepSize);
//		printTimeToPredictResults(outFolder, trainForTiming, testForTiming, maxTrainingSize,
//				trainingStepSize, numToTest);
	}
}
