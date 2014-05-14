package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.IntegerValueParser;

public class IndividualizedUserQuestionCountProcessor extends IndividualizedQuestionProcessor {

	Map<Integer, Integer> ownerQuestionCounts = new HashMap<Integer, Integer>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		File ownerFile = new File(questionPrefix + "_OWNER.TXT");
		Integer owner = CollectionIOAssist.readCollection(ownerFile, new IntegerValueParser())
				.iterator().next();

		Integer count = ownerQuestionCounts.get(owner);
		if (count == null) {
			count = 0;
		}
		ownerQuestionCounts.put(owner, count + 1);
	}

	public Map<Integer, Integer> getOwnerQuestionCounts() {
		return ownerQuestionCounts;
	}

}
