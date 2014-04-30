package bus.thunderbird;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ActionLogger {

	
	public void logAction(String logFileName, String message) throws IOException{
		logAction(new File(logFileName), message);
	}
	
	public void logAction(File logFile, String message) throws IOException{
		FileIO fileIO = new FileIO();
		fileIO.setFile(logFile);
		
		fileIO.append(message);
		fileIO.appendNewLine();
		fileIO.close();
	}

	@SuppressWarnings("rawtypes")
	public void logAddedRecipients(String logFile, Set addedRecipients) throws IOException{
		logAddedRecipients(new File(logFile), addedRecipients);
	}
	
	@SuppressWarnings("rawtypes")
	public void logAddedRecipients(File logFile, Set addedRecipients) throws IOException{
		logAction(logFile, "Manual, Added:"+addedRecipients);
	}
	
	@SuppressWarnings("rawtypes")
	public void logRemovedRecipients(String logFile, Set removedRecipients) throws IOException{
		logRemovedRecipients(new File(logFile), removedRecipients);
	}
	
	@SuppressWarnings("rawtypes")
	public void logRemovedRecipients(File logFile, Set removedRecipients) throws IOException{
		logAction(logFile, "Manual, Removed:"+removedRecipients);
	}
	
	@SuppressWarnings("rawtypes")
	public void logAddedAndRemovedRecipients(String logFile, Set addedRecipients, Set removedRecipients) throws IOException{
		logAddedAndRemovedRecipients(new File(logFile), addedRecipients, removedRecipients);
	}
	
	@SuppressWarnings("rawtypes")
	public void logAddedAndRemovedRecipients(File logFile, Set addedRecipients, Set removedRecipients) throws IOException{
		if(addedRecipients.size() == 1 && removedRecipients.size() == 1){
			String addedRecipient = (String) addedRecipients.iterator().next();
			String removedRecipient = (String) removedRecipients.iterator().next();
			
			if((removedRecipient.length() >0 && addedRecipient.indexOf(removedRecipient) == 0) ||
					(addedRecipient.length() >0 && removedRecipient.indexOf(addedRecipient) == 0) ){
				System.out.println("removedRecipient:"+removedRecipient + "addedRecipient:"+addedRecipient);
				return;
			}
		}
		
		logAction(logFile, "Manual, Added and Removed: Added:"+addedRecipients+"\tRemoved:"+removedRecipients);
	}
	
	public void logAddedPrediction(String logFile, String added) throws IOException{
		logAddedPrediction(new File(logFile), added);
	}
	
	public void logAddedPrediction(File logFile, String added) throws IOException{
		logAction(logFile, "Prediction, Added single:"+added);
	}
	
	public void logAddedPredictions(String logFile, String[] added) throws IOException{
		logAddedPredictions(new File(logFile), added);
	}
	
	public void logAddedPredictions(File logFile, String[] added) throws IOException{
		String addedAsString = "";
		for(int i=0; i<added.length ;i++){
			if(i>0) addedAsString += ",";
			addedAsString += added[i];
		}
		logAction(logFile, "Prediction, Added multi:"+addedAsString);
	}
}
