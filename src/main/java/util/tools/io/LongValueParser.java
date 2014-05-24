package util.tools.io;

public final class LongValueParser implements ValueParser<Long> {

	@Override
	public Long parse(String str) {
		return Long.parseLong(str);
	}
}