package recipients.groupbased.hierarchical;

import general.actionbased.CollaborativeAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import recipients.RecipientRecommendation;
import recipients.ScoredRecipientRecommendation;
import recipients.groupbased.GroupBasedRecipientRecommender;
import bus.data.structures.ComparableSet;

public class HierarchicalRecipientRecommender<V extends Comparable<V>> extends
		GroupBasedRecipientRecommender<V> {

	private final GroupBasedRecipientRecommender<V> groupBasedRecommender;

	public HierarchicalRecipientRecommender(
			GroupBasedRecipientRecommender<V> groupBasedRecommender) {
		this.groupBasedRecommender = groupBasedRecommender;
	}

	@Override
	public String getTypeOfRecommender() {
		return "hierarchical recipient recommender";
	}

	@Override
	public void addPastAction(CollaborativeAction<V> action) {
		groupBasedRecommender.addPastAction(action);

	}

	@Override
	public Collection<CollaborativeAction<V>> getPastActions() {
		return groupBasedRecommender.getPastActions();
	}

	@Override
	public Collection<Set<V>> getGroups() {
		return groupBasedRecommender.getGroups();
	}

	private Collection<HierarchicalRecommendation<V>> hierarchicallyGroupRecommendations(
			Collection<ScoredRecipientRecommendation<V>> singleRecipientRecommendations,
			Map<Set<V>, Double> groupToScore) {

		HierarchicalGroupRecommendation<V> predictionList =
				new HierarchicalGroupRecommendation<>(null);
		
		for(ScoredRecipientRecommendation<V> recipientRecommendation : singleRecipientRecommendations) {
			
			V recipient = recipientRecommendation.getRecipient();
			Set<V> highestScoredGroup = null;
			Double highestScore = null;

			for (Entry<Set<V>, Double> entry : groupToScore.entrySet()) {
				Set<V> group = entry.getKey();
				Double score = entry.getValue();
				if (group.contains(recipient) && highestScore == null
						|| highestScore < score) {
					highestScore = score;
					highestScoredGroup = group;
				}
			}

			if (highestScore == null) {
				highestScoredGroup = new TreeSet<>();
				highestScoredGroup.add(recipient);
			}
			predictionList.add(new HierarchicalIndividualRecommendation<V>(
					recipient, new ComparableSet<>(highestScoredGroup)));
		}
		return new ArrayList<>(predictionList.getValues());
	}
	
	@Override
	public double getGroupScore(CollaborativeAction<V> action, Set<V> group) {
		return groupBasedRecommender.getGroupScore(action, group);
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
				hierarchicallyGroupRecommendations(
						limitedSingleRecipientRecommendations, groupToScore));
	}

	@Override
	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action) {
		Map<V, Double> recipientToScore = new TreeMap<>();
		Map<Set<V>, Double> groupToScore = new HashMap<>();
		updateGroupAndRecipientScores(action, recipientToScore, groupToScore);

		Collection<ScoredRecipientRecommendation<V>> singleRecipientRecommendations = getAllSingleRecipientRecommendations(
				action, recipientToScore);

		return new ArrayList<RecipientRecommendation<V>>(
				hierarchicallyGroupRecommendations(
						singleRecipientRecommendations, groupToScore));
	}

}
