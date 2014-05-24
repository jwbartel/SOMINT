package data.preprocess.old.precomputeExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

import data.structures.AddressLists;
import bus.accounts.Account;

public class PrecomputeDataIdentifier {
	static boolean useContent = false;
	
	public static String getMsgListName(){
		return Account.ALL_MSGS_ADAPTED;
		//return Account.ALL_MSGS_ADAPTED_NO_LATE_DRAFTS;
	}
	
	public static String getGoogleGroupsList(){
		return Account.ADAPTED_GOOGLE_GROUPS_LIST;
	}
	
	public static Set<String> getPrecomputeFileSuffixes(){
		Set<String> toReturn = new TreeSet<String>();
		
		toReturn.add(Account.ADDR_FILE_SUFFIX);
		toReturn.add(Account.DATE_FILE_SUFFIX);
		toReturn.add(Account.WORD_COUNTS_SUFFIX);
		toReturn.add(Account.SUBJECT_WORD_COUNTS_SUFFIX);
		
		if(useContent) toReturn.add(Account.CONTENT_FILE_SUFFIX);
		
		return toReturn;
	}
	
	public static void printPrecomputeData(String msgFile, String suffix, BufferedWriter out) throws IOException, MessagingException{
		
		if(suffix.equals(Account.ADDR_FILE_SUFFIX)){
			printAddressData(msgFile, suffix, out);
		}else if(suffix.equals(Account.DATE_FILE_SUFFIX)){
			printDateData(msgFile, suffix, out);
		}else if(suffix.equals(Account.WORD_COUNTS_SUFFIX)){
			printWordCountsData(msgFile, suffix, out);
		}
		
	}
	
	protected static void printWordCountsData(String msgFile, String suffix, BufferedWriter out) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(msgFile+suffix));
		out.write("\tWORD COUNTS:");
		out.newLine();
		
		String line = in.readLine();
		while(line != null){
			out.write("\t\t"+line);
			out.newLine();
			
			line = in.readLine();
		}
		in.close();
	}
	
	protected static void printDateData(String msgFile, String suffix, BufferedWriter out) throws IOException, MessagingException{
		Date date = Account.getMessageDate(msgFile);
		out.write("\tDATE:");
		out.newLine();
		out.write("\t\t"+date.getTime());
		out.newLine();
	}
	
	protected static void printAddressData(String msgFile, String suffix, BufferedWriter out) throws IOException{
		File addressFile = new File(msgFile+suffix);
		if(!addressFile.exists()){
			Account.saveAddresses(new File(msgFile), addressFile);
		}
		
		AddressLists addresses = new AddressLists(addressFile);
		ArrayList<String> from  = addresses.getFrom();
		out.write("\tFROM:");
		out.newLine();
		printArrayList("\t\t", from, out);
		
		ArrayList<String> to  = addresses.getTo();
		out.write("\tTO:");
		out.newLine();
		printArrayList("\t\t", to, out);
		ArrayList<String> cc  = addresses.getCC();
		out.write("\tCC:");
		out.newLine();
		printArrayList("\t\t", cc, out);
		ArrayList<String> bcc = addresses.getBCC();
		out.write("\tBCC:");
		out.newLine();
		printArrayList("\t\t", bcc, out);
	}
	
	private static void printArrayList(String prefix, ArrayList<String> arrayList, BufferedWriter out) throws IOException{
		for(int i=0; i<arrayList.size(); i++){
			out.write(prefix+arrayList.get(i));
			out.newLine();
		}
	}
}

