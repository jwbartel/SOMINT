package kelli.friends;

import util.models.AListenableString;
import util.models.VectorListener;

public interface FriendListSelection extends VectorListener {

	public void all();

	public String getSelected();

	public AListenableString getSearch();

	public void setSearch(AListenableString newVal);

}