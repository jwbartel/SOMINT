package recommendation.groups.evolution.composed;

import java.util.Collection;
import java.util.Set;

import recommendation.groups.evolution.GroupEvolutionRecommender;
import recommendation.groups.evolution.GroupEvolutionRecommenderFactory;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;

public class ComposedGroupEvolutionRecommenderFactory<V> implements GroupEvolutionRecommenderFactory<V> {

	@Override
	public GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory) {
		return new ComposedGroupEvolutionRecommender<V>(seedlessRecommenderFactory);
	}
	
	@Override
	public GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory,
			Collection<Set<V>> prefetchedSeedlessPredictions) {
		return new ComposedGroupEvolutionRecommender<V>(seedlessRecommenderFactory,
				prefetchedSeedlessPredictions);
	}

}
