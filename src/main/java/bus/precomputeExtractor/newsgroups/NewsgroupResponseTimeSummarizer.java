package bus.precomputeExtractor.newsgroups;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import reader.threadfinder.IteratedThreadStatistics;
import reader.threadfinder.newsgroups.tools.NewsgroupPrecomputeBuilder;
import bus.accounts.Account;
import bus.data.parsers.FrequencyParser;
import bus.precomputeExtractor.TopRecipientsPrecomputesReader;

public class NewsgroupResponseTimeSummarizer {

	private final File messageRoot;
	private final File precomputesFolder;

	private final ArrayList<String> topAddresses;
	private final ArrayList<String> topBodyWords;
	private final ArrayList<String> topSubjectWords;
	private final ArrayList<String> punctuations;

	private Map<String, ArrayList<Double>> bodyWordsTimes;
	private Map<String, ArrayList<Double>> subjWordsTimes;
	private Map<String, ArrayList<Double>> bodyPunctuationTimes;
	private Map<String, ArrayList<Double>> subjPunctuationTimes;

	private SummaryStatistics[][] addressBodyWordsTimes;
	private SummaryStatistics[][] addressSubjectWordsTimes;
	private SummaryStatistics[][] addressBodyPunctuationTimes;
	private SummaryStatistics[][] addressSubjectPunctuationTimes;

	public NewsgroupResponseTimeSummarizer(File messageRoot, File precomputesFolder,
			File topAddressesFile, File topBodyWordsFile, File topSubjectWordsFile)
			throws IOException {

		this.messageRoot = messageRoot;
		this.precomputesFolder = precomputesFolder;

		topAddresses = new ArrayList<String>(loadList(topAddressesFile));
		topBodyWords = new ArrayList<String>(loadList(topBodyWordsFile));
		topSubjectWords = new ArrayList<String>(loadList(topSubjectWordsFile));
		punctuations = new ArrayList<String>(Arrays.asList(FrequencyParser.punctuations));
	}

