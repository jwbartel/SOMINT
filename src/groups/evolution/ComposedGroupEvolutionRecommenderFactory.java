package groups.evolution;

import groups.seedless.SeedlessGroupRecommenderFactory;

import java.util.Collection;
import java.util.Set;

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
