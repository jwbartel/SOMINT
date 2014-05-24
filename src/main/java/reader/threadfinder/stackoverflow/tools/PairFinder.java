package reader.threadfinder.stackoverflow.tools;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.tools.io.MapIOAssist.MapLineReader;

public class PairFinder<V1 extends Comparable<V1>, V2 extends Comparable<V2>> implements
		MapLineReader<V2, Collection<Integer>> {

	private final Set<Pair<V1, V2>> seenPairs = new TreeSet<Pair<V1, V2>>();
	private final Map<Integer, Collection<V1>> questionsToV1;

	public PairFinder(Map<Integer, Collection<V1>> questionsToV1) {
		this.questionsToV1 = questionsToV1;
	}

	public Set<Pair<V1, V2>> getSeenPairs() {
		return seenPairs;
	}

	@Override
	public void readLine(V2 key, Collection<Integer> questionsForVal2) {

		for (Integer question : questionsForVal2) {
			for (V1 val1 : questionsToV1.get(question)) {
				seenPairs.add(new Pair<V1, V2>(val1, key));
			}
		}
	}

}
