package reader.threadfinder.stackoverflow.tools;

import java.util.Map;

public class EarliestAnswerReader extends PostReader {

	Map<Integer, Long> timeOfQuestions;
	Map<Integer, Long> timeOfEarliestAnswers;

	public EarliestAnswerReader(Map<Integer, Long> timeOfQuestions,
			Map<Integer, Long> timeOfEarliestAnswers) {
		this.timeOfQuestions = timeOfQuestions;
		this.timeOfEarliestAnswers = timeOfEarliestAnswers;
	}

	@Override
	public void readPost(String post) {
		Integer id = readID(post);
		if (id == null) {
			return;
		}

		Long answerTime = readCreationDate(post);
		if (answerTime == null) {
			return;
		}
		Integer question = readParent(post);
		if (question == null) {
			return;
		}

		Long questionTime = timeOfQuestions.get(question);
		if (questionTime == null) {
			return;
		}

		long delta = answerTime - questionTime;
		if (delta > 0) {
			Long oldDelta = timeOfEarliestAnswers.get(question);
			if (oldDelta == null || oldDelta > delta) {
				timeOfEarliestAnswers.put(question, delta);
			}
		} else {
			timeOfQuestions.remove(question);
		}

	}

}
