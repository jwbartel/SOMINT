package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import util.tools.io.CollectionIOAssist;
import util.tools.io.IntegerValueParser;
import util.tools.io.MapIOAssist;
import util.tools.io.StringValueWriterAndParser;

public class IndividualizedTagWordQuestionListProcessor extends IndividualizedQuestionProcessor {

	Map<TagWordPair, Collection<Integer>> tagWordQuestionLists = new TreeMap<TagWordPair, Collection<Integer>>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Integer id = Integer.parseInt(new File(questionPrefix).getName());

		File wordFreqsFile = new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT");
		File tagsFile = new File(questionPrefix + "_TAGS.TXT");

		Map<String, Integer> wordFreqs = MapIOAssist.readMap(wordFreqsFile,
				new StringValueWriterAndParser(), new IntegerValueParser());
		Collection<String> tags = CollectionIOAssist.readCollection(tagsFile);
		for (String word : wordFreqs.keySet()) {
			for (String tag : tags) {
				TagWordPair pair = new TagWordPair(tag, word);
				Collection<Integer> list = tagWordQuestionLists.get(pair);
				if (list == null) {
					list = new TreeSet<Integer>();
					tagWordQuestionLists.put(pair, list);
				}
				list.add(id);
			}
		}
	}

	public Map<TagWordPair, Collection<Integer>> getTagWordQuestionLists() {
		return tagWordQuestionLists;
	}

}
