package bus.tools.io;

public interface ValueParser<V> {
	V parse(String str);
}