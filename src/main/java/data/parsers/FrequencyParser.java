package data.parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import data.parsers.attachments.MSOfficeTextExtractor;
import data.parsers.attachments.PDFTextExtractor;
import bus.accounts.Account;

public class FrequencyParser {

	public final static String stopWordsFile = "specs/stopwords.txt";

	public final static String wordcountFileSuffix = ".wordcounts.txt";

	private static Set<String> stopWords = new TreeSet<String>();

	private static MimetypesFileTypeMap mimeMap;

	protected File file;
	protected BufferedReader in;

	private int totalWords = 0;
	private int freq_threshold = -1;

	// String nonWords =
	// "([.?!,]?(\\s+|\\s*?\\z|\"))|(\\s*[/()\\[\\]\";:|<>@#]\\s*)";
	private final String nonWordChar = "([^a-zA-Z'-])";
	private final String nonWords = nonWordChar + "+";

	String legalWordREGEX = ".*[a-zA-Z].*";
	Pattern legalWord = Pattern.compile(legalWordREGEX);

	public static final String[] punctuations = { ".", "?", "!", ",", ";", ":", "-" };

	TreeMap<String, Integer> wordFreqs = new TreeMap<String, Integer>();
	TreeMap<String, Integer> punctuationFreqs = new TreeMap<String, Integer>();

