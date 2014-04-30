package kelli.friends;

import util.models.CheckedObject;
import util.models.ListenableVector;

public class AFriendListSaverCanceller extends bus.uigen.util.AbstractSaverCanceller {
	ListenableVector<CheckedObject<String>> friends;
	ListenableVector<CheckedObject<String>> originalFriends;
	public AFriendListSaverCanceller (ListenableVector<CheckedObject<String>> theFriends, 
			ListenableVector<CheckedObject<String>> theOriginalFriends) {
		friends = theFriends;
		originalFriends = theOriginalFriends;
	}
//	public String getFoo() {
//		return "foo";
//	}
	
	@Override
	public void save() {
		originalFriends = friends.deepClone();

		
	}
	
	public String toString() {
		return "Save/Cancel";
	}

}
