package recommendation.recipients.groupbased.hierarchical;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import recommendation.recipients.SingleRecipientRecommendation;
import bus.data.structures.ComparableSet;

public class HierarchicalIndividualRecommendation<V extends Comparable<V>>
		implements HierarchicalRecommendation<V>,
		SingleRecipientRecommendation<V> {

	final V individual;
	HierarchicalGroupRecommendation<V> parent = null;
	ComparableSet<V> associatedGroup;
	
	public HierarchicalIndividualRecommendation (V individual, ComparableSet<V> associatedGroup) {
		this.individual = individual;
		this.associatedGroup = associatedGroup;
	}
	
	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void setParent(HierarchicalGroupRecommendation<V> parent) {
		this.parent = parent;
	}

	@Override
	public ComparableSet<V> getAssociatedGroup() {
		return associatedGroup;
	}

	@Override
	public Set<V> getMembers() {
		return new TreeSet<>(Arrays.asList(individual));
	}

	@Override
	public V getRecipient() {
		return individual;
	}
	
	public String toString() {
		return individual.toString();
	}

}
