package recommendation.recipients.groupbased;

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

import bus.accounts.Account;
import bus.accounts.FileNameByOS;
import bus.data.structures.AddressLists;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.tools.AdaptedMessageListBuilder.AdaptedMessage;

public class GroupAccount extends Account {
	
	public static final String GROUPS_LIST = Account.ADAPTED_GOOGLE_GROUPS_LIST;
		
	protected GoogleGroupTracker groupTracker;
	
	static String msg_list = Account.ALL_MSGS_ADAPTED;
	
	protected File accountFolder;
	
	protected int msgNum = 0;
	protected int totalMsgs;
	protected String currMessage;
	protected ArrayList<String> currAttachments = new ArrayList<String>();
	
	protected BufferedReader in;
	protected String currLine;
	
	
	public GroupAccount(){
		
	}
	
	public GroupAccount(String accountFolder) throws IOException{
		init(new File(accountFolder));
	}
	
	public GroupAccount(File accountFolder) throws IOException{
		init(accountFolder);
	}
	
	protected void init(File accountFolder) throws IOException{
		//System.out.println(msg_list);
		
		this.accountFolder = accountFolder;
		
		in = new BufferedReader(new FileReader(new File(accountFolder,msg_list)));
		totalMsgs = Integer.parseInt(in.readLine());
		
		groupTracker = new GoogleGroupTracker( (int) (totalMsgs*Account.TRAINING_RATIO));
	}
	
	protected boolean hasNextMessage(){
		return msgNum < totalMsgs;
	}
	
	protected void getNextMessage() throws IOException, MessagingException{
		if(!hasNextMessage()){
			currMessage = null;
			currAttachments = null;
			return;
		}
		if(currLine==null){
			currLine = in.readLine();
		}
		currMessage =  FileNameByOS.getMappedFileName(currLine);
		currLine = in.readLine();
		
		currAttachments.clear();
		while(currLine != null && currLine.charAt(0)=='\t'){
			
			currAttachments.add(FileNameByOS.getMappedFileName(currLine.substring(1)));
			currLine = in.readLine();
		}
		
		msgNum++;
	}

	public void experimentalTraining() throws IOException, SQLException, MessagingException{
		
		long start = System.currentTimeMillis();
		
		int trainedMsgs = (int)(totalMsgs*Account.TRAINING_RATIO);
		for(int i=0; i<trainedMsgs; i++){
			getNextMessage();
			processCurrMessage();
		}
		
		groupTracker.save(new File(accountFolder, GROUPS_LIST));
		
		System.out.println("trained "+trainedMsgs+" messages for groups in "+(System.currentTimeMillis()-start)+" ms");
	}
	
	protected void processCurrMessage() throws IOException, MessagingException{
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
		
		Date date = null;
		try {
			date = getCurrMessageDate();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		if(date == null){
			throw new NullPointerException(currMessage);
		}
		
		AdaptedMessage adaptedMsg = new AdaptedMessage(currMessage);
		boolean wasReceived = !adaptedMsg.wasSent();
		
		groupTracker.foundMsgAddresses(addresses, currMessage, getCurrMessageDate(), wasReceived);;
	}
	
	protected Date getCurrMessageDate() throws IOException, MessagingException{
		return Account.getMessageDate(currMessage);
		
		/*StringBuffer msgBuffer = null;
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
		
		Date msgDate = message.getSentDate();
		if(msgDate == null){
			msgDate = message.getReceivedDate();
		}
		
		return msgDate;*/
		
	}
	
	public void experimentalPredictions() throws IOException, MessagingException {
		groupTracker.load(new File(accountFolder, GROUPS_LIST));
		/*Map<String, Double> normalizedWeights = groupTracker.getNormalizedAddressWeights();
		
		Iterator<String> addresses = normalizedWeights.keySet().iterator();
		while(addresses.hasNext()){
			String address = addresses.next();
			System.out.println(normalizedWeights.get(address)+"\t"+address);
		}*/
	}

	public void close() throws IOException{
		if(in != null){
			in.close();
		}
	}
	
	public static void main(String[] args) throws IOException, MessagingException, SQLException{
		Set<String> badAccounts = new TreeSet<String>();
		badAccounts.add("kaminski-v");
		
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		//boolean start = false;
		for(int i=0; i<accounts.length; i++){
			//if(accounts[i].getName().equals("lavorato-j")) start=true;
			
			//if(!accounts[i].getName().equals("bailey-s")) continue;
			
			//if(!start) continue;
			if(badAccounts.contains(accounts[i].getName())){
				System.out.println(accounts[i].getName());
				continue;
			}
			
			System.out.print(accounts[i].getName()+"...");
			GroupAccount account = new GroupAccount(accounts[i]);
			account.experimentalTraining();
			//account.experimentalPredictions();
			//account.groupTracker.print();
		}
	}
}
