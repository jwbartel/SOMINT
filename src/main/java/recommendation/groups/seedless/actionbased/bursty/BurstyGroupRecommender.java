package recommendation.groups.seedless.actionbased.bursty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.seedless.actionbased.ActionBasedSeedlessGroupRecommender;
import recommendation.groups.seedless.actionbased.GraphFormingActionBasedSeedlessGroupRecommender;
import data.representation.actionbased.CollaborativeAction;

public class BurstyGroupRecommender<Collaborator extends Comparable<Collaborator>> implements
		ActionBasedSeedlessGroupRecommender<Collaborator> {

	Collection<Set<Collaborator>> previousSeeds = new HashSet<>();
	Collection<Set<Collaborator>> previousRecommendations = new HashSet<>();
	CollaborativeAction<Collaborator> mostRecentAction;

	GraphFormingActionBasedSeedlessGroupRecommender<Collaborator> graphBasedRecommender;
	GroupMatcher<Collaborator> seedMatcher;
	GroupMatcher<Collaborator> recommendationMatcher;

	public BurstyGroupRecommender(
			GraphFormingActionBasedSeedlessGroupRecommender<Collaborator> graphBasedRecommender,
			GroupMatcher<Collaborator> seedMatcher,
			GroupMatcher<Collaborator> recommendationMatcher) {
		
		this.graphBasedRecommender = graphBasedRecommender;
		this.seedMatcher = seedMatcher;
		this.recommendationMatcher = recommendationMatcher;
	}

	@Override
	public String getTypeOfRecommender() {
		return "bursty group recommender";
	}

	@Override
	public Collection<Set<Collaborator>> getRecommendations() {
		Set<Collaborator> seed = new TreeSet<>(mostRecentAction.getCollaborators());
		for (Set<Collaborator> previousSeed : previousSeeds) {
			if (seedMatcher.groupsMatch(seed, previousSeed)) {
				return new ArrayList<>(0);
			}
		}
		Collection<Set<Collaborator>> recommendations = graphBasedRecommender.getRecommendations();
		Collection<Set<Collaborator>> toRemove = new ArrayList<>();
		for (Set<Collaborator> recommendation : recommendations) {
			if (!recommendation.containsAll(seed)) {
				toRemove.add(recommendation);
			} else {
				for (Set<Collaborator> previousRecommendation : previousRecommendations) {
					if (recommendationMatcher.groupsMatch(recommendation, previousRecommendation)) {
						toRemove.add(recommendation);
						break;
					}
				}
			}
		}
		recommendations.removeAll(toRemove);
		
		previousRecommendations.addAll(recommendations);
		return recommendations;
	}

	@Override
	public void addPastAction(CollaborativeAction<Collaborator> action) {
		graphBasedRecommender.addPastAction(action);
		if (mostRecentAction == null
				|| mostRecentAction.getLastActiveDate().before(action.getLastActiveDate())
				|| mostRecentAction.getLastActiveDate().equals(action.getLastActiveDate())) {
			if (mostRecentAction != null) {
				previousSeeds.add(new TreeSet<>(mostRecentAction.getCollaborators()));
			}
			mostRecentAction = action;
		}
	}

	@Override
	public Collection<CollaborativeAction<Collaborator>> getPastActions() {
		return graphBasedRecommender.getPastActions();
	}

}
