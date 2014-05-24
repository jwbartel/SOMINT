package reader.threadfinder.stackoverflow.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.dom4j.DocumentException;

import reader.threadfinder.stackoverflow.tools.eventflow.PostRowParser;
import util.tools.io.ArrayIOAssist;
import util.tools.io.CollectionIOAssist;
import util.tools.io.CollectionValueParser;
import util.tools.io.IntegerValueParser;
import util.tools.io.LongValueParser;
import util.tools.io.MapIOAssist;
import util.tools.io.StringValueWriterAndParser;
import util.tools.io.SummaryStatisticsValueWriter;
import util.tools.io.ValueParser;
import util.tools.sorting.SortedListAssist;

public class StackOverflowParser {

	public static final String QUESTION_IDS_FILE = "question ids.csv";
	public static final String QUESTION_TIMES_FILE = "question times.csv";
	public static final String QUESTION_OWNERS_FILE = "question owners.csv";
	public static final String QUESTION_ACCEPTED_ANSWER_FILE = "question accepted answer.csv";
	public static final String QUESTION_ACCEPTED_ANSWER_TIME_FILE = "question accepted answer time.csv";
	public static final String QUESTION_EARLIEST_ANSWER_TIME_FILE = "question earliest answer time.csv";

	public static final String PAIRS_OWNER_TAG_FILE = "pairs owner-tag.csv";
	public static final String PAIRS_OWNER_WORD_FILE = "pairs owner-word.csv";
	public static final String PAIRS_TAG_WORD_FILE = "pairs tag-word.csv";
	public static final String PAIRS_OWNER_AND_TAG_WORD_FILE = "pairs owner tag-word.csv";

	public static final String OWNER_QUESTION_COUNT_FILE = "owner question counts.csv";
	public static final String OWNER_QUESTION_LISTS_FILE = "owner question lists.csv";
	public static final String OWNER_EARLIEST_ANSWER_TIMES_FILE = "owner earliest answer times.csv";
	public static final String OWNER_BY_MOST_FREQUENT_FILE = "owner by most frequent.csv";

	public static final String TAG_QUESTION_COUNT_FILE = "tag question counts.csv";
	public static final String TAG_QUESTION_LISTS_FILE = "tag question lists.csv";
	public static final String TAG_EARLIEST_ANSWER_TIMES_FILE = "tag earliest answer times.csv";
	public static final String TAG_BY_MOST_FREQUENT_FILE = "tag by most frequent.csv";

	public static final String WORD_QUESTION_COUNT_FILE = "word question counts.csv";
	public static final String WORD_QUESTION_LISTS_FILE = "word question lists.csv";
	public static final String WORD_EARLIEST_ANSWER_TIMES_FILE = "word earliest answer times.csv";
	public static final String WORD_BY_MOST_FREQUENT_FILE = "word by most frequent.csv";

	public static final String TAG_WORD_QUESTION_COUNT_FILE = "tag_word question counts.csv";
	public static final String TAG_WORD_QUESTION_LISTS_FILE = "tag_word question lists.csv";
	public static final String TAG_WORD_EARLIEST_ANSWER_TIMES_FILE = "tag_word earliest answer times.csv";
	public static final String TAG_WORD_BY_MOST_FREQUENT_FILE = "tag_word by most frequent.csv";

	public static final String ANSWER_IDS_FILE = "answer ids.csv";
	public static final String ANSWER_TIMES_FILE = "answer times.csv";
	public static final String ANSWER_PARENTS_FILE = "answer parents.csv";

	private static Map<Integer, Integer> extractAnswerToQuestion(Set<Integer> questionIDs,
			Set<Integer> answerIDs, Map<Integer, Integer> answerParents) {

		Map<Integer, Integer> answerToQuestion = new TreeMap<Integer, Integer>();

		while (answerParents.size() > 0) {
			Set<Integer> answersToRemove = new TreeSet<Integer>();
			Map<Integer, Integer> parentsToChange = new TreeMap<Integer, Integer>();
			for (Entry<Integer, Integer> entry : answerParents.entrySet()) {
				Integer answer = entry.getKey();
				Integer parent = entry.getValue();

				if (!answerIDs.contains(parent) && !questionIDs.contains(parent)) {
					answersToRemove.add(answer);
					answerIDs.remove(answer);
					continue;
				}

				if (answerIDs.contains(parent)) {
					if (answerParents.containsKey(parent)) {
						parentsToChange.put(answer, answerParents.get(parent));
					} else if (answerToQuestion.containsKey(parent)) {
						answerToQuestion.put(answer, answerToQuestion.get(parent));
						answersToRemove.add(answer);
					}
					continue;
				}

				if (questionIDs.contains(parent)) {
					answerToQuestion.put(answer, parent);
					answersToRemove.add(answer);
				}
			}

			for (Integer answer : answersToRemove) {
				answerParents.remove(answer);
			}

			for (Entry<Integer, Integer> entry : parentsToChange.entrySet()) {
				Integer answer = entry.getKey();
				Integer parent = entry.getValue();
				answerParents.put(answer, parent);
			}
		}

		return answerToQuestion;
	}

	private static void extractQuestionData(File questionsFile, File destFolder) throws IOException {
		Map<Integer, Long> timeOfQuestion = new HashMap<Integer, Long>();
		Map<Integer, Integer> questionOwners = new HashMap<Integer, Integer>();
		Map<Integer, Integer> acceptedAnswers = new TreeMap<Integer, Integer>();
		Set<Integer> questionIDs = new HashSet<Integer>();

		PostReader.readPosts(questionsFile, new QuestionReader(timeOfQuestion, questionOwners,
				acceptedAnswers, questionIDs));

		MapIOAssist.writeMap(new File(destFolder, QUESTION_TIMES_FILE), timeOfQuestion);
		MapIOAssist.writeMap(new File(destFolder, QUESTION_OWNERS_FILE), questionOwners);
		MapIOAssist.writeMap(new File(destFolder, QUESTION_ACCEPTED_ANSWER_FILE), acceptedAnswers);
		CollectionIOAssist.writeCollection(new File(destFolder, QUESTION_IDS_FILE), questionIDs);
	}

	private static void extractAnswerData(File answersFile, File destFolder) throws IOException {
		Set<Integer> answerIDs = new TreeSet<Integer>();
		Map<Integer, Long> answerTimes = new TreeMap<Integer, Long>();
		Map<Integer, Integer> answerParents = new TreeMap<Integer, Integer>();

		PostReader.readPosts(answersFile, new AnswerReader(answerIDs, answerTimes, answerParents));

		MapIOAssist.writeMap(new File(destFolder, ANSWER_TIMES_FILE), answerTimes);
		MapIOAssist.writeMap(new File(destFolder, ANSWER_PARENTS_FILE), answerParents);
		CollectionIOAssist.writeCollection(new File(destFolder, ANSWER_IDS_FILE), answerIDs);
	}

