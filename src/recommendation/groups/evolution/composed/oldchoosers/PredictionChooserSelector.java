package recommendation.groups.evolution.composed.oldchoosers;

public class PredictionChooserSelector {

	@SuppressWarnings("rawtypes")
	static PredictionChooserFactory factory = new SinglePredictionMultiIdealPredictionChooserFactory<Integer>();
	
	@SuppressWarnings("rawtypes")
	public static void setFactory(PredictionChooserFactory f){
		factory = f;
	}
	
	@SuppressWarnings("rawtypes")
	public static PredictionChooser getChooser(){
		return factory.createPredictionChooser();
	}
}
