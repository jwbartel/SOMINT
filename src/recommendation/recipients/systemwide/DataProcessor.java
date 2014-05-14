package recommendation.recipients.systemwide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import bus.accounts.Account;
import bus.data.structures.AddressLists;
import bus.data.structures.DirectedEmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.tools.AdaptedMessageListBuilder.AdaptedMessage;

public class DataProcessor {

	public static final String list_file_name = Account.ALL_MSGS_ADAPTED;

	public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	public void countMsgs(String msgList) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		int count = 0;
		
		String line = in.readLine();
		line = in.readLine();
		while(line != null){
			if(line.charAt(0)!= '\t'){
				count++;
			}
			line = in.readLine();
		}
		
		System.out.println(""+count);
	}
	
	protected Date getMessageDate(File msgFile) throws IOException, MessagingException{
		StringBuffer msgBuffer = null;
		BufferedReader in = new BufferedReader(new FileReader(msgFile));
		String line = in.readLine();
		while(line != null){
			
			if(msgBuffer == null){
				msgBuffer = new StringBuffer(line);
			}else{
				msgBuffer.append("\n");
				msgBuffer.append(line);
			}
			
			line = in.readLine();
		}
		in.close();
		
		String msg = msgBuffer.toString();
		Session session = Session.getDefaultInstance(System.getProperties());
		
		MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(msg.getBytes()));
		
		Date msgDate = message.getSentDate();
		if(msgDate == null){
			msgDate = message.getReceivedDate();
		}
		
		return msgDate;
		
	}
	
	public static void buildGoogleGroupsList(String msgList, String fileDest) throws IOException, MessagingException{
		buildGoogleGroupsList(new File(msgList), new File(fileDest));
	}
	
	public static void buildGoogleGroupsList(File msgList, File fileDest) throws IOException, MessagingException{
		GoogleGroupTracker groupTracker;
		
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		String line = in.readLine();
		int numMsgs = Integer.parseInt(line);
		groupTracker = new GoogleGroupTracker(numMsgs);
		
		line = in.readLine();
		int msgCount = 0;
		while(line!= null && msgCount < ((double) numMsgs) * Account.TRAINING_RATIO){
			String currMessage = line;
			
			AdaptedMessage adptMsg = new AdaptedMessage(currMessage);
			
			Date date = adptMsg.getDate();
			boolean sent = adptMsg.wasSent();
			
			File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				Account.saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			Set<String> addresses = addressLists.getAll();
			
			groupTracker.foundMsgAddresses(addresses, currMessage, date, !sent);
			
			msgCount++;
			//System.out.println(msgCount);
			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
			
			
		}
		
		groupTracker.save(fileDest);
		System.out.println("saved groups");
	}
	
	public static void buildIndividualmsgList(String msgList, String dest) throws IOException, MessagingException{
		buildIndividualMsgList(new File(msgList), new File(dest));
	}
	
	public static void buildIndividualMsgList(File msgList, File dest) throws IOException, MessagingException{
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		Map<String, ArrayList<DirectedEmailInteraction>> individualInteractions = new TreeMap<String, ArrayList<DirectedEmailInteraction>>();
		
		String line = in.readLine();
		int numMsgs = Integer.parseInt(line);
		
		line = in.readLine();
		int msgCount = 0;
		while(line!= null && msgCount < ((double) numMsgs) * Account.TRAINING_RATIO){
			//System.out.println(msgCount);
			String currMessage = line;
			
			AdaptedMessage adptMsg = new AdaptedMessage(currMessage);
			
			Date date = adptMsg.getDate();
			boolean sent = adptMsg.wasSent();
			
			File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				Account.saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			Set<String> addresses = addressLists.getAll();
			
			Iterator<String> addressesIter = addresses.iterator();
			while(addressesIter.hasNext()){
				String address = addressesIter.next();
				
				ArrayList<DirectedEmailInteraction> interactions = individualInteractions.get(address);
				if(interactions == null){
					interactions = new ArrayList<DirectedEmailInteraction>();
					individualInteractions.put(address, interactions);
				}
				
				interactions.add(new DirectedEmailInteraction(currMessage, date, !sent));
			}
			
			msgCount++;
			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
			
		}
		
		in.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		Iterator<String> addresses = individualInteractions.keySet().iterator();
		while(addresses.hasNext()){
			String address = addresses.next();
			ArrayList<DirectedEmailInteraction> interactions = individualInteractions.get(address);

			for(int i=0; i<interactions.size(); i++){
				DirectedEmailInteraction interaction = interactions.get(i);
				out.write(""+interaction.wasReceived()+"\t"+dateFormat.format(interaction.getDate())+"\t"+interaction.getEmailLocation());
				out.newLine();
			}
			out.write("\t"+address);
			out.newLine();
		}
		
		out.flush();
		out.close();
		System.out.println("saved individuals");
	}
	
	public static void buildAddressLists(String account) throws IOException{
		buildAddressLists(new File(account));
	}
	
	public static void buildAddressLists(File account) throws IOException{
		File msgList = new File(account, list_file_name);
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		String line = in.readLine();
		//System.out.println(line);
		line = in.readLine();
		
		int count = 0;
		while(line != null){
			count++;
			File addressFile = new File(line+Account.ADDR_FILE_SUFFIX);
			Account.saveAddresses(new File(line), addressFile);
			//System.out.println(""+count);
			
			line = in.readLine();
			while(line != null && line.charAt(0)=='\t'){
				line = in.readLine();
			}
		}
		
		in.close();
		System.out.println("saved addresses");
	}
	
	private static class RecipientMsgLink implements Comparable<RecipientMsgLink>{
		String recipient;
		String msg;
		
		public RecipientMsgLink(String recipient, String msg){
			this.recipient = recipient;
			this.msg = msg;
		}
		
		@Override
		public int compareTo(RecipientMsgLink arg0) {
			return -1 * recipient.compareTo(arg0.recipient);
		}
		
		@Override
		public String toString(){
			return recipient + "\t" + msg;
		}
		
	}
	
	public static void printAllAddresses(String account) throws IOException{
		printAllAddresses(new File(account));
	}
	
	public static void printAllAddresses(File account) throws IOException{
		File msgList = new File(account, list_file_name);
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		String line = in.readLine();
		//System.out.println(line);
		line = in.readLine();
		
		Set<RecipientMsgLink> recipients = new TreeSet<RecipientMsgLink>();
		int count = 0;
		while(line != null){
			count++;
			if(count < 75000){// || count > 60000 ){
				line = in.readLine();
				while(line != null && line.startsWith("\t")){
					line = in.readLine();
				}
				continue;
			}
			if(count %10 == 0) System.out.println(count);
			String currMessage = line;
			
			File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				Account.saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			Set<String> addresses = addressLists.getAll();
			Iterator<String> addressIter = addresses.iterator();
			while(addressIter.hasNext()){
				String address = addressIter.next();
				recipients.add(new RecipientMsgLink(address, currMessage));
			}

			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
		}
		
		in.close();
		
		Iterator<RecipientMsgLink> recipientsIter = recipients.iterator();
		while(recipientsIter.hasNext()){
			System.out.println(recipientsIter.next());
		}
	}
	
	public static void printAllMisizedFrom(String account) throws IOException{
		printAllMisizedFrom(new File(account));
	}
	
	public static void printAllMisizedFrom(File account) throws IOException{
		File msgList = new File(account, list_file_name);
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		String line = in.readLine();
		//System.out.println(line);
		line = in.readLine();
		while(line != null){
			
			String currMessage = line;
			
			File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				Account.saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			ArrayList<String> from = addressLists.getFrom();
			if(from.size() > 1){
				System.out.println(currMessage);
			}
			
			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
		}
	}
	
	public static void buildDateFiles(String account) throws IOException, MessagingException{
		buildDateFiles(new File(account));
	}
	
	public static void buildDateFiles(File account) throws IOException, MessagingException{
		File msgList = new File(account, list_file_name);
		//System.out.println(msgList.getAbsolutePath());
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		String line = in.readLine();
		//System.out.println(line);
		line = in.readLine();
		while(line != null){
			
			String currMessage = line;
			
			File msgFile = new File(currMessage);
			
			File dateFile = new File(currMessage+Account.DATE_FILE_SUFFIX);
			Account.saveMessageDate(msgFile,  dateFile);
			//Account.getMessageDate(msgFile);
			
			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException, MessagingException{
		String accountsFolder = "D:\\Enron data\\extracted precomputes";//"/home/bartizzi/Research/Enron Accounts";
		//String accountsFolder = "/home/bartizzi/Shared/Dropbox/Sample Enron Accounts";
		
		//buildAddressLists(accountsFolder);
		//printAllAddresses(accountsFolder);
		//printAllMisizedFrom(accountsFolder);
		//buildGoogleGroupsList(new File(accountsFolder, list_file_name), new File(accountsFolder, Account.ADAPTED_GOOGLE_GROUPS_LIST));
		//buildIndividualMsgList(new File(accountsFolder, list_file_name), new File(accountsFolder, Account.INDIVIDUALS_GOOGLE_LIST));
		
		/*File[] accounts = (new File(accountsFolder)).listFiles();
		for(int i=0; i<accounts.length; i++){
			
			if(!accounts[i].isDirectory()) continue;
			
			File adaptedList = new File(accounts[i], Account.ALL_MSGS_ADAPTED);
			BufferedReader in = new BufferedReader(new FileReader(adaptedList));
			String line = in.readLine();
			in.close();
			
			System.out.println(accounts[i].getName() + "," + line);
		}*/
		
		//Account.saveAddresses(new File("/home/bartizzi/Research/Enron Accounts/baughman-d/Edward_Baughman_Jan2002/Baughman Jr., Don/Inbox/108"), new File("/home/bartizzi/Research/Enron Accounts/baughman-d/Edward_Baughman_Jan2002/Baughman Jr., Don/Inbox/108_ADDRESSES.TXT"));		
		//Account.saveAddresses(new File("/home/bartizzi/Research/Enron Accounts/lay-k/KLAY (Non-Privileged)/Lay, Kenneth/Inbox/620"), new File("/home/bartizzi/Research/Enron Accounts/lay-k/KLAY (Non-Privileged)/Lay, Kenneth/Inbox/620_ADDRESSES.TXT"));		
		//Account.saveAddresses(new File("/home/bartizzi/Research/Enron Accounts/shackleton-s/Sara_Shackleton_Dec2000_June2001_2/Notes Folders/Brazil/7"), new File("/home/bartizzi/Research/Enron Accounts/shackleton-s/Sara_Shackleton_Dec2000_June2001_2/Notes Folders/Brazil/7_ADDRESSES.TXT"));		
		
		//System.out.println("bob butts, ibuyit payables executivesponsor@enron".matches(AddressParser.enronSpecificEmailRegex));
		
		/*Matcher matcher = AddressParser.complexEntryPattern.matcher("\"breen, belinda\" <belinda_breen@ajg> <??S\"breen, belinda\" <belinda_breen@ajg>>, com@mailman.enron.com");
		while(matcher.find()){
			System.out.println(matcher.group());
		}*/
		/*String localChars = "[a-zA-Z0-9!#$%&*+/=?^_`{|}~-]+";
		String localName = localChars+"([.]"+localChars+")*";
		String domainChars = "[a-zA-Z0-9-]+";
		String domainName = domainChars+"([.]"+domainChars+")+";
		
		String contactName = "((\"[^\"]*?\")"  +  "|" + "('[^']*?')" + "|" + "([^\\s,;].*?))";
		String normEmailRegex = "("+localName+"@"+domainName+")";
		String enronStyleEmailRegex = "(/o=enron/ou=(na|eu)/cn=recipients/cn=[a-zA-Z0-9-_]+)"+"|"+"(\"?[a-zA-Z&%][a-zA-Z&% ]*(/[a-zA-Z_&%]+)*\"?@"+domainChars+"+)"+"|"+"(\"?[a-zA-Z&%][a-zA-Z0-9_&% ]*\"?@"+domainName+")";
		//String enronStyleEmailRegex = "(\"?[a-zA-Z&%][a-zA-Z0-9_&% ]*\"?@"+domainName+")";
		String enronSpecificEmailRegex = "("+"(([?][?]s)?(u|U)ndisclosed-(R|r)ecipient(s)?:(;)?@((enron)|("+domainName+")))"+"|"+"(office of the chairman-@enron)"+"|"+"(exchange system administrator <.>)"+"|"+"(clickathome and community relations-@enron)"+"|"+"(enron property & services corp.@enron)"+")";
		String emailRegex = "("+normEmailRegex+"|"+enronStyleEmailRegex+"|"+enronSpecificEmailRegex+")";

		Matcher matcher = Pattern.compile(emailRegex).matcher("\"staffan e andersson%abb_se01%abb_notes%abb_ch01%abb_abbzh%abb_ch01\"@ch.abb.com");
		//Matcher matcher = Pattern.compile("("+contactName+"\\s*)?"+"<"+emailRegex+"[;]?"+">").matcher("kemp, sam </o=enron/ou=eu/cn=recipients/cn=skemp1>");
		System.out.println(matcher.find());
		System.out.println(matcher.group());
		*/
		
		
		//Account.saveAddresses(new File("/home/bartizzi/Research/Enron Accounts/wolfe-j/JWOLFE (Non-Privileged)/Wolfe, Jason/Inbox/109"), new File("/home/bartizzi/Research/Enron Accounts/wolfe-j/JWOLFE (Non-Privileged)/Wolfe, Jason/Inbox/109"+Account.ADDR_FILE_SUFFIX));
		
		
		//File addressFile = new File("/home/bartizzi/Research/Enron Accounts/germany-c/Chris_Germany_Dec2000/Notes Folders/Ces/169" + Account.ADDR_FILE_SUFFIX);
		//Account.saveAddresses(new File("/home/bartizzi/Research/Enron Accounts/germany-c/Chris_Germany_Dec2000/Notes Folders/Ces/169"), addressFile);
		
		File[] accounts = (new File(accountsFolder)).listFiles();
		Arrays.sort(accounts);
		
		boolean start = false;
		for(int i=0; i<accounts.length; i++){
			if(!accounts[i].isDirectory()){
				continue;
			}
			
			if(accounts[i].getName().equals("allen-p")) start = true;
			
			//
			//if(!accounts[i].getName().equals("meyers-a")) continue;
			
			if(!start) continue;
			
			System.out.println(accounts[i].getName());
			//buildDateFiles(accounts[i]);
			//buildAddressLists(accounts[i]);
			buildGoogleGroupsList(new File(accounts[i], list_file_name), new File(accountsFolder, Account.ADAPTED_GOOGLE_GROUPS_LIST));
			buildIndividualMsgList(new File(accounts[i], list_file_name), new File(accountsFolder, Account.INDIVIDUALS_GOOGLE_LIST));
		}
	}

}
