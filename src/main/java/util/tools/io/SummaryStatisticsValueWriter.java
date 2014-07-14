package util.tools.io;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryStatisticsValueWriter implements ValueWriter<SummaryStatistics> {

	
	public String writeVal(SummaryStatistics value) {
		if (value != null) {
			return "" + value.getMean();
		} else {
			return "";
		}
	}

}
