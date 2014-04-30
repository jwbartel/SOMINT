package reader.threadfinder.stackoverflow.tools;

public class TagWordPair extends Pair<String, String> {

	public TagWordPair(String tag, String word) {
		super(tag, word);
	}

	@Override
	public String toString() {
		return "" + super.getFirstVal().toString() + "/" + super.getSecondVal().toString();
	}
}
