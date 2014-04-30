package kelli.friends;

import util.models.ListenableVector;
import util.models.StringInSets;

public interface CompactFriendList<ElementType extends StringInSets> {

	public ListenableVector<ElementType> getCompactFriends();

}