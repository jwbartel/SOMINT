package reader.threadfinder.stackoverflow.tools;

import java.util.Map;
import java.util.Set;

public class QuestionReader extends PostReader {

	Map<Integer, Long> timeOfQuestion;
	Map<Integer, Integer> questionOwners;
	Map<Integer, Integer> acceptedAnswers;
	Set<Integer> questionIDs;

	public QuestionReader(Map<Integer, Long> timeOfQuestion, Map<Integer, Integer> questionOwners,
			Map<Integer, Integer> acceptedAnswers, Set<Integer> questionIDs) {
		this.timeOfQuestion = timeOfQuestion;
		this.questionOwners = questionOwners;
		this.acceptedAnswers = acceptedAnswers;
		this.questionIDs = questionIDs;
	}

	@Override
	public void readPost(String post) {
		Integer id = readID(post);
		Long creationTime = readCreationDate(post);
		Integer acceptedAnswer = readAcceptedAnswer(post);

		if (id == null) {
			return;
		}
		questionIDs.add(id);

		if (creationTime != null) {
			timeOfQuestion.put(id, creationTime);
		}

		if (acceptedAnswer != null) {
			acceptedAnswers.put(id, acceptedAnswer);
		}
	}

}
