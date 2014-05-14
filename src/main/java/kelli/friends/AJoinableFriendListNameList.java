package kelli.friends;

import java.util.ArrayList;
import java.util.List;

import util.models.ACheckedObject;

public class AJoinableFriendListNameList implements JoinableFriendListNameList {
	//ListenableVector<String> names = new AListenableVector();
	FriendListNameList source;
	List<ACheckedObject<String>> contents = new ArrayList();
	public AJoinableFriendListNameList(FriendListNameList theSource) {
		source = theSource;
		for (int i = 0; i < source.size(); i++) {
			ACheckedObject checkedObject = new ACheckedObject(source.get(i), false, null);
			contents.add(checkedObject);
		}
		
	}
	/* (non-Javadoc)
	 * @see friends.JoinableFriendListNameList#size()
	 */
	public int size() {
		return contents.size();
	}
	/* (non-Javadoc)
	 * @see friends.JoinableFriendListNameList#get(int)
	 */
	public ACheckedObject<String> get (int index) {
		return contents.get(index);
	}
	
	/* (non-Javadoc)
	 * @see friends.JoinableFriendListNameList#join()
	 */
	public void join() {
		List<String> joinItems = new ArrayList();
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).getStatus()) {
				String joinedItem = contents.get(i).getObject();
				joinItems.add(joinedItem);
				System.out.println(joinedItem);
			}
		}
		
		
	}


}