	public static void extractTimeToAcceptedAnswer(File destFolder) throws IOException {
		Map<Integer, Long> timeOfQuestions = MapIOAssist.readMap(new File(destFolder,
				QUESTION_TIMES_FILE), new IntegerValueParser(), new LongValueParser());
		Map<Integer, Long> timeOfAnswers = MapIOAssist.readMap(new File(destFolder,
				ANSWER_TIMES_FILE), new IntegerValueParser(), new LongValueParser());
		Map<Integer, Integer> acceptedAnswers = MapIOAssist.readMap(new File(destFolder,
				QUESTION_ACCEPTED_ANSWER_FILE), new IntegerValueParser(), new IntegerValueParser());

		Map<Integer, Long> timeOfAcceptedAnswer = new HashMap<Integer, Long>();
		for (Entry<Integer, Integer> entry : acceptedAnswers.entrySet()) {
			Integer question = entry.getKey();
			Integer answer = entry.getValue();

			Long questionTime = timeOfQuestions.get(question);
			Long answerTime = timeOfAnswers.get(answer);
			if (questionTime == null || answerTime == null) {
				continue;
			}

			long delta = answerTime - questionTime;
			if (delta > 0) {
				timeOfAcceptedAnswer.put(question, delta);
			}
		}

		MapIOAssist.writeMap(new File(destFolder, QUESTION_ACCEPTED_ANSWER_TIME_FILE),
				timeOfAcceptedAnswer);
	}

	public static void extractTimeToEarliestAnswer(File answersFile, File destFolder)
			throws IOException {
		Map<Integer, Long> timeOfQuestions = MapIOAssist.readTreeMap(new File(destFolder,
				QUESTION_TIMES_FILE), new IntegerValueParser(), new LongValueParser());

		Map<Integer, Long> timeOfEarliestAnswers = new TreeMap<Integer, Long>();
		PostReader.readPosts(answersFile, new EarliestAnswerReader(timeOfQuestions,
				timeOfEarliestAnswers));

		MapIOAssist.writeMap(new File(destFolder, QUESTION_EARLIEST_ANSWER_TIME_FILE),
				timeOfEarliestAnswers);
	}

	public static void extractIndividualizedQuestionData(File questionsFile, File destFolder)
			throws IOException {
		Map<Integer, Long> timeOfEarliestAnswers = MapIOAssist.readMap(new File(destFolder,
				QUESTION_EARLIEST_ANSWER_TIME_FILE), new IntegerValueParser(),
				new LongValueParser());
		IndividualizedQuestionReader reader = new IndividualizedQuestionReader(destFolder,
				timeOfEarliestAnswers);
		PostReader.readPosts(questionsFile, reader);
		reader.close();
	}

	public static void writeIndividualizedTimeToAcceptedData(File destFolder) throws IOException {
		Map<Integer, Long> timeOfAcceptedAnswers = MapIOAssist.readMap(new File(destFolder,
				QUESTION_EARLIEST_ANSWER_TIME_FILE), new IntegerValueParser(),
				new LongValueParser());

		File questonList = new File(destFolder, "questions list.txt");
		IndividualizedAcceptedAnswerTimeProcessor processor = new IndividualizedAcceptedAnswerTimeProcessor(
				timeOfAcceptedAnswers);
		IndividualizedQuestionProcessor.processAllQuestions(destFolder, questonList, processor);
	}

	public static void extractEarliestAndAcceptedAnswerTimes(File questionsFile, File answersFile,
			File destFolder) throws IOException {

		extractQuestionData(questionsFile, destFolder);
		extractAnswerData(answersFile, destFolder);
		extractTimeToAcceptedAnswer(destFolder);
		extractTimeToEarliestAnswer(answersFile, destFolder);
		extractIndividualizedQuestionData(questionsFile, destFolder);
		writeIndividualizedTimeToAcceptedData(destFolder);

	}

	public static void parseEarliestAndAcceptedAnswers(File srcFolder, File destFolder,
			File questionsList) throws DocumentException, IOException {
		File postsFile = new File(srcFolder, "posts.xml");
		File postHistoryFile = new File(srcFolder, "posthistory.xml");
		File commentsFile = new File(srcFolder, "comments.xml");

		parseEarliestAndAcceptedAnswers(postsFile, postHistoryFile, commentsFile, destFolder,
				questionsList);
	}

	public static void extractNumQuestionsByUser(File precomputesFolder, File questonList)
			throws IOException {

		System.out.println("========NUM QUESTIONS BY USER=========");
		File countsFile = new File(precomputesFolder, OWNER_QUESTION_COUNT_FILE);
		IndividualizedUserQuestionCountProcessor processor = new IndividualizedUserQuestionCountProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(countsFile, processor.getOwnerQuestionCounts());
	}

	public static void extractTimeToAnswerByUser(File precomputesFolder, File questonList)
			throws IOException {

		System.out.println("========EARLIEST ANSWER TIME BY USER=========");
		File timesFile = new File(precomputesFolder, OWNER_EARLIEST_ANSWER_TIMES_FILE);
		IndividualizedUserAnswerTimeProcessor processor = new IndividualizedUserAnswerTimeProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(timesFile, processor.getOwnerSummaryStats(),
				new SummaryStatisticsValueWriter());
	}

	public static void extractNumQuestionsByTag(File precomputesFolder, File questonList)
			throws IOException {

		System.out.println("========NUM QUESTIONS BY TAG=========");
		File countsFile = new File(precomputesFolder, TAG_QUESTION_COUNT_FILE);
		IndividualizedTagQuestionCountProcessor processor = new IndividualizedTagQuestionCountProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(countsFile, processor.getTagQuestionCounts());
	}

	public static void extractTimeToAnswerByTag(File precomputesFolder, File questonList)
			throws IOException {

		System.out.println("========EARLIEST ANSWER TIME BY TAG=========");
		File timesFile = new File(precomputesFolder, TAG_EARLIEST_ANSWER_TIMES_FILE);
		IndividualizedTagAnswerTimeProcessor processor = new IndividualizedTagAnswerTimeProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(timesFile, processor.getTagSummaryStats(),
				new SummaryStatisticsValueWriter());
	}

