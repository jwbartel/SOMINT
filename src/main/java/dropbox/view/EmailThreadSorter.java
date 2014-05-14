package dropbox.view;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.JProgressBar;

import reader.threadfinder.EmailThread;
import bus.tools.Logger;
import dropbox.converter.EmailToDropboxTranslator;

public class EmailThreadSorter implements Runnable{

	EmailToDropboxTranslator translator;
	ConversionView view;
	JProgressBar progressBar;
	ArrayList<Message> messages;
	
	public EmailThreadSorter(EmailToDropboxTranslator translator, ConversionView view, JProgressBar progressBar, ArrayList<Message> messages){
		this.translator = translator;
		this.view = view;
		this.progressBar = progressBar;
		this.messages = messages;
	}
	
	public void run(){
		try{
			translator.clearThreads();

			Logger.log("Loading messages...");
			for(int i=0; i<messages.size(); i++){
				view.progressMessage("Loading messages ("+(i+1)+" of "+messages.size()+")...");
				translator.addMessage((MimeMessage) messages.get(i));
				progressBar.setValue(i);
			}
			Logger.logln("found "+translator.getMessageCount()+" messages");
			view.progressMessage("Sorting messages into threads");
			Logger.log("Sorting messages into threads...");
			TreeMap<String, EmailThread> threads = translator.sortThreads();
			view.progressMessage("Sorting complete");
			Logger.logln("Found "+threads.size()+" threads");
		} catch (MessagingException e) {
			Logger.logln("Error: "+e.toString());
		}
	}

}
