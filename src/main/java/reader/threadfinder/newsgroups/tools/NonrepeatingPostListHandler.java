package reader.threadfinder.newsgroups.tools;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class NonrepeatingPostListHandler implements NewsgroupPostHandler {
	
	Map<String, File> idToFileMap;
	
	public NonrepeatingPostListHandler(Map<String, File> idToFileMap){
		this.idToFileMap = idToFileMap;
	}
	
	protected boolean addToFileMap(MimeMessage post, File postLoc) throws MessagingException{
		String messageID = post.getMessageID();
		if(messageID == null || messageID.trim().equals("")){
			throw new RuntimeException("No message ID: "+postLoc.getPath());
		}
		if(idToFileMap.containsKey(messageID)) return false;
		idToFileMap.put(messageID, postLoc);
		return true;
	}
	
	@Override
	public void handle(File postLoc) {
		try {
			MimeMessage post = PostLoader.getPost(postLoc);
			addToFileMap(post, postLoc);
			
		} catch (IOException e) {
			System.out.println("Error with: "+postLoc.getPath());
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("Error with: "+postLoc.getPath());
			e.printStackTrace();
		}

	}

}
