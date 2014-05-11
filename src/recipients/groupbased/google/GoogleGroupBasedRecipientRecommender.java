package recipients.groupbased.google;

import general.actionbased.CollaborativeAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import recipients.ScoredRecipientRecommendation;
import recipients.groupbased.GroupBasedRecipientRecommender;
import recipients.groupbased.google.scoring.GroupScorer;
import recipients.groupbased.google.scoring.SubsetWeightedScore;

public class GoogleGroupBasedRecipientRecommender<V extends Comparable<V>> implements
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
	
	@Override
	public String getTypeOfRecommender() {
		return "google group-based recipient recommender";
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
	public Collection<ScoredRecipientRecommendation<V>> recommendRecipients(CollaborativeAction<V> action) {
		Map<V, Double> recipientToScore = new TreeMap<>();
		for (Entry<Set<V>, Collection<CollaborativeAction<V>>> entry : groupsToActions
				.entrySet()) {

			Set<V> pastGroup = entry.getKey();
			Collection<CollaborativeAction<V>> pastGroupActions = entry.getValue();
			double score = groupScorer.score(pastGroup, action, pastGroupActions);
			
			if (score != 0) {
				for (V recipient : new HashSet<>(action.getCollaborators())) {
					Double recipientScore = recipientToScore.get(recipient);
					recipientScore = (recipientScore == null)? score : recipientScore + score;
					recipientToScore.put(recipient, recipientScore);
				}
			}
		}
		
		Collection<ScoredRecipientRecommendation<V>> recommendations = new TreeSet<>();
		for (Entry<V,Double> entry : recipientToScore.entrySet()) {
			recommendations.add(new ScoredRecipientRecommendation<V>(entry.getKey(), entry.getValue()));
		}
		return recommendations;
	}

	@Override
	public Collection<ScoredRecipientRecommendation<V>> recommendRecipients(CollaborativeAction<V> action,
			int maxPredictions) {
		
		Collection<ScoredRecipientRecommendation<V>> allRecommendations = recommendRecipients(action);
		Collection<ScoredRecipientRecommendation<V>> recommendations = new TreeSet<>();
		
		Iterator<ScoredRecipientRecommendation<V>> iterator = allRecommendations.iterator();
		int count = 0;
		while (iterator.hasNext() && count < maxPredictions) {
			recommendations.add(iterator.next());
			count++;
		}
		return recommendations;
	}

}
