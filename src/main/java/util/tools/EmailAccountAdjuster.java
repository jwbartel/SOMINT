package util.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import data.structures.MessageList;
import bus.accounts.Account;
import bus.accounts.FileNameByOS;

public class EmailAccountAdjuster {
	
	
	public static void buildWordList(File accountFolder) throws IOException{
		Set<String> words = new TreeSet<String>();
		BufferedReader in = new BufferedReader(new FileReader(new File(accountFolder, Account.ALL_MSGS_NO_REPEATS)));
		
		String line = in.readLine();
		line = in.readLine();
		while(line != null){
			if(line.length()>0 && line.charAt(0)!='\t'){
				line = FileNameByOS.getMappedFileName(line);
				File wordsFile = new File(line+Account.TFIDF_SUFFIX);
				
				BufferedReader wordsReader = new BufferedReader(new FileReader(wordsFile));
				String wordsLine = wordsReader.readLine();
				while(wordsLine != null){
					String word = wordsLine.substring(0,wordsLine.lastIndexOf('\t'));
										
					words.add(word);
					
					wordsLine = wordsReader.readLine();
				}
				wordsReader.close();
			}
			line = in.readLine();
		}
		
		in.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(accountFolder,Account.WORDS_LIST)));
		Iterator<String> iter = words.iterator();
		boolean isStart = true;
		while(iter.hasNext()){
			if(isStart){
				isStart = false;
			}else{
				out.newLine();
			}
			out.write(iter.next());
		}
		out.close();
	}
	
	
	public final static String EDRM_SIGNATURE = "[*]{11}\nEDRM Enron Email Data Set has been produced in EML, PST and NSF format by ZL Technologies, Inc. This Data Set is licensed under a Creative Commons Attribution 3.0 United States License <http://creativecommons.org/licenses/by/3.0/us/> . To provide attribution, please cite to \"ZL Technologies, Inc. [(]http://www.zlti.com[)].\"\n[*]{11}";
	private final static Pattern sigPattern = Pattern.compile(EDRM_SIGNATURE);
	
	public static void removeEDRMSignatures(File accountFolder) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File(accountFolder, Account.ALL_MSGS)));
		
		String line = in.readLine();
		line = in.readLine();
		while(line != null){
			if(line.length()>0 && line.charAt(0)!='\t'){
				removeEDRMSignatureFromMessage(new File(line));
			}
			line = in.readLine();
		}
	}
	
	private static void removeEDRMSignatureFromMessage(File msgFile) throws IOException{
		StringBuffer msgBuff = null;
		BufferedReader in = new BufferedReader(new FileReader(msgFile));
		
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
		
		Matcher m = sigPattern.matcher(msg);
		if(m.find()){
			msg = msg.substring(0,m.start())+msg.substring(m.end());
			
			BufferedWriter out = new BufferedWriter(new FileWriter(msgFile));
			out.write(msg);
			out.flush();
			out.close();
		}else{
			//System.out.println("Not found: "+msgFile.getAbsolutePath());
		}
	}
	
	public static void buildListNoRepeats(String accountFolderName) throws IOException, MessagingException{
		buildListNoRepeats(new File(accountFolderName));
	}	
	public static void buildListNoRepeats(File accountFolder) throws IOException, MessagingException{
		Set<String> repeats = MessageList.getRepeats(accountFolder);
		ArrayList<String> buffer = new ArrayList<String>();
		
		
		File inFile = new File(accountFolder, Account.ALL_MSGS);
		File outFile = new File(accountFolder, Account.ALL_MSGS_NO_REPEATS);
		
		BufferedReader in = new BufferedReader(new FileReader(inFile));
		String line = in.readLine();
		line = in.readLine();
		int msgCount = 0;
		boolean isRepeat = false;
		while(line!=null){
			if(line.length()>0 && line.charAt(0)!='\t'){
				if(repeats.contains(line)){
					isRepeat = true;
				}else{
					isRepeat = false;
					buffer.add(line);
					msgCount++;
				}
			}else if(!isRepeat){
				buffer.add(line);
			}
			
			line = in.readLine();
		}
		
		in.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		out.write(""+msgCount);
		for(int i=0; i<buffer.size(); i++){
			out.newLine();
			out.write(buffer.get(i));
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
			if(accounts[i].getName().equals("allen-p")){
				start = true;
			}
			
			//if(!accounts[i].getName().equals("allen-p")){
			//	continue;
			//}
			
			if(!start) continue;
			System.out.println(accounts[i].getName());
			buildWordList(accounts[i]);
		}
	}
}
