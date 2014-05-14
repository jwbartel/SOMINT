package priority.ml;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.RandomRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class RandomRecommendationBuilder extends RecommendationBuilder {

	@Override
	public Recommender buildRecommender(DataModel trainData) throws TasteException, IOException {
		return new RandomRecommender(trainData);
	}

}
