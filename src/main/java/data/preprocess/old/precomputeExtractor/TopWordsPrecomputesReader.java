package data.preprocess.old.precomputeExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import bus.accounts.Account;

public class TopWordsPrecomputesReader extends TopRecipientsPrecomputesReader {

	private Set<String> topWords;

	private final Map<String, Map<String, Integer>> bodyWordFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> bodyWordMessageCounts = new TreeMap<String, Map<String, Integer>>();

	private final Map<String, Map<String, Integer>> subjectWordFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> subjectWordMessageCounts = new TreeMap<String, Map<String, Integer>>();

	private final Map<String, Map<String, Integer>> bodyPunctuationFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> bodyPunctuationMessageCounts = new TreeMap<String, Map<String, Integer>>();

	private final Map<String, Map<String, Integer>> subjectPunctuationFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> subjectPunctuationMessageCounts = new TreeMap<String, Map<String, Integer>>();

	public TopWordsPrecomputesReader(File topWordsListFile, File stopWordFile) throws IOException {
		super(topWordsListFile, stopWordFile);
	}

	public TopWordsPrecomputesReader(Set<String> words, File stopWordFile) throws IOException {

		super(words, stopWordFile);
	}

	@Override
	protected void init(File topRecipientsListFile) throws IOException {
		super.init(topRecipientsListFile);
		topWords = super.topRecipients;
	}

	@Override
	public void readPrecomputes(File precomputePrefix) throws IOException, MessagingException {
		File addressFile = new File(precomputePrefix + Account.ADDR_FILE_SUFFIX);

		File wordCountFile = new File(precomputePrefix + Account.WORD_COUNTS_SUFFIX);
		File subjWordCountFile = new File(precomputePrefix + Account.SUBJECT_WORD_COUNTS_SUFFIX);
		File punctuationCountFile = new File(precomputePrefix + Account.PUNCTUATION_COUNTS_SUFFIX);
		File subjPunctuationCountFile = new File(precomputePrefix
				+ Account.SUBJECT_PUNCTUATION_COUNTS_SUFFIX);

		readBodyWordCounts(wordCountFile, addressFile);
		readBodyPunctuationCounts(punctuationCountFile, addressFile);
		readSubjectWordFrequencies(subjWordCountFile, addressFile);
		readSubjectPunctuationFrequencies(subjPunctuationCountFile, addressFile);

	}

	@Override
	public void writeSummaries(File precomputesFolder) throws IOException {

		precomputesFolder = new File(precomputesFolder, "counts by subject word");
		if (!precomputesFolder.exists()) {
			precomputesFolder.mkdirs();
		}

		File wordsByMessageFile = new File(precomputesFolder, WORDS_BY_MESSAGE);
		File wordsBySubjectFile = new File(precomputesFolder, WORDS_BY_SUBJECT);
		// File punctuationByMessageFile = new File(precomputesFolder,
		// PUNCTUATION_BY_MESSAGE);
		// File punctuationBySubjectFile = new File(precomputesFolder,
		// PUNCTUATION_BY_SUBJECT);

		File totalWordsInBodyFile = new File(precomputesFolder, TOTAL_WORDS_BY_MESSAGE);
		File totalWordsInSubjectFile = new File(precomputesFolder, TOTAL_WORDS_BY_SUBJECT);
		// File totalPunctuationInBodyFile = new File(precomputesFolder,
		// TOTAL_PUNCTUATION_BY_MESSAGE);
		// File totalPunctuationInSubjectFile = new File(precomputesFolder,
		// TOTAL_PUNCTUATION_BY_SUBJECT);

		writeCounts(wordsByMessageFile, bodyWordMessageCounts);
		writeCounts(wordsBySubjectFile, subjectWordMessageCounts);
		// writeCounts(punctuationByMessageFile, bodyPunctuationMessageCounts);
		// writeCounts(punctuationBySubjectFile,
		// subjectPunctuationMessageCounts);
		writeCounts(totalWordsInBodyFile, bodyWordFrequencyCounts);
		writeCounts(totalWordsInSubjectFile, subjectWordFrequencyCounts);
		// writeCounts(totalPunctuationInBodyFile,
		// bodyPunctuationFrequencyCounts);
		// writeCounts(totalPunctuationInSubjectFile,
		// subjectPunctuationFrequencyCounts);
	}

	private void writeCounts(File dest, Map<String, Map<String, Integer>> counts)
			throws IOException {

		int numTopCounts = 10;

		String[][] topCounts = new String[counts.size()][numTopCounts + 1];
		int addrNum = 0;
		for (Entry<String, Map<String, Integer>> addressEntry : counts.entrySet()) {
			Map<Integer, Set<String>> orderedCounts = new TreeMap<Integer, Set<String>>(
					new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {
							return -1 * o1.compareTo(o2);
						}
					});

			Map<String, Integer> addrCounts = addressEntry.getValue();

			for (Entry<String, Integer> entry : addrCounts.entrySet()) {
				Set<String> keysWithCount = orderedCounts.get(entry.getValue());
				if (keysWithCount == null) {
					keysWithCount = new TreeSet<String>();
					orderedCounts.put(entry.getValue(), keysWithCount);
				}
				keysWithCount.add(entry.getKey());
			}

			topCounts[addrNum][0] = addressEntry.getKey();

			int pos = 1;
			for (Entry<Integer, Set<String>> entry : orderedCounts.entrySet()) {

				int count = entry.getKey();

				for (String key : entry.getValue()) {
					topCounts[addrNum][pos] = key + "(" + count + ")";
					pos++;
					if (pos > numTopCounts) {
						break;
					}
				}
				if (pos > numTopCounts) {
					break;
				}
			}
			addrNum++;
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for (int i = 0; i < topCounts[0].length; i++) {
			for (int j = 0; j < topCounts.length; j++) {
				out.write("=\"" + topCounts[j][i] + "\",");
			}
			out.newLine();
		}
		out.flush();
		out.close();

	}

	private void readCountsByMessage(File addrFile, File src,
			Map<String, Map<String, Integer>> messageCount,
			Map<String, Map<String, Integer>> totalCount, boolean useStopWords) throws IOException {

		Set<String> addresses = getAddresses(addrFile);

		BufferedReader in = new BufferedReader(new FileReader(src));

		String line = in.readLine();
		while (line != null) {

			String key = line.substring(0, line.lastIndexOf('\t'));
			if (!topWords.contains(key)) {
				line = in.readLine();
				continue;
			}
			int count = Integer.parseInt(line.substring(line.lastIndexOf('\t') + 1));

			Map<String, Integer> wordMessageCount = messageCount.get(key);
			if (wordMessageCount == null) {
				wordMessageCount = new TreeMap<String, Integer>();
				messageCount.put(key, wordMessageCount);
			}

			Map<String, Integer> wordTotalCount = totalCount.get(key);
			if (wordTotalCount == null) {
				wordTotalCount = new TreeMap<String, Integer>();
				totalCount.put(key, wordMessageCount);
			}

			for (String address : addresses) {
				incrementCount(wordMessageCount, address, 1, useStopWords);
				incrementCount(wordTotalCount, address, count, useStopWords);
			}

			line = in.readLine();
		}
		in.close();
	}

	@Override
	public void readBodyWordCounts(File src, File addrFile) throws IOException {
		readCountsByMessage(addrFile, src, bodyWordMessageCounts, bodyWordFrequencyCounts, true);
	}

	@Override
	public void readSubjectWordFrequencies(File src, File addrFile) throws MessagingException,
			IOException {
		readCountsByMessage(addrFile, src, subjectWordMessageCounts, subjectWordFrequencyCounts,
				true);
	}
}
