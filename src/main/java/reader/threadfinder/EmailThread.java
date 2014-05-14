package reader.threadfinder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.SummarizedMessage;

public class EmailThread{
	
	String subject;
	Map<String, SummarizedMessage> idTable = new TreeMap<String, SummarizedMessage>();
	Set<SummarizedMessage> roots = new HashSet<SummarizedMessage>();
	Set<SummarizedMessage> contents = new TreeSet<SummarizedMessage>();

	private void updateLinks(ArrayList<String> references){

		SummarizedMessage parent = null;
		
		if(references.size() > 0){
			parent = idTable.get(references.get(0));
			//Create the parent if necessary
			if(parent == null){
				parent = new SummarizedMessage(references.get(0));
				idTable.put(references.get(0), parent);

				roots.add(parent);
			}
		}
		
		//Link references accordingly
		for(int i=1; i<references.size(); i++){
			
			parent = idTable.get(references.get(i-1));
			SummarizedMessage child = idTable.get(references.get(i));
			
			//Create the child if necessary
			if(child == null){
				child = new SummarizedMessage(references.get(i));
				idTable.put(references.get(i), child);
			}
			
			//If the parent and child are linked successfully, the child should not be a root
			if(parent.addChild(child) && roots.contains(child)){
				roots.remove(child);
			}
		}
	}
	

	public boolean addElement(MimeMessage message){

		try {
			
			SummarizedMessage summarizedMessage = idTable.get(message.getMessageID());
			
			
			if(summarizedMessage == null){

				// Create a new summarized message for the ID if none exists
				summarizedMessage = new SummarizedMessage(message);

				idTable.put(summarizedMessage.getMessageId(), summarizedMessage);
				
			}else if(summarizedMessage.getMessage() == null){
				// Fill in the message field if it is empty
				summarizedMessage.setMessage(message);
				
			}else{
				// Don't do anything because this message has already been handled
				return true;
			}
			
			
			// Adjust for the list of references
			ArrayList<String> references = summarizedMessage.getReferences();
			updateLinks(references);
			
			//Remove the child from a previously determined parent from alternate messages
			if(summarizedMessage.getParent() != null){
				summarizedMessage.getParent().removeChild(summarizedMessage);
			}
			
			//Set the parent based on the list of references if possible
			if(references.size() > 0){
				SummarizedMessage parent = idTable.get(references.get(references.size() - 1));
				if(!parent.addChild(summarizedMessage)){
					roots.add(summarizedMessage);
				}
			}else{
				
				//If no parent, the child is a root
				roots.add(summarizedMessage);
			}

			//Set the subject of this thread if there is none
			if(subject == null){
				subject = summarizedMessage.getBaseSubject();
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void clean(){
		idTable.clear();
		Set<SummarizedMessage> copiedRoots = new HashSet<SummarizedMessage>(roots);
		for(SummarizedMessage currMessage: copiedRoots){
			cleanMessage(currMessage);
		}
	}
	
	public Set<SummarizedMessage> getRoots(){
		return roots;
	}
	
	private void cleanMessage(SummarizedMessage currMessage){
		if(currMessage.getMessage() == null && currMessage.getChildren().size() == 0){
			if(currMessage.getParent() != null)
				currMessage.getParent().removeChild(currMessage);
			return;
		}
		Set<SummarizedMessage> children = new HashSet<SummarizedMessage>(currMessage.getChildren());
		for(SummarizedMessage child: children){
			cleanMessage(child);
		}
		if(currMessage.getMessage() == null){
			SummarizedMessage parent = currMessage.getParent();
			if(parent != null){
				parent.removeChild(currMessage);
				for(SummarizedMessage child: currMessage.getChildren()){
					child.removeParent();
					parent.addChild(child);
				}
			}else{
				if(currMessage.getChildren().size() == 1){
					SummarizedMessage child = currMessage.getChildren().iterator().next();
					child.makeRoot();
					roots.add(child);
					roots.remove(currMessage);
				}
			}
		}
	}
	
}
