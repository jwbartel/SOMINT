package kelli.friends;

import java.awt.Checkbox;

import util.models.ListenableVector;
import util.models.StringInSets;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;

public class ACompactFriendList<ElementType extends StringInSets> implements CompactFriendList {
	Checkbox joinBox;
	String nameList;
	AFriendListName friendListName;
	//FriendList friendList;
	ListenableVector<ElementType> compactFriends;
	FriendListManager friendsListManager;
	FriendList friendList;
	
	public ACompactFriendList(String theName, 
			//FriendList theFriends,
			ListenableVector<ElementType> theCompactFriends,
			FriendListManager theFriendsListManager) {
		
		nameList = theName;
		//friendList = theFriends;
		compactFriends = theCompactFriends;
		friendsListManager = theFriendsListManager;		
		friendListName = new AFriendListName(friendsListManager, nameList);
		friendList = friendsListManager.getFriendList(nameList);
		//initializeStringInSetsList();
		
	}
	public ListenableVector<ElementType>  getCompactFriends() {
		return compactFriends;
	}
	
	public void createNewList() {
		FriendList emptyList = friendsListManager.createEmptyFriendList();
		ObjectEditor.edit(emptyList);
				
	}
	
	public void editList() {
		uiFrame friendListFrame = ObjectEditor.edit(friendList);
		friendListFrame.setSize(750, 500);
		friendListFrame.setTitle("Edit List");
		
	}
	
	public void deleteList() {
		friendsListManager.removeList(nameList);
		
	}
	
//	public void next() {
//		
//	}
//	
//	public void previous() {
//		
//	}
	
	public String getNumConnections() {
		return "" + compactFriends.size() + " Connections";
	}
	
	
//	CheckedStringEnum createCheckedStringEnum(int index) {
//		List<CheckedObject<String>> choices = new Vector();
//		ListenableVector<String> names = friendsListManager.getNames();
//		for (int i = 0; i < names.size(); i++) {
//			String tempListName = names.get(i);
//			CheckedObject<String>  friend = friendsListManager.getFriend(tempListName, index);
//			choices.add(new ACheckedObject<String>(tempListName, friend.getUserData(), friend.getStatus()));
//			
//		}
//		CheckedStringEnum retVal = new ACheckedStringEnum(choices);
//		return retVal;		
//	}
	
//	void initializeStringInSetsList() {
//		compactFriends = new AListenableVector();
//		for (int i = 0; i < friendList.getFriends().size(); i++) {
//			ListenableVector<String> names = friendsListManager.getNames();
//			CheckedObject<String> friend = friendsListManager.getFriend(listName, i);
//			if (!friend.getStatus())
//				continue;
//			StringInSets stringInSets = createStringInSets(friend);
//			compactFriends.add(stringInSets);
////			compactFriends.add(stringInSets);
////			StringInSets stringInSets = new AStringInSets();
////			for (String aListName:names) {				
////		
////				CheckedObject<String> aFriend = friendsListManager.getFriend(aListName, i);
////				if (aFriend.getStatus()) {
////					stringInSets = createStringInSets(friend);
////					compactFriends.add(stringInSets);
////			}
//		}		
//	}
	
//	StringInSets createStringInSets (CheckedObject<String> checkedObject) {
//		String name = checkedObject.getObject();
//		Integer id = (Integer) checkedObject.getUserData();
//		Integer index = friendsListManager.getFriendIndex(id);
//		CheckedStringEnum checkedStringEnum = createCheckedStringEnum(index);
//		StringInSets retVal = new AStringInSets(name,checkedStringEnum );
//		return  retVal;	
//		
//	}

}
