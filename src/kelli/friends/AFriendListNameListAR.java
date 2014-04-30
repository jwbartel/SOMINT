package kelli.friends;

import java.awt.Color;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AFriendListNameListAR implements ExecutableCommand {
	public Object execute(Object theFrame) {
		ObjectEditor.setPropertyAttribute(AFriendListNameList.class, "element", AttributeNames.COMPONENT_WIDTH, 230);
		ObjectEditor.setPropertyAttribute(AFriendListNameList.class, "element", AttributeNames.LABELLED, false);
		ObjectEditor.setPropertyAttribute(AFriendListNameList.class, "element", AttributeNames.COMPONENT_FOREGROUND, Color.BLUE);
		ObjectEditor.setAttribute(AFriendListNameList.class, AttributeNames.VERTICAL_GAP, 5);
		ObjectEditor.setAttribute(AFriendListNameList.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );
		ObjectEditor.setMethodAttribute(AFriendListNameList.class, "create", AttributeNames.SHOW_BUTTON, new Boolean(true) );
		ObjectEditor.setMethodAttribute(AFriendListNameList.class, "create", AttributeNames.ROW, 1 );
		//ObjectEditor.setMethodAttribute(AFriendListNameList.class, "create", AttributeNames.COLUMN, 0 );
		ObjectEditor.setMethodAttribute(AFriendListNameList.class, "join", AttributeNames.ROW, 2 );
		//ObjectEditor.setMethodAttribute(AFriendListNameList.class, "join", AttributeNames.COLUMN, 1 );
		ObjectEditor.setMethodAttribute(AFriendListNameList.class, "create", AttributeNames.LABEL_WIDTH, 0);

		ObjectEditor.setMethodAttribute(AFriendListNameList.class, "create", AttributeNames.LABEL_LEFT, "+" );

		
		  return null;
	}

}
