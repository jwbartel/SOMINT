package recommendation.groups.evolution;

import java.util.Collection;
import java.util.Set;

import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;

public interface GroupEvolutionRecommenderFactory<V> {

	public GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory);

	GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory,
			Collection<Set<V>> prefetchedSeedlessPredictions);
}
