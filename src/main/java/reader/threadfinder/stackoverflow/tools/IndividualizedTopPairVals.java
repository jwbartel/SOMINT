package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import util.tools.io.CollectionIOAssist;
import util.tools.io.IntegerValueParser;
import util.tools.io.LongValueParser;
import util.tools.io.MapIOAssist;

public class IndividualizedTopPairVals extends IndividualizedQuestionProcessor {

	private final ArrayList<String> topWords;
	private final ArrayList<String> topTags;
	private final ArrayList<Integer> topOwners;

	private final Integer[][] topWordTagCounts;
	private final Integer[][] topWordOwnerCounts;
	private final Integer[][] topOwnerTagCounts;

	private final SummaryStatistics[][] topWordTagEarliestTimes;
	private final SummaryStatistics[][] topWordOwnerEarliestTimes;
	private final SummaryStatistics[][] topOwnerTagEarliestTimes;

	private final SummaryStatistics[][] topWordTagAcceptedTimes;
	private final SummaryStatistics[][] topWordOwnerAcceptedTimes;
	private final SummaryStatistics[][] topOwnerTagAcceptedTimes;

	public IndividualizedTopPairVals(ArrayList<String> topWords, ArrayList<String> topTags,
			ArrayList<Integer> topOwners) {
		this.topWords = topWords;
		this.topTags = topTags;
		this.topOwners = topOwners;

		topWordTagCounts = new Integer[topWords.size()][topTags.size()];
		topWordTagEarliestTimes = new SummaryStatistics[topWords.size()][topTags.size()];
		topWordTagAcceptedTimes = new SummaryStatistics[topWords.size()][topTags.size()];

		topWordOwnerCounts = new Integer[topWords.size()][topOwners.size()];
		topWordOwnerEarliestTimes = new SummaryStatistics[topWords.size()][topOwners.size()];
		topWordOwnerAcceptedTimes = new SummaryStatistics[topWords.size()][topOwners.size()];

		topOwnerTagCounts = new Integer[topOwners.size()][topTags.size()];
		topOwnerTagEarliestTimes = new SummaryStatistics[topOwners.size()][topTags.size()];
		topOwnerTagAcceptedTimes = new SummaryStatistics[topOwners.size()][topTags.size()];
	}

	private void updateStats(int firstPos, int secondPos, Long earliestTime, Long acceptedTime,
			Integer[][] counts, SummaryStatistics[][] earliest, SummaryStatistics[][] accepted) {

		if (counts[firstPos][secondPos] == null) {
			counts[firstPos][secondPos] = 0;
		}
		counts[firstPos][secondPos]++;

		if (earliest[firstPos][secondPos] == null) {
			earliest[firstPos][secondPos] = new SummaryStatistics();
		}
		earliest[firstPos][secondPos].addValue(earliestTime);

		if (acceptedTime != null) {
			if (accepted[firstPos][secondPos] == null) {
				accepted[firstPos][secondPos] = new SummaryStatistics();
			}
			accepted[firstPos][secondPos].addValue(acceptedTime);
		}
	}

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Integer owner = CollectionIOAssist
				.readCollection(new File(questionPrefix + "_OWNER.TXT"), new IntegerValueParser())
				.iterator().next();
		boolean isTopOwner = topOwners.contains(owner);

		Set<String> tags = new TreeSet<String>(CollectionIOAssist.readCollection(new File(
				questionPrefix + "_TAGS.TXT")));

		tags.retainAll(topTags);
		boolean containsTopTags = tags.size() > 0;

		if (!isTopOwner && !containsTopTags) {
			return;
		}

		Map<String, Integer> wordFreqs = MapIOAssist.readMap(new File(questionPrefix
				+ "_SUBJECT_WORDCOUNTS.TXT"), new IntegerValueParser());
		Set<String> words = new TreeSet<String>(wordFreqs.keySet());
		words.retainAll(topWords);
		boolean containsTopWords = words.size() > 0;

		Long timeToEarliestAnswer = CollectionIOAssist
				.readCollection(new File(questionPrefix + "_EARLIEST_ANSWER.TXT"),
						new LongValueParser()).iterator().next();

		File acceptedTimeFile = new File(questionPrefix + "_TIME_TO_ACCEPTED_ANSWER.TXT");
		Long timeToAcceptedAnswer = null;
		if (acceptedTimeFile.exists()) {
			timeToAcceptedAnswer = CollectionIOAssist
					.readCollection(acceptedTimeFile, new LongValueParser()).iterator().next();
		}

		if (isTopOwner) {
			int ownerPos = topOwners.indexOf(owner);
			for (String tag : tags) {
				int tagPos = topTags.indexOf(tag);
				updateStats(ownerPos, tagPos, timeToEarliestAnswer, timeToAcceptedAnswer,
						topOwnerTagCounts, topOwnerTagEarliestTimes, topOwnerTagAcceptedTimes);
			}
			for (String word : words) {
				int wordPos = topWords.indexOf(word);
				updateStats(wordPos, ownerPos, timeToEarliestAnswer, timeToAcceptedAnswer,
						topWordOwnerCounts, topWordOwnerEarliestTimes, topWordOwnerAcceptedTimes);
			}
		}

		for (String tag : tags) {
			int tagPos = topTags.indexOf(tag);
			for (String word : words) {
				int wordPos = topWords.indexOf(word);
				updateStats(wordPos, tagPos, timeToEarliestAnswer, timeToAcceptedAnswer,
						topWordTagCounts, topWordTagEarliestTimes, topWordTagAcceptedTimes);
			}
		}
	}

	public Integer[][] getTopWordTagCounts() {
		return topWordTagCounts;
	}

	public Integer[][] getTopWordOwnerCounts() {
		return topWordOwnerCounts;
	}

	public Integer[][] getTopOwnerTagCounts() {
		return topOwnerTagCounts;
	}

	public SummaryStatistics[][] getTopWordTagEarliestTimes() {
		return topWordTagEarliestTimes;
	}

	public SummaryStatistics[][] getTopWordOwnerEarliestTimes() {
		return topWordOwnerEarliestTimes;
	}

	public SummaryStatistics[][] getTopOwnerTagEarliestTimes() {
		return topOwnerTagEarliestTimes;
	}

	public SummaryStatistics[][] getTopWordTagAcceptedTimes() {
		return topWordTagAcceptedTimes;
	}

	public SummaryStatistics[][] getTopWordOwnerAcceptedTimes() {
		return topWordOwnerAcceptedTimes;
	}

	public SummaryStatistics[][] getTopOwnerTagAcceptedTimes() {
		return topOwnerTagAcceptedTimes;
	}

}
