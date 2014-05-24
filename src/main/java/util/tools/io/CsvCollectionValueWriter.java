package util.tools.io;

import java.util.Collection;
import java.util.TreeSet;

public class CsvCollectionValueWriter<V> implements ValueWriter<Collection<V>> {

	private final ValueWriter<V> subwriter;

	public CsvCollectionValueWriter(ValueWriter<V> subwriter) {
		this.subwriter = subwriter;
	}

	@Override
	public String writeVal(Collection<V> collection) {
		String retVal = "";
		for (V item : collection) {
			retVal += subwriter.writeVal(item) + ",";
		}
		return retVal;

	}

}
