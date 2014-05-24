package data.structures;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import bus.accounts.Account;

public class MessageList {
	
	static Map<Date,ArrayList<String>> seenMessages = new HashMap<Date, ArrayList<String>>();
	

	static ArrayList<String> contents;
	static Date date = null;
	private static void getContents(File messageFile) throws IOException, MessagingException{
		contents = new ArrayList<String>();
		date = null;
		StringBuffer msgBuff = null;
		BufferedReader in = new BufferedReader(new FileReader(messageFile));
		String line = in.readLine();
		
		while(line !=null){
			if(msgBuff == null){
				msgBuff = new StringBuffer(line);
			}else{
				msgBuff.append("\n"+line);
			}
			line = in.readLine();
		}
		in.close();
		
		String msg = msgBuff.toString();
		Session session = Session.getDefaultInstance(System.getProperties());
		
		MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(msg.getBytes()));

		
		String[] from = message.getHeader("from");
		if(from != null){
			contents.add(from[0]);
		}else{
			contents.add(null);
		}
		
		String[] to = message.getHeader("to");
		if(to != null){
			contents.add(to[0]);
		}else{
			contents.add(null);
		}
		
		String[] cc = message.getHeader("cc");
		if(cc != null){
			contents.add(cc[0]);
		}else{
			contents.add(null);
		}
		
		String[] bcc = message.getHeader("bcc");
		if(bcc != null){
			contents.add(bcc[0]);
		}else{
			contents.add(null);
		}
		
		String[] subject = message.getHeader("subject");
		if(subject != null){
			contents.add(subject[0]);
		}else{
			contents.add(null);
		}
		
		date = message.getSentDate();
		if(date == null){
			date = message.getReceivedDate();
		}
		
		
		traverseMessage(message);
		//System.out.println(date);
	}
	
	public static String getRepeat(String message) throws IOException, MessagingException{
		return getRepeat(new File(message));
	}
	
	public static String getRepeat(File message) throws IOException, MessagingException{
		try{
			getContents(message);
		}catch(MessagingException e){
			String errorMsg = e.getMessage();
			if(errorMsg.equals("Missing start boundary")){
				return "";
			}
			throw e;
		}
		ArrayList<String> messageList = seenMessages.get(date);
		if(messageList == null){
			messageList = new ArrayList<String>();
			messageList.add(message.getAbsolutePath());
			seenMessages.put(date, messageList);
			return null;
		}else{
			ArrayList<String> currContents = contents;
			//Date currDate = date;
			for(int i=0; i<messageList.size(); i++){
				getContents(new File(messageList.get(i)));
				if(contents.size() != currContents.size()){
					continue;
				}
				boolean foundFalse = false;
				for(int j=0; j<contents.size(); j++){
					if(contents.get(j)==null){
						if(currContents.get(j)!=null){
							foundFalse = true;
							break;
						}
					}else if(!contents.get(j).equals(currContents.get(j))){
						foundFalse = true;
						break;
					}
				}
				if(!foundFalse){
					return messageList.get(i);
				}
			}
			return null;
		}
		
	}
	
	public static Set<String> getRepeats(String account) throws IOException, MessagingException{
		return getRepeats(new File(account));
	}
	
	public static Set<String> getRepeats(File account) throws IOException, MessagingException{
		Set<String> toReturn = new TreeSet<String>();

		BufferedReader in = new BufferedReader(new FileReader(new File(account, Account.ALL_MSGS)));
		String line = in.readLine();
		line = in.readLine();

		while(line != null){
			if(line.length()>0 && line.charAt(0)!='\t'){
				String repeatedFile = getRepeat(line);
				if(repeatedFile!=null){
					toReturn.add(line);
				}
			}
			line = in.readLine();
		}
		
		return toReturn;
	}
	
	private static void traverseMessage(Part part) throws MessagingException, IOException{
		if(part.isMimeType("text/*")){
			
			contents.add(part.getContent().toString());
		}
		
		if(part.isMimeType("multipart/*")){
			Multipart multi = (Multipart) part.getContent();
			for(int i=0; i<multi.getCount(); i++){
				Part subpart = multi.getBodyPart(i);
				traverseMessage(subpart);
			}
			
		}
	}
	
	public static void main(String[] args) throws IOException, MessagingException{

		File file = new File("/home/bartizzi/Research/Enron Accounts/bailey-s");
		
		System.out.println(getRepeats(file).size());
		
		/*BufferedReader in = new BufferedReader(new FileReader(new File(file, MessageGroupedAccount.FULL_MSG_LIST)));
		String line = in.readLine();
		line = in.readLine();
		int count = 0;
		while(line != null){
			if(count == 32){
				int x = 1;
			}
			if(line.length()>0 && line.charAt(0)!='\t'){
				String repeatedFile = getRepeat(line);
				if(repeatedFile!=null){
					System.out.println(line+"\t"+repeatedFile);
				}
			}
			line = in.readLine();
			count++;
		}*/
		
		//File file = new File("D:\\Enron Files\\bailey-s\\Susan_Bailey_June2001\\Notes Folders\\Notes inbox\\4");
		//File file = new File("D:\\Enron Files\\bailey-s\\Susan_Bailey_June2001\\Notes Folders\\All documents\\602");
		//getContents(file);
		/*StringBuffer msgBuff = null;
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		
		while(line !=null){
			if(msgBuff == null){
				msgBuff = new StringBuffer(line);
			}else{
				msgBuff.append("\n"+line);
			}
			line = in.readLine();
		}
		
		String msg = msgBuff.toString();
		Session session = Session.getDefaultInstance(System.getProperties());
		
		MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(msg.getBytes()));
		traverseMessage(message);
		System.out.println(contents);*/
	}
}