	private Set<String> loadList(File src) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(src));

		Set<String> list = new TreeSet<String>();

		String line = in.readLine();
		while (line != null) {
			list.add(line.toLowerCase());
			line = in.readLine();
		}

		in.close();
		return list;
	}

	private Set<String> getKeysList(File src) throws IOException {

		Set<String> keys = new HashSet<String>();
		BufferedReader in = new BufferedReader(new FileReader(src));

		String line = in.readLine();
		while (line != null) {

			String key = line.substring(0, line.lastIndexOf('\t')).toLowerCase();
			keys.add(key);

			line = in.readLine();
		}

		in.close();
		return keys;
	}

	public void writeCounts() throws IOException {

		File bodyWordFolder = new File(precomputesFolder, "body words response times");
		File subjWordFolder = new File(precomputesFolder, "subject words response times");
		File bodyPunctuationFolder = new File(precomputesFolder, "body punctuations response times");
		File subjPunctuationFolder = new File(precomputesFolder,
				"subject punctuations response times");

		writeListOfTimes(bodyWordFolder, bodyWordsTimes);
		writeListOfTimes(subjWordFolder, subjWordsTimes);
		writeListOfTimes(bodyPunctuationFolder, bodyWordsTimes);
		writeListOfTimes(subjPunctuationFolder, subjPunctuationTimes);

		writeStatSummaries(bodyWordFolder, addressBodyWordsTimes, topBodyWords);
		writeStatSummaries(subjWordFolder, addressSubjectWordsTimes, topSubjectWords);
		writeStatSummaries(bodyPunctuationFolder, addressBodyPunctuationTimes, punctuations);
		writeStatSummaries(subjPunctuationFolder, addressSubjectPunctuationTimes, punctuations);
	}

	private void writeStatSummaries(File folder, SummaryStatistics[][] summaries,
			ArrayList<String> topVals) throws IOException {

		if (!folder.exists()) {
			folder.mkdirs();
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(new File(folder,
				"mean response times by top address.csv")));

		for (String address : topAddresses) {
			out.write("," + address);
		}
		out.newLine();

		for (int i = 0; i < summaries.length; i++) {
			out.write("\"" + topVals.get(i) + "\"");
			for (int j = 0; j < summaries[i].length; j++) {
				out.write(",");
				if (summaries[i][j] != null) {
					out.write("" + summaries[i][j].getMean());
				}
			}
			out.newLine();
		}
		out.flush();
		out.close();
	}

	private void writeListOfTimes(File folder, Map<String, ArrayList<Double>> statsMap)
			throws IOException {

		if (!folder.exists()) {
			folder.mkdirs();
		}

		File key = new File(folder, "key.txt");
		BufferedWriter keyOut = new BufferedWriter(new FileWriter(key));

		int count = 1;
		for (Entry<String, ArrayList<Double>> entry : statsMap.entrySet()) {
			keyOut.write("" + count + " " + entry.getKey());
			keyOut.newLine();

			BufferedWriter out = new BufferedWriter(
					new FileWriter(new File(folder, count + ".txt")));
			ArrayList<Double> stats = entry.getValue();
			for (Double value : stats) {
				out.write("" + value);
				out.newLine();
			}
			out.flush();
			out.close();

			count++;
		}
		keyOut.flush();
		keyOut.close();
	}

	public void collectCounts() throws IOException, MessagingException {

		bodyWordsTimes = new TreeMap<String, ArrayList<Double>>();
		subjWordsTimes = new TreeMap<String, ArrayList<Double>>();
		bodyPunctuationTimes = new TreeMap<String, ArrayList<Double>>();
		subjPunctuationTimes = new TreeMap<String, ArrayList<Double>>();

		addressBodyWordsTimes = new SummaryStatistics[topBodyWords.size()][topAddresses.size()];
		addressSubjectWordsTimes = new SummaryStatistics[topSubjectWords.size()][topAddresses
				.size()];
		addressBodyPunctuationTimes = new SummaryStatistics[punctuations.size()][topAddresses
				.size()];
		addressSubjectPunctuationTimes = new SummaryStatistics[punctuations.size()][topAddresses
				.size()];

		NewsgroupPrecomputeBuilder builder = new NewsgroupPrecomputeBuilder(messageRoot,
				precomputesFolder);

		System.out.println("Getting iterated stats");
		ArrayList<IteratedThreadStatistics> iteratedStats = builder.getIteratedThreadStatistics();

		System.out.println("Sorting iterated stats");
		Map<Integer, ArrayList<IteratedThreadStatistics>> sortedThreadStats = builder
				.getSortedIteratedThreadStatistics(iteratedStats);

		System.out.println("Collecting summaries");
		System.out.println("total: " + sortedThreadStats.size());
		int threadCount = 1;
		for (Entry<Integer, ArrayList<IteratedThreadStatistics>> entry : sortedThreadStats
				.entrySet()) {

			if (threadCount % 1000 == 0)
				System.out.println(threadCount);
			threadCount++;

			ArrayList<IteratedThreadStatistics> statsList = entry.getValue();
			for (int i = 0; i < statsList.size() - 1; i++) {
				IteratedThreadStatistics currStats = statsList.get(i);
				IteratedThreadStatistics nextStats = statsList.get(i + 1);

				double responseTime = nextStats.getLatestDate().getTime()
						- currStats.getLatestDate().getTime();
				responseTime = responseTime / 1000 / 3600;

				File currMsgFile = currStats.getPostLocation();
				String precomputePrefix = currMsgFile.getAbsolutePath().substring(
						messageRoot.getAbsolutePath().length());
				precomputePrefix = new File(precomputesFolder, precomputePrefix).getAbsolutePath();

				File addressFile = new File(precomputePrefix + Account.ADDR_FILE_SUFFIX);

				Set<String> addresses = TopRecipientsPrecomputesReader.getAddresses(addressFile);

				Set<String> intersect = new HashSet<String>(addresses);
				intersect.retainAll(topAddresses);

				if (intersect.size() == 0) {
					continue;
				}

				File bodyWordCountFile = new File(precomputePrefix + Account.WORD_COUNTS_SUFFIX);
				File subjWordCountFile = new File(precomputePrefix
						+ Account.SUBJECT_WORD_COUNTS_SUFFIX);
				File punctuationCountFile = new File(precomputePrefix
						+ Account.PUNCTUATION_COUNTS_SUFFIX);
				File subjPunctuationCountFile = new File(precomputePrefix
						+ Account.SUBJECT_PUNCTUATION_COUNTS_SUFFIX);

				Set<String> bodyWords = getKeysList(bodyWordCountFile);
				bodyWords.retainAll(topBodyWords);
				recordTime(bodyWords, topBodyWords, intersect, responseTime, addressBodyWordsTimes,
						bodyWordsTimes);

				Set<String> subjWords = getKeysList(subjWordCountFile);
				subjWords.retainAll(topSubjectWords);
				recordTime(subjWords, topSubjectWords, intersect, responseTime,
						addressSubjectWordsTimes, subjWordsTimes);

				Set<String> bodyPunctuation = getKeysList(punctuationCountFile);
				recordTime(bodyPunctuation, punctuations, intersect, responseTime,
						addressBodyPunctuationTimes, bodyPunctuationTimes);

				Set<String> subjPunctuation = getKeysList(subjPunctuationCountFile);
				recordTime(subjPunctuation, punctuations, intersect, responseTime,
						addressSubjectPunctuationTimes, subjPunctuationTimes);
			}
		}
	}

	private void recordTime(Set<String> words, ArrayList<String> topWords, Set<String> addresses,
			double responseTime, SummaryStatistics[][] summaries,
			Map<String, ArrayList<Double>> wordSummaries) {
		for (String word : words) {
			ArrayList<Double> wordStats = wordSummaries.get(word);
			if (wordStats == null) {
				wordStats = new ArrayList<Double>();
				wordSummaries.put(word, wordStats);
			}
			wordStats.add(responseTime);

			int wordPos = topWords.indexOf(word);
			for (String address : addresses) {
				int addressPos = topAddresses.indexOf(address);
				if (summaries[wordPos][addressPos] == null) {
					summaries[wordPos][addressPos] = new SummaryStatistics();
				}
				summaries[wordPos][addressPos].addValue(responseTime);
			}
		}
	}

	public static void main(String[] args) throws IOException, MessagingException {
		File rootFolder = new File("D:\\Newsgroup data\\posts");
		File precomputesFolder = new File("D:\\Newsgroup data\\precomputes");
		File topAddressesFile = new File(precomputesFolder, "top_recipients.txt");
		File topBodyWordsFile = new File(precomputesFolder, "top_words.txt");
		File topSubjectWordsFile = new File(precomputesFolder, "top_subject_words.txt");

		NewsgroupResponseTimeSummarizer summarizer = new NewsgroupResponseTimeSummarizer(
				rootFolder, precomputesFolder, topAddressesFile, topBodyWordsFile,
				topSubjectWordsFile);
		summarizer.collectCounts();
		summarizer.writeCounts();
	}
}
