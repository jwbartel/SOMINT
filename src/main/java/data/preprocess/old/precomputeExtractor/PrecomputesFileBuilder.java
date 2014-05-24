package data.preprocess.old.precomputeExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import data.parsers.AddressParser;
import data.parsers.FrequencyParser;
import data.parsers.MessageFrequencyParser;
import reader.ContentParser;
import reader.ImapThreadFinder;
import bus.accounts.Account;

public class PrecomputesFileBuilder implements PrecomputeWriter{
	
	protected File messageFile;
	protected MimeMessage message;
	
	public PrecomputesFileBuilder() {
		
	}
	
	public PrecomputesFileBuilder(File messageFile) throws IOException, MessagingException {
		this.messageFile = messageFile;
		this.message = getMessage(messageFile);
	}
	
	public void writePrecomputes(File messageFile, File precomputePrefix) throws IOException, MessagingException {
		this.messageFile = messageFile;
		this.message = getMessage(messageFile);
		writePrecomputes(precomputePrefix);
	}
	
	public void writePrecomputes(File precomputePrefix) throws IOException, MessagingException {
		File addressFile = new File(precomputePrefix + Account.ADDR_FILE_SUFFIX);
		File dateFile = new File(precomputePrefix + Account.DATE_FILE_SUFFIX);
		File wordCountFile = new File(precomputePrefix + Account.WORD_COUNTS_SUFFIX);
		File subjWordCountFile = new File(precomputePrefix + Account.SUBJECT_WORD_COUNTS_SUFFIX);
		File punctuationCountFile = new File(precomputePrefix + Account.PUNCTUATION_COUNTS_SUFFIX);
		File subjPunctuationCountFile = new File(precomputePrefix + Account.SUBJECT_PUNCTUATION_COUNTS_SUFFIX);
		
		writeEmailAddresses(addressFile);
		writeMessageDate(dateFile);
		writeBodyWordCounts(wordCountFile);
		writeBodyPunctuationCounts(punctuationCountFile);
		writeSubjectWordFrequencies(subjWordCountFile);
		writeSubjectPunctuationFrequencies(subjPunctuationCountFile);
	}
	
	public void writePrecomputes() throws IOException, MessagingException {
		writePrecomputes(messageFile);
	}
	
	private String getContents(File messageLoc) throws IOException{
		if(messageLoc == null || !messageLoc.exists()){
			return null;
		}
		
		StringBuffer messageBuffer = null;
		BufferedReader in = new BufferedReader(new FileReader(messageLoc));
		String line = in.readLine();
		while(line != null){
			
			if(messageBuffer == null){
				if(line.length() > 0)
					messageBuffer = new StringBuffer(line);
			}else{
				messageBuffer.append("\n");
				messageBuffer.append(line);
			}
			
			line = in.readLine();
		}
		in.close();
		
		String postStr = messageBuffer.toString();
		return postStr;
		
	}
	
	private MimeMessage createMessage(String postStr) throws MessagingException{
		Session session = Session.getDefaultInstance(System.getProperties());
		
		MimeMessage post = new MimeMessage(session, new ByteArrayInputStream(postStr.getBytes()));
		return post;
	}

	private MimeMessage getMessage(File messageLoc) throws IOException, MessagingException{
		
		String messageStr = getContents(messageLoc);
		
		return createMessage(messageStr);
	}
	
	public void writeEmailAddresses(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("from");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("FROM:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("to");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("TO:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("cc");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("CC:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("bcc");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("BCC:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		out.flush();
		out.close();
	}
	
	protected static ArrayList<String> removeDuplicates(ArrayList<String> list){
		ArrayList<String> toReturn = new ArrayList<String>();
		Set<String> seenValues = new TreeSet<String>();
		
		for(int i=0; i<list.size(); i++){
			if(!seenValues.contains(list.get(i))){
				seenValues.add(list.get(i));
				toReturn.add(list.get(i));
			}
		}
		
		
		return toReturn;
	}
	
	public void writeBodyWordCounts(File dest) throws IOException {
		MessageFrequencyParser parser = new MessageFrequencyParser(messageFile);
		Map<String, Integer> counts = parser.getAllWordsWithCounts();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		for(Entry<String, Integer> entry: counts.entrySet()) {
			out.write(entry.getKey()+"\t"+entry.getValue());
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void writeBodyPunctuationCounts(File dest) throws IOException {
		MessageFrequencyParser parser = new MessageFrequencyParser(messageFile);
		Map<String, Integer> counts = parser.getPunctuationCounts();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		for(Entry<String, Integer> entry: counts.entrySet()) {
			out.write(entry.getKey()+"\t"+entry.getValue());
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void writeSubjectWordFrequencies(File dest) throws MessagingException, IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		String[] subjHeaders = message.getHeader("subject");
		if(subjHeaders != null){
			String subject = subjHeaders[0];
			String baseSubject = ImapThreadFinder.getBaseSubject(subject);
			Map<String, Integer> counts = ContentParser.parse(baseSubject);
			
			for(Entry<String, Integer> entry: counts.entrySet()) {
				out.write(entry.getKey()+"\t"+entry.getValue());
				out.newLine();
			}
		}
		out.flush();
		out.close();
	}
	
	public void writeSubjectPunctuationFrequencies(File dest) throws MessagingException, IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		String[] subjHeaders = message.getHeader("subject");
		if(subjHeaders != null){
			String subject = subjHeaders[0];
			String baseSubject = ImapThreadFinder.getBaseSubject(subject);
			Map<String, Integer> counts = FrequencyParser.countPunctuation(baseSubject);
			
			for(Entry<String, Integer> entry: counts.entrySet()) {
				out.write(entry.getKey()+"\t"+entry.getValue());
				out.newLine();
			}
		}
		out.flush();
		out.close();
	}
	
	public void writeMessageDate(File dest) throws IOException, MessagingException {
		Account.saveMessageDate(messageFile, dest);
	}
}
