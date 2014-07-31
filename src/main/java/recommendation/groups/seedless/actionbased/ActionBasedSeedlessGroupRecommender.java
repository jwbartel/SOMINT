package recommendation.groups.seedless.actionbased;

import data.representation.actionbased.ActionBasedRecommender;
import data.representation.actionbased.CollaborativeAction;
import recommendation.groups.seedless.SeedlessGroupRecommender;

public interface ActionBasedSeedlessGroupRecommender<CollaboratorType, ActionType extends CollaborativeAction<CollaboratorType>> extends
		SeedlessGroupRecommender<CollaboratorType>, ActionBasedRecommender<CollaboratorType, ActionType> {

}
