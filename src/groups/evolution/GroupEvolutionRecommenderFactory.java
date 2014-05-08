package groups.evolution;

import groups.seedless.SeedlessGroupRecommenderFactory;

import java.util.Collection;
import java.util.Set;

public interface GroupEvolutionRecommenderFactory<V> {

	public GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory);

	GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory,
			Collection<Set<V>> prefetchedSeedlessPredictions);
}
