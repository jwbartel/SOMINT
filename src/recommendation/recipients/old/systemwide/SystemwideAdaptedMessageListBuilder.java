package recommendation.recipients.old.systemwide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.MessagingException;

import bus.accounts.Account;
import bus.tools.AdaptedMessageListBuilder;

public class SystemwideAdaptedMessageListBuilder extends AdaptedMessageListBuilder {
	
	public static final String src_file_name = Account.ALL_MSGS_ADAPTED;
	public static final String dest_file_name = Account.ALL_MSGS_ADAPTED;

	public static void buildSystemwideAdaptedMessageList(String accountsFolder, String dest) throws IOException, MessagingException{
		buildSystemwideAdaptedMessageList(new File(accountsFolder), new File(dest));
	}
	
	static String[] comparisonDates = {"Sun, 30 Nov 2002 00:00:00 -0800 (PST)", "Tue, 1 Jan 1980 00:00:00 -0800 (PST)"};

	public static void buildSystemwideAdaptedMessageList(File accountsFolder, File dest) throws IOException, MessagingException{

		Map<Date, ArrayList<AdaptedMessage>> msgs = new TreeMap<Date, ArrayList<AdaptedMessage>>();
		int size = 0;
		
		File[] accounts = accountsFolder.listFiles();
		Arrays.sort(accounts);
		for(int a=0; a<accounts.length ;a++){
			if(accounts[a].isDirectory()){
				
				File src = new File(accounts[a], src_file_name);
				System.out.println(accounts[a].getName());
				
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
					
					
					if(date == null || msg.isInFolder("calendar") || msg.isInFolder("contacts") || msg.isCalendarEntry()){
						line = in.readLine();
						continue;
					}

					SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z (z)");
					
					boolean shouldContinue = false;
					for(int i=0; i<comparisonDates.length; i++){

						try {
							Date comparisonDate = format.parse(comparisonDates[i]);
							if(date.equals(comparisonDate)){
								line = in.readLine();
								shouldContinue = true;
								break;
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}	
					if(shouldContinue) continue;
					
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
				
			}
		}
		
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
		//String accountsFolder = "/home/bartizzi/Research/Enron Accounts";
		//String dest = "/home/bartizzi/Research/Enron Accounts/"+dest_file_name;
		
		String accountsFolder = "/home/bartizzi/Shared/Dropbox/Sample Enron Accounts";
		String dest = "/home/bartizzi/Shared/Dropbox/Sample Enron Accounts/"+dest_file_name;
		
		buildSystemwideAdaptedMessageList(accountsFolder, dest);
	}
}
