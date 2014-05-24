package dropbox.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import reader.ImapConnector;
import reader.MailReader;
import util.tools.Logger;

public class LoginListener implements ActionListener{
	
	ConversionView view;
	JTextField host, address;
	JPasswordField password;
	

	ImapConnector connector;

	public LoginListener(ConversionView view, JTextField host, JTextField address, JPasswordField password){
		this.view = view;
		this.host = host;
		this.address = address;
		this.password = password;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		Thread thread = new Thread(){
			public void run(){
				
				String hostVal = host.getText();
				String addressVal = address.getText();
				String passwordVal = password.getText();
				
				if(hostVal.equals("") || addressVal.equals("") || passwordVal.equals("")){
					view.errorMessage("Please specify all IMAP credentials");
					return;
				}
				
				view.connect(hostVal, addressVal, passwordVal);

				/*view.shouldAllowTranslation(false);
				Logger.log("Logging in...");
				try {
					connector = new ImapConnector(hostVal, addressVal, passwordVal);
				} catch (MessagingException e) {
					Logger.logln("Failed: "+e.getMessage());
					return;
				}
				Logger.logln("Complete.");
				
				Logger.log("Retrieving folders...");
				MailReader reader = new MailReader(connector);
				try {
					Folder[] folders = reader.getFolders();
					Logger.logln("Found "+folders.length+" folders.");
					view.showFolders(folders);
				} catch (MessagingException e) {
					Logger.logln("Failed: "+e.getMessage());
					e.printStackTrace();
				}*/
				
			}
		};
		
		thread.start();
	}

}
