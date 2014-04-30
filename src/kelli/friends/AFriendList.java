package kelli.friends;

import util.models.ACheckedObject;
import util.models.AListSelection;
import util.models.AListenableVector;
import util.models.CheckedObject;
import util.models.ListSelection;
import util.models.ListenableVector;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.util.AbstractSaverCanceller;

public class AFriendList implements FriendList {
	String name;
	AFriendListName friendListName;
	ListenableVector<CheckedObject<String>> friends;
	ListenableVector<CheckedObject<String>> originalFriends;
	ListSelection<CheckedObject<String>, String> selectedFriends;
	AbstractSaverCanceller saverCanceller;
	AFriendListSelection friendListSelection;
	//Map<String, ListenableVector<CheckedObject<String>>>  nameToCheckedObjectList;
	FriendListManager friendsListManager;
	public AFriendList(String theName, 
			ListenableVector<CheckedObject<String>> theFriends,
			FriendListManager theFriendsListManager) {
		name = theName;
		friends = theFriends;
		friendsListManager = theFriendsListManager;
		originalFriends = theFriends.deepClone();
		saverCanceller = new AFriendListSaverCanceller(friends, originalFriends);
		friendListName = new AFriendListName(friendsListManager, name);
		selectedFriends = new AListSelection<CheckedObject<String>, String>(friends, null, null);
		friendListSelection = new AFriendListSelection(friends, selectedFriends);
	}
//	boolean nameEditable = false;
//	@ComponentWidth(200)
//	public String getName() {
//		return name;
//	}
//	public void editName() {
//		nameEditable = true;
//	}
//	public boolean preSetName() {
//		return nameEditable;
//	}
//	public void setName(String name) {
//		this.name = name;
//		nameEditable = false;
//	}
//	
	public ListSelection<CheckedObject<String>, String>  getFriends() {
		return selectedFriends;
	}
	public void setFriends(ListSelection<CheckedObject<String>, String>  newVal) {
		this.selectedFriends = newVal;
	}
	public String toString() {
		return name;
	}
	public AbstractSaverCanceller getSaverCanceller() {
		return saverCanceller;
	}
	public AFriendListName getFriendListName() {
		return friendListName;
	}
	public FriendListSelection getFriendListSelection() {
		return friendListSelection;
	}
	
//	public void saveList() {
//		originalFriends = friends.deepClone();
//		
//	}
//	public void all() {
//		for (int i = 0; i < friends.size(); i++) {
//			friends.get(i).setStatus(true);
//		}
//	}
//	int numSelected() {
//		int retVal = 0;
//		for (int i = 0; i < friends.size(); i++) {
//			if (friends.get(i).getStatus()) 
//				retVal++;
//		}
//		return retVal;
//	}
//	public String getSelected() {
//		return "Selected (" + numSelected() + " )";
//	}
//	public void cancel() {
//		friends = originalFriends;
//		originalFriends = friends.deepClone();
//	}
	
	public static void main (String[] args) {
//		ObjectEditor.setAttribute(AFriendListSaverCanceller.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );
//		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "cancel", AttributeNames.SHOW_BUTTON, new Boolean(true) );
//
//		ObjectEditor.setAttribute(AFriendListSaverCanceller.class, AttributeNames.SHOW_UNBOUND_BUTTONS, new Boolean(true) );
		ObjectEditor.setDefaultAttribute(AttributeNames.HORIZONTAL_GAP, 4);

		ListenableVector<CheckedObject<String>> list = new AListenableVector();
		CheckedObject<String> checkedObject = new ACheckedObject ("Test", true, 5);
		CheckedObject<String> checkedObject2 = new ACheckedObject ("Test2", true, 5);
		list.add(checkedObject);
		//list.add(checkedObject2);
		//FriendList friendList = new AFriendList("TestName", list, null);
		FriendList friendList = new AFriendList("", list, null);
		//ObjectEditor.edit(list);
		//ObjectEditor.treeEdit(friendList);
		uiFrame friendListFrame = ObjectEditor.edit(friendList);
		friendListFrame.setSize(750, 500);
		friendListFrame.setTitle("Edit List");
		//StackTraceElement[] elements;
		//ObjectEditor.edit(checkedObject);
//		try {
//			throw new Exception();
//		} catch (Exception e) {
//			elements = e.getStackTrace();
//			
//		}
//		System.out.println(elements);
		//ObjectEditor.treeEdit(friendList);
		//ObjectEditor.edit(checkedObject);
		//DisplayedComponent componentTree =  friendListFrame.getComponentTree();
		//ObjectEditor.treeEdit(componentTree);
		//ObjectEditor.profiledEdit(componentTree);
		//ObjectEditor.treeEdit ("hello world");
	}
	public String getName() {
		return name;
	}
	public void setName(String newName) {
		name = newName;
	}
	@Override
	public boolean isJoinChecked() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
