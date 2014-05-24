package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import util.tools.io.IntegerValueParser;
import util.tools.io.MapIOAssist;
import util.tools.io.StringValueWriterAndParser;

public class IndividualizedWordQuestionListProcessor extends IndividualizedQuestionProcessor {

	Map<String, Collection<Integer>> wordQuestionLists = new HashMap<String, Collection<Integer>>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Integer id = Integer.parseInt(new File(questionPrefix).getName());

		File wordFreqsFile = new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT");
		Map<String, Integer> wordFreqs = MapIOAssist.readMap(wordFreqsFile,
				new StringValueWriterAndParser(), new IntegerValueParser());
		for (String word : wordFreqs.keySet()) {
			Collection<Integer> list = wordQuestionLists.get(word);
			if (list == null) {
				list = new TreeSet<Integer>();
				wordQuestionLists.put(word, list);
			}
			list.add(id);
		}
	}

	public Map<String, Collection<Integer>> getWordQuestionLists() {
		return wordQuestionLists;
	}

}
