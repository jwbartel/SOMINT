package prediction.features.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for determining whether the index of a word in a feature vector
 */
public class SimpleWordIndexFinder implements WordIndexFinder {


	// String nonWords =
	// "([.?!,]?(\\s+|\\s*?\\z|\"))|(\\s*[/()\\[\\]\";:|<>@#]\\s*)";
	private static final String nonWordChar = "([^a-zA-Z'-])";
	private static final String nonWords = nonWordChar + "+";

	private static final String legalWordREGEX = ".*[a-zA-Z].*";
	private static final Pattern legalWord = Pattern.compile(legalWordREGEX);
	

	private Set<String> stopWords;
	private Map<String,Integer> wordIndices = new TreeMap<>();
	
	public SimpleWordIndexFinder(Set<String> words, Collection<String> stopWords) {
		this.stopWords = new HashSet<>(stopWords);
		ArrayList<String> wordList = new ArrayList<>(words);
		for (int i=0; i<wordList.size(); i++) {
			String word = wordList.get(i);
			wordIndices.put(word, i+1);
		}
	}
	
	/**
	 * Parses the words from a set of text
	 * @param text
	 * 			The text to parse words from
	 * @return The set of words in the text
	 */
	public static Set<String> parseWords(String text) {
		if (text == null) {
			return new TreeSet<>();
		}
		Set<String> wordSet = new HashSet<>();
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
					wordSet.addAll(parseWords(words[i].substring(0, index) + " "
							+ words[i].substring(index)));
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

				if (key.length() == 1) {
					continue;
				}

				wordSet.add(key);
			}
		}
		return wordSet;

	}
	
	/**
	 * Get the index of the word in the feature vector
	 * @param word
	 * 			The word to obtain the index of
	 * @return the index of the word
	 * @return null if the word does not have an index
	 */
	@Override
	public Integer indexOf(String word) {
		if (!stopWords.contains(word)) {
			return wordIndices.get(word);
		}
		return null;
	}

	/**
	 * The number of words that can be indexed
	 * @return The number of words
	 */
	@Override
	public int numWords() {
		return wordIndices.size();
	}
	
	/**
	 * The set of stop words that are not counted because they give no syntactic value
	 * @return The set of stop words
	 */
	public Set<String> stopWords() {
		return stopWords;
	}

}
