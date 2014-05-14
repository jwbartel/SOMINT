package reader.threadfinder.stackoverflow.tools;

import java.util.Map;
import java.util.Set;

public class AnswerReader extends PostReader {

	Set<Integer> answerIDs;
	Map<Integer, Long> answerTimes;
	Map<Integer, Integer> answerToParent;

	public AnswerReader(Set<Integer> answerIDs, Map<Integer, Long> answerTimes,
			Map<Integer, Integer> answerToParent) {
		this.answerIDs = answerIDs;
		this.answerTimes = answerTimes;
		this.answerToParent = answerToParent;
	}

	@Override
	public void readPost(String post) {
		Integer id = readID(post);
		if (id == null) {
			return;
		}

		Long creationTime = readCreationDate(post);
		if (creationTime == null) {
			return;
		}
		Integer parent = readParent(post);
		if (parent == null) {
			return;
		}

		answerIDs.add(id);
		answerTimes.put(id, creationTime);
		answerToParent.put(id, parent);

//		long timeSinceParent = creationTime - parentTime;
//		if (timeSinceParent < 0) {
//			return;
//		}
//		if (acceptedAnswers.containsKey(id)) {
//			timeToAcceptedAnswer.put(acceptedAnswers.get(id), timeSinceParent);
//		}
//		Long oldEarliest = timeToFirstAnswer.get(id);
//		if (oldEarliest == null || oldEarliest > timeSinceParent) {
//			timeToFirstAnswer.put(id, timeSinceParent);
//		}

	}

}
