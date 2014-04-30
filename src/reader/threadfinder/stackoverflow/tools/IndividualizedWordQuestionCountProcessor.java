package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bus.tools.io.IntegerValueParser;
import bus.tools.io.MapIOAssist;
import bus.tools.io.StringValueWriterAndParser;

public class IndividualizedWordQuestionCountProcessor extends IndividualizedQuestionProcessor {

	Map<String, Integer> wordQuestionCounts = new HashMap<String, Integer>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File tagsFile = new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT");
		Map<String, Integer> wordFreqs = MapIOAssist.readMap(tagsFile,
				new StringValueWriterAndParser(), new IntegerValueParser());
		for (String word : wordFreqs.keySet()) {
			Integer count = wordQuestionCounts.get(word);
			if (count == null) {
				count = 0;
			}
			wordQuestionCounts.put(word, count + 1);
		}
	}

	public Map<String, Integer> getWordQuestionCounts() {
		return wordQuestionCounts;
	}

}
