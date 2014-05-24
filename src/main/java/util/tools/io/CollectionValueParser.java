package util.tools.io;

import java.util.Collection;
import java.util.TreeSet;

public class CollectionValueParser<V> implements ValueParser<Collection<V>> {

	private final ValueParser<V> subparser;

	public CollectionValueParser(ValueParser<V> subparser) {
		this.subparser = subparser;
	}

	@Override
	public Collection<V> parse(String str) {
		Collection<V> retVal = new TreeSet<V>();

		if (!(str.startsWith("[") && str.endsWith("]"))) {
			return retVal;
		}

		str = str.substring(1, str.length() - 1);
		if (str.length() > 0) {
			String[] vals = str.split(", ");
			for (String val : vals) {
				retVal.add(subparser.parse(val));
			}
		}

		return retVal;

	}

}
