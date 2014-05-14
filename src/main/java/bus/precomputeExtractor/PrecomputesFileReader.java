package bus.precomputeExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import bus.accounts.Account;

public class PrecomputesFileReader implements PrecomputeReader {

	private final Set<String> stopWords = new HashSet<String>();

	public static final String ADDRESS_BY_MESSAGE = "MESSAGES_WITH_ADDRESS.txt";

	public static final String WORDS_BY_MESSAGE = "MESSAGES_WITH_BODY_WORD.txt";
	public static final String WORDS_BY_SUBJECT = "MESSAGES_WITH_SUBJECT_WORD.txt";

	public static final String PUNCTUATION_BY_MESSAGE = "MESSAGAGES_WITH_BODY_PUNCTUATION.txt";
	public static final String PUNCTUATION_BY_SUBJECT = "MESSAGAGES_WITH_SUBJECT_PUNCTUATION.txt";

	public static final String TOTAL_WORDS_BY_MESSAGE = "BODY_WORD_TOTAL_COUNTS.txt";
	public static final String TOTAL_WORDS_BY_SUBJECT = "SUBJECT_WORD_TOTAL_COUNTS.txt";

	public static final String TOTAL_PUNCTUATION_BY_MESSAGE = "BODY_PUNCTUATION_TOTAL_COUNTS.txt";
	public static final String TOTAL_PUNCTUATION_BY_SUBJECT = "SUBJECT_PUNCTUATION_TOTAL_COUNTS.txt";

	private final Map<String, Integer> emailAddresses = new TreeMap<String, Integer>();

	private final Map<String, Integer> wordsByMessageBody = new TreeMap<String, Integer>();
	private final Map<String, Integer> wordsBySubject = new TreeMap<String, Integer>();

	private final Map<String, Integer> punctuationByMessageBody = new TreeMap<String, Integer>();
	private final Map<String, Integer> punctuationBySubject = new TreeMap<String, Integer>();

	private final Map<String, Integer> totalWordsByMessageBody = new TreeMap<String, Integer>();
	private final Map<String, Integer> totalWordsBySubject = new TreeMap<String, Integer>();

	private final Map<String, Integer> totalPunctuationByMessageBody = new TreeMap<String, Integer>();
	private final Map<String, Integer> totalPunctuationBySubject = new TreeMap<String, Integer>();

	public PrecomputesFileReader(File stopWordFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(stopWordFile));

