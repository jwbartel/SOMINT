package util.tools.io;

public class DoubleValueWriter implements ValueWriter<Double> {

	@Override
	public String writeVal(Double value) {
		if (value == null) {
			return "";
		} else {
			return "" + value;
		}
	}

}
