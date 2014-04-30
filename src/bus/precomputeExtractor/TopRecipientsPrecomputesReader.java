package bus.precomputeExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import bus.accounts.Account;

public class TopRecipientsPrecomputesReader extends PrecomputesFileReader {

	protected Set<String> topRecipients;

	private final Map<String, Map<String, Integer>> bodyWordFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> bodyWordMessageCounts = new TreeMap<String, Map<String, Integer>>();

	private final Map<String, Map<String, Integer>> subjectWordFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> subjectWordMessageCounts = new TreeMap<String, Map<String, Integer>>();

	private final Map<String, Map<String, Integer>> bodyPunctuationFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> bodyPunctuationMessageCounts = new TreeMap<String, Map<String, Integer>>();

	private final Map<String, Map<String, Integer>> subjectPunctuationFrequencyCounts = new TreeMap<String, Map<String, Integer>>();
	private final Map<String, Map<String, Integer>> subjectPunctuationMessageCounts = new TreeMap<String, Map<String, Integer>>();

	public TopRecipientsPrecomputesReader(File topRecipientsListFile, File stopWordFile)
			throws IOException {
		super(stopWordFile);
		init(topRecipientsListFile);
	}

	public TopRecipientsPrecomputesReader(Set<String> recipients, File stopWordFile)
			throws IOException {

		super(stopWordFile);
		init(recipients);
	}

	protected void init(File topRecipientsListFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(topRecipientsListFile));

		Set<String> recipients = new TreeSet<String>();

		String line = in.readLine();
		while (line != null) {
			recipients.add(line.toLowerCase());
			line = in.readLine();
		}

		in.close();
		init(recipients);
	}

	@Override
	public void readPrecomputes(File precomputePrefix) throws IOException, MessagingException {
		File addressFile = new File(precomputePrefix + Account.ADDR_FILE_SUFFIX);

		Set<String> addresses = getAddresses(addressFile);
		int origSize = addresses.size();
		addresses.removeAll(topRecipients);
		if (origSize - addresses.size() == 0) {
			return;
		}

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

		precomputesFolder = new File(precomputesFolder, "counts by recipient");
		if (!precomputesFolder.exists()) {
			precomputesFolder.mkdirs();
		}

		File wordsByMessageFile = new File(precomputesFolder, WORDS_BY_MESSAGE);
		File wordsBySubjectFile = new File(precomputesFolder, WORDS_BY_SUBJECT);
		File punctuationByMessageFile = new File(precomputesFolder, PUNCTUATION_BY_MESSAGE);
		File punctuationBySubjectFile = new File(precomputesFolder, PUNCTUATION_BY_SUBJECT);

		File totalWordsInBodyFile = new File(precomputesFolder, TOTAL_WORDS_BY_MESSAGE);
		File totalWordsInSubjectFile = new File(precomputesFolder, TOTAL_WORDS_BY_SUBJECT);
		File totalPunctuationInBodyFile = new File(precomputesFolder, TOTAL_PUNCTUATION_BY_MESSAGE);
		File totalPunctuationInSubjectFile = new File(precomputesFolder,
				TOTAL_PUNCTUATION_BY_SUBJECT);

		writeCounts(wordsByMessageFile, bodyWordMessageCounts);
		writeCounts(wordsBySubjectFile, subjectWordMessageCounts);
		writeCounts(punctuationByMessageFile, bodyPunctuationMessageCounts);
		writeCounts(punctuationBySubjectFile, subjectPunctuationMessageCounts);
		writeCounts(totalWordsInBodyFile, bodyWordFrequencyCounts);
		writeCounts(totalWordsInSubjectFile, subjectWordFrequencyCounts);
		writeCounts(totalPunctuationInBodyFile, bodyPunctuationFrequencyCounts);
		writeCounts(totalPunctuationInSubjectFile, subjectPunctuationFrequencyCounts);
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

	public static Set<String> getAddresses(File src) throws IOException {
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

		in.close();

		return addresses;
	}

	private void readCountsByMessage(File addrFile, File src,
			Map<String, Map<String, Integer>> messageCount,
			Map<String, Map<String, Integer>> totalCount, boolean useStopWords) throws IOException {

		Set<String> addresses = getAddresses(addrFile);
		addresses.retainAll(topRecipients);

		BufferedReader in = new BufferedReader(new FileReader(src));

		String line = in.readLine();
		while (line != null) {

			String key = line.substring(0, line.lastIndexOf('\t'));
			int count = Integer.parseInt(line.substring(line.lastIndexOf('\t') + 1));

			for (String address : addresses) {
				Map<String, Integer> addrMessageCount = messageCount.get(address);
				if (addrMessageCount == null) {
					addrMessageCount = new TreeMap<String, Integer>();
					messageCount.put(address, addrMessageCount);
				}

				Map<String, Integer> addrTotalCount = totalCount.get(address);
				if (addrTotalCount == null) {
					addrTotalCount = new TreeMap<String, Integer>();
					totalCount.put(address, addrMessageCount);
				}

				incrementCount(addrMessageCount, key, 1, useStopWords);
				incrementCount(addrTotalCount, key, count, useStopWords);
			}

			line = in.readLine();
		}
		in.close();
	}

	private void init(Set<String> recipients) {
		this.topRecipients = recipients;
	}

	public void readBodyWordCounts(File src, File addrFile) throws IOException {
		readCountsByMessage(addrFile, src, bodyWordMessageCounts, bodyWordFrequencyCounts, true);
	}

	public void readBodyPunctuationCounts(File src, File addrFile) throws IOException {
		readCountsByMessage(addrFile, src, bodyPunctuationMessageCounts,
				bodyPunctuationFrequencyCounts, false);
	}

	public void readSubjectWordFrequencies(File src, File addrFile) throws MessagingException,
			IOException {
		readCountsByMessage(addrFile, src, subjectWordMessageCounts, subjectWordFrequencyCounts,
				true);
	}

	public void readSubjectPunctuationFrequencies(File src, File addrFile)
			throws MessagingException, IOException {
		readCountsByMessage(addrFile, src, subjectPunctuationMessageCounts,
				subjectPunctuationFrequencyCounts, false);
	}
}
