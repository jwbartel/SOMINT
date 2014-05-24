package util.tools.io;

public final class IntegerValueParser implements ValueParser<Integer>{

	@Override
	public Integer parse(String str) {
		return Integer.parseInt(str);
	}
}