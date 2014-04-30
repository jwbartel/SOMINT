package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.LongValueParser;

public class IndividualizedQuestionToEarliestProcessor extends IndividualizedQuestionProcessor {

	Map<Integer, Long> questionEarliestTimes = new TreeMap<Integer, Long>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Integer id = Integer.parseInt(new File(questionPrefix).getName());
		File earliestAnswerTimeFile = new File(questionPrefix + "_EARLIEST_ANSWER.TXT");
		Long earliestAnswerTime = CollectionIOAssist
				.readCollection(earliestAnswerTimeFile, new LongValueParser()).iterator().next();

		questionEarliestTimes.put(id, earliestAnswerTime);

	}

	public Map<Integer, Long> getQuestionEarliestTimes() {
		return questionEarliestTimes;
	}

}
