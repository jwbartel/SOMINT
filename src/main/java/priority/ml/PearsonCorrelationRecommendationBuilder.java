package priority.ml;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class PearsonCorrelationRecommendationBuilder extends ItemSimilarityRecommendationBuilder {

	@Override
	ItemSimilarity getItemSimilarity(DataModel trainData) throws TasteException {
		return new PearsonCorrelationSimilarity(trainData);
	}

}
