package recommendation.recipients.groupbased.interactionrank;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import data.structures.AddressLists;
import data.structures.groups.GoogleGroupTracker;
import bus.accounts.Account;
import bus.accounts.FileNameByOS;

public class GoogleAccount extends Account {
	
	public static final int SEED_SIZE = 1;
	public static final int PREDICTION_LIST_SIZE = 4;
	public static final double WEIGHT_THRESHOLD = 0.0; 
	
	protected File accountFolder;
	
	BufferedReader receivedList;
	int totalReceived;
	Date receivedDate;
	String currReceived;
	
	BufferedReader sentList;
	int totalSent;
	Date sentDate;
	String currSent;
	
	int msgCount = 0;
	protected int totalMsgs;
	
	protected String currMessage;
	protected Date currDate;
	protected boolean wasReceived = false;
	protected ArrayList<String> currAttachments = new ArrayList<String>();
	
	protected GoogleGroupTracker groupTracker;
	
	
	public GoogleAccount(String accountFolder) throws IOException, MessagingException{
		init(new File(accountFolder));
	}
	
	public GoogleAccount(File accountFolder) throws IOException, MessagingException{
		init(accountFolder);
	}
	
	protected void init(File accountFolder) throws IOException, MessagingException{
		this.accountFolder = accountFolder;
		
		receivedList = new BufferedReader(new FileReader(new File(accountFolder, Account.RECEIVED_EMAIL_MSGS_NO_REPEATS)));
		totalReceived = Integer.parseInt(receivedList.readLine());
		currReceived = FileNameByOS.getMappedFileName(receivedList.readLine());
		receivedDate = getMessageDate(currReceived);
				
		sentList = new BufferedReader(new FileReader(new File(accountFolder, Account.SENT_EMAIL_MSGS_NO_REPEATS)));
		totalSent = Integer.parseInt(sentList.readLine());
		currSent = FileNameByOS.getMappedFileName(sentList.readLine());
		sentDate = getMessageDate(currSent);
		
		totalMsgs = totalReceived+totalSent;
		groupTracker = new GoogleGroupTracker(totalMsgs);
	}
	
	protected boolean hasNextMessage(){
		return msgCount < totalMsgs;
	}
	
	protected void getNextMessage() throws IOException, MessagingException{
		if(!hasNextMessage()){
			currMessage = null;
			currDate = null;
			currAttachments = null;
			return;
		}
		
		currAttachments.clear();
		
		if(currReceived != null && (currSent == null || receivedDate.before(sentDate))){
			currMessage = currReceived;
			currDate = receivedDate;
			wasReceived = true;
			
			String line = receivedList.readLine();
			while(line != null && line.charAt(0)=='\t'){
				currAttachments.add(line);
				line = receivedList.readLine();
			}
			
			currReceived = FileNameByOS.getMappedFileName(line);
			receivedDate = getMessageDate(currReceived);
		}else{
			currMessage = currSent;
			currDate = sentDate;
			wasReceived = false;
			
			String line = sentList.readLine();
			while(line != null && line.charAt(0)=='\t'){
				currAttachments.add(line);
				line = sentList.readLine();
			}
			
			currSent =FileNameByOS.getMappedFileName(line);
			sentDate = getMessageDate(currSent);
		}
		msgCount++;
	}
	
	
	public void experimentalPredictions() throws IOException {
		groupTracker.load(new File(accountFolder, Account.GOOGLE_GROUPS_LIST));
		int n = 0;
		n++;
		
		/*int trainedMsgs = (int) (totalMsgs * Account.TRAINING_RATIO);
		
		int i=0;
		for(; i<trainedMsgs; i++){
			try {
				getNextMessage();
			} catch (MessagingException e) {
				throw new RuntimeException(currMessage);
			}
		}
		
		for(; i<totalMsgs; i++){
			try {
				getNextMessage();
				predictCurrMessage();
			} catch (MessagingException e) {
				throw new RuntimeException(currMessage);
			}
		}*/
		
	}
	
	

	protected MimeMessage getCurrMessage() throws IOException, MessagingException{
		StringBuffer msgBuffer = null;
		BufferedReader in = new BufferedReader(new FileReader(currMessage));
		
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
		
		return message;
		
		
	}
	
	
	
	public void experimentalTraining() throws IOException, SQLException {
		
		
		int trainedMsgs = (int) (totalMsgs * Account.TRAINING_RATIO);
		
		long start = System.currentTimeMillis();
		
		for(int i=0; i<trainedMsgs; i++){
			try {
				getNextMessage();
				trainCurrMessage();
			} catch (MessagingException e) {
				throw new RuntimeException(currMessage);
			}
			
		}
		
		groupTracker.save(new File(accountFolder, Account.GOOGLE_GROUPS_LIST));
		
		System.out.println("trained "+trainedMsgs+" messages for groups in "+(System.currentTimeMillis()-start)+" ms");
	}
	
	protected void trainCurrMessage() throws IOException{
		File messageFile = new File(currMessage);
		if(!messageFile.exists()){
			return;
		}
		
		File addressFile = new File(currMessage+ADDR_FILE_SUFFIX);
		if(!addressFile.exists()){
			saveAddresses(new File(currMessage), addressFile);
		}
		
		AddressLists addressLists = new AddressLists(addressFile);
		Set<String> addresses = addressLists.getAll();
		
		if(currDate==null){
			throw new NullPointerException(currMessage);
		}
		
		groupTracker.foundMsgAddresses(addresses, currMessage, currDate, wasReceived);
	}
	
	
	public void close() throws IOException{
		receivedList.close();
		sentList.close();
	}
	
	protected static Set<String> ignoredAccounts = new TreeSet<String>();
	
	protected static void buildIgnoredAccounts(){
		ignoredAccounts.clear();
		
		ignoredAccounts.add("kaminski-v");
	}
	
	public static void main(String[] args) throws IOException, MessagingException, SQLException{
		buildIgnoredAccounts();
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = true;
		for(int i=0; i<accounts.length; i++){
			
			if(!accounts[i].getName().equals("allen-p")) start=true;
			
			if(!accounts[i].getName().equals("meyers-a")) continue;
			
			if(!start) continue;
			
			if(ignoredAccounts.contains(accounts[i].getName())){
				System.out.println(accounts[i].getName()+"...");
				continue;
			}
			
			System.out.print(accounts[i].getName()+"...");
			GoogleAccount account = new GoogleAccount(accounts[i]);
			account.experimentalTraining();
			//account.experimentalPredictions();
			
		}
		
		
	}

}
