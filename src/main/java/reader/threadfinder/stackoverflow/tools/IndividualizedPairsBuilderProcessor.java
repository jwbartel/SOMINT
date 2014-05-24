package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import util.tools.io.CollectionIOAssist;
import util.tools.io.LongValueParser;

public class IndividualizedPairsBuilderProcessor<V1 extends Comparable<V1>, V2 extends Comparable<V2>>
		extends IndividualizedQuestionProcessor {

	private final ArrayList<V1> v1Vals;
	private final ArrayList<V2> v2Vals;

	private final ExperimentFeatureReader<V1> v1Reader;
	private final ExperimentFeatureReader<V2> v2Reader;

	private final Map<Pair<Integer, Integer>, Integer> counts = new TreeMap<Pair<Integer, Integer>, Integer>();
	private final Map<Pair<Integer, Integer>, SummaryStatistics> earliestTimes = new TreeMap<Pair<Integer, Integer>, SummaryStatistics>();
	private final Map<Pair<Integer, Integer>, SummaryStatistics> acceptedTimes = new TreeMap<Pair<Integer, Integer>, SummaryStatistics>();

	public IndividualizedPairsBuilderProcessor(ArrayList<V1> v1Vals, ArrayList<V2> v2Vals,
			ExperimentFeatureReader<V1> v1Reader, ExperimentFeatureReader<V2> v2Reader) {
		this.v1Vals = v1Vals;
		this.v2Vals = v2Vals;
		this.v1Reader = v1Reader;
		this.v2Reader = v2Reader;
	}

	private void updateStats(V1 v1Val, V2 v2Val, Long earliestTime, Long acceptedTime) {

		int v1Pos = v1Vals.indexOf(v1Val);
		int v2Pos = v2Vals.indexOf(v2Val);
		if (v1Pos == -1 || v2Pos == -1) {
			return;
		}

		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(v1Pos, v2Pos);
		Integer oldCount = counts.get(pair);
		if (oldCount == null) {
			oldCount = 0;
		}
		counts.put(pair, oldCount + 1);

		SummaryStatistics earlyStats = earliestTimes.get(pair);
		if (earlyStats == null) {
			earlyStats = new SummaryStatistics();
			earliestTimes.put(pair, earlyStats);
		}
		earlyStats.addValue(earliestTime);

		if (acceptedTime != null) {

			SummaryStatistics acceptedStats = acceptedTimes.get(pair);
			if (acceptedStats == null) {
				acceptedStats = new SummaryStatistics();
				acceptedTimes.put(pair, acceptedStats);
			}
			acceptedStats.addValue(acceptedTime);
		}
	}

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Collection<V1> v1s = v1Reader.readValues(questionPrefix);
		Collection<V2> v2s = v2Reader.readValues(questionPrefix);

		Long timeToEarliestAnswer = CollectionIOAssist
				.readCollection(new File(questionPrefix + "_EARLIEST_ANSWER.TXT"),
						new LongValueParser()).iterator().next();

		File acceptedTimeFile = new File(questionPrefix + "_TIME_TO_ACCEPTED_ANSWER.TXT");
		Long timeToAcceptedAnswer = null;
		if (acceptedTimeFile.exists()) {
			timeToAcceptedAnswer = CollectionIOAssist
					.readCollection(acceptedTimeFile, new LongValueParser()).iterator().next();
		}

		for (V1 v1 : v1s) {
			for (V2 v2 : v2s) {
				updateStats(v1, v2, timeToEarliestAnswer, timeToAcceptedAnswer);
			}
		}
	}

	public Map<Pair<Integer, Integer>, Integer> getCounts() {
		return counts;
	}

	public Map<Pair<Integer, Integer>, SummaryStatistics> getEarliestTimes() {
		return earliestTimes;
	}

	public Map<Pair<Integer, Integer>, SummaryStatistics> getAcceptedTimes() {
		return acceptedTimes;
	}

}
