package prediction.features.messages;

/**
 * Class for determining whether the index of a word in a feature vector
 */
public interface WordIndexFinder {

	/**
	 * Get the index of the word in the feature vector
	 * @param word
	 * 			The word to obtain the index of
	 * @return the index of the word
	 */
	public Integer indexOf(String word);
	
	/**
	 * The number of words that can be indexed
	 * @return The number of words
	 */
	public int numWords();
}
