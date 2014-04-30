package dropbox.converter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.Connector;
import reader.ImapConnector;
import reader.MailReader;
import reader.SummarizedMessage;
import reader.threadfinder.EmailThread;

public class EmailToDropboxTranslator {

	String host, email, password;
	Connector connector;
	MailReader reader;

	TreeSet<SummarizedMessage> summarizedMessages = new TreeSet<SummarizedMessage>();
	TreeMap<String, EmailThread> threads = new TreeMap<String, EmailThread>();
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public void connect() throws ImapException, MessagingException{
		if(host == null || email == null || password == null){
			throw new ImapException("Must specify a host, email, and password to connect");
		}
		connector = new ImapConnector(host, email, password);
		reader = new MailReader(connector);
	}
	
	public Folder[] getFolders() throws MessagingException{
		if(reader == null) return null;
		
		return reader.getFolders();
	}
	
	public Folder[] getFolders(ArrayList<String> folderNames) throws MessagingException{
		if(reader == null) return null;
		
		return reader.getFolders(folderNames);
	}
	
	public ArrayList<Message> getMessages(Folder[] folders) throws MessagingException{
		
		ArrayList<Message> messages = new ArrayList<Message>();
		for(Folder folder: folders){
			Message[] folderMessages = reader.getMessages(folder);
			for(Message message: folderMessages){
				messages.add(message);
			}
		}
		return messages;
	}
	
	public void clearThreads(){
		summarizedMessages.clear();
		threads.clear();
	}
	
	public void addMessage(MimeMessage message) throws MessagingException{
		summarizedMessages.add(new SummarizedMessage(message));
	}
	
	public int getMessageCount(){
		return summarizedMessages.size();
	}
	
	public TreeMap<String, EmailThread> sortThreads(){
		for(SummarizedMessage message: summarizedMessages){
			String cleanedSubject;
			try {
				cleanedSubject = message.getBaseSubject();
				
				EmailThread thread = threads.get(cleanedSubject);
				if(thread == null){
					thread = new EmailThread();
					threads.put(cleanedSubject, thread);
				}
				
				thread.addElement(message.getMessage());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		for(EmailThread thread: threads.values()){
			thread.clean();
		}
		return threads;
	}
	
}
