package bus.accounts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import data.parsers.AddressParser;
import data.structures.AddressLists;

public abstract class Account {
	public static final double TRAINING_RATIO = 0.9;
	
	public static final String MSG_CATEGORIES = "MSG_CATEGORIES.TXT";
	
	public static final String FULL_ADDRESS_LIST = "FULL_ADDRESS_LIST.TXT";
	
	public static final String ATTACHMENT_WITH_KEYWORD_LIST_NAME = "ATTACHMENTS_WITH_KEYWORDS.TXT";
	public static final String ALL_MSGS = "ALL_MESSAGES.TXT";
	public static final String ALL_MSGS_ADAPTED = "ALL_MESSAGES_ADAPTED.TXT";
	public static final String ALL_MSGS_ADAPTED_NO_LATE_DRAFTS = "ALL_MESSAGES_ADAPTED_NO_LATE_DRAFTS.TXT";
	public static final String ALL_MSGS_NO_REPEATS = "ALL_MESSAGES_NO_REPEATS.TXT";
	
	public static final String EMAIL_MSGS = "EMAIL_MSGS.TXT";
	public static final String EMAIL_MSGS_NO_REPEATS = "EMAIL_MSGS_NO_REPEATS.TXT";
	
	public static final String SENT_EMAIL_MSGS = "SENT_EMAIL_MSGS.TXT";
	public static final String SENT_EMAIL_MSGS_NO_REPEATS = "SENT_EMAIL_MSGS_NO_REPEATS.TXT";
	
	public static final String SENT_AND_DRAFT_EMAIL_MSGS = "SENT_AND_DRAFT_EMAIL_MSGS.TXT";
	public static final String SENT_AND_DRAFT_EMAIL_MSGS_NO_REPEATS = "SENT_AND_DRAFT_EMAIL_MSGS_NO_REPEATS.TXT";
	
	public static final String RECEIVED_EMAIL_MSGS = "RECEIVED_EMAIL_MSGS.TXT";
	public static final String RECEIVED_EMAIL_MSGS_NO_REPEATS = "RECEIVED_EMAIL_MSGS_NO_REPEATS.TXT";
	

	public static final String INDIVIDUALS_LIST = "INDIVIDUALS_LIST.TXT";
	public static final String INDIVIDUALS_GOOGLE_LIST = "INDIVIDUALS_GOOGLE_LIST.TXT";
	
	public static final String GROUPS_LIST = "GROUPS_LIST.TXT";
	public static final String ADAPTED_GROUPS_LIST = "ADAPTED_GROUPS_LIST.TXT";
	public static final String EMAIL_GROUPS_LIST = "EMAIL_GROUPS_LIST.TXT";
	public static final String SENT_GROUPS_LIST = "SENT_GROUPS_LIST.TXT";
	public static final String SENT_AND_DRAFT_GROUPS_LIST = "SENT_AND_DRAFT_GROUPS_LIST.TXT";
	public static final String RECEIVED_GROUPS_LIST = "RECEIVED_GROUPS_LIST.TXT";
	

	public static final String GOOGLE_GROUPS_LIST = "GOOGLE_GROUPS_LIST.TXT";
	public static final String ADAPTED_GOOGLE_GROUPS_LIST = "ADAPTED_GOOGLE_GROUPS_LIST.TXT";
	
	public static final String INDIVIDUAL_ADDRESS_BOOK = "INDIVIDUAL_ADDRESS_BOOK.TXT";
	public static final String INDIVIDUAL_FREQ_KEYWORD_ADDRESS_BOOK = "INDIVIDUAL_FREQ_KEYWORD_ADDRESS_BOOK.TXT";
	public static final String INDIVIDUAL_RATIO_ADDRESS_BOOK = "INDIVIDUAL_RATIO_ADDRESS_BOOK.TXT";
	public static final String INDIVIDUAL_RATIO_FREQ_KEYWORD_ADDRESS_BOOK = "INDIVIDUAL_RATIO_FREQ_KEYWORD_ADDRESS_BOOK.TXT";

