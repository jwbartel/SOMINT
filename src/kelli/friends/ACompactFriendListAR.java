package kelli.friends;

import java.awt.Color;

import util.models.ACheckedStringEnum;
import util.models.ADeletableStringInSets;
import util.models.AStringInSets;
import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;
import bus.uigen.widgets.VirtualLabel;

public class ACompactFriendListAR implements ExecutableCommand {
	public Object execute(Object frame) {
		setTopAttributes();
		setCompactFriendsAttributes();
		setStringInSetsAttributes();
	    return null;
	}
	
	public void setTopAttributes() {
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.LABELLED, false);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.VECTOR_NAVIGATOR, true);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.UNBOUND_LAYOUT, AttributeNames.GRID_LAYOUT);	
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.CLASS_VIEW_GROUP, ACompactFriendList.class.getName());
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.CLASS_VIEW_GROUP, ACompactFriendList.class.getName());
		//otherwise a hierachical title area is created
		ObjectEditor.setDefaultAttribute( AttributeNames.IS_FLAT_TABLE_ROW, false);
		// since it is readonly, it can be bound to label
		ObjectEditor.setPreferredWidget(ACompactFriendList.class, "NumConnections", VirtualLabel.class);
		ObjectEditor.setPropertyLabelled(ACompactFriendList.class, "NumConnections", false);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "NumConnections", AttributeNames.LABEL_LEFT, "");
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "NumConnections", AttributeNames.LABEL_WIDTH, 100);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.LABEL, ">");
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.LABEL, "<");
		// overriding what AVectorNavigator has
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.LABEL_WIDTH, 100);
		ObjectEditor.setAttribute(ACompactFriendList.class, AttributeNames.STRETCH_COLUMNS, false);
		ObjectEditor.setMethodRowColumn(ACompactFriendList.class, "EditList", 0, 2);
		ObjectEditor.setMethodRowColumn(ACompactFriendList.class, "DeleteList", 0, 1);
		ObjectEditor.setMethodRowColumn(ACompactFriendList.class, "CreateNewList", 0, 0);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "NumConnections", 0, 3);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "compactFriends.Next", 0, 5);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "compactFriends.Previous", 0, 4);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "compactFriends.Next", 0, 5);		
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.COMPONENT_WIDTH, 25);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.COMPONENT_WIDTH, 25);
		ObjectEditor.setPropertyRow(ACompactFriendList.class, "CompactFriends", 1);
	}
	public void setCompactFriendsAttributes() {			
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.VECTOR_NAVIGATOR_SIZE, 25);		
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.VERTICAL_GAP, 10);
		ObjectEditor.setAttribute(ACompactFriendList.class, AttributeNames.HORIZONTAL_BOUND_GAP, 4);
		
	}
	public void setStringInSetsAttributes() {
		ObjectEditor.setPreferredWidget(AStringInSets.class, "NumLists",VirtualLabel.class);
		ObjectEditor.setPreferredWidget(AStringInSets.class, "CheckedSets",VirtualLabel.class);
		//
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.COMPONENT_FOREGROUND, Color.BLUE);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "*", AttributeNames.COMPONENT_FOREGROUND, Color.GRAY);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_BACKGROUND, Color.LIGHT_GRAY);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_FOREGROUND, Color.WHITE);
		//
		ObjectEditor.setAttribute(AStringInSets.class, AttributeNames.HORIZONTAL_BOUND_GAP, 4);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.COLUMN, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.COLUMN, 2);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.ROW, 0);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COLUMN, 3);	
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.COLUMN, 1);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.LABEL_LEFT, "");
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "CheckedSets", AttributeNames.COLUMN, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "CheckedSets", AttributeNames.ROW, 1);
		//
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "*", AttributeNames.COMPONENT_HEIGHT, 25);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.COMPONENT_WIDTH, 200);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.COMPONENT_WIDTH, 40);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "CheckedSets", AttributeNames.COMPONENT_WIDTH, 500);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.LABEL_WIDTH, 300);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_HEIGHT, 25);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_WIDTH, 25);		
	
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.LABEL, "X");	
		ObjectEditor.setAttribute(ACheckedStringEnum.class, AttributeNames.REFRESH_ON_NOTIFICATION, true);


		
	}
	public Object executeMonolithic(Object theFrame) {
		//ObjectEditor.setDefaultAttribute(AttributeNames.COMPONENT_HEIGHT, 22);
		//ObjectEditor.setDefaultAttribute(AttributeNames.CONTAINER_HEIGHT, 30);
		//ObjectEditor.setDefaultAttribute(AttributeNames.COMPONENT_BACKGROUND, Color.WHITE);
		ObjectEditor.setDefaultAttribute( AttributeNames.IS_FLAT_TABLE_ROW, false);

		ObjectEditor.setAttribute(AStringInSets.class, AttributeNames.HORIZONTAL_BOUND_GAP, 4);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "*", AttributeNames.COMPONENT_HEIGHT, 25);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.LABELLED, false);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.COMPONENT_FOREGROUND, Color.BLUE);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "*", AttributeNames.COMPONENT_FOREGROUND, Color.GRAY);

		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.COLUMN, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.COLUMN, 2);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.ROW, 0);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COLUMN, 3);
		//ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.SHOW_BUTTON, true);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.LABEL, "X");
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_BACKGROUND, Color.LIGHT_GRAY);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_FOREGROUND, Color.WHITE);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_HEIGHT, 25);
		ObjectEditor.setMethodAttribute(ADeletableStringInSets.class, "delete", AttributeNames.COMPONENT_WIDTH, 25);

		//ObjectEditor.setAttribute(ADeletableStringInSets.class,  AttributeNames.SHOW_UNBOUND_BUTTONS, true);


		ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.ROW, 0);
		//ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.PREFERRED_WIDGET, 0);
		
		
		ObjectEditor.setPreferredWidget(AStringInSets.class, "NumLists",VirtualLabel.class);
		ObjectEditor.setPreferredWidget(AStringInSets.class, "CheckedSets",VirtualLabel.class);

		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.ROW, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.COLUMN, 1);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.LABEL_LEFT, "");
		//ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.LABEL_LEFT, "");
		
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.LABEL_WIDTH, 300);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "NumLists", AttributeNames.COMPONENT_WIDTH, 40);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "CheckedSets", AttributeNames.COMPONENT_WIDTH, 500);

		ObjectEditor.setPropertyAttribute(AStringInSets.class, "CheckedSets", AttributeNames.COLUMN, 0);
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "CheckedSets", AttributeNames.ROW, 1);
		
		ObjectEditor.setPropertyAttribute(AStringInSets.class, "Sets", AttributeNames.COMPONENT_WIDTH, 200);
		
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.VECTOR_NAVIGATOR, true);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.VECTOR_NAVIGATOR_SIZE, 25);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.DIRECTION, AttributeNames.VERTICAL);

		
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "CompactFriends", AttributeNames.VERTICAL_GAP, 10);
		ObjectEditor.setAttribute(ACompactFriendList.class, AttributeNames.HORIZONTAL_BOUND_GAP, 4);

