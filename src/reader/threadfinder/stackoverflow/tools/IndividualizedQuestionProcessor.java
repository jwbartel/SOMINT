package reader.threadfinder.stackoverflow.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class IndividualizedQuestionProcessor {

	public abstract void processQuestion(String questionPrefix) throws IOException;

	public static void processAllQuestions(File questionsRoot, File questionList,
			IndividualizedQuestionProcessor processor) throws IOException {

		System.out.print("Starting processing...");

		BufferedReader in = new BufferedReader(new FileReader(questionList));

		int count = 1;
		String line = in.readLine();
		while (line != null) {
			if (count % 10000 == 0) {
				System.out.print(count + "...");
			}

			File questionPrefixFile = new File(questionsRoot, line);
			String questionPrefix = questionPrefixFile.getAbsolutePath();

			processor.processQuestion(questionPrefix);

			line = in.readLine();
			count++;
		}

		in.close();
		System.out.println("Done.");
	}
}