	public static void loadData() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(stopWordsFile));
			stopWords.clear();
			String line = in.readLine();
			while (line != null) {
				stopWords.add(line);

				line = in.readLine();
			}

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FrequencyParser(String fileName) {
		file = new File(fileName);
		try {
			in = new BufferedReader(new FileReader(file));
			in.close();
			buildFreqList();
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

	public FrequencyParser(File file) throws IOException {
		this.file = file;
		in = new BufferedReader(new FileReader(file));
		in.close();
		buildFreqList();
	}

	protected void buildFreqList() throws IOException {
		File wordCounts = new File(file.getAbsolutePath() + wordcountFileSuffix);
		if (!wordCounts.exists()) {
			try {
				String mimeType = getMimeType(file.getAbsolutePath());

				if (mimeType == null) {

				} else if (mimeType.equals("application/pdf")) {
					// PDF

					String contents = PDFTextExtractor.extractFromPDF(file);
					parse(contents);

				} else if (mimeType.equals("application/vnd.ms-powerpoint")) {
					// Microsoft Powerpoint file

					String contents = MSOfficeTextExtractor.extractFromPowerPoint(file);
					parse(contents);

				} else if (mimeType.equals("application/msword")) {
					// Microsoft Word file

					String contents = MSOfficeTextExtractor.extractFromWord(file);
					parse(contents);

				} else if (mimeType.equals("application/vnd.ms-excel")) {
					// Microsoft Excel File

					String contents = MSOfficeTextExtractor.extractFromExcel(file);
					parse(contents);

				} else if (mimeType
						.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
					// Microsoft Excel 2007 File

					String contents = MSOfficeTextExtractor.extractFromExcel2007(file);
					parse(contents);

				} else if (mimeType
						.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
					// Microsoft Word 2007 File

					String contents = MSOfficeTextExtractor.extractFromWord2007(file);
					parse(contents);

				} else if (mimeType
						.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
					// Microsoft Powerpoint 2007 File

					String contents = MSOfficeTextExtractor.extractFromPowerpoint2007(file);
					parse(contents);

				} else if (mimeType.equals("text/plain") || mimeType.equals("text/html")
						|| mimeType.equals("application/rtf")
						|| mimeType.equals("application/octet-stream")) {

					parse();
				}
				in.close();

				saveToFile(wordCounts);

			} catch (XmlException e) {
				e.printStackTrace();
			} catch (OpenXML4JException e) {
				e.printStackTrace();
			} catch (AbstractMethodError e) {
			} catch (FileNotFoundException e) {
			}
		} else {
			in.close();
			loadFromFile(wordCounts);
		}
	}

	public void loadFromFile(File file) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(file));

		String line = in.readLine();
		totalWords = Integer.parseInt(line);
		line = in.readLine();
		line = in.readLine();

		while (line != null) {

			String[] split = line.split("\t");

			wordFreqs.put(split[0], Integer.parseInt(split[1]));

			line = in.readLine();
		}

		in.close();

	}

	public void saveToFile(File file) throws IOException {
		BufferedWriter out;
		try {

			out = new BufferedWriter(new FileWriter(file));

		} catch (FileNotFoundException e) {
			return;
		}

		out.write("" + totalWords);
		out.newLine();
		out.newLine();

		Iterator<String> words = wordFreqs.keySet().iterator();

		while (words.hasNext()) {
			String word = words.next();

			out.write(word + "\t" + wordFreqs.get(word));
			out.newLine();
			out.flush();
		}

		out.close();
	}

	public static String getMimeType(String fileName) throws IOException {
		if (mimeMap == null) {
			mimeMap = new MimetypesFileTypeMap("specs/mime.types");
		}

		return mimeMap.getContentType(fileName);
	}

	protected void parse(String text) {
		if (text == null) {
			return;
		}

		countPunctuationIncrementally(text);
		String[] words = text.split(nonWords);

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			while (word.length() > 0 && (word.charAt(0) == '-' || word.charAt(0) == '\'')) {
				words[i] = words[i].substring(1);
				word = words[i];
			}

			while (words[i].length() > 0 && words[i].charAt(words[i].length() - 1) == '\'') {
				words[i] = words[i].substring(0, words[i].length() - 1);
				word = words[i];
			}

			if (words[i].length() == 0) {
				continue;
			}

			if (!(words[i].equals("-") || words[i].equals("'"))) {

				if (words[i].charAt(words[i].length() - 1) == '-') {
					if (words[i].length() > 1 && words[i].charAt(words[i].length() - 2) == '-') {
						while (words[i].length() > 0
								&& (words[i].charAt(words[i].length() - 1) == '-'
										|| words[i].charAt(words[i].length() - 1) == '@' || words[i]
										.charAt(words[i].length() - 1) == '\'')) {
							words[i] = words[i].substring(0, words[i].length() - 1);
							word = words[i];
						}
						if (words[i].length() == 0) {
							continue;
						}
					} else {
						i++;
						if (i < words.length) {
							words[i] = words[i - 1].substring(0, words[i - 1].length() - 1)
									+ words[i];
							// System.out.println(words[i]);
						} else {
							break;
						}
					}
				}

				Matcher m = Pattern.compile("(-|[']){2}").matcher(words[i]);

				if (m.find()) {
					int index = m.start();
					parse(words[i].substring(0, index) + " " + words[i].substring(index));
					continue;
				}

				int possIndex = words[i].lastIndexOf("'s");
				if (possIndex != -1 && possIndex == words[i].length() - 2) {
					words[i] = words[i].substring(0, words[i].length() - 2);
				}

				String key = words[i].toLowerCase();

				if (!legalWord.matcher(key).matches()) {
					continue;
				}

				totalWords++;

				if (key.length() == 1 || stopWords.contains(key)) {
					continue;
				}

				if (wordFreqs.containsKey(key)) {
					int freq = wordFreqs.get(key) + 1;
					wordFreqs.put(key, freq);
				} else {
					wordFreqs.put(key, 1);
				}
			}
		}

	}

	private void parse() throws IOException {
		String line = in.readLine();
		while (line != null) {

			countPunctuationIncrementally(line);
			String[] words = line.split(nonWords);

			for (int i = 0; i < words.length; i++) {
				if (words[i].length() == 0) {
					continue;
				}

				if (!(words[i].equals("-") | words[i].equals("@") | words[i].equals("'"))) {

					if (words[i].charAt(words[i].length() - 1) == '-') {
						i++;
						if (i < words.length) {
							words[i] = words[i - 1].substring(0, words[i - 1].length() - 1)
									+ words[i];
							// System.out.println(words[i]);
						} else {
							break;
						}
					}

					String key = words[i].toLowerCase();

					if (!legalWord.matcher(key).matches()) {
						continue;
					}

					totalWords++;
					if (key.length() == 1 || stopWords.contains(key)) {
						continue;
					}

					if (wordFreqs.containsKey(key)) {
						int freq = wordFreqs.get(key) + 1;
						wordFreqs.put(key, freq);
					} else {
						wordFreqs.put(key, 1);
					}
				}
			}

			line = in.readLine();
		}
	}

	public Set<String> getFreqWords() {
		return getFreqWordsWithCounts().keySet();
	}

	public Map<String, Integer> getFreqWordsWithCounts() {
		if (freq_threshold == -1) {
			freq_threshold = (int) (0.00575 * totalWords + 0.769);
			if (freq_threshold < 3) {
				freq_threshold = 3;
			}
		}

		// System.out.println(totalWords);
		// System.out.println(freq_threshold);

		Map<String, Integer> freqWords = new TreeMap<String, Integer>();

		Iterator<String> words = wordFreqs.keySet().iterator();
		while (words.hasNext()) {
			String word = words.next();
			int freq = wordFreqs.get(word);
			if (!stopWords.contains(word) && freq >= freq_threshold) {
				freqWords.put(word, freq);
			}
		}

		return freqWords;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getAllWordsWithCounts() {
		return (Map<String, Integer>) wordFreqs.clone();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getPunctuationCounts() {
		return (Map<String, Integer>) punctuationFreqs.clone();
	}

	public int getWordCount() {
		return totalWords;
	}

	public static void main(String[] args) throws IOException {
		// FrequencyParser parser = new
		// FrequencyParser("/home/bartizzi/Research/Enron Accounts/meyers-a/ExMerge - Meyers, Albert/Inbox/8-EnrolForm.pdf");
		// System.out.println(parser.getWordCount());
		// System.out.println(parser.getFreqWords());
		// System.out.println(parser.freq_threshold);

		FrequencyParser.loadData();

		/*
		 * File folder = new
		 * File("/home/bartizzi/Documents/Papers/Template Detection"
		 * );//("/media/PENDRIVE/test docs"); File[] children =
		 * folder.listFiles(); for(int i=0; i<children.length; i++){
		 * FrequencyParser parser = new FrequencyParser(children[i]);
		 * 
		 * System.out.println(children[i].getName());
		 * System.out.println(parser.getWordCount());
		 * System.out.println(parser.getFreqWords());
		 * System.out.println(parser.freq_threshold); System.out.println("\n");
		 * }
		 */

		File folder = new File("/home/bartizzi/Research/Enron Accounts/lay-k");
		// File[] accounts = folder.listFiles();
		// Arrays.sort(accounts);
		// for(int i=0; i<accounts.length; i++){

		long start = System.currentTimeMillis();

		System.out.print(folder.getName() + "...");
		BufferedReader in = new BufferedReader(new FileReader(new File(folder, Account.ALL_MSGS)));

		String line = in.readLine();
		while (line != null) {

			if (line.length() > 0 && line.charAt(0) == '\t') {
				String attachmentName = line.substring(1);
				FrequencyParser parser = new FrequencyParser(attachmentName);
				parser.getAllWordsWithCounts();
			}

			line = in.readLine();
		}

		in.close();

		System.out.println("extracted keywords in " + (System.currentTimeMillis() - start) + " ms");
		// }
	}

	private void countPunctuationIncrementally(String content) {
		for (String punctuation : punctuations) {

			int count = content.length() - content.replace(punctuation, "").length();
			if (count > 0) {
				Integer oldCount = punctuationFreqs.get(punctuation);
				oldCount = (oldCount == null) ? 0 : oldCount;
				punctuationFreqs.put(punctuation, oldCount + count);
			}

		}
	}

	public static Map<String, Integer> countPunctuation(String content) {

		Map<String, Integer> punctuationFreqs = new TreeMap<String, Integer>();

		for (String punctuation : punctuations) {

			int count = content.length() - content.replace(punctuation, "").length();
			if (count > 0) {
				Integer oldCount = punctuationFreqs.get(punctuation);
				oldCount = (oldCount == null) ? 0 : oldCount;
				punctuationFreqs.put(punctuation, oldCount + count);
			}

		}

		return punctuationFreqs;
	}
}
