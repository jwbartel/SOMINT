package reader.threadfinder.newsgroups.tools;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class OrderedNonRepeatingPostListHandler extends
		NonrepeatingPostListHandler {

	Map<Date, ArrayList<File>> dateToFileMap;
	
	public OrderedNonRepeatingPostListHandler(Map<Date, ArrayList<File>> dateToFileMap) {
		super(new HashMap<String, File>());
		this.dateToFileMap = dateToFileMap;
	}
	
	@Override
	public void handle(File postLoc) {
		try {
			MimeMessage post = PostLoader.getPost(postLoc);
			if(addToFileMap(post, postLoc)){
				Date date = null;
				date = PostLoader.getDate(postLoc);
				if(date == null){
					System.out.println(postLoc.getPath());
					System.exit(0);
				}
				ArrayList<File> postLocs = dateToFileMap.get(date);
				if(postLocs== null){
					postLocs = new ArrayList<File>();
					dateToFileMap.put(date, postLocs);
				}
				
				boolean shouldAdd = true;
				if(postLocs.size()>0){
					String postContents = PostLoader.getContents(postLoc);
					for(File storedPostLoc: postLocs){
						String storedPostContents = PostLoader.getContents(storedPostLoc);
						if(storedPostContents != null && storedPostContents.equals(postContents)){
							shouldAdd = false;
							System.out.println("Found already stored message: "+storedPostLoc.getPath()+"\t"+postLoc.getPath());
							break;
						}
					}
				}
				
				if(shouldAdd){
					postLocs.add(postLoc);
				}
			}
			
		} catch (IOException e) {
			System.out.println("Error with: "+postLoc.getPath());
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("Error with: "+postLoc.getPath());
			e.printStackTrace();
		}

	}

}
