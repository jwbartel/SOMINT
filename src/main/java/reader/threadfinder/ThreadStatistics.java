package reader.threadfinder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.ContentParser;
import reader.SummarizedMessage;

public class ThreadStatistics {
	
	Date timeLastMessage;
	int numMessages;
	double averageTimeBetweenMessages;
	long totalTime;
	Set<String> recipients;
	Map<String, Integer> subjectWordFrequencies;
	
	public ThreadStatistics(){
	}
	
	public ThreadStatistics(ThreadStatistics stats){
		init(stats.getTimeLastMessage(), stats.getNumMessages(), stats.getAverageTimeBetweenMessages(), stats.getTotalTime(), stats.getSubjectWordFrequencies());
	}
	
	public ThreadStatistics(String baseSubject, SummarizedMessage root) throws MessagingException{
		init(baseSubject, root);
	}
	
	public ThreadStatistics(Collection<MimeMessage> threadMessages) throws MessagingException{
		init(null, threadMessages);
	}
	
	protected void init(String baseSubject, SummarizedMessage root) throws MessagingException{
		init(baseSubject, buildMessageList(root));
	}
	
	protected void init(String baseSubject, Collection<MimeMessage> threadMessages) throws MessagingException{
		Date earliestDate = null;
		Date latestDate = null;
		Set<String> recipients = null;
		numMessages = 0;
		
		for(MimeMessage message: threadMessages){
			SummarizedMessage summarizedMessage = new SummarizedMessage(message);
			Date date = summarizedMessage.getSentDate();
			
			if(earliestDate == null || earliestDate.after(date)){
				earliestDate = date;
			}
			if(latestDate == null || latestDate.before(date)){
				latestDate = date;
			}
			if(baseSubject == null){
				try {
					baseSubject = summarizedMessage.getBaseSubject();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if(recipients == null){
				recipients = summarizedMessage.getRecipients();
			}
			numMessages++;
		}
		
		timeLastMessage = latestDate;
		if(earliestDate != null && latestDate != null){
			totalTime = latestDate.getTime() - earliestDate.getTime();
			averageTimeBetweenMessages = ((double) totalTime)/((double) numMessages);
		}else{
			totalTime = 0L;
			averageTimeBetweenMessages = 0;
		}
		this.recipients = recipients;
		subjectWordFrequencies = ContentParser.parse(baseSubject);
		
	}
	
	protected void init(Date timeLastMessage, int numMessages, double averageTimeBetweenMessages, long totalTime, Map<String, Integer> subjectWordFrequencies) {
		this.timeLastMessage = timeLastMessage;
		this.numMessages = numMessages;
		this.averageTimeBetweenMessages = averageTimeBetweenMessages;
		this.totalTime = totalTime;
		this.subjectWordFrequencies = subjectWordFrequencies;
	}
	
	private ArrayList<MimeMessage> buildMessageList(SummarizedMessage root) throws MessagingException{
		Map<Date, ArrayList<MimeMessage>> messages = new TreeMap<Date, ArrayList<MimeMessage>>();
		extractMessages(root, messages);
		
		ArrayList<MimeMessage> retVal = new ArrayList<MimeMessage>();
		for(Date key: messages.keySet()){
			ArrayList<MimeMessage> messageList = messages.get(key);
			for(MimeMessage message: messageList){
				retVal.add(message);
			}
		}
		return retVal;
		
	}
	
	private void extractMessages(SummarizedMessage summarizedMsg, Map<Date, ArrayList<MimeMessage>> messages) throws MessagingException{
		if(summarizedMsg.getMessage() != null){
			Date date = summarizedMsg.getSentDate();
			ArrayList<MimeMessage> prevMessages = messages.get(date);
			if(prevMessages == null){
				prevMessages = new ArrayList<MimeMessage>();
				messages.put(date, prevMessages);
			}
			prevMessages.add(summarizedMsg.getMessage());
		}
		
		for(SummarizedMessage child: summarizedMsg.getChildren()){
			extractMessages(child, messages);
		}
	}
	
	public Date getTimeLastMessage() {
		return timeLastMessage;
	}

	public int getNumMessages() {
		return numMessages;
	}

	public double getAverageTimeBetweenMessages() {
		return averageTimeBetweenMessages;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public Map<String, Integer> getSubjectWordFrequencies() {
		return subjectWordFrequencies;
	}

	public void setTimeLastMessage(Date timeLastMessage) {
		this.timeLastMessage = timeLastMessage;
	}

	public void setNumMessages(int numMessages) {
		this.numMessages = numMessages;
	}

	public void setAverageTimeBetweenMessages(double averageTimeBetweenMessages) {
		this.averageTimeBetweenMessages = averageTimeBetweenMessages;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public void setSubjectWordFrequencies(
			Map<String, Integer> subjectWordFrequencies) {
		this.subjectWordFrequencies = subjectWordFrequencies;
	}

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}
	
}
