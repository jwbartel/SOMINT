package groups.evolution.fullrecommendation;

import java.util.Collection;
import java.util.Set;

import groups.evolution.GroupEvolutionRecommender;
import groups.evolution.GroupEvolutionRecommenderFactory;
import groups.seedless.SeedlessGroupRecommenderFactory;

public class FullRecommendationGroupEvolutionRecommenderFactory<V> implements
		GroupEvolutionRecommenderFactory<V> {

	@Override
	public GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory) {
		return new FullRecommendationGroupEvolutionRecommender<>(
				seedlessRecommenderFactory);
	}

	@Override
	public GroupEvolutionRecommender<V> create(
			SeedlessGroupRecommenderFactory<V> seedlessRecommenderFactory,
			Collection<Set<V>> prefetchedSeedlessPredictions) {
		return new FullRecommendationGroupEvolutionRecommender<>(
				seedlessRecommenderFactory, prefetchedSeedlessPredictions);
	}

}
