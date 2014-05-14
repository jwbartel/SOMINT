package priority.ml;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public abstract class ItemSimilarityRecommendationBuilder extends RecommendationBuilder {

	abstract ItemSimilarity getItemSimilarity(DataModel trainData) throws TasteException;

	@Override
	public Recommender buildRecommender(DataModel trainData) throws TasteException, IOException {
		return new GenericItemBasedRecommender(trainData, new GenericItemSimilarity(
				getItemSimilarity(trainData), trainData, 10000));
	}

}
