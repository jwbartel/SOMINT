package kelli.mergeAttempts.SimplifiedCliqueDriver;

import java.util.ArrayList;

public class FriendList {
	String name;
	ArrayList<String> members;
	
	FriendList(String listName, ArrayList<String> newMembers){
		name = listName;
		members = newMembers;
	}
	FriendList(String listName){
		name = listName;
		members = new ArrayList<String>();
	}
	void addMember(String newMember){
		members.add(newMember);
	}
	void changeName(String newName){
		name = newName;
	}
	int getSize(){
		return members.size();
	}
}
