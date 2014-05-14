package kelli.friends;

import java.util.Collection;

import util.models.CheckedObject;
import util.models.ListenableVector;

public interface FriendListManager {

	Collection<NamedString> getNamedStringCliques();

	ListenableVector<String> getNames();

	FriendList getFriendList(String name);
	
	CompactFriendList getCompactFriendList(String name);

	FriendListNameList getFriendListNameList();
	
	void changeName(String originalName, String newName);
	
	void addName(String name);
	
	void removeList(String name);

	CheckedObject<String> getFriend(String listName, int index);

	void setFriendStatus(String listName, int index, boolean status);

	int getFriendIndex(NameIDRecord friend);

	FriendList createEmptyFriendList();

	void addFriendList(String name, FriendList friendList);

	

}