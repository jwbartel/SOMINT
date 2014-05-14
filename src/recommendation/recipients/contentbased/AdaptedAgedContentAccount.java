package recommendation.recipients.contentbased;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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
import bus.data.parsers.MessageFrequencyParser;
import bus.data.structures.AddressLists;
import bus.data.structures.DirectedEmailInteraction;
import bus.data.structures.EmailInteraction;
import bus.tools.AdaptedMessageListBuilder.AdaptedMessage;

public class AdaptedAgedContentAccount extends SimpleTFIDFAccount{
	static String msg_list =  Account.ALL_MSGS_ADAPTED_NO_LATE_DRAFTS;
	static String addr_book = Account.INDIVIDUALS_GOOGLE_LIST;
	
	public static final String tf_suffix = Account.TF_SUFFIX;
	public static final String idf_list = Account.IDF_LIST;

	protected boolean wasReceived = false;
	protected Date currDate;
	
	Map<String, double[]> addressBook = new TreeMap<String, double[]>();
	
	int totalDocs = 0;
	Map<String, Integer> docsWithWord = new TreeMap<String, Integer>();

	public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	protected static Map<String, ArrayList<EmailInteraction>> individualMsgs = null;//new TreeMap<String, ArrayList<EmailInteraction>>();
	protected static Date oldestIndividualDate;
	
	public AdaptedAgedContentAccount(){
		
	}
	
	public AdaptedAgedContentAccount(String accountFolder) throws IOException, MessagingException{
		super(accountFolder);
	}
	
	public AdaptedAgedContentAccount(File accountFolder) throws IOException, MessagingException{
		super(accountFolder);
	}
	
	public void experimentalTraining() throws IOException, SQLException{
		long start = System.currentTimeMillis();
		
		int trainedMsgs = (int)(totalMsgs*Account.TRAINING_RATIO);
		for(int i=0; i<trainedMsgs; i++){
			try {
				getNextMessage();
				processCurrMessage();
				///super.trainCurrMessage();
			} catch (MessagingException e) {
				throw new RuntimeException(currMessage);
			}
		}
		writeIDFs();
		System.out.println("normalized TF-IDF vectors in "+(System.currentTimeMillis()-start)+" ms");
	}
	
	public void getNextMessage() throws IOException, MessagingException{
		super.getNextMessage();
		currDate = getCurrMessageDate();
	}
	
	protected void processCurrMessage() throws IOException{
		MessageFrequencyParser parser = new MessageFrequencyParser(currMessage);
		File tfidf = new File(currMessage+tf_suffix);
		
		Map<String, Integer> freqs = parser.getFreqWordsWithCounts();//parser.getAllWordsWithCounts();
		
		Iterator<String> words = freqs.keySet().iterator();
		BufferedWriter out = new BufferedWriter(new FileWriter(tfidf));
		boolean isFirst = true;
		while(words.hasNext()){
			String word = words.next();
			
			if(isFirst){
				isFirst = false;
			}else{
				out.newLine();
			}
			
			double tf = getTF(freqs.get(word), parser.getWordCount());
			
			if(docsWithWord.containsKey(word)){
				docsWithWord.put(word, docsWithWord.get(word)+1);
			}else{
				docsWithWord.put(word, 1);
			}
			
			out.write(word+"\t"+tf);
		}
		
		totalDocs++;
		
		out.flush();
		out.close();
	}
	
	
	
