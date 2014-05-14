package kelli.friends;

import util.models.ACheckedObject;
import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AFriendListAR implements ExecutableCommand {
	
	public Object execute(Object theFrame) {
		setTopLevelRowColumnAttributes();
		setFriendsAndChildrenAttributes();
		return null;
	}
	public Object setTopLevelRowColumnAttributes() {
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.STRETCH_COLUMNS, false);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "FriendListName", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "FriendListSelection", AttributeNames.ROW, 1);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.ROW, 2);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "SaverCanceller", AttributeNames.ROW, 3);
		ObjectEditor.setDefaultAttribute(AttributeNames.HORIZONTAL_BOUND_GAP, 0);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.COMPONENT_WIDTH, 700);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.COMPONENT_HEIGHT, 30);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.COMPONENT_HEIGHT, 125);		
		return null;
	}
	public Object setFriendsAndChildrenAttributes() {
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.NUM_COLUMNS, 3);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.NUM_ROWS, 4);		
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.LABELLED, false);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.VECTOR_NAVIGATOR, true);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.VECTOR_NAVIGATOR_SIZE, 12);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.DIRECTION, AttributeNames.BOX);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.HORIZONTAL_GAP, 60);
		// Bound gap is the gap between elements bound to row,column positions and overrides HORIZONTAL GAP
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.HORIZONTAL_BOUND_GAP, 0);
		ObjectEditor.setAttribute(ACheckedObject.class, AttributeNames.STRETCH_COLUMNS, false);
		// makes sure number of selected gets refreshed
		ObjectEditor.setAttribute(ACheckedObject.class, AttributeNames.REFRESH_ON_NOTIFICATION, true);

		;
		
		return null;
	}
	public Object executeOld(Object theFrame) {
		//ObjectEditor.setDefaultAttribute(AttributeNames.COMPONENT_HEIGHT, 22);
		//ObjectEditor.setDefaultAttribute(AttributeNames.CONTAINER_HEIGHT, 30);
		//ObjectEditor.setDefaultAttribute(AttributeNames.COMPONENT_BACKGROUND, Color.WHITE);
		
		ObjectEditor.setPropertyAttribute(AFriendList.class, "FriendListName", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "FriendListSelection", AttributeNames.ROW, 1);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.ROW, 2);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "SaverCanceller", AttributeNames.ROW, 3);

		//ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.STRETCH_UNBOUND_COLUMNS, false);
		ObjectEditor.setAttribute(ACheckedObject.class, AttributeNames.STRETCH_COLUMNS, false);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.HORIZONTAL_GAP, 60);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.HORIZONTAL_BOUND_GAP, 0);
		ObjectEditor.setDefaultAttribute(AttributeNames.HORIZONTAL_BOUND_GAP, 0);

		//ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.COMPONENT_WIDTH, 600);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.CONTAINER_WIDTH, 700);
		//ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.*", AttributeNames.COMPONENT_HEIGHT, 125);

		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.CONTAINER_HEIGHT, 30);
		//ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.CONTAINER_HEIGHT, 125);

		//ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.COMPONENT_HEIGHT, 125);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.CONTAINER_HEIGHT, 125);

		//ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.CONTAINER_HEIGHT, 125);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.STRETCH_COLUMNS, false);
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.Next", AttributeNames.CLASS_VIEW_GROUP, AFriendList.class.getName());
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.Next", AttributeNames.POSITION, 4);
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.Next", AttributeNames.COLUMN, -1);

		//ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.COMPONENT_HEIGHT, 125);

		//ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.CONTAINER_HEIGHT, 125);
		//ObjectEditor.setAttribute(AFriendListSaverCanceller.class, AttributeNames.CONTAINER_HEIGHT, 25);
		//ObjectEditor.setAttribute(AFriendListName.class, AttributeNames.CONTAINER_HEIGHT, 25);
		//ObjectEditor.setAttribute(AFriendListSelection.class, AttributeNames.CONTAINER_HEIGHT, 50);


		//ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.STRETCH_ROWS, true);
//		ObjectEditor.setAttribute(AFriendList.class, AttributeNames.STRETCH_COLUMNS, false);
//		ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.STRETCH_COLUMNS, false);
//		ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.DIRECTION, AttributeNames.BOX);
//		ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.NUM_COLUMNS, 3);
//		ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.NUM_ROWS, 4);
//		ObjectEditor.setAttribute(AVectorNavigator.class, AttributeNames.UNBOUND_BUTTONS_PLACEMENT, BorderLayout.WEST);

//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.STRETCH_COLUMNS, false);
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.STRETCH_COLUMNS, false);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.DIRECTION, AttributeNames.BOX);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.NUM_COLUMNS, 3);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.NUM_ROWS, 4);
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends", AttributeNames.UNBOUND_BUTTONS_PLACEMENT, BorderLayout.WEST);
		
		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.LABELLED, false);

		//ObjectEditor.setAttribute(AFriendList.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );
		//ObjectEditor.setAttribute(AFriendList.class, AttributeNames.SHOW_UNBOUND_BUTTONS, new Boolean(true) );
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.VECTOR_NAVIGATOR, true);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.VECTOR_NAVIGATOR_SIZE, 12);
		//ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends.Next", AttributeNames.CLASS_VIEW_GROUP, "ACompactFriendList");
		//ObjectEditor.setPropertyAttribute(AFriendList.class, "Next", AttributeNames.CLASS_VIEW_GROUP, "ACompactFriendList");

		
//		//ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.COMPONENT_COLOR, Color.GREEN);
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "Selected", AttributeNames.COMPONENT_COLOR, Color.blue);
//		ObjectEditor.setMethodAttribute(AFriendList.class, "SaveList", AttributeNames.COMPONENT_COLOR, Color.blue);
//		ObjectEditor.setMethodAttribute(AFriendList.class, "All", AttributeNames.COMPONENT_COLOR, Color.blue);
//		ObjectEditor.setMethodAttribute(AFriendList.class, "EditName", AttributeNames.COMPONENT_COLOR, Color.blue);


//		ObjectEditor.setAttribute(ANamedString.class, AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IN_BORDER);
//		ObjectEditor.setPreferredWidget(ANamedString.class, "FriendNames",  JTextArea.class);
//		ObjectEditor.setPropertyAttribute(ANamedString.class, "FriendNames", AttributeNames.COMPONENT_HEIGHT, 80);
//		//ObjectEditor.setPropertyAttribute(ANamedClique.class, "FriendNames", AttributeNames.SCROLLED, true);
//
//		//ObjectEditor.setPropertyAttribute(ANamedClique.class, "FriendNames", AttributeNames.COMPONENT_HEIGHT, 70);
//		ObjectEditor.setPreferredWidget(CliqueDriver.class, "LargerClique", JTextArea.class);
//		ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.COMPONENT_HEIGHT, 20);
		
		return null;
	}

}