	public static void extractNumQuestionsByWord(File precomputesFolder, File questonList)
			throws IOException {

		System.out.println("========NUM QUESTIONS BY TITLE WORD=========");
		File countsFile = new File(precomputesFolder, WORD_QUESTION_COUNT_FILE);
		IndividualizedWordQuestionCountProcessor processor = new IndividualizedWordQuestionCountProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(countsFile, processor.getWordQuestionCounts());
	}

	public static void extractTimeToAnswerByWord(File precomputesFolder, File questonList)
			throws IOException {

		System.out.println("========EARLIEST ANSWER TIME BY TITLE WORD=========");
		File timesFile = new File(precomputesFolder, WORD_EARLIEST_ANSWER_TIMES_FILE);
		IndividualizedWordAnswerTimeProcessor processor = new IndividualizedWordAnswerTimeProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(timesFile, processor.getWordSummaryStats(),
				new SummaryStatisticsValueWriter());
	}

	public static void parseDataByUserTagsWords(File precomputesFolder, File questionsList)
			throws DocumentException, IOException {

		extractNumQuestionsByUser(precomputesFolder, questionsList);
		extractTimeToAnswerByUser(precomputesFolder, questionsList);

		extractNumQuestionsByTag(precomputesFolder, questionsList);
		extractTimeToAnswerByTag(precomputesFolder, questionsList);

		extractNumQuestionsByWord(precomputesFolder, questionsList);
		extractTimeToAnswerByWord(precomputesFolder, questionsList);
	}

	public static void writeQuestionsListsByUser(File precomputesFolder, File questonList)
			throws IOException {
		System.out.println("========QUESTION LISTS BY USER=========");
		File listFile = new File(precomputesFolder, OWNER_QUESTION_LISTS_FILE);
		IndividualizedUserQuestionListProcessor processor = new IndividualizedUserQuestionListProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(listFile, processor.getOwnerQuestionLists());
	}

	public static void writeQuestionsListsByTag(File precomputesFolder, File questonList)
			throws IOException {
		System.out.println("========QUESTION LISTS BY TAG=========");
		File listFile = new File(precomputesFolder, TAG_QUESTION_LISTS_FILE);
		IndividualizedTagQuestionListProcessor processor = new IndividualizedTagQuestionListProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(listFile, processor.getTagQuestionLists());
	}

