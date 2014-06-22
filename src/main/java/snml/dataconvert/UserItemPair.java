package snml.dataconvert;

public class UserItemPair implements Comparable<UserItemPair> {

	final Object user;
	final Object item;
	
	public UserItemPair(Object user, Object item) {
		this.user = user;
		this.item = item;
	}
	
	@Override
	public int compareTo(UserItemPair arg0) {
		int userCompare = user.toString().compareTo(item.toString());
		if (userCompare != 0) {
			return userCompare;
		}
		return item.toString().compareTo(item.toString());
	}

	public Object getUser() {
		return user;
	}

	public Object getItem() {
		return item;
	}

}
