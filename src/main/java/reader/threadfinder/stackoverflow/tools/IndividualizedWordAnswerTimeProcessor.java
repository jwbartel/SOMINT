package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.IntegerValueParser;
import bus.tools.io.LongValueParser;
import bus.tools.io.MapIOAssist;
import bus.tools.io.StringValueWriterAndParser;

public class IndividualizedWordAnswerTimeProcessor extends IndividualizedQuestionProcessor {

	Map<String, SummaryStatistics> wordTimeStats = new HashMap<String, SummaryStatistics>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File earliestAnswerTimeFile = new File(questionPrefix + "_EARLIEST_ANSWER.TXT");
		Long earliestAnswerTime = CollectionIOAssist
				.readCollection(earliestAnswerTimeFile, new LongValueParser()).iterator().next();

		File tagsFile = new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT");
		Map<String, Integer> wordFreqs = MapIOAssist.readMap(tagsFile,
				new StringValueWriterAndParser(), new IntegerValueParser());
		for (String word : wordFreqs.keySet()) {
			SummaryStatistics stats = wordTimeStats.get(word);
			if (stats == null) {
				stats = new SummaryStatistics();
				wordTimeStats.put(word, stats);
			}
			stats.addValue(earliestAnswerTime);
		}

	}

	public Map<String, SummaryStatistics> getWordSummaryStats() {
		return wordTimeStats;
	}

}
