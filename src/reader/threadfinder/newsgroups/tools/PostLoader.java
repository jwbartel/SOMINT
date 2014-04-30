package reader.threadfinder.newsgroups.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class PostLoader {
	
	public static String getContents(File postLoc) throws IOException{
		if(postLoc == null || !postLoc.exists()){
			return null;
		}
		
		StringBuffer postBuffer = null;
		BufferedReader in = new BufferedReader(new FileReader(postLoc));
		String line = in.readLine();
		while(line != null){
			
			if(postBuffer == null){
				if(line.length() > 0)
					postBuffer = new StringBuffer(line);
			}else{
				postBuffer.append("\n");
				postBuffer.append(line);
			}
			
			line = in.readLine();
		}
		in.close();
		
		String postStr = postBuffer.toString();
		return postStr;
		
	}
	
	public static MimeMessage createPost(String postStr) throws MessagingException{
		Session session = Session.getDefaultInstance(System.getProperties());
		
		MimeMessage post = new MimeMessage(session, new ByteArrayInputStream(postStr.getBytes()));
		return post;
	}

	public static MimeMessage getPost(File postLoc) throws IOException, MessagingException{
		
		String postStr = getContents(postLoc);
		
		return createPost(postStr);
	}
	
	public static Map<MimeMessage, File> loadPostToFileMapping(ArrayList<File> postFiles) throws IOException, MessagingException{
		Map<MimeMessage,File> retVal = new HashMap<MimeMessage, File>();
		for(File postFile: postFiles){
			MimeMessage post = getPost(postFile);
			retVal.put(post, postFile);
		}
		return retVal;
	}
	
	static DateFormat[] dateFormats = {
		new SimpleDateFormat("E, d MMM y k:m:s z")
	};
	public static Date getDate(MimeMessage post) throws MessagingException{
		if(post != null){
			Date date = null;
			
			try {
				date = post.getSentDate();
			} catch (MessagingException e) {
			}
			
			if(date == null){
				try {
					date = post.getReceivedDate();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
			if(date == null){
				String[] dateStrs = post.getHeader("Date");
				if(dateStrs != null && dateStrs.length > 0){
					String dateStr = dateStrs[0];
					for(DateFormat dateFormat: dateFormats){
						try {
							date = dateFormat.parse(dateStr);
							break;
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			return date;
		}
		return null;
		
	}
	
	public static Date getDate(File postLoc) throws IOException, MessagingException{
		MimeMessage post = getPost(postLoc);
		return getDate(post);
	}
	
	public static void test() throws IOException, MessagingException{
		File testFolder = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\cleaned newsgroups");
		File fileListLoc = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\Cleaned Post List.txt");
		
		//Test mapping messages to file
		PostLister lister = new PostLister();
		ArrayList<File> fileList = lister.loadPostList(testFolder, fileListLoc);
		System.out.println("loaded file list");
		Map<MimeMessage, File> messageToFile = loadPostToFileMapping(fileList);
		System.out.println("loaded message to file mappings");
		for(Entry<MimeMessage,File> entry: messageToFile.entrySet()){
			File file = entry.getValue();
			if(!fileList.contains(file)){
				throw new RuntimeException("Found file not in filelist");
			}
			fileList.remove(file);
		}
		if(fileList.size() > 0){
			System.out.println("Found unmapped Files");
			for(File file: fileList){
				System.out.println("\t"+file.getPath());
			}
		}
		System.out.println(fileList.size());
	}
	
	public static void main(String[] args) throws IOException, MessagingException{
		test();
	}
}
