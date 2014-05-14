package reader.threadfinder.stackoverflow.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class IndividualizedAcceptedAnswerTimeProcessor extends IndividualizedQuestionProcessor {

	private final Map<Integer, Long> timeToAcceptedAnswers;

	public IndividualizedAcceptedAnswerTimeProcessor(Map<Integer, Long> timeToAcceptedAnswer) {
		this.timeToAcceptedAnswers = timeToAcceptedAnswer;
	}

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File timeToAcceptedAnswerFile = new File(questionPrefix + "_TIME_TO_ACCEPTED_ANSWER.TXT");
		if (timeToAcceptedAnswerFile.exists()) {
			return;
		}

		Integer id = Integer.parseInt(new File(questionPrefix).getName());
		Long timeToAcceptedAnswer = timeToAcceptedAnswers.get(id);

		if (timeToAcceptedAnswer == null) {
			return;
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(timeToAcceptedAnswerFile));
		out.write("" + timeToAcceptedAnswer);
		out.newLine();
		out.flush();
		out.close();
	}

}
