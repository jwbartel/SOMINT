package kelli.friends;

import util.models.ACheckedObject;

public interface JoinableFriendListNameList {

	public int size();

	public ACheckedObject<String> get(int index);

	public void join();

}