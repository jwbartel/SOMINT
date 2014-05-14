package recommendation.groups.evolution.fullrecommendation;

import java.util.Collection;
import java.util.Set;

import recommendation.groups.evolution.GroupEvolutionRecommender;
import recommendation.groups.evolution.GroupEvolutionRecommenderFactory;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;

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
