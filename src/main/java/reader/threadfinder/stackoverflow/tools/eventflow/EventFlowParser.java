package reader.threadfinder.stackoverflow.tools.eventflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import util.tools.io.FileSorter;
import util.tools.io.StringValueWriterAndParser;

public class EventFlowParser {

	private final File questionsFile;
	private final File answersFile;
	private final File commentsFile;

	private final Set<Integer> questionIds = new TreeSet<Integer>();
	private final Map<Integer, Integer> answerToRoot = new TreeMap<Integer, Integer>();

	public EventFlowParser(File questionsFile, File answersFile, File commentsFile) {
		this.questionsFile = questionsFile;
		this.answersFile = answersFile;
		this.commentsFile = commentsFile;
	}

	public void writeThreads(File dest) throws IOException {

//		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
//
//		sortQuestionsIntoThreads(out);
//		System.out.println("sorted questions");
//
//		sortAnswersIntoThreads(out);
//		System.out.println("sorted answers");
//
//		sortCommentsIntoThreads(out);
//		System.out.println("sorted comments");
//
//		out.flush();
//		out.close();

		// Sort the thread items
		FileSorter.externalSort(dest, new StringValueWriterAndParser(),
				new StringValueWriterAndParser(), 10);
	}

	private void sortCommentsIntoThreads(BufferedWriter out) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(commentsFile));
		String line = in.readLine();
		while (line != null) {

			String parentStr = line.substring(line.indexOf("Parent:") + 7);
			int pos = parentStr.indexOf('\t');
			if (pos >= 0) {
				parentStr = parentStr.substring(0, parentStr.indexOf('\t'));
			}
			Integer parent = Integer.parseInt(parentStr);

			Integer root = parent;
			String type = null;
			if (questionIds.contains(parent)) {
				type = "question_comment";
			} else {
				root = answerToRoot.get(parent);
				if (root != null) {
					type = "answer_comment";
				}
			}

			if (type != null) {

				String timeStr = line.substring(line.indexOf("CreationDate:") + 13);
				timeStr = timeStr.substring(0, timeStr.indexOf('\t'));
				Long time = Long.parseLong(timeStr);

				String ownerStr = line.substring(line.indexOf("Owner:") + 6);
				ownerStr = ownerStr.substring(0, ownerStr.indexOf('\t'));
				Integer owner = null;
				if (!ownerStr.equals("null")) {
					owner = Integer.parseInt(ownerStr);
				} else {
					line = in.readLine();
					continue;
				}

				out.write("" + root + "," + new ThreadItem(type, time, owner));
				out.newLine();
				out.flush();
			}

			line = in.readLine();
		}
		in.close();
	}

	private void sortAnswersIntoThreads(BufferedWriter out) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(answersFile));
		String line = in.readLine();
		while (line != null) {

			String parentStr = line.substring(line.indexOf("Parent:") + 7);
			parentStr = parentStr.substring(0, parentStr.indexOf('\t'));
			Integer parent = Integer.parseInt(parentStr);

			if (questionIds.contains(parent)) {

				String idStr = line.substring(line.indexOf("ID:") + 3);
				idStr = idStr.substring(0, idStr.indexOf('\t'));
				Integer id = Integer.parseInt(idStr);

				answerToRoot.put(id, parent);

				String timeStr = line.substring(line.indexOf("CreationDate:") + 13);
				timeStr = timeStr.substring(0, timeStr.indexOf('\t'));
				Long time = Long.parseLong(timeStr);

				String ownerStr = line.substring(line.indexOf("Owner:") + 6);
				ownerStr = ownerStr.substring(0, ownerStr.indexOf('\t'));
				Integer owner = null;
				if (!ownerStr.equals("null")) {
					owner = Integer.parseInt(ownerStr);
				} else {
					line = in.readLine();
					continue;
				}

				out.write("" + parent + "," + (new ThreadItem("answer", time, owner)));
				out.newLine();
				out.flush();
			}

			line = in.readLine();
		}
		in.close();
	}

	private void sortQuestionsIntoThreads(BufferedWriter out) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(questionsFile));
		String line = in.readLine();
		while (line != null) {

			if (line.length() == 0) {
				line = in.readLine();
				continue;
			}

			String idStr = line.substring(line.indexOf("ID:") + 3);
			idStr = idStr.substring(0, idStr.indexOf("\t"));
			Integer id = Integer.parseInt(idStr);
			questionIds.add(id);

			String timeStr = line.substring(line.indexOf("CreationDate:") + 13);
			timeStr = timeStr.substring(0, timeStr.indexOf('\t'));
			Long time = Long.parseLong(timeStr);

			String ownerStr = line.substring(line.indexOf("Owner:") + 6);
			ownerStr = ownerStr.substring(0, ownerStr.indexOf('\t'));
			Integer owner = null;
			if (!ownerStr.equals("null")) {
				owner = Integer.parseInt(ownerStr);
			} else {
				line = in.readLine();
				continue;
			}

			out.write("" + id + "," + (new ThreadItem("question", time, owner)));
			out.newLine();
			out.flush();

			line = in.readLine();
		}
		in.close();
	}

	public static void main(String[] args) throws IOException {
		File questionsFile = new File("D:\\Stack Overflow data\\all questions vals\\questions.txt");
		File answersFile = new File("D:\\Stack Overflow data\\all questions vals\\answers.txt");
		File commentsFile = new File("D:\\Stack Overflow data\\all questions vals\\comments.txt");

		File csvFile = new File("D:\\Stack Overflow data\\all questions vals\\threads.csv");

		EventFlowParser parser = new EventFlowParser(questionsFile, answersFile, commentsFile);
		parser.writeThreads(csvFile);
	}
}
