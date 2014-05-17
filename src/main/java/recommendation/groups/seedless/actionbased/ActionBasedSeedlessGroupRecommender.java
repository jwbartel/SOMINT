package recommendation.groups.seedless.actionbased;

import data.representation.actionbased.ActionBasedRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommender;

public interface ActionBasedSeedlessGroupRecommender<CollaboratorType> extends
		SeedlessGroupRecommender<CollaboratorType>, ActionBasedRecommender<CollaboratorType> {

}
