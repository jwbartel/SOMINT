package kelli.friends;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AFriendListNameAR implements ExecutableCommand {
	public Object execute(Object theFrame) {
		ObjectEditor.setAttribute(AFriendListName.class,  AttributeNames.LAYOUT, AttributeNames.FLOW_LAYOUT);
		ObjectEditor.setAttribute(AFriendListName.class,  AttributeNames.ALIGNMENT, FlowLayout.LEFT);
		ObjectEditor.setPropertyAttribute(AFriendListName.class, "name", AttributeNames.COLUMN, 0);
		ObjectEditor.setPropertyAttribute(AFriendListName.class, "name", AttributeNames.PROMPT, "Enter a Name");

		ObjectEditor.setPropertyAttribute(AFriendListName.class, "name", AttributeNames.COMPONENT_HEIGHT, 22);

		ObjectEditor.setMethodAttribute(AFriendListName.class, "editName", AttributeNames.COLUMN, 1);
		ObjectEditor.setMethodAttribute(AFriendListName.class, "editName", AttributeNames.COMPONENT_HEIGHT, 22);

		ObjectEditor.setMethodAttribute(AFriendListName.class, "editName", AttributeNames.COMPONENT_FOREGROUND, Color.BLUE);

		ObjectEditor.setLabelled(AFriendListName.class, false);
		ObjectEditor.setHorizontal(AFriendListName.class);
		ObjectEditor.setAttribute(AFriendListName.class, AttributeNames.UNBOUND_BUTTONS_PLACEMENT, BorderLayout.EAST);

		ObjectEditor.setAttribute(AFriendListName.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );
		//ObjectEditor.setAttribute(AFriendListName.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );

		ObjectEditor.setAttribute(AFriendListName.class, AttributeNames.SHOW_UNBOUND_BUTTONS, new Boolean(true) );
		  return null;
	}

}
