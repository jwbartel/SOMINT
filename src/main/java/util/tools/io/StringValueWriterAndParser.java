package util.tools.io;

public final class StringValueWriterAndParser implements ValueWriter<String>, ValueParser<String> {

	@Override
	public String parse(String str) {
		return str;
	}

	@Override
	public String writeVal(String value) {
		return value;
	}
}