	public static final String GROUP_ADDRESS_BOOK = "GROUP_ADDRESS_BOOK.TXT";
	public static final String GROUP_FREQ_KEYWORD_ADDRESS_BOOK = "GROUP_FREQ_KEYWORD_ADDRESS_BOOK.TXT";
	public static final String GROUP_RATIO_ADDRESS_BOOK = "INDIVIDUAL_RATIO_ADDRESS_BOOK.TXT";
	public static final String GROUP_RATIO_FREQ_KEYWORD_ADDRESS_BOOK = "INDIVIDUAL_RATIO_FREQ_KEYWORD_ADDRESS_BOOK.TXT";
	
	public static final String EMAIL_ADDRESS_BOOK = "EMAIL_ADDRESS_BOOK.TXT";
	public static final String RECEIVED_ADDRESS_BOOK = "RECEIVED_ADDRESS_BOOK.TXT";
	public static final String SENT_ADDRESS_BOOK = "SENT_ADDRESS_BOOK.TXT";
	public static final String SENT_AND_DRAFT_ADDRESS_BOOK = "SENT_AND_DRAFT_ADDRESS_BOOK.TXT";
	public static final String WORDS_LIST = "WORDS_LIST.TXT";
	

	public static final String EMAIL_TFIDF_PREDICTION_FILE_SUFFIX = "_EMAIL_TFIDF_PREDICTIONS.TXT";
	public static final String RECIEVED_TFIDF_PREDICTION_FILE_SUFFIX = "_RECIEVED_TFIDF_PREDICTIONS.TXT";
	public static final String SENT_TFIDF_PREDICTION_FILE_SUFFIX = "_SENT_TFIDF_PREDICTIONS.TXT";
	public static final String SENT_AND_DRAFT_TFIDF_PREDICTION_FILE_SUFFIX = "_SENT_AND_DRAFT_TFIDF_PREDICTIONS.TXT";
	
	public static final String TF_SUFFIX = "_TFs.TXT";
	public static final String FREQ_TF_SUFFIX = "_FREQ_TFs.TXT";
	
	public static final String IDF_LIST = "IDFs.TXT";
	public static final String FREQ_IDF_LIST = "FREQ_IDFs.TXT";
	
	public static final String GROUP_CONTENT_SIMILARITY_SUFFIX = "_GROUP_CONTENT_SIMILARITY.txt";
	public static final String GROUP_FREQ_CONTENT_SIMILARITY_SUFFIX = "_GROUP_FREQ_CONTENT_SIMILARITY.txt";
	
	public static final String INDIVIDUAL_CONTENT_SIMILARITY_SUFFIX = "_INDIVIDUAL_CONTENT_SIMILARITY.txt";
	public static final String INDIVIDUAL_FREQ_CONTENT_SIMILARITY_SUFFIX = "_INDIVIDUAL_FREQ_CONTENT_SIMILARITY.txt";
	
	//TODO: Check after here
	
	//public static final String MSG_LIST_NAME = "MESSAGES.TXT";
	public static final String MSG_WITH_KEYWORD_LIST_NAME = "MESSAGES_WITH_KEYWORDS.TXT";
	
	public static final String ADDRESS_MAP_NAME = "ADDRESS_MAPPING.txt";
	public static final String KEYWORD_MAP_NAME = "KEYWORD_MAPPING.txt";
	
	public static final String PERMUTED_GROUPS_LIST = "PERMUTED_GROUPS_LIST.TXT";
	
	
	public static final String ADDR_FILE_SUFFIX = "_ADDRESSES.TXT";
	public static final String DATE_FILE_SUFFIX = "_DATE.TXT";
	public static final String WORD_COUNTS_SUFFIX = "_WORDCOUNTS.txt";
	public static final String PUNCTUATION_COUNTS_SUFFIX = "_PUNCTUATION_COUNTS.txt";
	public static final String SUBJECT_WORD_COUNTS_SUFFIX = "_SUBJECT_WORDCOUNTS.txt";
	public static final String SUBJECT_PUNCTUATION_COUNTS_SUFFIX = "_SUBJECT_PUNCTUATUON_COUNTS.txt";
	public static final String CONTENT_FILE_SUFFIX = "_CONTENT.TXT";
	public static final String GROUP_TFIDF_PREDICTION_FILE_SUFFIX = "_GROUP_TFIDF_PREDICTIONS.TXT";
	public static final String SIMPLE_TFIDF_PREDICTION_FILE_SUFFIX = "_SIMPLE_TFIDF_PREDICTIONS.TXT";
	public static final String PREDICTION_FILE_SUFFIX = "_PREDICTIONS.TXT";
	public static final String GROUP_PRED_SUFFIX = "_GROUP_PREDICTIONS.TXT";
	public static final String TFIDF_SUFFIX = "_TFIDF.TXT";
	//public static final String FREQ_TFIDF_SUFFIX = "_FREQ_TFIDF.TXT";
	public static final String NORMALIZED_TFIDF_SUFFIX = "_NORMALIZED_TFIDF.TXT";

