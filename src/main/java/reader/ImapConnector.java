package reader;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class ImapConnector implements Connector {
	
	public static final int DEFAULT_PORT = 993;
	
	Store store;
	
	public ImapConnector(String host, String address, String password) throws MessagingException{
		init(host, DEFAULT_PORT, address, password);
	}
	
	public ImapConnector(String host, int port, String address, String password) throws MessagingException{
		init(host, port, address, password);
	}
	
	private void init(String host, int port, String address, String password) throws MessagingException{
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		
		Session session = Session.getDefaultInstance(props, null);
		store = session.getStore("imaps");
		store.connect(host, port, address, password);
	}
	
	public Store getStore(){
		return store;
	}

}
