package recommendation.recipients.groupbased.hierarchical;

import java.util.Set;

import data.structures.ComparableSet;
import recommendation.recipients.RecipientRecommendation;

public interface HierarchicalRecommendation<V extends Comparable<V>> extends
		RecipientRecommendation<V> {

	public int getSize();

	public void setParent(HierarchicalGroupRecommendation<V> parent);

	public ComparableSet<V> getAssociatedGroup();

	public Set<V> getMembers();
}
