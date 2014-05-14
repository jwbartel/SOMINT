package bus.thunderbird;

import java.applet.Applet;
import java.util.Set;

public class PredictionFetcher extends Applet{

	
	public static PredictionFetcher newInstance(){
		return new PredictionFetcher();
	}
	
	public String[] getPredictions(Set<String> seed){
		String[] toReturn = new String[3];
		toReturn[0] = "Bob";
		toReturn[1] = "Sue";
		toReturn[2] = "Joe";
		return toReturn;
	}
}
