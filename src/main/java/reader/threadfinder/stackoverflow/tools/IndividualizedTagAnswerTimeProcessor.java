package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.LongValueParser;

public class IndividualizedTagAnswerTimeProcessor extends IndividualizedQuestionProcessor {

	Map<String, SummaryStatistics> tagTimeStats = new HashMap<String, SummaryStatistics>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File earliestAnswerTimeFile = new File(questionPrefix + "_EARLIEST_ANSWER.TXT");
		Long earliestAnswerTime = CollectionIOAssist
				.readCollection(earliestAnswerTimeFile, new LongValueParser()).iterator().next();

		File tagsFile = new File(questionPrefix + "_TAGS.TXT");
		Collection<String> tags = CollectionIOAssist.readCollection(tagsFile);
		for (String tag : tags) {
			SummaryStatistics stats = tagTimeStats.get(tag);
			if (stats == null) {
				stats = new SummaryStatistics();
				tagTimeStats.put(tag, stats);
			}
			stats.addValue(earliestAnswerTime);
		}

	}

	public Map<String, SummaryStatistics> getTagSummaryStats() {
		return tagTimeStats;
	}

}
