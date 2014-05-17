package recommendation.recipients.groupbased;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import data.representation.actionbased.CollaborativeAction;
import recommendation.recipients.RecipientRecommender;
import recommendation.recipients.ScoredRecipientRecommendation;

public abstract class GroupBasedRecipientRecommender<V extends Comparable<V>>
		implements RecipientRecommender<V> {

	public abstract Collection<Set<V>> getGroups();
	
	public abstract double getGroupScore(CollaborativeAction<V> action, Set<V> group);
	
	protected boolean updateIndividualScores(Set<V> group, double score,
			Map<V, Double> recipientToScore) {

		boolean didUpdate = false;
		if (score != 0) {
			for (V recipient : new HashSet<>(group)) {
				Double recipientScore = recipientToScore.get(recipient);
				recipientScore = (recipientScore == null) ? score
						: recipientScore + score;
				recipientToScore.put(recipient, recipientScore);
				didUpdate = true;
			}
		}
		return didUpdate;
	}
	
	protected void updateGroupAndRecipientScores(CollaborativeAction<V> action,
			Map<V, Double> recipientToScore, Map<Set<V>, Double> groupToScore) {

		for (Set<V> pastGroup : getGroups()) {

			double score = getGroupScore(action, pastGroup);
			if (updateIndividualScores(pastGroup, score, recipientToScore)) {
				groupToScore.put(pastGroup, score);
			}
		}
	}

	protected Collection<ScoredRecipientRecommendation<V>> getAllSingleRecipientRecommendations(
			CollaborativeAction<V> action, Map<V, Double> recipientToScore) {
		Collection<V> collaborators = new HashSet<>(action.getCollaborators());
		Collection<ScoredRecipientRecommendation<V>> recommendations = new TreeSet<>();
		for (Entry<V, Double> entry : recipientToScore.entrySet()) {

			V recipient = entry.getKey();
			if (!collaborators.contains(recipient)) {
				recommendations.add(new ScoredRecipientRecommendation<V>(
						recipient, entry.getValue()));
			}
		}
		return recommendations;
	}
}