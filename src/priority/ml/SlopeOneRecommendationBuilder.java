package priority.ml;

import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.MemoryDiffStorage;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class SlopeOneRecommendationBuilder extends RecommendationBuilder {

	@Override
	public Recommender buildRecommender(DataModel trainData) throws TasteException, IOException {
//		File diffsFile = new File("diffs.txt");
//		if (diffsFile.exists()) {
//			diffsFile.delete();
//		}
//		diffsFile.createNewFile();
		return new SlopeOneRecommender(trainData, null, null, new MemoryDiffStorage(trainData,
				null, 100000L));
	}

}
