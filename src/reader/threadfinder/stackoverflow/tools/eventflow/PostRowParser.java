package reader.threadfinder.stackoverflow.tools.eventflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.dom4j.Element;


public class PostRowParser extends RowParser<PostItem> {

	File questionsFile;
	File answersFile;

	BufferedWriter questionsOut;
	BufferedWriter answersOut;

	public PostRowParser(File questionsFile, File answersFile) throws IOException {
		this.questionsFile = questionsFile;
		this.answersFile = answersFile;
		questionsOut = new BufferedWriter(new FileWriter(questionsFile));
		answersOut = new BufferedWriter(new FileWriter(answersFile));
	}

	@Override
	public void parseRow(Element row) {
		try {

			PostItem post = new PostItem(row);
			if (post.getType().equals("Question")) {
				questionsOut.write(post.toString());
				questionsOut.newLine();
				questionsOut.flush();
			} else {
				answersOut.write(post.toString());
				answersOut.newLine();
				answersOut.flush();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	void close() throws IOException {
		questionsOut.flush();
		questionsOut.close();

		answersOut.flush();
		answersOut.close();
	}
}