	public static final String FILETYPE_PREDICTIONS_SUFFIX = "_FILETYPE_PREDICTIONS.txt";
	
	public static final String TEMPLATE_IMAGE_LOCATION = "specs/template.png";
	public static final int IMAGE_FONT_SIZE = 11;
	public static final int IMAGE_WIDTH = 711;
	public static final int IMAGE_TEXT_V_OFFEST = 2;
	public static final int IMAGE_TEXT_H_OFFEST = 2;

	
	public abstract void experimentalPredictions() throws IOException, MessagingException;
	public abstract void experimentalTraining() throws IOException, SQLException, MessagingException;
	
	public static Message getMessage(File messageFile){
		try{
			StringBuffer contentsBuff = null;
			
			BufferedReader in = new BufferedReader(new FileReader(messageFile));
			String line = in.readLine();
			
			while(line != null){
				
				if(contentsBuff == null){
					if(line.length() > 0)
						contentsBuff = new StringBuffer(line);
				}else{
					contentsBuff.append("\n");
					contentsBuff.append(line);
				}
				
				line = in.readLine();
			}
			
			String contents = contentsBuff.toString();
			Session session = Session.getDefaultInstance(System.getProperties());
			Message message = new MimeMessage(session, new ByteArrayInputStream(contents.getBytes()));
			
			in.close();
			
			return message;
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void saveAddresses(File currMessageFile, File addressFile) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(addressFile));
		
		Message mimeObj = getMessage(currMessageFile);
		try {
			
			String[] from = mimeObj.getHeader("x-zl-from");
			String[] to = mimeObj.getHeader("x-zl-to");
			String[] cc = mimeObj.getHeader("x-zl-cc");
			String[] bcc = mimeObj.getHeader("x-zl-bcc");
			
			if(from == null){
				from = mimeObj.getHeader("from");
			}
			if(to == null){
				to = mimeObj.getHeader("to");
			}
			if(cc == null){
				cc = mimeObj.getHeader("cc");
			}
			if(bcc == null){
				bcc = mimeObj.getHeader("bcc");
			}
			
			
			out.write("FROM:");
			out.newLine();
			if(from!= null){
				AddressParser fromList = new AddressParser(from[0]);
				for(int j=0; j<fromList.size(); j++){
					out.write(fromList.get(j));
					out.newLine();
				}
			}
			out.newLine();
			
			out.write("TO:");
			out.newLine();
			if(to != null){
				
				AddressParser toList = new AddressParser(to[0]);
				for(int j=0; j<toList.size(); j++){
					out.write(toList.get(j));
					out.newLine();
				}
			}
			out.newLine();
			
			out.write("CC:");
			out.newLine();
			if(cc != null){
				AddressParser ccList = new AddressParser(cc[0]);
				for(int j=0; j<ccList.size(); j++){
					out.write(ccList.get(j));
					out.newLine();
				}
			}
			out.newLine();
			
			out.write("BCC:");
			out.newLine();
			if(bcc != null){
				AddressParser bccList = new AddressParser(bcc[0]);
				for(int j=0; j<bccList.size(); j++){
					out.write(bccList.get(j));
					out.newLine();
				}
			}
		
			out.flush();
			out.close();
			
			/*System.out.println("-------------");
			
			if(from!= null){System.out.println("From:"+from[0]);}
			if(to!= null){System.out.println("To:"+to[0]);}
			if(cc!= null){System.out.println("CC:"+cc[0]);}
			if(bcc!= null){System.out.println("BCC:"+bcc[0]);}
			System.out.println("\n");*/
		
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveAddressesForAccount(File account) throws IOException{
		File allMsgs = new File(account, Account.ALL_MSGS);
		BufferedReader in = new BufferedReader(new FileReader(allMsgs));
		String line =in.readLine();
		
		line = in.readLine();
		while(line != null){
			
			if(line.length()>0 && line.charAt(0)!='\t'){
				line = FileNameByOS.getMappedFileName(line);
				saveAddresses(new File(line), new File(line+Account.ADDR_FILE_SUFFIX));
			}
			
			line = in.readLine();
		}
	}
	
	public static void main(String[] args) throws Exception{
		Set<String> toFix = new TreeSet<String>();
		toFix.add("lavorato-j");
		
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		for(int i=0; i<accounts.length; i++){
			if(!toFix.contains(accounts[i].getName())){
				//continue;
			}
			
			if(!accounts[i].getName().equals("arnold-j")) continue;
			
			System.out.print(accounts[i].getName()+"...");
			long start = System.currentTimeMillis();
			saveAddressesForAccount(accounts[i]);
			System.out.println("extracted addresses in "+(System.currentTimeMillis()-start)+" ms");
		}
		
		//File inFile = new File("/home/bartizzi/Research/Enron Accounts/meyers-a/ExMerge - Meyers, Albert/Inbox/4");
		//File outFile = new File("/home/bartizzi/Research/Enron Accounts/bailey-s/Susan_Bailey_June2001/Notes Folders/Notes inbox/2-test");
		//saveAddresses(inFile, outFile);
	}
	
	public static Date getMessageDate(String msgFile) throws IOException, MessagingException{
		return getMessageDate(new File(msgFile));
	}
	
	public static Date getMessageDate(File msgFile) throws IOException, MessagingException{
		File dateFile = new File(msgFile.getAbsolutePath()+DATE_FILE_SUFFIX);
		
		if(!dateFile.exists()){
			saveMessageDate(msgFile, dateFile);
		}
		
		return loadMessageDate(dateFile);
		
	}
	
	protected static Date loadMessageDate(File dateFile) throws IOException{
		if(dateFile == null || !dateFile.exists()){
			return null;
		}
		
		BufferedReader in = new BufferedReader(new FileReader(dateFile));
		String dateStr = in.readLine();
		if(dateStr == null){
			return null;
		}
		
		Date date = new Date(Long.parseLong(dateStr));
		return date;
	}
	
	public static void saveMessageDate(String msgFile, String dest) throws IOException, MessagingException{
		saveMessageDate(new File(msgFile), new File(dest));
	}
	
	public static void saveMessageDate(File msgFile, File dest) throws IOException, MessagingException{
		if(msgFile == null || !msgFile.exists()){
			return;
		}
		
		StringBuffer msgBuffer = null;
		BufferedReader in = new BufferedReader(new FileReader(msgFile));
		String line = in.readLine();
		while(line != null){
			
			if(msgBuffer == null){
				if(line.length() > 0)
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
		
		if(msgDate == null) return;
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		out.write(""+msgDate.getTime());
		out.flush();
		out.close();
	}
	
	protected static String getSender(String messageFile) throws IOException{
		return getSender(new File(messageFile));
	}
	
	protected static String getSender(File messageFile) throws IOException{
		File addressFile = new File(messageFile.getAbsolutePath()+Account.ADDR_FILE_SUFFIX);
		if(!addressFile.exists()){
			Account.saveAddresses(messageFile, addressFile);
		}
		
		AddressLists addressLists = new AddressLists(addressFile);
		ArrayList<String> from = addressLists.getFrom();
		if(addressLists.getFrom().size() > 1){
			System.out.println(from);
			throw new RuntimeException("Wrong sized from:"+messageFile.getAbsolutePath());
		}
		if(from.size() == 0) return null;
		return addressLists.getFrom().get(0);
	}
}
