package prediction.features.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class for determining whether the index of a word in a feature vector
 */
public class SimpleWordIndexFinder implements WordIndexFinder {

	private Set<String> stopWords;
	private Map<String,Integer> wordIndices = new TreeMap<>();
	
	public SimpleWordIndexFinder(Set<String> words, Collection<String> stopWords) {
		this.stopWords = new HashSet<>(stopWords);
		ArrayList<String> wordList = new ArrayList<>(words);
		for (int i=0; i<wordList.size(); i++) {
			String word = wordList.get(i);
			wordIndices.put(word, i);
		}
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

}
