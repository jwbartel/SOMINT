package bus.thunderbird;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class ReportPackager {

	public String getReport(String actionLogFile) throws IOException{
		return getReport(new File(actionLogFile));
	}
	
	public String getReport(File actionLogFile) throws IOException{
		
		String prefix ="Please add any comments here:\n\n\n";
		
		String output = "History of actions:\n";
		
		BufferedReader in = new BufferedReader(new FileReader(actionLogFile));
		
		Set<String> linesToIgnore = new TreeSet<String>();
		Set<String> predictedRecipients = new TreeSet<String>();
		
		boolean lastLineWasAddSingle = false;
		
		String line = in.readLine();
		while(line != null){
			
			if(linesToIgnore.contains(line)){
				linesToIgnore.remove(line);
			}else if(line.equals("Manual, Added:[]")){
				//New message started

				linesToIgnore.clear();
				predictedRecipients.clear();
				lastLineWasAddSingle = false;
			}else if(line.startsWith("Prediction, Added ")){
				//Predictions added
				
				lastLineWasAddSingle = false;
				line = line.substring(("Prediction, Added ").length());
				if(line.startsWith("single:")){
					//individual prediction
					
					line = line.substring("single:".length());
					linesToIgnore.add("Manual, Added:["+line+"]");
					predictedRecipients.add(line);
					output += "Accepted:1\n";
					
				}else{
					// group prediction
					line = line.substring("multi:".length());
					linesToIgnore.add("Manual, Added:["+line.replaceAll(",", ", ")+"]");
					String[] addresses = line.split(",");
					for(int i=0; i<addresses.length; i++){
						predictedRecipients.add(addresses[i]);
					}
					output += "Accepted:"+addresses.length+"\n";
				}
				
				
			}else if(line.startsWith("Manual, Added:")){
				//Input changed to add elements
				
				line = line.substring("Manual, Added:".length()+1, line.length() - 1);
				String[] addresses = line.split(", ");
				if(addresses.length == 1){
					lastLineWasAddSingle = true;
				}else{
					lastLineWasAddSingle = false;
				}
				output += "Manual add:"+addresses.length+"\n";
				
			}else if(line.startsWith("Manual, Removed:")){
				//Input changed to remove elements
				
				line = line.substring("Manual, Removed:[".length(), line.length() - 1);
				String[] addresses = line.split(", ");
				int removedPredictions = 0;
				for(int i=0; i<addresses.length; i++){
					if(predictedRecipients.contains(addresses[i])){
						removedPredictions++;
						predictedRecipients.remove(addresses[i]);
					}
				}
				output += "Removed:"+removedPredictions+" predictions, "+(addresses.length-removedPredictions)+" manual\n";
				
			}else if(line.startsWith("Manual, Added and Removed: ")){
				//Input changed, removing and adding some
				
				line = line.substring("Manual, Added and Removed: Added:".length());
				
				String addedStr = line.substring(0, line.indexOf("\tRemoved:"));
				line = line.substring(addedStr.length());
				addedStr = addedStr.substring(1, addedStr.length()-1);
				String[] added = addedStr.split(", ");
				
				
				String removedStr = line.substring("\tRemoved:".length());
				removedStr = removedStr.substring(1, removedStr.length()-1);
				String[] removed = removedStr.split(", ");
				
				if(lastLineWasAddSingle && added.length == 1 && removed.length == 1){
					//Just a byproduct of Thunderbird's token completion
				}else{
					output += "Manual add:"+added.length + " and remove:"+removed.length+"\n";
				}
				
			}
			line = in.readLine();
			
		}
		in.close();
		
		output = prefix+output;
		output = output.replaceAll(" ", "%20").replaceAll("\n", "%0A");
		
		return output;
	}
	
	
	public static void main(String[] args) throws IOException{
		ReportPackager packager = new ReportPackager();
		System.out.println(packager.getReport("C:\\hierarchical_email_predictions\\ActionLog.txt"));
	}
}


