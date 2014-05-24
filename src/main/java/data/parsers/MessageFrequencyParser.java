package data.parsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


public class MessageFrequencyParser extends FrequencyParser {


	String contents;
	public MessageFrequencyParser(String fileName) throws IOException{
		super(fileName);
	}
	
	public MessageFrequencyParser(File file) throws IOException {
		super(file);
	}
	
	protected void buildFreqList() throws IOException{
		
		File wordCounts = new File(file.getAbsolutePath()+wordcountFileSuffix);
//		if(!wordCounts.exists()){
			try{
				getMessageContents();
				parse(contents);
//				saveToFile(wordCounts);
			}catch(MessagingException e){
				e.printStackTrace();
			}
//		}else{
//			loadFromFile(wordCounts);
//		}
	}
	
	
	private void getMessageContents() throws IOException, MessagingException{
		StringBuffer msgBuff = null;
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		while(line != null){
			if(msgBuff == null){
				msgBuff = new StringBuffer(line);
			}else{
				msgBuff.append('\n');
				msgBuff.append(line);
			}
			line = in.readLine();
		}
		in.close();
		
		String msg = msgBuff.toString();
		Session session = Session.getDefaultInstance(System.getProperties());
		
		MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(msg.getBytes()));
		extractContents(message);
		in.close();
	}
	
	private void extractContents(Part part) throws MessagingException, IOException{
		if(part.isMimeType("multipart/*")){
			Multipart multi = (Multipart) part.getContent();
			for(int i=0; i<multi.getCount(); i++){
				Part subpart = multi.getBodyPart(i);
				extractContents(subpart);
			}
		}else if(part.isMimeType("text/*")){
			
			if(contents == null){
				
				contents = part.getContent().toString();
				
			}else{
				
				if(part.isMimeType("text/plain")){
					contents = part.getContent().toString();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		MessageFrequencyParser.loadData();
		
		//File folder = new File("");
		
		//MessageFrequencyParser parser = new MessageFrequencyParser("/home/bartizzi/Research/Enron Accounts/allen-p/PALLEN (Non-Privileged)/Allen, Phillip K/Inbox/18");
	}

}
