package util.tools.io;

public class DoubleValueWriter implements ValueWriter<Double> {

	
	public String writeVal(Double value) {
		if (value == null) {
			return "";
		} else {
			return "" + value;
		}
	}

}
