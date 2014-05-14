package reader.threadfinder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.threadfinder.newsgroups.tools.PostLoader;

public class IteratedThreadStatistics {
	Integer threadID;
	File postLocation;
	ThreadStatistics stats;
	Date latestDate;
	
	public IteratedThreadStatistics(Integer threadID, File postLocation, ThreadStatistics stats){
		this.threadID = threadID;
		this.postLocation =postLocation;
		this.stats = stats;
	}
	
	public Integer getThreadID(){
		return threadID;
	}
	
	public ThreadStatistics getStatistics(){
		return stats;
	}
	
	public File getPostLocation(){
		return postLocation;
	}
	
	public double[] getVector(Date currDate, Map<String,Integer> allSubjectWordPositions){
		double[] vector = new double[4+allSubjectWordPositions.size()];
		if(stats.getTimeLastMessage() != null){
			vector[0] = (currDate.getTime() - stats.getTimeLastMessage().getTime());
		}else{
			return null;
		}
		vector[1] = stats.getNumMessages();
		vector[2] = stats.getAverageTimeBetweenMessages();
		vector[3] = stats.getTotalTime();
		for(Entry<String,Integer> entry : allSubjectWordPositions.entrySet()){
			String word = entry.getKey();
			Integer freq = entry.getValue();
			Integer pos = allSubjectWordPositions.get(word);
			
			vector[pos+4] = freq;
		}
		return vector;
	}
	
	public double[] getVector(Date currDate){
		double[] vector = new double[5];
		if(stats.getTimeLastMessage() == null){
			return null;
		}
		double timescale = 1000*60*60;
		vector[0] = ((double) (currDate.getTime() - stats.getTimeLastMessage().getTime()))/timescale;
		vector[1] = stats.getNumMessages();
		vector[2] = ((double) stats.getAverageTimeBetweenMessages())/timescale;
		vector[3] = ((double) stats.getTotalTime())/timescale;
		vector[4] = vector[2] - vector[0];
		return vector;
	}
	
	public double[] getVector(Date prevDate, Date currDate, Map<String, Integer> recipientPositions){
		double[] vector = new double[5+recipientPositions.size()];

		double timescale = 1000*60*60;
		vector[0] = (prevDate != null)? ((double) (currDate.getTime() - prevDate.getTime()))/timescale: 0;
		vector[1] = stats.getNumMessages();
		vector[2] = ((double) stats.getAverageTimeBetweenMessages())/timescale;
		vector[3] = ((double) stats.getTotalTime())/timescale;
		vector[4] = vector[2] - vector[0];
		for(String recipient: stats.getRecipients()){
			int pos = recipientPositions.get(recipient);
			vector[pos+5] = 1;
		}
		return vector;
	}
	
	public Date getLatestDate(){
		if(latestDate != null){
			return latestDate;
		}
		try {
			MimeMessage message = PostLoader.getPost(this.getPostLocation());
			latestDate = message.getReceivedDate();
			if(latestDate == null){
				latestDate = message.getSentDate();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return latestDate;
	}
}
