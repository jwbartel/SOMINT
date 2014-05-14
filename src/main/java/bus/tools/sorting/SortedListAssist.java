package bus.tools.sorting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class SortedListAssist<V> {

	public static <V extends Comparable<V>> ArrayList<V> orderByFrequency(Map<V, Integer> freqCounts) {
		Map<Integer, Collection<V>> orderedFreqs = new TreeMap<Integer, Collection<V>>(
				new Comparator<Integer>() {

					@Override
					public int compare(Integer o1, Integer o2) {
						return -1 * o1.compareTo(o2);
					}
				});

		for (Entry<V, Integer> entry : freqCounts.entrySet()) {
			Collection<V> collection = orderedFreqs.get(entry.getValue());
			if (collection == null) {
				collection = new TreeSet<V>();
				orderedFreqs.put(entry.getValue(), collection);
			}
			collection.add(entry.getKey());
		}

		ArrayList<V> retVal = new ArrayList<V>();
		for (Entry<Integer, Collection<V>> entry : orderedFreqs.entrySet()) {
			for (V value : entry.getValue()) {
				retVal.add(value);
			}
		}

		return retVal;
	}
}
