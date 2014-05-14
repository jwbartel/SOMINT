package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.IntegerValueParser;

public class IndividualizedUserQuestionListProcessor extends IndividualizedQuestionProcessor {

	Map<Integer, Collection<Integer>> ownerQuestionLists = new HashMap<Integer, Collection<Integer>>();

	@Override
	public void processQuestion(String questionPrefix) throws IOException {

		Integer id = Integer.parseInt(new File(questionPrefix).getName());

		File ownerFile = new File(questionPrefix + "_OWNER.TXT");
		Integer owner = CollectionIOAssist.readCollection(ownerFile, new IntegerValueParser())
				.iterator().next();

		Collection<Integer> list = ownerQuestionLists.get(owner);
		if (list == null) {
			list = new TreeSet<Integer>();
			ownerQuestionLists.put(owner, list);
		}
		list.add(id);
	}

	public Map<Integer, Collection<Integer>> getOwnerQuestionLists() {
		return ownerQuestionLists;
	}

}
