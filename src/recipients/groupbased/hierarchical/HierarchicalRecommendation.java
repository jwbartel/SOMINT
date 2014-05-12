package recipients.groupbased.hierarchical;

import java.util.Set;

import recipients.RecipientRecommendation;
import bus.data.structures.ComparableSet;

public interface HierarchicalRecommendation<V extends Comparable<V>> extends
		RecipientRecommendation<V> {

	public int getSize();

	public void setParent(HierarchicalGroupRecommendation<V> parent);

	public ComparableSet<V> getAssociatedGroup();

	public Set<V> getMembers();
}