	protected double getTF(int freq, int totalWordsInDoc){
		return ((double) freq)/(double) totalWordsInDoc;	
	}
	
	
	protected void writeIDFs() throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(this.accountFolder, idf_list)));
		Iterator<String> words = docsWithWord.keySet().iterator();
		while(words.hasNext()){
			String word = words.next();
			double idf = getIDF(word);
			
			out.write(word+"\t"+idf);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	private double getIDF(String word){
		double toLog = ((double) totalDocs)/((double) docsWithWord.get(word));
		return Math.log10(toLog);
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
	
	static Map<String, Date> msgDates = new TreeMap<String, Date>();
	public static Date getMessageDate(String msgFile) throws IOException, MessagingException{
		if(msgDates.containsKey(msgFile)){
			return msgDates.get(msgFile);
		}else{
			Date date = Account.getMessageDate(msgFile);
			msgDates.put(msgFile, date);
			return date;
		}
	}
	
	protected static Set<String> ignoredAccounts = new TreeSet<String>();
	
	protected static void buildIgnoredAccounts(){
		ignoredAccounts.clear();
		ignoredAccounts.add("kaminski-v");
	}
	
	protected void buildIndividualMsgList(File dest) throws IOException, MessagingException{
		init(accountFolder);
		if(individualMsgs != null){
			individualMsgs.clear();
		}else{
			individualMsgs = new TreeMap<String, ArrayList<EmailInteraction>>();
		}
		
		int trainedMsgs = (int)(totalMsgs * Account.TRAINING_RATIO);
		for(int i=0; i<trainedMsgs; i++){
			getNextMessage();
			
			File addressFile = new File(currMessage+ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			
			AdaptedMessage adaptedMsg = new AdaptedMessage(currMessage);
			boolean received = !adaptedMsg.wasSent();
			
			if(currMessage.equals("/home/bartizzi/Research/Enron Accounts/allen-p/PALLEN (Non-Privileged)/Allen, Phillip K/Sent Items/59")){
				int x = 0;
			}
			Date date = getCurrMessageDate();
			
			Iterator<String> addresses = addressLists.getAll().iterator();
			while(addresses.hasNext()){
				String address = addresses.next();
				if(!individualMsgs.containsKey(address)){
					individualMsgs.put(address, new ArrayList<EmailInteraction>());
				}
				
				DirectedEmailInteraction interaction = new DirectedEmailInteraction(currMessage, date, received);
				individualMsgs.get(address).add(interaction);
			}
		}
		
		saveIndividualMsgList(dest);
		
		super.close();
		init(accountFolder);
	}
	

	
	protected void saveIndividualMsgList(File dest) throws IOException{
		if(individualMsgs == null){
			return;
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			ArrayList<EmailInteraction> messages = individualMsgs.get(individual);
			for(int i=0; i<messages.size(); i++){
				DirectedEmailInteraction interaction = (DirectedEmailInteraction) messages.get(i);
				out.write(""+interaction.wasReceived()+"\t"+dateFormat.format(interaction.getDate())+"\t"+interaction.getEmailLocation());
				out.newLine();
			}
			out.write("\t"+individual);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	@SuppressWarnings("unchecked")
	protected void loadIndividualMsgList(File src) throws IOException{
		if(individualMsgs != null){
			individualMsgs.clear();
		}else{
			individualMsgs = new TreeMap<String, ArrayList<EmailInteraction>>();
		}
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		ArrayList<EmailInteraction> messages = new ArrayList<EmailInteraction>();
		while(line != null){
			
			if(line.length()>0 && line.charAt(0)=='\t'){
				String individual = line.substring(1);
				individualMsgs.put(individual, (ArrayList<EmailInteraction>) messages.clone());
				messages.clear();
			}else{
				int splitPt = line.indexOf('\t');
				boolean received = Boolean.parseBoolean(line.substring(0, splitPt));
				
				line = line.substring(splitPt+1);
				splitPt = line.indexOf('\t');
				
				try {
					Date date = dateFormat.parse(line.substring(0,splitPt));
					messages.add(new DirectedEmailInteraction(line.substring(splitPt+1), date, received));
					Date oldestDate = oldestIndividualDate;
					if(oldestIndividualDate == null || date.before(oldestIndividualDate)){
						oldestIndividualDate = date;
					}
				} catch (ParseException e) {
					throw new RuntimeException("Error parsing date "+line.substring(0,splitPt));
				}
			}
			
			line = in.readLine();
		}
		in.close();
	}
	
	public static Date getOldestIndividualDate(){
		return oldestIndividualDate;
	}
	
	public static void main(String[] args) throws IOException, MessagingException, SQLException{
		buildIgnoredAccounts();
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = false;
		
		for(int i=0; i<accounts.length; i++){
			
			if(accounts[i].getName().equals("allen-p")) start = true;
			
			//if(!accounts[i].getName().equals("allen-p")) continue;
			
			if(ignoredAccounts.contains(accounts[i].getName())){
				continue;
			}
			
			if(!start) continue;
			
			System.out.println(accounts[i].getName()+"...");
			AdaptedAgedContentAccount account = new AdaptedAgedContentAccount(accounts[i]);
			//account.experimentalTraining();
			
			
			File individualsListFile = new File(account.accountFolder, Account.INDIVIDUALS_GOOGLE_LIST);
			//if(individualsListFile.exists()) continue;
			account.buildIndividualMsgList(individualsListFile);
			//account.loadIndividualMsgList(individualsListFile);
		}
	}
}
