package kelli.friends;

import util.annotations.ComponentWidth;

public class AFriendListName {
	boolean nameEditable = false;
	
	String name;
	FriendListManager friendListManager;
	//FriendList friendList;
	public AFriendListName(FriendListManager theFriendListManager, String theName) {
		friendListManager = theFriendListManager;	
		name = theName;
		if (name == null || name.equals(""))
			nameEditable = true;
	}
//	public String getFoo() {
//		return "foo";
//	}
	@ComponentWidth(200)
	public String getName() {
		return name;
	}
	public void editName() {
		nameEditable = true;
	}
	public boolean preSetName() {
		return nameEditable;
	}
	public void setName(String newVal) {
		if (friendListManager != null)
		friendListManager.changeName(name, newVal);
		this.name = newVal;
		nameEditable = false;
		
	}

}
