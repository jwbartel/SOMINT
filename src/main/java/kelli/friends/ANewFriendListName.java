package kelli.friends;

public class ANewFriendListName extends AFriendListName{
	FriendList friendList;
	public ANewFriendListName(FriendListManager theFriendListManager,
			FriendList theFriendList) {
		super(theFriendListManager, "");
		friendList = theFriendList;		
	}
	public void setName(String newVal) {
		if (friendListManager != null)
		name = newVal;
		friendListManager.addFriendList(newVal, friendList);		
		nameEditable = false;		
	}

}