//		ObjectEditor.setAttribute(AStringInSets.class, AttributeNames.COMPONENT_HEIGHT, 100);
//		ObjectEditor.setAttribute(AStringInSets.class, AttributeNames.COMPONENT_WIDTH, 500);
//		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.COMPONENT_HEIGHT, 50);
//		ObjectEditor.setPropertyAttribute(AStringInSets.class, "String", AttributeNames.COMPONENT_WIDTH, 50);
		ObjectEditor.setPreferredWidget(ACompactFriendList.class, "NumConnections", VirtualLabel.class);

		ObjectEditor.setPropertyLabelled(ACompactFriendList.class, "NumConnections", false);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "NumConnections", AttributeNames.LABEL_LEFT, "");
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "NumConnections", AttributeNames.LABEL_WIDTH, 100);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "NumConnections", 0, 3);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "compactFriends.Next", 0, 5);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "compactFriends.Previous", 0, 4);
		ObjectEditor.setPropertyRowColumn(ACompactFriendList.class, "compactFriends.Next", 0, 5);


//		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.ICON, "PL.GIf");
//		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.ICON, "RW.GIf");
//		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.LABEL, "PL.GIf");
//		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.LABEL, "RW.GIf");
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.LABEL, ">");
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.LABEL, "<");
		// overriding what AVectorNavigator has
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.LABEL_WIDTH, 100);

		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.COMPONENT_WIDTH, 25);
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.COMPONENT_WIDTH, 25);
		// this was not commented
//		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.*", AttributeNames.COMPONENT_HEIGHT, 20);
//		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.*", AttributeNames.COMPONENT_HEIGHT, 20);


		ObjectEditor.setPropertyRow(ACompactFriendList.class, "CompactFriends", 1);
		//ObjectEditor.setAttribute(ACompactFriendList.class, AttributeNames.SHOW_UNBOUND_BUTTONS, true);
		ObjectEditor.setAttribute(ACompactFriendList.class, AttributeNames.STRETCH_COLUMNS, false);

		//ObjectEditor.setMethodAttribute(ACompactFriendList.class, "*", AttributeNames.SHOW_BUTTON, true);
//		ObjectEditor.setAttribute(ACompactFriendList.class, AttributeNames.SHOW_BUTTON, true);
//		ObjectEditor.setMethodAttribute(ACompactFriendList.class, "EditList", AttributeNames.ROW, 0);
//		ObjectEditor.setMethodAttribute(ACompactFriendList.class, "EditList", AttributeNames.COLUMN, 0);
		ObjectEditor.setMethodRowColumn(ACompactFriendList.class, "EditList", 0, 2);
		ObjectEditor.setMethodRowColumn(ACompactFriendList.class, "DeleteList", 0, 1);
		ObjectEditor.setMethodRowColumn(ACompactFriendList.class, "CreateNewList", 0, 0);
		//ObjectEditor.setPropertyAttribute(AVectorNavigator.class, "Next", AttributeNames.CLASS_VIEW_GROUP, "ACompactFriendList");
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Next", AttributeNames.CLASS_VIEW_GROUP, ACompactFriendList.class.getName());
		ObjectEditor.setPropertyAttribute(ACompactFriendList.class, "compactFriends.Previous", AttributeNames.CLASS_VIEW_GROUP, ACompactFriendList.class.getName());

		//ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.Next", AttributeNames.CLASS_VIEW_GROUP, AFriendList.class.getName());
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.Next", AttributeNames.POSITION, 4);
//		ObjectEditor.setPropertyAttribute(AFriendList.class, "friends.Next", AttributeNames.COLUMN, -1);
		
		
		return null;
	}

}
