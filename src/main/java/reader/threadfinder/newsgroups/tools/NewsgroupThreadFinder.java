package reader.threadfinder.newsgroups.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.SummarizedMessage;
import reader.threadfinder.EmailThread;

public class NewsgroupThreadFinder {
	
	public static Collection<Set<Integer>> sameSubjectThreads(ArrayList<File> files, Map<MimeMessage,File> messageToFile, Map<File,Integer> fileToThreadId) throws IOException, MessagingException{
		return sameSubjectThreads(messageToFile, fileToThreadId);
	}
	
	public static Map<Integer, SummarizedMessage> getThreadIdsMap(Map<String, EmailThread> threads, Map<MimeMessage,File> messageToFile, Map<File,Integer> fileToThreadId){
		Map<Integer, SummarizedMessage> retVal = new HashMap<Integer, SummarizedMessage>();
		for(Entry<String, EmailThread> entry: threads.entrySet()){
			EmailThread thread = entry.getValue();
			for(SummarizedMessage root: thread.getRoots()){
				Integer threadID = root.getThreadID(messageToFile, fileToThreadId);
				if(threadID != null){
					retVal.put(threadID, root);
				}
			}
		}
		
		return retVal;
	}
	
	public static Map<Integer, String> getThreadsToBaseSubject(Map<Integer, File> threadToLastFile, Map<MimeMessage,File> messageToFile, Map<File,Integer> fileToThreadId) throws MessagingException, IOException{
		
		Map<File, MimeMessage> fileToMessage = new HashMap<File, MimeMessage>();
		for(Entry<MimeMessage,File> entry: messageToFile.entrySet()){
			fileToMessage.put(entry.getValue(), entry.getKey());
		}
		
		Map<Integer, String> retVal = new HashMap<Integer, String>();
		for(Entry<Integer, File> entry: threadToLastFile.entrySet()){
			
			Integer threadID = entry.getKey();
			File finalThreadLoc = entry.getValue();
			
			MimeMessage msg = PostLoader.getPost(finalThreadLoc);
			SummarizedMessage summarizedMessage = new SummarizedMessage(msg);
			
			String baseSubject = summarizedMessage.getBaseSubject();
			
			retVal.put(threadID, baseSubject);
		}
		
		return retVal;
	}
	
	public static Map<Integer,Integer> getVirtualThreadMappings(Collection<Set<Integer>> sameSubjectThreads, Map<Integer,String> threadBaseSubjects) throws MessagingException{
		
		Map<Integer, Integer> retVal = new TreeMap<Integer, Integer>();
		
		Integer currVirtualThread = 1;
		for(Set<Integer> threadSet : sameSubjectThreads){
			String baseSubject = null;
			for(Integer threadID: threadSet){
				baseSubject = threadBaseSubjects.get(threadID);
				if(baseSubject != null) break;
			}
			
			if(baseSubject == null || baseSubject.equals("") || baseSubject.equals("<none>")){
				for(Integer threadID: threadSet){
					retVal.put(threadID, currVirtualThread);
					currVirtualThread++;
				}
			}else{
				for(Integer threadID: threadSet){
					retVal.put(threadID, currVirtualThread);
				}
				currVirtualThread++;
			}
		}
		
		Set<Integer> oldThreadIDs = new TreeSet<Integer>(threadBaseSubjects.keySet());
		Set<Integer> newThreadIDs = new TreeSet<Integer>(retVal.keySet());
		boolean match = newThreadIDs.containsAll(oldThreadIDs);
		
		return retVal;
	}
	
	public static Map<File, Integer> getFileToVirtualThreadID(Map<File, Integer> fileToThreadID, Map<Integer, Integer> threadToVirtualThread){
		Map<File,Integer> retVal = new HashMap<File, Integer>();
		for(Entry<File, Integer> entry: fileToThreadID.entrySet()){
			File file = entry.getKey();
			Integer threadID = entry.getValue();
			if(threadID == null){
				 throw new RuntimeException("null threadID");
			}
			Integer virtualThreadID = threadToVirtualThread.get(threadID);
			if(virtualThreadID == null){
				 throw new RuntimeException("null virtualThreadID");
			}
			retVal.put(file, virtualThreadID);
		}
		return retVal;
	}
	
