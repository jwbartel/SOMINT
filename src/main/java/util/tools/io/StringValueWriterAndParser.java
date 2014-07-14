package util.tools.io;

public final class StringValueWriterAndParser implements ValueWriter<String>, ValueParser<String> {

	
	public String parse(String str) {
		return str;
	}

	
	public String writeVal(String value) {
		return value;
	}
}