package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import util.tools.io.CollectionIOAssist;
import util.tools.io.IntegerValueParser;
import util.tools.io.LongValueParser;

public class IndividualizedUserAnswerTimeProcessor extends IndividualizedQuestionProcessor {

	Map<Integer, SummaryStatistics> ownerTimeStats = new HashMap<Integer, SummaryStatistics>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File ownerFile = new File(questionPrefix + "_OWNER.TXT");
		Integer owner = CollectionIOAssist.readCollection(ownerFile, new IntegerValueParser())
				.iterator().next();

		File earliestAnswerTimeFile = new File(questionPrefix + "_EARLIEST_ANSWER.TXT");
		Long earliestAnswerTime = CollectionIOAssist
				.readCollection(earliestAnswerTimeFile, new LongValueParser()).iterator().next();

		SummaryStatistics stats = ownerTimeStats.get(owner);
		if (stats == null) {
			stats = new SummaryStatistics();
			ownerTimeStats.put(owner, stats);
		}
		stats.addValue(earliestAnswerTime);
	}

	public Map<Integer, SummaryStatistics> getOwnerSummaryStats() {
		return ownerTimeStats;
	}

}
