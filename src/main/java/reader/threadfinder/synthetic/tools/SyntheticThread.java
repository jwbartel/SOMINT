package reader.threadfinder.synthetic.tools;

import java.util.ArrayList;
import java.util.Random;

public class SyntheticThread {

	int threadID;
	long startTime;
	int numMessages;
	long totalTime;
	
	public SyntheticThread(long startTime, int numMessages, long totalTime){
		this.startTime = startTime;
		this.numMessages = numMessages;
		this.totalTime = totalTime;
	}
	
	public ArrayList<SyntheticMessage> getMessages(){
		
		ArrayList<SyntheticMessage> messages = new ArrayList<SyntheticMessage>();
		messages.add(new SyntheticMessage(startTime));
		if(numMessages >= 2){
			messages.add(new SyntheticMessage(startTime+totalTime));
		}
		
		Random random = new Random();
		for(int i=0; i< numMessages - 2; i++){
			long relativeTime = random.nextInt((int) totalTime+1);
			messages.add(new SyntheticMessage(startTime+relativeTime));
		}
		
		return messages;
	}
	
	public void setThreadID(int threadID){
		this.threadID = threadID;
	}
	
	public int getThreadID() {
		return threadID;
	}

	public long getStartTime() {
		return startTime;
	}

	public int getNumMessages() {
		return numMessages;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public String toString(){
		return "<startTime="+startTime+", numThreads"+numMessages+", totalTime="+totalTime+">";
	}
}
