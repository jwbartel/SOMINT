package groups.evolution.predictions.oldchoosers;

public class PredictionChooserSelector {

	@SuppressWarnings("rawtypes")
	static PredictionChooserFactory factory;
	
	@SuppressWarnings("rawtypes")
	public static void setFactory(PredictionChooserFactory f){
		factory = f;
	}
	
	@SuppressWarnings("rawtypes")
	public static PredictionChooser getChooser(){
		return factory.createPredictionChooser();
	}
}