	public static Collection<Set<Integer>> sameSubjectThreads(Map<MimeMessage,File> messageToFile, Map<File,Integer> fileToThreadId) throws UnsupportedEncodingException, MessagingException{
		
		
		
		

		Map<String,Set<Integer>> subjectToThreads = new HashMap<String, Set<Integer>>();
		for(Entry<MimeMessage, File> entry: messageToFile.entrySet()){
			File file = entry.getValue();
			MimeMessage msg = entry.getKey();
			SummarizedMessage summarizedMessage = new SummarizedMessage(msg);
			String baseSubject = summarizedMessage.getBaseSubject();
			
			Set<Integer> pastThreads = subjectToThreads.get(baseSubject);
			if(pastThreads == null){
				pastThreads = new TreeSet<Integer>();
				subjectToThreads.put(baseSubject, pastThreads);
			}
			
			Integer threadID = fileToThreadId.get(file);
			pastThreads.add(threadID);
			
		}
		
		Collection<Set<Integer>> retVal = new ArrayList<Set<Integer>>();
		retVal.addAll(subjectToThreads.values());
		return retVal;
	}
	
	
	public static Map<File,Integer> mapFileToThreadID(ArrayList<File> postFiles) throws IOException, MessagingException{
		
		Map<MimeMessage,File> postToFile = PostLoader.loadPostToFileMapping(postFiles);
		Map<String, EmailThread> threads = sortThreads(postToFile.keySet());
		
		Map<File, Integer> retVal = new HashMap<File,Integer>();
		int threadID = 1;
		for(EmailThread thread: threads.values()){
			for(SummarizedMessage root: thread.getRoots()){
				
				addFileToThreadMapping(postToFile, root, threadID, retVal);
				threadID++;
			}
		}
		return retVal;
	}
	
	private static void addFileToThreadMapping(Map<MimeMessage,File> postToFile, SummarizedMessage summarizedMessage, int threadID, Map<File, Integer> threadMapping){
		if(summarizedMessage.getMessage() != null){
			File file = postToFile.get(summarizedMessage.getMessage());
			threadMapping.put(file, threadID);
		}
		for(SummarizedMessage child: summarizedMessage.getChildren()){
			addFileToThreadMapping(postToFile, child, threadID, threadMapping);
		}
	}
	
	public static Map<String, EmailThread> sortThreads(Collection<MimeMessage> posts) throws MessagingException {
		Map<String, EmailThread> threads = new TreeMap<String, EmailThread>();
		for(MimeMessage post: posts){
			SummarizedMessage summarizedPost = new SummarizedMessage(post);
			
			String cleanedSubject;
			try {
				cleanedSubject = summarizedPost.getBaseSubject();
				
				EmailThread thread = threads.get(cleanedSubject);
				if(thread == null){
					thread = new EmailThread();
					threads.put(cleanedSubject, thread);
				}
				
				thread.addElement(summarizedPost.getMessage());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		for(EmailThread thread: threads.values()){
			thread.clean();
		}
		return threads;
	}
	
	
	public static Map<String, EmailThread> sortThreads(ArrayList<File> postList) throws IOException, MessagingException{
		
		ArrayList<MimeMessage> posts = new ArrayList<MimeMessage>();
		for(File postLoc: postList){
			MimeMessage post = PostLoader.getPost(postLoc);
			posts.add(post);
		}
		return sortThreads(posts);
	}
	
	private static void subtractFromThread(SummarizedMessage thread, Map<MimeMessage, File> messageToFile, ArrayList<File> fileList){
			MimeMessage message = thread.getMessage();
			if(message != null){
				File file = messageToFile.get(message);
				if(file != null){
					if(fileList.contains(file)){
						fileList.remove(file);
					}else{
						//throw new RuntimeException("Missing file from file list"+fileList);
					}
				}
			}
			
			for(SummarizedMessage child: thread.getChildren()){
				subtractFromThread(child, messageToFile, fileList);
			}
	}
	
	public static void test() throws IOException, MessagingException{
		File testFolder = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\cleaned newsgroups");
		File fileListLoc = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\Cleaned Post List.txt");
		
		//Test mapping messages to file
		PostLister lister = new PostLister();
		ArrayList<File> fileList = lister.loadPostList(testFolder, fileListLoc);
		System.out.println("loaded file list");
		Map<MimeMessage, File> messageToFile = PostLoader.loadPostToFileMapping(fileList);
		System.out.println("loaded message to file mappings");
		
		Map<String, EmailThread> threads = sortThreads(messageToFile.keySet());
		for(Entry<String, EmailThread> entry: threads.entrySet()){
			EmailThread thread = entry.getValue();
			for(SummarizedMessage root: thread.getRoots()){
				subtractFromThread(root, messageToFile, fileList);
			}
		}
		System.out.println(fileList.size());
	}

}
