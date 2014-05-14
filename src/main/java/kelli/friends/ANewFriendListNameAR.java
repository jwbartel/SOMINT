package kelli.friends;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

public class ANewFriendListNameAR extends AFriendListNameAR implements ExecutableCommand {
	public Object execute(Object theFrame) {
		


		
		ObjectEditor.setMethodAttribute(ANewFriendListName.class, "editName", AttributeNames.VISIBLE, false);
		
		  return null;
	}

}
