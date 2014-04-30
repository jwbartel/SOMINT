package kelli.friends;

import util.models.CheckedObject;
import util.models.ListenableVector;

public class ANewFriendList extends AFriendList {

	public ANewFriendList(
			ListenableVector<CheckedObject<String>> theFriends,
			FriendListManager theFriendsListManager) {
		super("", theFriends, theFriendsListManager);
		friendListName = new ANewFriendListName(theFriendsListManager, this);		

	}

}
