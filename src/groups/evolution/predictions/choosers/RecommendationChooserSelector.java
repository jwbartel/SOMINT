package groups.evolution.predictions.choosers;

public class RecommendationChooserSelector {

	@SuppressWarnings("rawtypes")
	static RecommendationChooserFactory factory = new SingleRecommenderEngineResultRecommendationChooserFactory<Integer>();
	
	@SuppressWarnings("rawtypes")
	public static void setFactory(RecommendationChooserFactory f){
		factory = f;
	}
	
	@SuppressWarnings("rawtypes")
	public static RecommendationChooser getChooser(){
		return factory.createPredictionChooser();
	}
}
