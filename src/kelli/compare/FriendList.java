package kelli.compare;

import java.util.ArrayList;

public class FriendList {
	public String name;
	public ArrayList<String> members;
	
	public FriendList(String listName, ArrayList<String> newMembers){
		name = listName;
		members = newMembers;
	}
	public FriendList(String listName){
		name = listName;
		members = new ArrayList<String>();
	}
	public void addMember(String newMember){
		members.add(newMember);
	}
	public void changeName(String newName){
		name = newName;
	}
	public int getSize(){
		return members.size();
	}
}
