package bus.tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import bus.accounts.Account;


public class AdaptedMessageListBuilder {
	public static class AdaptedMessage{
		public String msgLoc;
		public AdaptedMessage(String loc) throws MessagingException, IOException{
			msgLoc = loc;
			getComparators();
		}
		
		Date date;
		public Date getDate() throws IOException, MessagingException{
			if(date != null){
				return date;
			}else{
				date = Account.getMessageDate(msgLoc);
				return date;
			}
		}

		boolean foundComparators = false;
		String[] from;
		String[] to;
		String[] cc;
		String[] bcc;
		String subject;
		String contents;
		private void getComparators() throws MessagingException, IOException{
			if(foundComparators){
				return;
			}
			
			getDate();
			Message msg = Account.getMessage(new File(msgLoc));
			from = msg.getHeader("from");
			to = msg.getHeader("to");
			cc = msg.getHeader("cc");
			bcc = msg.getHeader("bcc");
			subject = msg.getSubject();
			extractContents(msg);
			foundComparators = true;
		}
		
		public boolean equals(AdaptedMessage msg) throws MessagingException, IOException{
			
			if(date.equals(msg.date)){
				if(compareRecipients(from, msg.from) && compareRecipients(to, msg.to) && compareRecipients(cc, msg.cc) && compareRecipients(bcc, msg.bcc)){
					if(subject.equals(msg.subject)){
						if(contents == null || contents.equals(msg.contents)){
							return true;
						}
					}
				}
			}
			return false;
			
		}
		
		
		private boolean compareRecipients(String[] recipients1, String[] recipients2){
			if( (recipients1==null && recipients2 != null) || (recipients1!=null && recipients2 == null) ){
				return false;
			}
			
			if( recipients1!=null && recipients2 != null){
				return recipients1[0].equals(recipients2[0]);
			}
			
			return true;
		}
		
		public boolean wasSent(){
			boolean sent = isInFolder("sent items") || isInFolder("'sent mail") || isInFolder("sent") || isInFolder("'sent");
			boolean draft = isInFolder("drafts");
			
			return sent || draft;
		}
		
		public boolean isInFolder(String folderName){
			File loc = new File(msgLoc);
			File parent = loc.getParentFile();
			while(parent != null){
				if(parent.getName().equalsIgnoreCase(folderName)){
					return true;
				}
				parent = parent.getParentFile();
			}
			return false;
		}
		
		public boolean isOrganizerNote(){
			return subject.startsWith("Do not delete Organizer note-");
		}
		
		public boolean isCalendarEntry(){
			return subject != null && to == null && cc == null && bcc == null && from != null;
		}
		
		
		private void extractContents(Part part) throws MessagingException, IOException{
			try{
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
			}catch(MessagingException e){
				String eMsg = e.getMessage();
				if(!eMsg.equals("Missing start boundary"))
					throw e;
			}
		}
		
		public ArrayList<String> attachments;
		public void addAttachment(String attachmentLoc){
			if(attachments == null){
				attachments = new ArrayList<String>();
			}
			attachments.add(attachmentLoc);
		}
	}
	
	
	
	public static void buildAdaptedMessageList(String src, String dest) throws IOException, MessagingException{
		
		Map<Date, ArrayList<AdaptedMessage>> msgs = new TreeMap<Date, ArrayList<AdaptedMessage>>();
		int size = 0;
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		in.readLine();
		String line = in.readLine();
		AdaptedMessage msg = null;
		while(line != null){
			if(line.charAt(0)=='\t'){
				msg.addAttachment(line.substring(1));
				line = in.readLine();
				continue;
			}
			
			msg = new AdaptedMessage(line);
			Date date = msg.getDate();
			if(date == null && !(msg.isInFolder("calendar"))){
				throw new RuntimeException(line);
			}
			
			
			if(date == null || msg.isInFolder("calendar") || msg.isInFolder("contacts") || msg.isInFolder("drafts") || msg.isCalendarEntry()){
				line = in.readLine();
				continue;
			}
			
			ArrayList<AdaptedMessage> msgsAtDate = msgs.get(date);
			if(msgsAtDate == null){
				msgsAtDate = new ArrayList<AdaptedMessage>();
				msgsAtDate.add(msg);
				msgs.put(date, msgsAtDate);
				size++;
			}else{
				boolean shouldAdd = true;
				for(int i=0; i<msgsAtDate.size(); i++){
					boolean removeAndAdd = false;
					AdaptedMessage toCompare = msgsAtDate.get(i);
					if(msg.equals(toCompare)){
						if(!msg.isInFolder("all documents")){
							if(msg.isInFolder("discussion threads")){
								if(toCompare.isInFolder("all documents")){
									removeAndAdd = true;
								}
							}else{
								if(toCompare.isInFolder("all documents") || toCompare.isInFolder("discussion threads")){
									removeAndAdd = true;
								}else if(msg.isInFolder("sent") && toCompare.isInFolder("'sent")){
									removeAndAdd = true;
								}else if(msg.isInFolder("'sent") && toCompare.isInFolder("sent")){
									shouldAdd = false;
									break;
								}else{
									shouldAdd = false;
									break;
									//throw new RuntimeException("Files: "+msg.msgLoc +" and "+toCompare.msgLoc);
								}
							}
						}
						
						if(removeAndAdd){
							msgsAtDate.remove(i);
							msgsAtDate.add(i, msg);
						}
						shouldAdd = false;
						break;
					}
				}
				if(shouldAdd){
					msgsAtDate.add(msg);
					size++;
				}
			}
			
			line = in.readLine();
		}
		in.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		out.write(""+size);
		out.newLine();
		Iterator<Date> dates = msgs.keySet().iterator();
		while(dates.hasNext()){
			Date date = dates.next();
			ArrayList<AdaptedMessage> msgsAtDate = msgs.get(date);
			for(int i=0; i<msgsAtDate.size(); i++){
				AdaptedMessage currMsg = msgsAtDate.get(i);
				out.write(currMsg.msgLoc);
				out.newLine();
				if(currMsg.attachments != null){
					for(int j=0; j<currMsg.attachments.size(); j++){
						out.write("\t"+currMsg.attachments.get(j));
						out.newLine();
					}
				}
			}
		}
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws IOException, MessagingException{
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = false;
		for(int i=0; i<accounts.length; i++){

			
			if(accounts[i].getName().equals("keavey-p")){
				start = true;
			}
			
			if(!start){
				continue;
			}
			
			
			
			System.out.println(accounts[i].getName()+"...");
			File msgFile = new File(accounts[i], "ALL_MESSAGES.TXT");
			File toCreate = new File(accounts[i], "ALL_MESSAGES_ADAPTED.TXT");
			
			/*if(toCreate.exists()){
				BufferedReader in = new BufferedReader(new FileReader(toCreate));
				String line = in.readLine();
				in.close();
				
				System.out.println(accounts[i].getName()+","+line);
			}*/
			
			buildAdaptedMessageList(msgFile.getAbsolutePath(), toCreate.getAbsolutePath());
		}
		
		//buildAdaptedMessageList("/home/bartizzi/Research/Enron Accounts/lay-k/ALL_MESSAGES.TXT", "/home/bartizzi/Research/Enron Accounts/lay-k/ALL_MESSAGES_ADAPTED.TXT");
		
		
		//AdaptedMessage msg = new AdaptedMessage("/home/bartizzi/Research/Enron Accounts/lay-k/Kenneth_Lay_Dec2000/Notes Folders/Discussion threads/63");
		//System.out.println(msg.isCalendarEntry());
	}
	
}
