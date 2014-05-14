package kelli.friends;

import util.annotations.ComponentWidth;
import util.models.AListenableString;
import util.models.CheckedObject;
import util.models.ListSelection;
import util.models.ListenableVector;
import util.models.VectorChangeEvent;

public class AFriendListSelection implements FriendListSelection  {
	ListenableVector<CheckedObject<String>> friends;
	//ListenableVector<CheckedObject<String>> originalFriends;
	ListSelection<CheckedObject<String>, String> selectedFriends;

	AListenableString searchString  = new AListenableString("");
	boolean searchStringInitialized = false;
	public AFriendListSelection (ListenableVector<CheckedObject<String>> theFriends, 
			ListSelection<CheckedObject<String>, String> theSelectedFriends) {
		friends = theFriends;
		selectedFriends = theSelectedFriends;
		searchString.addVectorListener(this);
	}
	public void all() {
		for (int i = 0; i < friends.size(); i++) {
			friends.get(i).setStatus(true);
		}
	}
	int numSelected() {
		int retVal = 0;
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).getStatus()) 
				retVal++;
		}
		return retVal;
	}
	@ComponentWidth(90)
	public String getSelected() {
		return "Selected (" + numSelected() + " )";
	}
	
	
	public AListenableString getSearch() {
		return searchString;		
	}
	public void setSearch(AListenableString newVal) {
		searchString = newVal;
		selectedFriends.setQuery(newVal.toString());
	}
	public void updateVector(VectorChangeEvent evt) {
//		if (!searchStringInitialized) {		
//			searchStringInitialized = true;
//			switch (evt.getEventType()) {
//			case VectorChangeEvent.InsertComponentEvent: 
//				int index = evt.getPosition();
//				char insertedChar = searchString.charAt(index);
//				searchString.clear();
//				searchString.addElement(insertedChar);
//				break;
//			
//			}
//		}
		selectedFriends.setQuery(searchString.toString());
		
	}
	
	public String toString() {
		return "AFriendListSelection " + getSelected() + " " + getSearch();
	}

}
