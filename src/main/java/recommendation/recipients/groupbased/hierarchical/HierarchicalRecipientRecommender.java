package recommendation.recipients.groupbased.hierarchical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import data.representation.actionbased.CollaborativeAction;
import data.structures.ComparableSet;
import recommendation.recipients.RecipientRecommendation;
import recommendation.recipients.ScoredRecipientRecommendation;
import recommendation.recipients.groupbased.GroupBasedRecipientRecommender;

public class HierarchicalRecipientRecommender<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>> extends
		GroupBasedRecipientRecommender<Collaborator, Action> {

	private final GroupBasedRecipientRecommender<Collaborator, Action> groupBasedRecommender;

	public HierarchicalRecipientRecommender(
			GroupBasedRecipientRecommender<Collaborator, Action> groupBasedRecommender) {
		this.groupBasedRecommender = groupBasedRecommender;
	}
	
	public GroupBasedRecipientRecommender<Collaborator, Action> getBaseRecommender() {
		return groupBasedRecommender;
	}

	@Override
	public String getTypeOfRecommender() {
		return "hierarchical recipient recommender";
	}

	@Override
	public void addPastAction(Action action) {
		groupBasedRecommender.addPastAction(action);

	}

	@Override
	public Collection<Action> getPastActions() {
		return groupBasedRecommender.getPastActions();
	}

	@Override
	public Collection<Set<Collaborator>> getGroups() {
		return groupBasedRecommender.getGroups();
	}

	private Collection<HierarchicalRecommendation<Collaborator>> hierarchicallyGroupRecommendations(
			Collection<ScoredRecipientRecommendation<Collaborator>> singleRecipientRecommendations,
			Map<Set<Collaborator>, Double> groupToScore) {

		HierarchicalGroupRecommendation<Collaborator> predictionList =
				new HierarchicalGroupRecommendation<>(null);
		
		for(ScoredRecipientRecommendation<Collaborator> recipientRecommendation : singleRecipientRecommendations) {
			
			Collaborator recipient = recipientRecommendation.getRecipient();
			Set<Collaborator> highestScoredGroup = null;
			Double highestScore = null;

			for (Entry<Set<Collaborator>, Double> entry : groupToScore.entrySet()) {
				Set<Collaborator> group = entry.getKey();
				Double score = entry.getValue();
				if (group.contains(recipient) && (highestScore == null
						|| highestScore < score)) {
					highestScore = score;
					highestScoredGroup = group;
				}
			}

			if (highestScore == null) {
				highestScoredGroup = new TreeSet<>();
				highestScoredGroup.add(recipient);
			}
			predictionList.add(new HierarchicalIndividualRecommendation<Collaborator>(
					recipient, new ComparableSet<>(highestScoredGroup)));
		}
		return new ArrayList<>(predictionList.getValues());
	}
	
	@Override
	public double getGroupScore(CollaborativeAction<Collaborator> action, Set<Collaborator> group) {
		return groupBasedRecommender.getGroupScore(action, group);
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
				hierarchicallyGroupRecommendations(
						limitedSingleRecipientRecommendations, groupToScore));
	}

	@Override
	public Collection<RecipientRecommendation<Collaborator>> recommendRecipients(
			CollaborativeAction<Collaborator> action) {
		Map<Collaborator, Double> recipientToScore = new TreeMap<>();
		Map<Set<Collaborator>, Double> groupToScore = new HashMap<>();
		updateGroupAndRecipientScores(action, recipientToScore, groupToScore);

		Collection<ScoredRecipientRecommendation<Collaborator>> singleRecipientRecommendations = getAllSingleRecipientRecommendations(
				action, recipientToScore);

		return new ArrayList<RecipientRecommendation<Collaborator>>(
				hierarchicallyGroupRecommendations(
						singleRecipientRecommendations, groupToScore));
	}

}
