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

public abstract class GroupBasedRecipientRecommender<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>>
		implements RecipientRecommender<Collaborator, Action> {

	public abstract Collection<Set<Collaborator>> getGroups();
	
	public abstract double getGroupScore(CollaborativeAction<Collaborator> action, Set<Collaborator> group);
	
	protected boolean updateIndividualScores(Set<Collaborator> group, double score,
			Map<Collaborator, Double> recipientToScore) {

		boolean didUpdate = false;
		if (score != 0) {
			for (Collaborator recipient : new HashSet<>(group)) {
				Double recipientScore = recipientToScore.get(recipient);
				recipientScore = (recipientScore == null) ? score
						: recipientScore + score;
				recipientToScore.put(recipient, recipientScore);
				didUpdate = true;
			}
		}
		return didUpdate;
	}
	
	protected void updateGroupAndRecipientScores(CollaborativeAction<Collaborator> action,
			Map<Collaborator, Double> recipientToScore, Map<Set<Collaborator>, Double> groupToScore) {

		for (Set<Collaborator> pastGroup : getGroups()) {
			double score = getGroupScore(action, pastGroup);
			if (updateIndividualScores(pastGroup, score, recipientToScore)) {
				groupToScore.put(pastGroup, score);
			}
		}
	}

	protected Collection<ScoredRecipientRecommendation<Collaborator>> getAllSingleRecipientRecommendations(
			CollaborativeAction<Collaborator> action, Map<Collaborator, Double> recipientToScore) {
		Collection<Collaborator> collaborators = new TreeSet<>(action.getCollaborators());
		Collection<ScoredRecipientRecommendation<Collaborator>> recommendations = new TreeSet<>();
		for (Entry<Collaborator, Double> entry : recipientToScore.entrySet()) {

			Collaborator recipient = entry.getKey();
			if (!collaborators.contains(recipient)) {
				recommendations.add(new ScoredRecipientRecommendation<Collaborator>(
						recipient, entry.getValue()));
			}
		}
		return recommendations;
	}
}