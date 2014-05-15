package recommendation.groups.seedless.actionbased;

import recommendation.general.actionbased.ActionBasedRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommender;

public interface ActionBasedSeedlessGroupRecommender<CollaboratorType> extends
		SeedlessGroupRecommender<CollaboratorType>, ActionBasedRecommender<CollaboratorType> {

}
