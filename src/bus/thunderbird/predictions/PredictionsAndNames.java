package bus.thunderbird.predictions;

public class PredictionsAndNames {
	String[] names;
	Object[] predictions;
	
	public PredictionsAndNames(String[] names, Object[] predictions){
		this.names = names;
		this.predictions = predictions;
	}
	
	public String[] getNames(){
		return names;
	}
	
	public Object[] getPredictions(){
		return predictions;
	}

}
