package recipients.groupbased.google;

import general.actionbased.CollaborativeAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import recipients.RecipientRecommendation;
import recipients.ScoredRecipientRecommendation;
import recipients.groupbased.GroupBasedRecipientRecommender;
import recipients.groupbased.google.scoring.GroupScorer;
import recipients.groupbased.google.scoring.SubsetWeightedScore;

public class GoogleGroupBasedRecipientRecommender<V extends Comparable<V>> extends
		GroupBasedRecipientRecommender<V> {

	private final GroupScorer<V> groupScorer;
	private Collection<CollaborativeAction<V>> actions = new HashSet<>();
	private Map<Set<V>, Collection<CollaborativeAction<V>>> groupsToActions = new HashMap<>();

	public GoogleGroupBasedRecipientRecommender(GroupScorer<V> groupScorer) {
		this.groupScorer = groupScorer;
	}

	public GoogleGroupBasedRecipientRecommender(double wOut, double halfLife) {
		this.groupScorer = new SubsetWeightedScore<>(wOut, halfLife);
	}
	
	protected GroupScorer<V> getGroupScorer() {
		return groupScorer;
	}
	
	@Override
	public String getTypeOfRecommender() {
		return "google group-based recipient recommender";
	}
	
	@Override
	public Collection<Set<V>> getGroups() {
		return groupsToActions.keySet();
	}
	
	@Override
	public double getGroupScore(CollaborativeAction<V> action, Set<V> group) {
		Collection<CollaborativeAction<V>> pastGroupActions = groupsToActions
				.get(group);
		double score = groupScorer.score(group, action, pastGroupActions);
		return score;
	}

	@Override
	public void addPastAction(CollaborativeAction<V> action) {
		if (actions.contains(action)) {
			return;
		}
		
		actions.add(action);

		Set<V> group = new HashSet<>(action.getCollaborators());
		for (Entry<Set<V>, Collection<CollaborativeAction<V>>> entry : groupsToActions
				.entrySet()) {

			Set<V> pastGroup = entry.getKey();
			Collection<CollaborativeAction<V>> pastGroupActions = entry.getValue();
			if (group.containsAll(pastGroup)) {
				pastGroupActions.add(action);
			}
		}
		if (!groupsToActions.containsKey(group)) {
			Collection<CollaborativeAction<V>> groupActions = new ArrayList<>();
			groupActions.add(action);
			groupsToActions.put(group, groupActions);
		}
	}

	@Override
	public Collection<CollaborativeAction<V>> getPastActions() {
		return new ArrayList<>(actions);
	}

	@Override
	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action, int maxPredictions) {

		Map<V, Double> recipientToScore = new TreeMap<>();
		Map<Set<V>, Double> groupToScore = new HashMap<>();
		updateGroupAndRecipientScores(action, recipientToScore, groupToScore);

		Collection<ScoredRecipientRecommendation<V>> allSingleRecipientRecommendations =
				getAllSingleRecipientRecommendations(
				action, recipientToScore);
		Collection<ScoredRecipientRecommendation<V>> limitedSingleRecipientRecommendations =
				new TreeSet<>();
		for (ScoredRecipientRecommendation<V> recommendation : allSingleRecipientRecommendations) {
			if (limitedSingleRecipientRecommendations.size() >= maxPredictions) {
				break;
			}
			limitedSingleRecipientRecommendations.add(recommendation);
		}
		return new ArrayList<RecipientRecommendation<V>>(
				limitedSingleRecipientRecommendations);
	}

	@Override
	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action) {
		Map<V, Double> recipientToScore = new TreeMap<>();
		Map<Set<V>, Double> groupToScore = new HashMap<>();
		updateGroupAndRecipientScores(action, recipientToScore, groupToScore);

		Collection<ScoredRecipientRecommendation<V>> singleRecipientRecommendations =
				getAllSingleRecipientRecommendations(
				action, recipientToScore);
		return new ArrayList<RecipientRecommendation<V>>(singleRecipientRecommendations);
	}

}
