package reader;

import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MailReader {

	Connector connector;
	
	public MailReader(Connector connector){
		this.connector = connector;
	}
	
	
	public Folder[] getFolders() throws MessagingException{
		return connector.getStore().getDefaultFolder().list();
	}
	
	public Folder getFolder(String folderName) throws MessagingException{
		return connector.getStore().getFolder(folderName);
	}
	
	public Folder[] getFolders(ArrayList<String> folderNames) throws MessagingException{
		Folder[] folders = new Folder[folderNames.size()];
		for(int i=0; i<folderNames.size(); i++){
			folders[i] = getFolder(folderNames.get(i));
		}
		return folders;
	}
	
	public Message[] getMessages(String folderName) throws MessagingException{
		return getMessages(getFolder(folderName));
	}
	
	public Message[] getMessages(Folder folder) throws MessagingException{
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.getMessages();
		return messages;
	}
}
