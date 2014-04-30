package reader.threadfinder.stackoverflow.tools;

public class Pair<V1 extends Comparable<V1>, V2 extends Comparable<V2>> implements
		Comparable<Pair<V1, V2>> {
	private final V1 val1;
	private final V2 val2;

	public Pair(V1 v1, V2 v2) {
		this.val1 = v1;
		this.val2 = v2;
	}

	@Override
	public int compareTo(Pair<V1, V2> o) {
		int compare = this.val1.compareTo(o.val1);
		if (compare != 0) {
			return compare;
		}
		return this.val2.compareTo(o.val2);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		return this.compareTo((Pair) o) == 0;
	}

	@Override
	public String toString() {
		return "" + val1.toString() + "," + val2.toString();
	}

	public V1 getFirstVal() {
		return val1;
	}

	public V2 getSecondVal() {
		return val2;
	}

}