		String line = in.readLine();
		while (line != null) {
			stopWords.add(line.toLowerCase());
			line = in.readLine();
		}
		in.close();
	}

	@Override
	public void readPrecomputes(File precomputePrefix) throws IOException, MessagingException {
		File addressFile = new File(precomputePrefix + Account.ADDR_FILE_SUFFIX);
		File wordCountFile = new File(precomputePrefix + Account.WORD_COUNTS_SUFFIX);
		File subjWordCountFile = new File(precomputePrefix + Account.SUBJECT_WORD_COUNTS_SUFFIX);
		File punctuationCountFile = new File(precomputePrefix + Account.PUNCTUATION_COUNTS_SUFFIX);
		File subjPunctuationCountFile = new File(precomputePrefix
				+ Account.SUBJECT_PUNCTUATION_COUNTS_SUFFIX);

		readEmailAddresses(addressFile);
		readBodyWordCounts(wordCountFile);
		readBodyPunctuationCounts(punctuationCountFile);
		readSubjectWordFrequencies(subjWordCountFile);
		readSubjectPunctuationFrequencies(subjPunctuationCountFile);

	}

	public void writeSummaries(File precomputesFolder) throws IOException {

		File addressCountsFile = new File(precomputesFolder, ADDRESS_BY_MESSAGE);

		File wordsByMessageFile = new File(precomputesFolder, WORDS_BY_MESSAGE);
		File wordsBySubjectFile = new File(precomputesFolder, WORDS_BY_SUBJECT);
		File punctuationByMessageFile = new File(precomputesFolder, PUNCTUATION_BY_MESSAGE);
		File punctuationBySubjectFile = new File(precomputesFolder, PUNCTUATION_BY_SUBJECT);

		File totalWordsInBodyFile = new File(precomputesFolder, TOTAL_WORDS_BY_MESSAGE);
		File totalWordsInSubjectFile = new File(precomputesFolder, TOTAL_WORDS_BY_SUBJECT);
		File totalPunctuationInBodyFile = new File(precomputesFolder, TOTAL_PUNCTUATION_BY_MESSAGE);
		File totalPunctuationInSubjectFile = new File(precomputesFolder,
				TOTAL_PUNCTUATION_BY_SUBJECT);

		writeCounts(addressCountsFile, emailAddresses);
		writeCounts(wordsByMessageFile, wordsByMessageBody);
		writeCounts(wordsBySubjectFile, wordsBySubject);
		writeCounts(punctuationByMessageFile, punctuationByMessageBody);
		writeCounts(punctuationBySubjectFile, punctuationBySubject);
		writeCounts(totalWordsInBodyFile, totalWordsByMessageBody);
		writeCounts(totalWordsInSubjectFile, totalWordsBySubject);
		writeCounts(totalPunctuationInBodyFile, totalPunctuationByMessageBody);
		writeCounts(totalPunctuationInSubjectFile, totalPunctuationBySubject);
	}

	private void writeCounts(File dest, Map<String, Integer> counts) throws IOException {
		Map<Integer, Set<String>> orderedCounts = new TreeMap<Integer, Set<String>>();

		for (Entry<String, Integer> entry : counts.entrySet()) {
			Set<String> keysWithCount = orderedCounts.get(entry.getValue());
			if (keysWithCount == null) {
				keysWithCount = new TreeSet<String>();
				orderedCounts.put(entry.getValue(), keysWithCount);
			}
			keysWithCount.add(entry.getKey());
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for (Entry<Integer, Set<String>> entry : orderedCounts.entrySet()) {
			out.write("" + entry.getKey());
			for (String key : entry.getValue()) {
				out.write("," + key);
			}
			out.newLine();
		}
		out.flush();
		out.close();
	}

	protected void incrementCount(Map<String, Integer> map, String key, int increment,
			boolean useStopWords) {

		key = key.toLowerCase();
		if (useStopWords && stopWords.contains(key)) {
			return;
		}

		Integer count = map.get(key);
		count = (count == null) ? increment : count + increment;
		map.put(key, count);
	}

	public void readEmailAddresses(File src) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(src));

		Set<String> addresses = new HashSet<String>();

		String line = in.readLine();
		while (line != null) {

			if (line.startsWith("\t")) {
				String address = line.substring(1).toLowerCase();
				addresses.add(address);
			}

			line = in.readLine();
		}

		for (String address : addresses) {
			incrementCount(emailAddresses, address, 1, false);
		}

		in.close();
	}

	private void readCountsByMessage(File src, Map<String, Integer> countPresence,
			Map<String, Integer> totalCount, boolean useStopWords) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(src));

		String line = in.readLine();
		while (line != null) {

			String key = line.substring(0, line.lastIndexOf('\t'));
			int count = Integer.parseInt(line.substring(line.lastIndexOf('\t') + 1));

			incrementCount(countPresence, key, 1, useStopWords);
			incrementCount(totalCount, key, count, useStopWords);

			line = in.readLine();
		}
		in.close();
	}

	public void readBodyWordCounts(File src) throws IOException {
		readCountsByMessage(src, wordsByMessageBody, totalWordsByMessageBody, true);
	}

	public void readBodyPunctuationCounts(File src) throws IOException {
		readCountsByMessage(src, punctuationByMessageBody, totalPunctuationByMessageBody, false);
	}

	public void readSubjectWordFrequencies(File src) throws MessagingException, IOException {
		readCountsByMessage(src, wordsBySubject, totalWordsBySubject, true);
	}

	public void readSubjectPunctuationFrequencies(File src) throws MessagingException, IOException {
		readCountsByMessage(src, punctuationBySubject, totalPunctuationBySubject, false);
	}
}
