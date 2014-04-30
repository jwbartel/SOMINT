package kelli.friends;

import util.models.CheckedObject;
import util.models.ListSelection;
import bus.uigen.util.AbstractSaverCanceller;

public interface FriendList {

	public boolean isJoinChecked();
	public String getName();
//
	public void setName(String name);

	//public ListenableVector<CheckedObject<String>> getFriends();
	public ListSelection<CheckedObject<String>, String> getFriends();

	public void setFriends(ListSelection<CheckedObject<String>, String> friends);

	AbstractSaverCanceller getSaverCanceller();

	AFriendListName getFriendListName();

	FriendListSelection getFriendListSelection();

}