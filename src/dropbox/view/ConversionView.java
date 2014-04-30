package dropbox.view;

import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;

public interface ConversionView {

	public void connect(String host, String email, String password);
	
	public void showFolders(Folder[] folders);
	public Folder[] getSelectedFolders();
	
	public void errorMessage(String message);
	public void progressMessage(String message);
	
	public void displayTranslation(ArrayList<Message> messages);
	
	public void shouldAllowTranslation(boolean allowed);
	
}
