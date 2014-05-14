package kelli.friends;

import util.models.VectorListener;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;

public class AFriendListNameList implements FriendListNameList {
	//ListenableVector<String> names = new AListenableVector();
	FriendListManager friendListManager;
	public AFriendListNameList(FriendListManager theFriendListManager) {
		friendListManager = theFriendListManager;
	}
	public int size() {
		return friendListManager.getNames().size();
	}
	public String get (int index) {
		return friendListManager.getNames().get(index);
	}
	void openFriendList(String name) {
		FriendList friendList = friendListManager.getFriendList(name);
		uiFrame frame = ObjectEditor.edit(friendList);
		friendList.getSaverCanceller().setFrame(frame);
		frame.setSize(750, 500);
		frame.setTitle("Edit List");
	}
	void openCompactFriendList(String name) {
		CompactFriendList compactFriendList = friendListManager.getCompactFriendList(name);
		uiFrame frame = ObjectEditor.edit(compactFriendList);
		frame.setSize(750, 500);
		frame.setTitle("Friend List");
	}
	public void open(String name) {
		openCompactFriendList(name);
	}
	public void addVectorListener(VectorListener l) {
		friendListManager.getNames().addVectorListener(l);
		
	}
	public void create() {
		FriendList emptyList = friendListManager.createEmptyFriendList();
		ObjectEditor.edit(emptyList);
	}
//	public void join(String name1, String name2) {
//		System.out.println(name1 + name2);
//	}


}
