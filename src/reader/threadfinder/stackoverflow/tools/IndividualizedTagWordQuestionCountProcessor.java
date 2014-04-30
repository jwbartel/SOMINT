package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.IntegerValueParser;
import bus.tools.io.MapIOAssist;
import bus.tools.io.StringValueWriterAndParser;

public class IndividualizedTagWordQuestionCountProcessor extends IndividualizedQuestionProcessor {

	Map<TagWordPair, Integer> tagWordQuestionCounts = new TreeMap<TagWordPair, Integer>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File wordFreqsFile = new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT");
		File tagsFile = new File(questionPrefix + "_TAGS.TXT");

		Map<String, Integer> wordFreqs = MapIOAssist.readMap(wordFreqsFile,
				new StringValueWriterAndParser(), new IntegerValueParser());
		Collection<String> tags = CollectionIOAssist.readCollection(tagsFile);
		for (String word : wordFreqs.keySet()) {
			for (String tag : tags) {
				TagWordPair pair = new TagWordPair(tag, word);
				Integer count = tagWordQuestionCounts.get(pair);
				if (count == null) {
					count = 0;
				}
				tagWordQuestionCounts.put(pair, count + 1);
			}
		}
	}

	public Map<TagWordPair, Integer> getTagWordQuestionCounts() {
		return tagWordQuestionCounts;
	}

}
