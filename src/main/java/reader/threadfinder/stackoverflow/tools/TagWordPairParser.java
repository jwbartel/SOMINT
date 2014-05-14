package reader.threadfinder.stackoverflow.tools;

import bus.tools.io.ValueParser;

public class TagWordPairParser implements ValueParser<TagWordPair> {

	@Override
	public TagWordPair parse(String str) {
		String[] split = str.split("/");
		return new TagWordPair(split[0], split[1]);
	}

}
