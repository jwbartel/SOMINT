package reader.threadfinder;

import java.util.Map;
import java.util.TreeMap;

import reader.SummarizedMessage;

public class SubjectGroupedMessages {
	
	String subject;
	Map<String, SummarizedMessage> id_table = new TreeMap<String, SummarizedMessage>();
	
	public SubjectGroupedMessages(String cleanedSubject){
		this.subject = cleanedSubject;
	}
	
	
	public void addMessage(SummarizedMessage message){
		//TODO: add to id_table
		//TODO: sort into threads
	}
	
}
