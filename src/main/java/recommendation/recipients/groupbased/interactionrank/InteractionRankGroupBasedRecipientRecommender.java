package recommendation.recipients.groupbased.interactionrank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import data.representation.actionbased.CollaborativeAction;
import recommendation.recipients.RecipientRecommendation;
import recommendation.recipients.ScoredRecipientRecommendation;
import recommendation.recipients.groupbased.GroupBasedRecipientRecommender;
import recommendation.recipients.groupbased.GroupScorer;
import recommendation.recipients.groupbased.interactionrank.scoring.SubsetWeightedScore;

public class InteractionRankGroupBasedRecipientRecommender<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>> extends
		GroupBasedRecipientRecommender<Collaborator, Action> {

	private final GroupScorer<Collaborator> groupScorer;
	private Collection<Action> actions = new HashSet<>();
	private Map<Set<Collaborator>, Collection<CollaborativeAction<Collaborator>>> groupsToActions = new HashMap<>();

	public InteractionRankGroupBasedRecipientRecommender(GroupScorer<Collaborator> groupScorer) {
		this.groupScorer = groupScorer;
	}

	public InteractionRankGroupBasedRecipientRecommender(double wOut, double halfLife) {
		this.groupScorer = new SubsetWeightedScore<>(wOut, halfLife);
	}
	
	public GroupScorer<Collaborator> getGroupScorer() {
		return groupScorer;
	}
	
	@Override
	public String getTypeOfRecommender() {
		return "google group-based recipient recommender";
	}
	
	@Override
	public Collection<Set<Collaborator>> getGroups() {
		return groupsToActions.keySet();
	}
	
	@Override
	public double getGroupScore(CollaborativeAction<Collaborator> action, Set<Collaborator> group) {
		Collection<CollaborativeAction<Collaborator>> pastGroupActions = groupsToActions
				.get(group);
		double score = groupScorer.score(group, action, pastGroupActions);
		return score;
	}

	@Override
	public void addPastAction(Action action) {
		if (actions.contains(action)) {
			return;
		}
		
		actions.add(action);

		Set<Collaborator> group = new TreeSet<>(action.getCollaborators());
		for (Entry<Set<Collaborator>, Collection<CollaborativeAction<Collaborator>>> entry : groupsToActions
				.entrySet()) {

			Set<Collaborator> pastGroup = entry.getKey();
			Collection<CollaborativeAction<Collaborator>> pastGroupActions = entry.getValue();
			if (group.containsAll(pastGroup)) {
				pastGroupActions.add(action);
			}
		}
		if (!groupsToActions.containsKey(group)) {
			Collection<CollaborativeAction<Collaborator>> groupActions = new ArrayList<>();
			groupActions.add(action);
			groupsToActions.put(group, groupActions);
		}
	}

	@Override
	public Collection<Action> getPastActions() {
		return new ArrayList<>(actions);
	}

	@Override
	public Collection<RecipientRecommendation<Collaborator>> recommendRecipients(
			CollaborativeAction<Collaborator> action, int maxPredictions) {

		Map<Collaborator, Double> recipientToScore = new TreeMap<>();
		Map<Set<Collaborator>, Double> groupToScore = new HashMap<>();
		updateGroupAndRecipientScores(action, recipientToScore, groupToScore);

		Collection<ScoredRecipientRecommendation<Collaborator>> allSingleRecipientRecommendations =
				getAllSingleRecipientRecommendations(
				action, recipientToScore);
		Collection<ScoredRecipientRecommendation<Collaborator>> limitedSingleRecipientRecommendations =
				new TreeSet<>();
		for (ScoredRecipientRecommendation<Collaborator> recommendation : allSingleRecipientRecommendations) {
			if (limitedSingleRecipientRecommendations.size() >= maxPredictions) {
				break;
			}
			limitedSingleRecipientRecommendations.add(recommendation);
		}
		return new ArrayList<RecipientRecommendation<Collaborator>>(
				limitedSingleRecipientRecommendations);
	}

	@Override
	public Collection<RecipientRecommendation<Collaborator>> recommendRecipients(
			CollaborativeAction<Collaborator> action) {
		Map<Collaborator, Double> recipientToScore = new TreeMap<>();
		Map<Set<Collaborator>, Double> groupToScore = new HashMap<>();
		updateGroupAndRecipientScores(action, recipientToScore, groupToScore);

		Collection<ScoredRecipientRecommendation<Collaborator>> singleRecipientRecommendations =
				getAllSingleRecipientRecommendations(
				action, recipientToScore);
		return new ArrayList<RecipientRecommendation<Collaborator>>(singleRecipientRecommendations);
	}

}
