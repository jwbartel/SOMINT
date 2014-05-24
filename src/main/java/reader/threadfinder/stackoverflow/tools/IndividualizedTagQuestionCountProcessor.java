package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import util.tools.io.CollectionIOAssist;

public class IndividualizedTagQuestionCountProcessor extends IndividualizedQuestionProcessor {

	Map<String, Integer> tagQuestionCounts = new HashMap<String, Integer>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File tagsFile = new File(questionPrefix + "_TAGS.TXT");
		Collection<String> tags = CollectionIOAssist.readCollection(tagsFile);
		for (String tag : tags) {
			Integer count = tagQuestionCounts.get(tag);
			if (count == null) {
				count = 0;
			}
			tagQuestionCounts.put(tag, count + 1);
		}
	}

	public Map<String, Integer> getTagQuestionCounts() {
		return tagQuestionCounts;
	}

}