	public static void writeQuestionsListsByWord(File precomputesFolder, File questonList)
			throws IOException {
		System.out.println("========QUESTION LISTS BY WORD=========");
		File listFile = new File(precomputesFolder, WORD_QUESTION_LISTS_FILE);
		IndividualizedWordQuestionListProcessor processor = new IndividualizedWordQuestionListProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);
		MapIOAssist.writeMap(listFile, processor.getWordQuestionLists());
	}

	public static void writeQuestionListsByValues(File precomputesFolder, File questionsList)
			throws IOException {
		writeQuestionsListsByUser(precomputesFolder, questionsList);
		writeQuestionsListsByTag(precomputesFolder, questionsList);
		writeQuestionsListsByWord(precomputesFolder, questionsList);
	}

	public static <V extends Comparable<V>> void writeValsByMostFrequent(File srcFreq, File dest,
			ValueParser<V> parser) throws IOException {
		Map<V, Integer> freqs = MapIOAssist.readMap(srcFreq, parser, new IntegerValueParser());
		ArrayList<V> sortedVals = SortedListAssist.orderByFrequency(freqs);
		CollectionIOAssist.writeCollection(dest, sortedVals);
	}

	public static <V extends Comparable<V>> void writeAllValsByMostFrequent(File precomputesFolder)
			throws IOException {

		writeValsByMostFrequent(new File(precomputesFolder, OWNER_QUESTION_COUNT_FILE), new File(
				precomputesFolder, OWNER_BY_MOST_FREQUENT_FILE), new IntegerValueParser());
		writeValsByMostFrequent(new File(precomputesFolder, TAG_QUESTION_COUNT_FILE), new File(
				precomputesFolder, TAG_BY_MOST_FREQUENT_FILE), new StringValueWriterAndParser());
		writeValsByMostFrequent(new File(precomputesFolder, WORD_QUESTION_COUNT_FILE), new File(
				precomputesFolder, WORD_BY_MOST_FREQUENT_FILE), new StringValueWriterAndParser());
	}

	public static <V extends Comparable<V>> Map<Integer, Collection<V>> getQuestionsToVal(File src,
			ValueParser<V> parser) throws IOException {
		System.out.print("Loading vals to questions...");
		Map<V, Collection<Integer>> valToMessages = MapIOAssist.readMap(src, parser,
				new CollectionValueParser<Integer>(new IntegerValueParser()));
		System.out.println("done.");

		System.out.print("Converting to questions to vals...");
		Map<Integer, Collection<V>> retVal = new TreeMap<Integer, Collection<V>>();

		for (Entry<V, Collection<Integer>> entry : valToMessages.entrySet()) {
			V val = entry.getKey();
			for (Integer message : entry.getValue()) {

				Collection<V> valCollection = retVal.get(message);
				if (valCollection == null) {
					valCollection = new TreeSet<V>();
					retVal.put(message, valCollection);
				}
				valCollection.add(val);
			}
		}
		System.out.println("done.");
		return retVal;
	}

	public static <V1 extends Comparable<V1>, V2 extends Comparable<V2>> void writePairs(File src1,
			File src2, ValueParser<V1> parser1, ValueParser<V2> parser2, File dest)
			throws IOException {

		CollectionValueParser<Integer> msgListParser = new CollectionValueParser<Integer>(
				new IntegerValueParser());

		Map<Integer, Collection<V1>> questionsToV1 = getQuestionsToVal(src1, parser1);
		PairFinder<V1, V2> pairFinder = new PairFinder<V1, V2>(questionsToV1);

		System.out.print("Reading map line by line...");
		MapIOAssist.readMapLineByLine(src2, parser2, msgListParser, pairFinder);
		System.out.println("done.");

		System.out.print("Wrinting map...");
		CollectionIOAssist.writeCollection(dest, pairFinder.getSeenPairs());
		System.out.println("done.");
	}

	public static void writeAllPairs(File precomputesFile) throws IOException {
		File ownersFile = new File(precomputesFile, OWNER_QUESTION_LISTS_FILE);
		File tagsFile = new File(precomputesFile, TAG_QUESTION_LISTS_FILE);
		File wordsFile = new File(precomputesFile, WORD_QUESTION_LISTS_FILE);

		writePairs(ownersFile, tagsFile, new IntegerValueParser(),
				new StringValueWriterAndParser(), new File(precomputesFile, PAIRS_OWNER_TAG_FILE));
		writePairs(ownersFile, wordsFile, new IntegerValueParser(),
				new StringValueWriterAndParser(), new File(precomputesFile, PAIRS_OWNER_WORD_FILE));
		writePairs(tagsFile, wordsFile, new StringValueWriterAndParser(),
				new StringValueWriterAndParser(), new File(precomputesFile, PAIRS_TAG_WORD_FILE));
	}

	public static void writeTopValueStats(File precomputesFolder) throws IOException {

		ArrayList<Integer> topOwners = new ArrayList<Integer>(CollectionIOAssist.readCollection(
				new File(precomputesFolder, "top owners.csv"), new IntegerValueParser()));
		ArrayList<String> topTags = new ArrayList<String>(
				CollectionIOAssist.readCollection(new File(precomputesFolder, "top tags.csv")));
		ArrayList<String> topWords = new ArrayList<String>(
				CollectionIOAssist.readCollection(new File(precomputesFolder, "top words.csv")));

		IndividualizedTopPairVals processor = new IndividualizedTopPairVals(topWords, topTags,
				topOwners);
		File questonList = new File(precomputesFolder, "questions list.txt");
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questonList,
				processor);

		ArrayIOAssist.writeArray(new File(precomputesFolder, "top word and owner counts.csv"),
				processor.getTopWordOwnerCounts());
		ArrayIOAssist.writeArray(new File(precomputesFolder,
				"top word and owner earliest times.csv"), processor.getTopWordOwnerEarliestTimes(),
				new SummaryStatisticsValueWriter());
		ArrayIOAssist.writeArray(new File(precomputesFolder,
				"top word and owner accepted times.csv"), processor.getTopWordOwnerAcceptedTimes(),
				new SummaryStatisticsValueWriter());

		ArrayIOAssist.writeArray(new File(precomputesFolder, "top word and tag counts.csv"),
				processor.getTopWordTagCounts());
		ArrayIOAssist.writeArray(
				new File(precomputesFolder, "top word and tag earliest times.csv"),
				processor.getTopWordTagEarliestTimes(), new SummaryStatisticsValueWriter());
		ArrayIOAssist.writeArray(
				new File(precomputesFolder, "top word and tag accepted times.csv"),
				processor.getTopWordTagAcceptedTimes(), new SummaryStatisticsValueWriter());

		ArrayIOAssist.writeArray(new File(precomputesFolder, "top owner and tag counts.csv"),
				processor.getTopOwnerTagCounts());
		ArrayIOAssist.writeArray(
				new File(precomputesFolder, "top owner and tag earliest times.csv"),
				processor.getTopOwnerTagEarliestTimes(), new SummaryStatisticsValueWriter());
		ArrayIOAssist.writeArray(
				new File(precomputesFolder, "top owner and tag accepted times.csv"),
				processor.getTopOwnerTagAcceptedTimes(), new SummaryStatisticsValueWriter());
	}

	public static Map<String, Set<String>> getOccurrences(File pairsFile) throws IOException {
		Map<String, Set<String>> retVal = new HashMap<String, Set<String>>();
		Collection<String> pairs = CollectionIOAssist.readCollection(pairsFile);
		for (String pair : pairs) {
			String[] splitPair = pair.split(",");

			Set<String> vals = retVal.get(splitPair[0]);
			if (vals == null) {
				vals = new TreeSet<String>();
				retVal.put(splitPair[0], vals);
			}
			vals.add(splitPair[1]);
		}
		return retVal;
	}

	public static void writeQuestionToEarliestTime(File precomputesFolder, File questionListFile)
			throws IOException {

		IndividualizedQuestionToEarliestProcessor processor = new IndividualizedQuestionToEarliestProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questionListFile,
				processor);

		MapIOAssist.writeMap(new File(precomputesFolder, "questions to earliest times.csv"),
				processor.getQuestionEarliestTimes());
	}

	public static void writeComparisonsForVerifyingCollaborative(File pairsFile, File outputFile,
			Map<String, Collection<Integer>> val1ToQuestion,
			Map<String, Collection<Integer>> val2ToQuestion) throws IOException {
		Map<String, Set<String>> occurrences = getOccurrences(pairsFile);
		ArrayList<String> keyVals = new ArrayList<String>(occurrences.keySet());

		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		int count = 0;
		int maxCount = 500;
		for (int i = 0; i < keyVals.size(); i++) {
			for (int j = i + 1; j < keyVals.size(); j++) {
				String key1 = keyVals.get(i);
				String key2 = keyVals.get(j);

				Set<Integer> key1Questions = new TreeSet<Integer>(val1ToQuestion.get(key1));
				key1Questions.retainAll(val1ToQuestion.get(key2));
				if (key1Questions.size() > 0) {
					continue;
				}

				ArrayList<String> vals = new ArrayList<String>(occurrences.get(key1));
				vals.retainAll(occurrences.get(key2));

				boolean found = false;
				for (int pos1 = 0; pos1 < vals.size(); pos1++) {
					for (int pos2 = pos1 + 1; pos2 < vals.size(); pos2++) {
						String val1 = vals.get(pos1);
						String val2 = vals.get(pos2);

						Set<Integer> val1Questions = new TreeSet<Integer>(val2ToQuestion.get(val1));
						val1Questions.retainAll(val2ToQuestion.get(val2));
						if (val1Questions.size() > 0) {
							continue;
						}
						found = true;
						count++;
						System.out.println(count);
						out.write(key1 + "," + val1);
						out.write(";");
						out.write(key2 + "," + val1);
						out.write(";");
						out.write(key1 + "," + val2);
						out.write(";");
						out.write(key2 + "," + val2);
						out.newLine();
						out.flush();

						if (found || count > maxCount) {
							break;
						}
					}
					if (found || count > maxCount) {
						break;
					}
				}

				if (found || count > maxCount) {
					break;
				}
			}
			if (count > maxCount) {
				break;
			}
		}
	}

	public static void writeComparisonPoints(File precomputesFolder) throws IOException {
		String[] pairTypes = { "owner and tag", "owner and word", "tag and word" };
		for (String pairType : pairTypes) {
			System.out.println("=========COMPARISONS FOR " + pairType.toUpperCase() + "=========");
			File pairFile = new File(precomputesFolder, pairType + " earliest times.csv");
			File outFile = new File(precomputesFolder, "comparisons for " + pairType + ".csv");
			String[] pairParts = pairType.split(" and ");
			Map<String, Collection<Integer>> part1Questions = MapIOAssist.readMap(new File(
					precomputesFolder, pairParts[0] + " question lists.csv"),
					new CollectionValueParser<Integer>(new IntegerValueParser()));
			Map<String, Collection<Integer>> part2Questions = MapIOAssist.readMap(new File(
					precomputesFolder, pairParts[1] + " question lists.csv"),
					new CollectionValueParser<Integer>(new IntegerValueParser()));
			writeComparisonsForVerifyingCollaborative(pairFile, outFile, part1Questions,
					part2Questions);
		}
	}

	public static <V1 extends Comparable<V1>, V2 extends Comparable<V2>> Map<Pair<V1, V2>, Double> getPairVals(
			File pairsFile, ValueParser<V1> parser1, ValueParser<V2> parser2) throws IOException {
		Map<Pair<V1, V2>, Double> retVal = new TreeMap<Pair<V1, V2>, Double>();

		Collection<String> lines = CollectionIOAssist.readCollection(pairsFile);
		for (String line : lines) {
			String[] parts = line.split(",");

			Pair<V1, V2> pair = new Pair<V1, V2>(parser1.parse(parts[0]), parser2.parse(parts[1]));
			Double val = Double.parseDouble(parts[2]);
			retVal.put(pair, val);
		}

		return retVal;

	}

	public static void writeQuestionsToValLists(File precomputesFolder, String valType)
			throws IOException {
		Map<Integer, Set<Integer>> questionToVals = new TreeMap<Integer, Set<Integer>>();

		ArrayList<String> vals = new ArrayList<String>(CollectionIOAssist.readCollection(new File(
				precomputesFolder, valType + " by most frequent.csv")));
		Map<String, Collection<Integer>> valToQuestions = MapIOAssist.readMap(new File(
				precomputesFolder, valType + " question lists.csv"),
				new CollectionValueParser<Integer>(new IntegerValueParser()));

		for (Entry<String, Collection<Integer>> entry : valToQuestions.entrySet()) {
			String val = entry.getKey();
			Integer valID = vals.indexOf(val);
			for (Integer question : entry.getValue()) {
				Set<Integer> questionVals = questionToVals.get(question);
				if (questionVals == null) {
					questionVals = new TreeSet<Integer>();
					questionToVals.put(question, questionVals);
				}
				questionVals.add(valID);
			}
		}

		File outputFile = new File(precomputesFolder, "questions to " + valType + " lists.csv");
		MapIOAssist.writeMap(outputFile, questionToVals);
	}

	public static void writeAllQuestionsToValLists(File precomputesFolder) throws IOException {
		String[] valTypes = { "owner", "word", "tag" };
		for (String valType : valTypes) {
			writeQuestionsToValLists(precomputesFolder, valType);
		}
	}

	public static <V1 extends Comparable<V1>, V2 extends Comparable<V2>> Double getVal(
			String pairStr, ValueParser<V1> parser1, ValueParser<V2> parser2,
			Map<Pair<V1, V2>, Double> pairToVal) {

		String[] parts = pairStr.split(",");
		V1 val1 = parser1.parse(parts[0]);
		V2 val2 = parser2.parse(parts[1]);
		return pairToVal.get(new Pair<V1, V2>(val1, val2));
	}

	public static <V1 extends Comparable<V1>, V2 extends Comparable<V2>> Collection<Pair<Double, Double>> getComparisonVals(
			File pairsFile, File comparisonPointsFile, ValueParser<V1> parser1,
			ValueParser<V2> parser2) throws IOException {

		Map<Pair<V1, V2>, Double> pairToVal = getPairVals(pairsFile, parser1, parser2);
		Collection<String> lines = CollectionIOAssist.readCollection(comparisonPointsFile);
		Collection<Pair<Double, Double>> comparisonVals = new TreeSet<Pair<Double, Double>>();
		for (String line : lines) {
			String[] pairs = line.split(";");

			Double val1 = getVal(pairs[0], parser1, parser2, pairToVal);
			Double val2 = getVal(pairs[1], parser1, parser2, pairToVal);
			Double val3 = getVal(pairs[2], parser1, parser2, pairToVal);
			Double val4 = getVal(pairs[3], parser1, parser2, pairToVal);

			Double diff1 = (val1 - val2) / val2;
			Double diff2 = (val3 - val4) / val4;

			comparisonVals.add(new Pair<Double, Double>(diff1, diff2));
		}

		return comparisonVals;
	}

	public static void writeAllComparisonVals(File precomputesFolder) throws IOException {
		String[] pairTypes = { /* "owner and tag", "owner and word", */"tag and word" };
		for (String pairType : pairTypes) {
			System.out.println("=========CALCULATING COMPARISON VALUES FOR "
					+ pairType.toUpperCase() + "=========");
			File pairFile = new File(precomputesFolder, pairType + " earliest times.csv");
			File comparisonsFile = new File(precomputesFolder, "comparisons for " + pairType
					+ ".csv");
			File outFile = new File(precomputesFolder, "comparisons vals for " + pairType + ".csv");
			Collection<Pair<Double, Double>> vals = getComparisonVals(pairFile, comparisonsFile,
					new StringValueWriterAndParser(), new StringValueWriterAndParser());
			CollectionIOAssist.writeCollection(outFile, vals);
		}
	}

	public static void writeAllOwnersWithTagWordPairs(File precomputesFolder, File questionsList)
			throws IOException {
//		extractNumQuestionsByTagWord(precomputesFolder, questionsList);
//		writeQuestionsListsByTagWord(precomputesFolder, questionsList);
//		writeTagWordByMostFrequent(precomputesFolder);
//		writeQuestionsToValLists(precomputesFolder, "tag_word");
//		writeOwnerAndTagWordPairs(precomputesFolder);
//		writeOwnerAndTagWordPairsForExperiments(precomputesFolder, questionsList);
		reduceSetofExperimentPairsSize(precomputesFolder, "owner and tag_word", 750000);
		randomizeSetOfExperimentPairs(precomputesFolder, "reduced owner and tag_word");
		buildSetOfExperimentRuns(precomputesFolder, "reduced owner and tag_word");
		writeOwnerAndTagWordComparisons(precomputesFolder);
	}

	public static void extractNumQuestionsByTagWord(File precomputesFolder, File questionList)
			throws IOException {

		System.out.println("========NUM QUESTIONS BY TAG/WORD=========");
		File countsFile = new File(precomputesFolder, TAG_WORD_QUESTION_COUNT_FILE);
		IndividualizedTagWordQuestionCountProcessor processor = new IndividualizedTagWordQuestionCountProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questionList,
				processor);
		MapIOAssist.writeMap(countsFile, processor.getTagWordQuestionCounts());
	}

	public static void writeQuestionsListsByTagWord(File precomputesFolder, File questionList)
			throws IOException {
		System.out.println("========QUESTION LISTS BY TAG/WORD=========");
		File listFile = new File(precomputesFolder, TAG_WORD_QUESTION_LISTS_FILE);
		IndividualizedTagWordQuestionListProcessor processor = new IndividualizedTagWordQuestionListProcessor();
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questionList,
				processor);
		MapIOAssist.writeMap(listFile, processor.getTagWordQuestionLists());
	}

	public static void writeTagWordByMostFrequent(File precomputesFolder) throws IOException {
		System.out.println("========TAG/WORD BY MOST FREQUENT=========");
		File questionCountFile = new File(precomputesFolder, TAG_WORD_QUESTION_COUNT_FILE);
		File listFile = new File(precomputesFolder, TAG_WORD_BY_MOST_FREQUENT_FILE);
		writeValsByMostFrequent(questionCountFile, listFile, new StringValueWriterAndParser());
	}

	public static void writeOwnerAndTagWordPairs(File precomputesFile) throws IOException {
		File ownersFile = new File(precomputesFile, OWNER_QUESTION_LISTS_FILE);
		File tagWordsFile = new File(precomputesFile, TAG_WORD_QUESTION_LISTS_FILE);

		writePairs(ownersFile, tagWordsFile, new IntegerValueParser(),
				new StringValueWriterAndParser(), new File(precomputesFile,
						PAIRS_OWNER_AND_TAG_WORD_FILE));
	}

	private static void writeOwnerAndTagWordPairsForExperiments(File precomputesFolder,
			File questionListsFile) throws IOException {
		System.out.println("==========EXTRACTING OWNER and TAG/WORD EXPERIMENT ARRAYS===========");
		ArrayList<Integer> owners = new ArrayList<Integer>(CollectionIOAssist.readCollection(
				new File(precomputesFolder, OWNER_BY_MOST_FREQUENT_FILE), new IntegerValueParser()));
		ArrayList<Pair<String, String>> tagWords = new ArrayList<Pair<String, String>>(
				CollectionIOAssist.readCollection(new File(precomputesFolder,
						TAG_WORD_BY_MOST_FREQUENT_FILE), new TagWordPairParser()));

		int numSplits = 2;
		int splitSize = tagWords.size() / numSplits;

		for (int splitNum = 1; splitNum < numSplits; splitNum++) {
			System.out.println("~~~Starting split " + splitNum + "~~~");

			boolean append = splitNum != 0;
			int start = splitNum * splitSize;
			int end = Math.min(tagWords.size() - 1, start + splitSize);
			ArrayList<Pair<String, String>> tagWordsSplit = new ArrayList<Pair<String, String>>();
			for (int i = 0; i < start; i++) {
				tagWordsSplit.add(null);
			}
			tagWordsSplit.addAll(start, tagWords.subList(start, end));

			IndividualizedPairsBuilderProcessor<Integer, Pair<String, String>> processor = new IndividualizedPairsBuilderProcessor<Integer, Pair<String, String>>(
					owners, tagWordsSplit, new ExperimentFeatureReader.OwnerFeatureReader(),
					new ExperimentFeatureReader.TagWordFeatureReader());
			processAndWriteExtractedExperimentPairs(append, precomputesFolder, questionListsFile,
					processor, "owner and tag_word");
		}

	}

	public static void writeOwnerAndTagWordComparisons(File precomputesFolder) throws IOException {
		String pairType = "owner and tag_word";
		System.out.println("=========COMPARISONS FOR " + pairType.toUpperCase() + "=========");
		File pairFile = new File(precomputesFolder, pairType + " earliest times.csv");
		File outFile = new File(precomputesFolder, "comparisons for " + pairType + ".csv");
		String[] pairParts = pairType.split(" and ");
		Map<String, Collection<Integer>> part1Questions = MapIOAssist.readMap(new File(
				precomputesFolder, pairParts[0] + " question lists.csv"),
				new CollectionValueParser<Integer>(new IntegerValueParser()));
		Map<String, Collection<Integer>> part2Questions = MapIOAssist.readMap(new File(
				precomputesFolder, pairParts[1] + " question lists.csv"),
				new CollectionValueParser<Integer>(new IntegerValueParser()));
		writeComparisonsForVerifyingCollaborative(pairFile, outFile, part1Questions, part2Questions);
	}

	public static void parseEarliestAndAcceptedAnswers(File postsFile, File postHistoryFile,
			File commentsFile, File destFolder, File questionsList) throws DocumentException,
			IOException {

		File questionsFile = new File(destFolder, "questions.txt");
		File answersFile = new File(destFolder, "answers.txt");

		PostRowParser postParser = new PostRowParser(questionsFile, answersFile);
		StackExchangeDumpReader.readRows(postsFile, postParser);
//
//		extractEarliestAndAcceptedAnswerTimes(questionsFile, answersFile, destFolder);
//		parseDataByUserTagsWords(destFolder, questionsList);
//		writeQuestionListsByValues(destFolder, questionsList);
//		writeAllQuestionsToValLists(destFolder);
//		writeQuestionToEarliestTime(destFolder, questionsList);
//		writeAllValsByMostFrequent(destFolder);
//		writeAllPairs(destFolder);
//		writePairsForExperiments(destFolder, questionsList);
//		randomizeAllExperimentPairs(destFolder);
//		buildAllExperimentRuns(destFolder);
//		writeTopValueStats(destFolder);
//		writeComparisonPoints(destFolder);
//		writeAllComparisonVals(destFolder);
		writeAllOwnersWithTagWordPairs(destFolder, questionsList);
	}

	private static <V1 extends Comparable<V1>, V2 extends Comparable<V2>> void processAndWriteExtractedExperimentPairs(
			boolean append, File precomputesFolder, File questionListsFile,
			IndividualizedPairsBuilderProcessor<V1, V2> processor, String outFilePrefix)
			throws IOException {
		IndividualizedQuestionProcessor.processAllQuestions(precomputesFolder, questionListsFile,
				processor);
		MapIOAssist.writeMap(new File(precomputesFolder, outFilePrefix + " counts.csv"), append,
				processor.getCounts());
		MapIOAssist.writeMap(new File(precomputesFolder, outFilePrefix + " earliest times.csv"),
				append, processor.getEarliestTimes(), new SummaryStatisticsValueWriter());
		MapIOAssist.writeMap(new File(precomputesFolder, outFilePrefix + " accepted times.csv"),
				append, processor.getAcceptedTimes(), new SummaryStatisticsValueWriter());
	}

	private static void writeOwnerTagPairsForExperiments(File precomputesFolder,
			File questionListsFile) throws IOException {
		System.out.println("==========EXTRACTING OWNER/TAG EXPERIMENT ARRAYS===========");
		ArrayList<Integer> owners = new ArrayList<Integer>(MapIOAssist.readTreeMap(
				new File(precomputesFolder, "owner question counts.csv"), new IntegerValueParser(),
				new StringValueWriterAndParser()).keySet());
		ArrayList<String> tags = new ArrayList<String>(MapIOAssist.readTreeMap(
				new File(precomputesFolder, "tag question counts.csv"),
				new StringValueWriterAndParser(), new StringValueWriterAndParser()).keySet());

		IndividualizedPairsBuilderProcessor<Integer, String> processor = new IndividualizedPairsBuilderProcessor<Integer, String>(
				owners, tags, new ExperimentFeatureReader.OwnerFeatureReader(),
				new ExperimentFeatureReader.TagFeatureReader());

		processAndWriteExtractedExperimentPairs(false, precomputesFolder, questionListsFile,
				processor, "owner and tag");
	}

	private static void writeTagWordPairsForExperiments(File precomputesFolder,
			File questionListsFile) throws IOException {
		System.out.println("==========EXTRACTING WORD/TAG EXPERIMENT ARRAYS===========");
		ArrayList<String> words = new ArrayList<String>(MapIOAssist.readTreeMap(
				new File(precomputesFolder, "word question counts.csv"),
				new StringValueWriterAndParser(), new StringValueWriterAndParser()).keySet());
		ArrayList<String> tags = new ArrayList<String>(MapIOAssist.readTreeMap(
				new File(precomputesFolder, "tag question counts.csv"),
				new StringValueWriterAndParser(), new StringValueWriterAndParser()).keySet());

		IndividualizedPairsBuilderProcessor<String, String> processor = new IndividualizedPairsBuilderProcessor<String, String>(
				tags, words, new ExperimentFeatureReader.TagFeatureReader(),
				new ExperimentFeatureReader.WordFeatureReader());

		processAndWriteExtractedExperimentPairs(false, precomputesFolder, questionListsFile,
				processor, "tag and word");
	}

	private static void writeOwnerWordPairsForExperiments(File precomputesFolder,
			File questionListsFile) throws IOException {
		System.out.println("==========EXTRACTING OWNER/WORD EXPERIMENT ARRAYS===========");
		ArrayList<Integer> owners = new ArrayList<Integer>(MapIOAssist.readTreeMap(
				new File(precomputesFolder, "owner question counts.csv"), new IntegerValueParser(),
				new StringValueWriterAndParser()).keySet());
		ArrayList<String> words = new ArrayList<String>(MapIOAssist.readTreeMap(
				new File(precomputesFolder, "word question counts.csv"),
				new StringValueWriterAndParser(), new StringValueWriterAndParser()).keySet());

		IndividualizedPairsBuilderProcessor<Integer, String> processor = new IndividualizedPairsBuilderProcessor<Integer, String>(
				owners, words, new ExperimentFeatureReader.OwnerFeatureReader(),
				new ExperimentFeatureReader.WordFeatureReader());

		processAndWriteExtractedExperimentPairs(false, precomputesFolder, questionListsFile,
				processor, "owner and word");
	}

	private static void writePairsForExperiments(File precomputesFolder, File questionListFile)
			throws IOException {
		writeOwnerTagPairsForExperiments(precomputesFolder, questionListFile);
		writeTagWordPairsForExperiments(precomputesFolder, questionListFile);
		writeOwnerWordPairsForExperiments(precomputesFolder, questionListFile);
	}

	private static void randomizeExperimentPairsFile(File precomputesFolder, String fileName)
			throws IOException {
		System.out.println("=======RANDOMIZING " + fileName + "=======");
		Set<String> lines = new HashSet<String>(CollectionIOAssist.readCollection(new File(
				precomputesFolder, fileName)));
		CollectionIOAssist.writeCollection(new File(precomputesFolder, "randomized " + fileName),
				lines);
	}

	private static void randomizeSetOfExperimentPairs(File precomputesFolder, String prefix)
			throws IOException {
		randomizeExperimentPairsFile(precomputesFolder, prefix + " counts.csv");
		randomizeExperimentPairsFile(precomputesFolder, prefix + " earliest times.csv");
		randomizeExperimentPairsFile(precomputesFolder, prefix + " accepted times.csv");
	}

	private static void reduceExperimentPairsSize(File precomputesFolder, String filename,
			int maxSize) throws IOException {
		System.out.println("=======REDUCING SIZE OF " + filename + "=======");
		ArrayList<String> lines = new ArrayList<String>(CollectionIOAssist.readCollection(new File(
				precomputesFolder, filename)));

		while (lines.size() > maxSize) {
			lines.remove(lines.size() - 1);
		}

		CollectionIOAssist.writeCollection(new File(precomputesFolder, "reduced " + filename),
				lines);
	}

	private static void reduceSetofExperimentPairsSize(File precomputesFolder, String prefix,
			int maxSize) throws IOException {
		reduceExperimentPairsSize(precomputesFolder, prefix + " counts.csv", maxSize);
		reduceExperimentPairsSize(precomputesFolder, prefix + " earliest times.csv", maxSize);
		reduceExperimentPairsSize(precomputesFolder, prefix + " accepted times.csv", maxSize);
	}

	private static void randomizeAllExperimentPairs(File precomputesFolder) throws IOException {
		randomizeSetOfExperimentPairs(precomputesFolder, "owner and tag");
		randomizeSetOfExperimentPairs(precomputesFolder, "owner and word");
		randomizeSetOfExperimentPairs(precomputesFolder, "tag and word");
	}

	private static void buildExperimentRunsForPairs(File precomputesFolder, String prefix,
			String pairsType) throws IOException {

		System.out.println("=======BUILDING EXPERIMENT RUNS FOR " + prefix + " " + pairsType
				+ "=======");
		File pairsFile = new File(precomputesFolder, "randomized " + prefix + " " + pairsType
				+ ".csv");
		ArrayList<String> pairs = (ArrayList<String>) CollectionIOAssist.readCollection(pairsFile);

		File folder = new File(new File(new File(precomputesFolder, "experiment sets"), prefix),
				pairsType);

		int numRuns = 10;
		int testSize = pairs.size() / numRuns;
		for (int run = 0; run < numRuns; run++) {
			System.out.print("run " + run + "...");
			File runFolder = new File(folder, "" + (run + 1));
			if (!runFolder.exists()) {
				runFolder.mkdirs();
			}
			BufferedWriter trainOut = new BufferedWriter(new FileWriter(new File(runFolder,
					"train.csv")));
			BufferedWriter testOut = new BufferedWriter(new FileWriter(new File(runFolder,
					"test.csv")));

			for (int i = 0; i < pairs.size(); i++) {
				BufferedWriter out;
				if (i < run * testSize || i >= (run + 1) * testSize) {
					out = trainOut;
				} else {
					out = testOut;
				}
				out.write(pairs.get(i));
				out.newLine();
				out.flush();
			}
			trainOut.close();
			testOut.close();
		}
		System.out.println("done.");
	}

	private static void buildSetOfExperimentRuns(File precomputesFolder, String prefix)
			throws IOException {
		buildExperimentRunsForPairs(precomputesFolder, prefix, "earliest times");
//		buildExperimentRunsForPairs(precomputesFolder, prefix, "accepted times");
	}

	private static void buildAllExperimentRuns(File precomputesFolder) throws IOException {
		buildSetOfExperimentRuns(precomputesFolder, "owner and tag");
		buildSetOfExperimentRuns(precomputesFolder, "owner and word");
		buildSetOfExperimentRuns(precomputesFolder, "tag and word");
	}

	private static void extractRandomSampleOfQuestions(File precomputesFolder, int sampleSize)
			throws IOException {
		File questionList = new File(precomputesFolder, "questions list.txt");
		ArrayList<String> questions = new ArrayList<String>(
				CollectionIOAssist.readCollection(questionList));

		Set<String> sampledQuestions = new TreeSet<String>();
		Random rand = new Random();

		while (sampledQuestions.size() < sampleSize) {
			int pos = rand.nextInt(questions.size());
			sampledQuestions.add(questions.get(pos));
		}

		CollectionIOAssist.writeCollection(
				new File(precomputesFolder, "sampled questions list.txt"), sampledQuestions);
	}

	public static void computeStats(File destFolder) {

		try {
			Map<Integer, Long> timeOfQuestion = MapIOAssist.readMap(new File(destFolder,
					QUESTION_TIMES_FILE), new IntegerValueParser(), new LongValueParser());

			System.out.println("Number of questions: " + timeOfQuestion.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Collection<Integer> answerIDs = CollectionIOAssist.readCollection(new File(destFolder,
					ANSWER_IDS_FILE), new IntegerValueParser());

			System.out.println("Number of answers: " + answerIDs.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Map<Integer, Long> timeOfEarliestAnswers = MapIOAssist.readMap(new File(destFolder,
					QUESTION_EARLIEST_ANSWER_TIME_FILE), new IntegerValueParser(),
					new LongValueParser());

			System.out.println("Questions with answers: " + timeOfEarliestAnswers.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Map<Integer, Integer> acceptedAnswers = MapIOAssist.readMap(new File(destFolder,
					QUESTION_ACCEPTED_ANSWER_FILE), new IntegerValueParser(),
					new IntegerValueParser());

			System.out.println("Questions with accepted answers: " + acceptedAnswers.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static Map<String, SummaryStatistics> getAnswerTimesStats(File precomputesFolder,
			String feature) throws IOException {
		Map<Integer, Long> questionToTime = MapIOAssist.readTreeMap(new File(precomputesFolder,
				"question earliest answer time.csv"), new IntegerValueParser(),
				new LongValueParser());

		File questionListFile = new File(precomputesFolder, feature + " question lists.csv");
		Map<String, Collection<Integer>> userToQuestionList = MapIOAssist.readTreeMap(
				questionListFile, new StringValueWriterAndParser(),
				new CollectionValueParser<Integer>(new IntegerValueParser()));

		Map<String, SummaryStatistics> stats = new TreeMap<String, SummaryStatistics>();
		for (Entry<String, Collection<Integer>> entry : userToQuestionList.entrySet()) {
			String user = entry.getKey();
			Collection<Integer> questions = entry.getValue();

			SummaryStatistics stat = new SummaryStatistics();
			for (Integer question : questions) {
				Long time = questionToTime.get(question);
				if (time != null) {
					stat.addValue(time);
				}
			}
			stats.put(user, stat);
		}
		return stats;
	}

	public static void writeVarianceByFeature(File precomputesFolder, String feature)
			throws IOException {
		Map<String, SummaryStatistics> stats = getAnswerTimesStats(precomputesFolder, feature);

		File stdevFile = new File(precomputesFolder, feature + " time stdev.csv");
		File rangeFile = new File(precomputesFolder, feature + " time range.csv");

		BufferedWriter stdevOut = new BufferedWriter(new FileWriter(stdevFile));
		BufferedWriter rangeOut = new BufferedWriter(new FileWriter(rangeFile));

		for (Entry<String, SummaryStatistics> entry : stats.entrySet()) {
			String user = entry.getKey();
			SummaryStatistics userStats = entry.getValue();
			if (userStats.getN() < 2) {
				continue;
			}

			stdevOut.write(user + "," + userStats.getStandardDeviation());
			stdevOut.newLine();
			stdevOut.flush();

			rangeOut.write(user + "," + (userStats.getMax() - userStats.getMin()));
			rangeOut.newLine();
			rangeOut.flush();
		}

		stdevOut.close();
		rangeOut.close();

	}

	public static void main(String[] args) throws DocumentException, IOException {

//		File precomputeFolder = new File("D:\\Stack Overflow data\\all questions vals");
//		writeVarianceByFeature(precomputeFolder, "owner");

		File dataFolder = new File(
				"C:\\Users\\bartel\\Workspaces\\StackExchange Data\\Stack Exchange Data Dump - Aug 2012\\Content");
		File precomputeFolder = new File("D:\\Stack Overflow data");
//		File questionsFile = new File(precomputeFolder, "questions list.txt");
		File questionsFile = new File(precomputeFolder, "sampled questions list.txt");
//		extractRandomSampleOfQuestions(precomputeFolder, 100000);
		parseEarliestAndAcceptedAnswers(dataFolder, precomputeFolder, questionsFile);
//		computeStats(precomputeFolder);
	}
}
