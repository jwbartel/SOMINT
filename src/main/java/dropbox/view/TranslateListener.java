package dropbox.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import bus.tools.Logger;
import dropbox.converter.EmailToDropboxTranslator;

public class TranslateListener implements ActionListener {

	ConversionView view;
	EmailToDropboxTranslator translator;
	
	public TranslateListener(ConversionView view, EmailToDropboxTranslator translator){
		this.view = view;
		this.translator = translator;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Folder[] folders = view.getSelectedFolders();
		try {
			ArrayList<Message> messages = translator.getMessages(folders);
			view.displayTranslation(messages);
		} catch (MessagingException e) {
			Logger.log("Error: "+e.getMessage());
		}
	}

}
