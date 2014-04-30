package bus.thunderbird;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class MessageTracker {
	
	Map<Date, ArrayList<String>> dateHashedMessages = new TreeMap<Date, ArrayList<String>>();
	
	public MessageTracker(){
		
	}
	
	public void loadMessages(File file) throws IOException{
		if(file.exists()){
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = in.readLine();
			
			while(line != null){
				int splitPt = line.indexOf(',');
				Date date = new Date(Long.parseLong(line.substring(0, splitPt)));
				String contents = line.substring(splitPt+1);
				
				ArrayList<String> bucket = dateHashedMessages.get(date);
				if(bucket == null){
					bucket = new ArrayList<String>(1);
					dateHashedMessages.put(date, bucket);
				}
				if(!bucket.contains(contents)){
					bucket.add(contents);
				}
				
				line = in.readLine();
			}
		}
	}
	
	public boolean addMessage(String date, String messageContents){
		Date dateObj = new Date(Long.parseLong(date));
		
		ArrayList<String> bucket = dateHashedMessages.get(dateObj);
		if(bucket == null){
			bucket = new ArrayList<String>(1);
			dateHashedMessages.put(dateObj, bucket);
			bucket.add(messageContents);
			return true;
		}
		if(!bucket.contains(messageContents)){
			bucket.add(messageContents);
			return true;
		}
		
		return false;
	}
	
	public void clear(){
		dateHashedMessages.clear();
	}
	
}
