package util.tools.io;

public final class LongValueParser implements ValueParser<Long> {

	
	public Long parse(String str) {
		return Long.parseLong(str);
	}
}