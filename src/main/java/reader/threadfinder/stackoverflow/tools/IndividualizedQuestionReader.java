package reader.threadfinder.stackoverflow.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import reader.ContentParser;
import bus.tools.io.CollectionIOAssist;
import bus.tools.io.MapIOAssist;

public class IndividualizedQuestionReader extends PostReader {

	private static final String PRECOMPUTES_FOLDER_NAME = "precomputes";

	Map<Integer, Long> timeOfEarliestAnswers;

	File precomputesFolder;
	BufferedWriter questionListOut;
	int questionCount = 0;

	public IndividualizedQuestionReader(File destFolder, Map<Integer, Long> timeOfEarliestAnswers)
			throws IOException {
		this.timeOfEarliestAnswers = timeOfEarliestAnswers;
		precomputesFolder = new File(destFolder, PRECOMPUTES_FOLDER_NAME);
		if (!precomputesFolder.exists()) {
			precomputesFolder.mkdirs();
		}
		questionListOut = new BufferedWriter(new FileWriter(new File(destFolder,
				"questions list.txt")));
	}

	@Override
	public void readPost(String post) {
		Integer id = readID(post);
		Long earliestAnswer = timeOfEarliestAnswers.get(id);
		if (earliestAnswer == null) {
			return;
		}
		Integer owner = readOwner(post);
		String[] tags = readTags(post);
		String title = readTitle(post);

		if (id == null) {
			return;
		}
		try {
			questionCount++;
			int subfolder = (questionCount / 25000) + 1;
			File precomputesFolder = new File(this.precomputesFolder, "" + subfolder);
			if (!precomputesFolder.exists()) {
				precomputesFolder.mkdirs();
			}

			questionListOut.write(PRECOMPUTES_FOLDER_NAME + "/" + subfolder + "/" + id);
			questionListOut.newLine();
			questionListOut.flush();

			try {
				if (tags != null) {
					File earliestAnswerFile = new File(precomputesFolder, "" + id
							+ "_EARLIEST_ANSWER.TXT");
					ArrayList<Long> tempList = new ArrayList<Long>();
					tempList.add(earliestAnswer);
					CollectionIOAssist.writeCollection(earliestAnswerFile, tempList);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

			try {
				if (tags != null) {
					File tagsFile = new File(precomputesFolder, "" + id + "_TAGS.TXT");
					CollectionIOAssist.writeCollection(tagsFile, Arrays.asList(tags));
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

			try {
				if (owner != null) {
					File ownerFile = new File(precomputesFolder, "" + id + "_OWNER.TXT");
					Set<Integer> ownerSet = new TreeSet<Integer>();
					ownerSet.add(owner);
					CollectionIOAssist.writeCollection(ownerFile, ownerSet);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

			try {
				if (title != null) {
					Map<String, Integer> titleWordFreqs = ContentParser.parse(title);
					File wordsFile = new File(precomputesFolder, "" + id
							+ "_SUBJECT_WORDCOUNTS.TXT");
					MapIOAssist.writeMap(wordsFile, titleWordFreqs);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void close() throws IOException {
		questionListOut.close();
	}

}
