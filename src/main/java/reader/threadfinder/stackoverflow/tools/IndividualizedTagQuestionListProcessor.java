package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import bus.tools.io.CollectionIOAssist;

public class IndividualizedTagQuestionListProcessor extends IndividualizedQuestionProcessor {

	Map<String, Collection<Integer>> tagQuestionLists = new HashMap<String, Collection<Integer>>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Integer id = Integer.parseInt(new File(questionPrefix).getName());

		File tagsFile = new File(questionPrefix + "_TAGS.TXT");
		Collection<String> tags = CollectionIOAssist.readCollection(tagsFile);
		for (String tag : tags) {
			Collection<Integer> list = tagQuestionLists.get(tag);
			if (list == null) {
				list = new TreeSet<Integer>();
				tagQuestionLists.put(tag, list);
			}
			list.add(id);
		}
	}

	public Map<String, Collection<Integer>> getTagQuestionLists() {
		return tagQuestionLists;
	}

}
