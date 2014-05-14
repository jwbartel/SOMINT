package priority.ml;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public abstract class RecommendationBuilder {

	public abstract Recommender buildRecommender(DataModel trainData) throws TasteException,
			IOException;
}
