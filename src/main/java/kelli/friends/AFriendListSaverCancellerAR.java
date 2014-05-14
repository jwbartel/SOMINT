package kelli.friends;

import java.awt.Color;
import java.awt.FlowLayout;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class AFriendListSaverCancellerAR implements ExecutableCommand {
	public Object execute(Object theFrame) {
		
		ObjectEditor.setLabelled(AFriendListSaverCanceller.class, false);
		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Save", AttributeNames.LABEL, "Save List");
		ObjectEditor.setAttribute(AFriendListSaverCanceller.class,  AttributeNames.STRETCHABLE_BY_PARENT, true);

		ObjectEditor.setAttribute(AFriendListSaverCanceller.class,  AttributeNames.LAYOUT, AttributeNames.FLOW_LAYOUT);
		ObjectEditor.setAttribute(AFriendListSaverCanceller.class,  AttributeNames.ALIGNMENT, FlowLayout.RIGHT);

		//ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Save", AttributeNames.LABEL_LEFT, "");
		//ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Save", AttributeNames.LABEL_WIDTH, 550);
		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Save", AttributeNames.COLUMN, 0);
		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Cancel", AttributeNames.COLUMN, 1);

		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Save", AttributeNames.COMPONENT_BACKGROUND, Color.BLUE);
		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "Save", AttributeNames.COMPONENT_FOREGROUND, Color.WHITE);

		ObjectEditor.setAttribute(AFriendListSaverCanceller.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );
		ObjectEditor.setMethodAttribute(AFriendListSaverCanceller.class, "cancel", AttributeNames.SHOW_BUTTON, new Boolean(true) );

		ObjectEditor.setAttribute(AFriendListSaverCanceller.class, AttributeNames.SHOW_UNBOUND_BUTTONS, new Boolean(true) );
		  return null;
	}

}
