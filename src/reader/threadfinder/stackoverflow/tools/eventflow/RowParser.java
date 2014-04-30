package reader.threadfinder.stackoverflow.tools.eventflow;

import org.dom4j.Element;

public abstract class RowParser<V> {

	public abstract void parseRow(Element row);
}
