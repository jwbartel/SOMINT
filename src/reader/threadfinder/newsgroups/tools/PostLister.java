package reader.threadfinder.newsgroups.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import reader.threadfinder.EmailThread;
import reader.threadfinder.IteratedThreadStatistics;
import reader.threadfinder.ThreadStatistics;
import bus.accounts.FileNameByOS;
import bus.tools.Histogram;

public class PostLister {
	
	private void traverseFileTree(File loc, NewsgroupPostHandler handler){
		if(!loc.isDirectory()){
			handler.handle(loc);
		}else{
			
			File[] subLocs = loc.listFiles();
			for(File subLoc: subLocs){
				traverseFileTree(subLoc, handler);
			}
			
		}
	}
	
	public ArrayList<File> buildList(File root){
		root = FileNameByOS.getMappedNewsgroupFile(root);
		
		ArrayList<File> fileList = new ArrayList<File>();
		NewsgroupPostHandler handler = new PostListHandler(fileList);
		traverseFileTree(root, handler);
		
		return fileList;
		
	}
	
	public ArrayList<File> buildNonRepeatingList(File root){
		root = FileNameByOS.getMappedNewsgroupFile(root);
		
		Map<String, File> idToFileMap = new HashMap<String, File>();
		NewsgroupPostHandler handler = new NonrepeatingPostListHandler(idToFileMap);
		traverseFileTree(root, handler);
		
		ArrayList<File> fileList = new ArrayList<File>();
		for(File post: idToFileMap.values()){
			fileList.add(post);
		}
		return fileList;
		
	}
	
	public ArrayList<File> buildOrderedNonRepeatingList(File root){

		root = FileNameByOS.getMappedNewsgroupFile(root);
		
		Map<Date, ArrayList<File>> dateToFileMap = new TreeMap<Date, ArrayList<File>>();
		NewsgroupPostHandler handler = new OrderedNonRepeatingPostListHandler(dateToFileMap);
		traverseFileTree(root, handler);
		
		ArrayList<File> fileList = new ArrayList<File>();
		for(Entry<Date, ArrayList<File>> entry : dateToFileMap.entrySet()){
			for(File post: entry.getValue()){
				fileList.add(post);
			}
		}
		return fileList;
	}
	
	public void writeFileList(ArrayList<File> files, File root, File dest) throws IOException{
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		for(File file: files){
			out.write(file.getPath().substring(root.getPath().length()+1));
			out.newLine();
		}
		
		out.flush();
		out.close();
	}
	
	public static void copyFile(File source, File dest) throws IOException {
		File parent = dest.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		if(!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		}
		finally {
			if(in != null) {
				in.close();
			}
			if(out != null) {
				out.close();
			}
		}
	}
	
	public ArrayList<File> loadPostList(File postsRoot, File listFile) throws IOException{
		ArrayList<File> retVal = new ArrayList<File>();
		BufferedReader in = new BufferedReader(new FileReader(listFile));
		String line = in.readLine();
		while(line != null){
			retVal.add(new File(postsRoot, line));
			line = in.readLine();
		}
		return retVal;
	}
	
	public void copyFilesInList(File list, File oldRoot, File newRoot) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(list));
		String file = in.readLine();
		while(file != null){
			File oldFile = new File(oldRoot, file);
			File newFile = new File(newRoot, file);
			
			copyFile(oldFile, newFile);
			
			file = in.readLine();
		}
	}
	
	public void writeFileThreadAssociations(File fileListLoc, File root, File dest) throws IOException, MessagingException{
		ArrayList<File> fileList = loadPostList(root, fileListLoc);
		Map<File, Integer> fileToThreadID = NewsgroupThreadFinder.mapFileToThreadID(fileList);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(File postLoc: fileList){
			String relativePath = postLoc.getPath().substring(root.getPath().length()+1);
			Integer threadID = fileToThreadID.get(postLoc);
			if(threadID == null){
				throw new RuntimeException("null thread for post: "+root);
			}
			out.write(threadID+"\t"+relativePath);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public Map<File, Integer> loadFileThreadAssociations(File assocSrc, File postRoot) throws IOException, MessagingException{
		Map<File,Integer> fileToThreadMap = new TreeMap<File, Integer>();
		
		BufferedReader in = new BufferedReader(new FileReader(assocSrc));
		String line = in.readLine();
		while(line != null){
			int splitPt = line.indexOf("\t");
			int threadId = Integer.parseInt(line.substring(0, splitPt));
			String relativePath = line.substring(splitPt + 1);
			File file = new File(postRoot, relativePath);
			
			fileToThreadMap.put(file, threadId);
			
			line = in.readLine();
		}
		return fileToThreadMap;
	}
	
	public void writeSameSubjectThreads(File fileListLoc, Map<File,Integer> fileToThreadId, File root, File dest) throws IOException, MessagingException{
		ArrayList<File> files = loadPostList(root, fileListLoc);
		Map<MimeMessage, File> messageToFile = PostLoader.loadPostToFileMapping(files);
		Collection<Set<Integer>> linkedThreads = NewsgroupThreadFinder.sameSubjectThreads(files, messageToFile, fileToThreadId);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(Set<Integer> linkedSet: linkedThreads){
			boolean isStart = true;
			for(Integer threadID: linkedSet){
				if(isStart){
					isStart = false;
				}else{
					out.write(",");
				}
				out.write(""+threadID);
			}
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void writeFinalThreadStatistics(Map<Integer, Collection<MimeMessage>> threadMessageCollections, File dest) throws MessagingException, IOException{
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		for(Entry<Integer, Collection<MimeMessage>> entry: threadMessageCollections.entrySet()){
			Collection<MimeMessage> threadMessages = entry.getValue();
			ThreadStatistics stats = new ThreadStatistics(threadMessages);
			
			out.write(""+entry.getKey());
			out.newLine();
			out.write("\tmessage count:"+stats.getNumMessages());
			out.newLine();
			if(stats.getTimeLastMessage() != null){
				out.write("\tlast message:"+stats.getTimeLastMessage());
				out.newLine();
			}
			out.write("\taverage time between messages:"+stats.getAverageTimeBetweenMessages());
			out.newLine();
			out.write("\ttotal life time:"+stats.getTotalTime());
			out.newLine();
			out.write("\trecipients:");
			boolean isFirstRecipint = true;
			for(String recipient: stats.getRecipients()){
				if(!isFirstRecipint){
					out.write(",");
				}
				out.write(recipient);
				isFirstRecipint = false;
			}
			out.newLine();
			out.write("\tsubject word frequencies:");
			out.newLine();
			Map<String,Integer> subjectWordFrequencies = stats.getSubjectWordFrequencies();
			for(Entry<String,Integer> freqEntry: subjectWordFrequencies.entrySet()){
				String word = freqEntry.getKey();
				Integer freq = freqEntry.getValue();
				out.write("\t\t"+freq+"\t"+word);
				out.newLine();
			}
		}
		
		out.flush();
		out.close();
	}
	
	public Map<Integer, ThreadStatistics> loadFinalThreadStatistics(File src, File postRoot) throws IOException {
		
		Map<Integer, ThreadStatistics> finalThreadStatistics = new HashMap<Integer, ThreadStatistics>();
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		while(line != null){
			
			Integer threadID = Integer.parseInt(line);
			ThreadStatistics threadStatistics = new ThreadStatistics();
			finalThreadStatistics.put(threadID, threadStatistics);
			line = in.readLine();
			
			while(line != null && line.startsWith("\t")){
				
				if(line.startsWith("\tmessage count:")){
					threadStatistics.setNumMessages(Integer.parseInt(line.substring("\tmessage count:".length())));
				}else if(line.startsWith("\tlast message:")){
					Date lastDate = new Date(Date.parse(line.substring("\tlast message:".length())));
					threadStatistics.setTimeLastMessage(lastDate);
				}else if(line.startsWith("\taverage time between messages:")){
					threadStatistics.setAverageTimeBetweenMessages(Double.parseDouble(line.substring("\taverage time between messages:".length())));
				}else if(line.startsWith("\ttotal life time:")){
					threadStatistics.setTotalTime(Long.parseLong(line.substring("\ttotal life time:".length())));
				}else if(line.startsWith("\trecipients:")){
					Set<String> recipients = new TreeSet<String>();
					String[] splitRecipients = line.substring("\trecipients:".length()).split(",");
					for(String recipient: splitRecipients){
						recipients.add(recipient);
					}
					threadStatistics.setRecipients(recipients);
				}else if(line.equals("\tsubject word frequencies:")){
					line = in.readLine();
					Map<String, Integer> subjectWordFreqs = new TreeMap<String, Integer>();
					while(line != null && line.startsWith("\t\t")){
						line = line.substring(2);
						
						int splitPt = line.indexOf('\t');
						
						Integer freq = Integer.parseInt(line.substring(0,splitPt));
						String word = line.substring(splitPt+1);
						subjectWordFreqs.put(word, freq);
						
						line = in.readLine();
					}
					threadStatistics.setSubjectWordFrequencies(subjectWordFreqs);
					continue;
				}
				
				line = in.readLine();
			}
		}
		
		return finalThreadStatistics;
	}
	
	public void writeThreadSizeHistogram(Map<Integer, ThreadStatistics> finalThreadStatistics, File dest) throws IOException{
		
		Histogram messageCountsHist = new Histogram();
		
		for(ThreadStatistics stats: finalThreadStatistics.values()){
			if(stats.getNumMessages() > 1){
				messageCountsHist.addValue(stats.getNumMessages());
			}
		}
		
		messageCountsHist.write(dest);
	}
	
	public void writeThreadTotalTimeHistogram(Map<Integer, ThreadStatistics> finalThreadStatistics, File dest) throws IOException{
		
		Histogram messageCountsHist = new Histogram(30);
		
		for(ThreadStatistics stats: finalThreadStatistics.values()){
			if(stats.getNumMessages() > 1){
				messageCountsHist.addValue(((double) stats.getTotalTime())/60000);
			}
		}
		
		messageCountsHist.write(dest);
	}
	
	public void writeTimeBetweenThreadsHistogram(ArrayList<IteratedThreadStatistics> iteratedThreadStatistics, File dest) throws IOException{
		
		Set<Integer> seenThreads = new HashSet<Integer>();
		Set<Long> creationDates = new TreeSet<Long>();
		
		for(IteratedThreadStatistics iterStats: iteratedThreadStatistics){
			if(!seenThreads.contains(iterStats.getThreadID()) && iterStats.getStatistics().getNumMessages() > 1 && iterStats.getStatistics().getTimeLastMessage() != null){
				long startTime = iterStats.getStatistics().getTimeLastMessage().getTime() - iterStats.getStatistics().getTotalTime();
				creationDates.add(startTime);
				seenThreads.add(iterStats.getThreadID());
			}
		}
		
		Histogram messageCountsHist = new Histogram(1);
		
		Long prevCreationDate = null;
		for(Long creationDate: creationDates){
			if(prevCreationDate != null){
				messageCountsHist.addValue(((double) (creationDate - prevCreationDate))/10);
			}
			
			prevCreationDate = creationDate;
		}
		
		messageCountsHist.write(dest);
	}
	
	public void writeThreadsToVirtualThreads(ArrayList<File> fileList, Map<Integer, File> threadTerminationPoints, Map<File,Integer> fileToThreadID, File root, File dest) throws IOException, MessagingException{
		Map<MimeMessage, File> messageToFile = PostLoader.loadPostToFileMapping(fileList);
		Map<String, EmailThread> threads = NewsgroupThreadFinder.sortThreads(messageToFile.keySet());
		Map<Integer, String> threadsToBaseSubject = NewsgroupThreadFinder.getThreadsToBaseSubject(threadTerminationPoints, messageToFile, fileToThreadID);
		Collection<Set<Integer>> sameSubjectThreads = NewsgroupThreadFinder.sameSubjectThreads(messageToFile, fileToThreadID);
		Map<Integer, Integer> threadToVirtualThread = NewsgroupThreadFinder.getVirtualThreadMappings(sameSubjectThreads, threadsToBaseSubject);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(Entry<Integer,Integer> entry: threadToVirtualThread.entrySet()){
			Integer thread = entry.getKey();
			Integer virtualThread = entry.getValue();
			out.write(""+thread+"\t"+virtualThread);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public Map<Integer,Integer> loadThreadsToVirtualThreads(File src) throws IOException{
		
		Map<Integer, Integer> retVal = new TreeMap<Integer, Integer>();
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		while(line != null){
			String[] split = line.split("\t");
			Integer threadID = Integer.parseInt(split[0]);
			Integer virtualThreadID = Integer.parseInt(split[1]);
			
			retVal.put(threadID, virtualThreadID);
			
			line = in.readLine();
		}
		in.close();
		
		return retVal;
	}
	
	public void writeVirtualThreadIDs(Map<File,Integer> fileToThreadID, Map<Integer, Integer> threadToVirtualThread, File root, File dest) throws IOException{
		
		Map<File, Integer> fileToVirtualThread = NewsgroupThreadFinder.getFileToVirtualThreadID(fileToThreadID, threadToVirtualThread);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(Entry<File,Integer> entry: fileToVirtualThread.entrySet()){
			String fileLoc = entry.getKey().getPath();
			Integer virtualThreadID = entry.getValue();
			if(virtualThreadID == null){
				throw new RuntimeException("Null virtual thread ID:"+fileLoc);
			}
			String relativeFileLoc = fileLoc.substring(root.getPath().length()+1);
			out.write(""+virtualThreadID+"\t"+relativeFileLoc);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void writeIteratedThreadStatistics(ArrayList<File> fileList,
			Map<File, Integer> fileToThreadID, File root, File dest) 
			throws IOException, MessagingException{
		
		Map<Integer, Collection<MimeMessage>> threadMessageCollections = new HashMap<Integer, Collection<MimeMessage>>();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(File file: fileList){
			
			Integer threadID = fileToThreadID.get(file);
			MimeMessage message = PostLoader.getPost(file);
			out.write(file.getPath().substring(root.getPath().length()+1));
			out.newLine();
			if(threadMessageCollections.containsKey(threadID)){
				Collection<MimeMessage> messages = threadMessageCollections.get(threadID);
				messages.add(message);
				ThreadStatistics stats = new ThreadStatistics(messages);
				out.write("\tupdated thread:"+threadID);
				out.newLine();
				out.write("\tmessage count:"+stats.getNumMessages());
				out.newLine();
				if(stats.getTimeLastMessage() != null){
					out.write("\tlast message:"+stats.getTimeLastMessage());
					out.newLine();
				}
				out.write("\taverage time between messages:"+stats.getAverageTimeBetweenMessages());
				out.newLine();
				out.write("\ttotal life time:"+stats.getTotalTime());
				out.newLine();
				out.write("\trecipients:");
				boolean isFirstRecipint = true;
				for(String recipient: stats.getRecipients()){
					if(!isFirstRecipint){
						out.write(",");
					}
					out.write(recipient);
					isFirstRecipint = false;
				}
				out.newLine();
			}else{
				Collection<MimeMessage> messages = new ArrayList<MimeMessage>();
				threadMessageCollections.put(threadID, messages);
				messages.add(message);
				ThreadStatistics stats = new ThreadStatistics(messages);
				
				out.write("\tnew thread:"+threadID);
				out.newLine();
				out.write("\tmessage count:"+stats.getNumMessages());
				out.newLine();
				if(stats.getTimeLastMessage() != null){
					out.write("\tlast message:"+stats.getTimeLastMessage());
					out.newLine();
				}
				out.write("\taverage time between messages:"+stats.getAverageTimeBetweenMessages());
				out.newLine();
				out.write("\ttotal life time:"+stats.getTotalTime());
				out.newLine();
				out.write("\trecipients:");
				boolean isFirstRecipint = true;
				for(String recipient: stats.getRecipients()){
					if(!isFirstRecipint){
						out.write(",");
					}
					out.write(recipient);
					isFirstRecipint = false;
				}
				out.newLine();
				out.write("\tsubject word frequencies:");
				out.newLine();
				Map<String,Integer> subjectWordFrequencies = stats.getSubjectWordFrequencies();
				for(Entry<String,Integer> freqEntry: subjectWordFrequencies.entrySet()){
					String word = freqEntry.getKey();
					Integer freq = freqEntry.getValue();
					out.write("\t\t"+freq+"\t"+word);
					out.newLine();
				}
			}
			
		}
		out.flush();
		out.close();
		
	}
	
	public ArrayList<IteratedThreadStatistics> loadIteratedThreadStatistics(File src, File postRoot) throws IOException{
		ArrayList<IteratedThreadStatistics> retVal = new ArrayList<IteratedThreadStatistics>();
		Map<Integer, ThreadStatistics> seenThreads = new HashMap<Integer, ThreadStatistics>();
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		while(line != null){
			
			String relativeLoc = line;
			File postLoc = new File(postRoot, relativeLoc);
			
			line = in.readLine();
			
			ThreadStatistics stats;
			Integer threadID;
			
			
			
			if(line.startsWith("\tnew thread:")){
				//Create new thread
				threadID = Integer.parseInt(line.substring("\tnew thread:".length()));
				stats = new ThreadStatistics();
				seenThreads.put(threadID, stats);
			}else{
				//Update an existing thread
				threadID = Integer.parseInt(line.substring("\tupdated thread:".length()));
				stats = seenThreads.get(threadID);
				if(stats == null){
					stats = new ThreadStatistics();
					seenThreads.put(threadID, stats);
					throw new RuntimeException("null stats");
				}
				stats = new ThreadStatistics(stats);
			}
				
			
			line = in.readLine();
			while(line != null && line.startsWith("\t")){

				if(line.startsWith("\tmessage count:")){

					Integer numMessages = Integer.parseInt(line.substring("\tmessage count:".length()));
					stats.setNumMessages(numMessages);

				}else if(line.startsWith("\tlast message:")){

					Date timeLastMessage = new Date(Date.parse(line.substring("\tlast message:".length())));
					stats.setTimeLastMessage(timeLastMessage);

				}else if(line.startsWith("\taverage time between messages:")){

					Double averageTimeBetweenMessages = Double.parseDouble(line.substring("\taverage time between messages:".length()));
					stats.setAverageTimeBetweenMessages(averageTimeBetweenMessages);

				}else if(line.startsWith("\ttotal life time:")){

					Long totalTime = Long.parseLong(line.substring("\ttotal life time:".length()));
					stats.setTotalTime(totalTime);

				}else if(line.startsWith("\trecipients:")){
					Set<String> recipients = new TreeSet<String>();
					String[] splitRecipients = line.substring("\trecipients:".length()).split(",");
					for(String recipient: splitRecipients){
						recipients.add(recipient);
					}
					stats.setRecipients(recipients);
				}else if(line.startsWith("\tsubject word frequencies:")){

					Map<String, Integer> subjectWordFreqs = new TreeMap<String, Integer>();
					line = in.readLine();
					while(line != null && line.startsWith("\t\t")){

						line = line.substring(2);
						String[] split = line.split("\t");
						Integer freq = Integer.parseInt(split[0]);
						String word = split[1];

						subjectWordFreqs.put(word, freq);

						line = in.readLine();
					}
					stats.setSubjectWordFrequencies(subjectWordFreqs);
					continue;
				}

				line = in.readLine();
			}
			
			IteratedThreadStatistics iteratedStats = new IteratedThreadStatistics(threadID, postLoc, stats);
			retVal.add(iteratedStats);
		}
		
		return retVal;
	}
	
	public void writeLastFileOFThreads(ArrayList<File> fileList, Map<File, Integer> fileToThreadID, File root, File dest) throws IOException{
		Map<Integer, File> threadIDToFinalFile = new TreeMap<Integer, File>();
		for(File file: fileList){
			Integer threadID = fileToThreadID.get(file);
			threadIDToFinalFile.put(threadID, file);
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(Entry<Integer, File> entry: threadIDToFinalFile.entrySet()){
			out.write(""+entry.getKey()+"\t"+entry.getValue().getPath().substring(root.getPath().length()+1));
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public Map<Integer, File> loadThreadToLastFile(File src, File postRoot) throws IOException{
		Map<Integer, File> threadToLastFile = new HashMap<Integer, File>();
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		while(line != null){
			
			int splitPt = line.indexOf('\t');
			Integer threadId = Integer.parseInt(line.substring(0, splitPt));
			File lastFile = new File(postRoot, line.substring(splitPt+1));
			threadToLastFile.put(threadId, lastFile);
			
			line = in.readLine();
		}
		in.close();
		
		return threadToLastFile;
	}
	
	
	public void writeAllSubjectWords(Map<Integer, ThreadStatistics> finalThreadStatistics, File dest) throws IOException{
		Set<String> words = new TreeSet<String>();
		
		for(ThreadStatistics threadStats: finalThreadStatistics.values()){
			if(threadStats.getNumMessages()>1){
				words.addAll(threadStats.getSubjectWordFrequencies().keySet());
			}
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(String word: words){
			out.write(word);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public void writeAllRecipients(ArrayList<IteratedThreadStatistics> iteratedThreadStats, File dest) throws IOException{
		Set<String> recipients = new TreeSet<String>();
		
		for(IteratedThreadStatistics threadStats: iteratedThreadStats){
			recipients.addAll(threadStats.getStatistics().getRecipients());
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(String recipient: recipients){
			out.write(recipient);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public Map<String,Integer> loadWordPositions(File src) throws IOException{
		Map<String, Integer> words = new HashMap<String, Integer>();
		
		int position = 0;
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		while(line != null){
			
			words.put(line, position);
			
			position++;
			line = in.readLine();
		}
		in.close();
		
		return words;
	}
	
	public void writeVectoredData(Map<Integer, ArrayList<IteratedThreadStatistics>> sortedIteratedStats,
			Map<String,Integer> subjectWordPositions, Map<String, Integer> recipientPositions,
			File xDest, File yDest, File zDest) throws IOException{
		
		BufferedWriter xOut = new BufferedWriter(new FileWriter(xDest));
		BufferedWriter yOut = new BufferedWriter(new FileWriter(yDest));
		BufferedWriter zOut = new BufferedWriter(new FileWriter(zDest));
		
		for(Integer threadID : sortedIteratedStats.keySet()){
			ArrayList<IteratedThreadStatistics> iteratedStatsList = sortedIteratedStats.get(threadID);
			Date prevDate = null;
			if(iteratedStatsList.size() <= 1){
				continue;
			}
			for(int i=0; i<iteratedStatsList.size(); i++){
				IteratedThreadStatistics iteratedStats = iteratedStatsList.get(i);
				
				Date currDate = iteratedStats.getLatestDate();
				if(currDate == null){
					continue;
				}
				double y = (i==iteratedStatsList.size()-1)? -1: +1;
				String z = (y==-1)? "Inf": null;
				if(z==null){
					Date nextTime = iteratedStatsList.get(i+1).getLatestDate();
					if(nextTime == null){
						continue;
					}
					z = "" + (nextTime.getTime() - currDate.getTime());
				}
				
				double[] vector = iteratedStats.getVector(prevDate, currDate, recipientPositions);//, subjectWordPositions);
				if(vector == null){
					continue;
				}
				
				String vectorStr = Arrays.toString(vector);
				xOut.write(vectorStr.substring(1, vectorStr.length()-1).replaceAll(", ", ","));
				xOut.newLine();
				
				yOut.write(""+y);
				yOut.newLine();
				
				zOut.write(z);
				zOut.newLine();
				
				prevDate = currDate;
			}
			
			
		}
		
		xOut.flush();
		xOut.close();
		
		yOut.flush();
		yOut.close();
		
		zOut.flush();
		zOut.close();
		System.out.println("Precomputes for "+sortedIteratedStats.size()+" threads.");
	}
	
	public void writeTruncatedVectoredData(int bufferSize, ArrayList<IteratedThreadStatistics> iteratedStatsList, 
			Map<Integer, File> threadToFinalFile, Map<String,Integer> subjectWordPositions,
			File xDest, File yDest) throws IOException{
		
		BufferedWriter xOut = new BufferedWriter(new FileWriter(xDest));
		BufferedWriter yOut = new BufferedWriter(new FileWriter(yDest));
		
		Set<Integer> completedThreads = new TreeSet<Integer>();
		Set<File> completingFiles = new TreeSet<File>(threadToFinalFile.values());
		ArrayList<Integer> unknownAliveOrDeadPastThreads = new ArrayList<Integer>();
		Map<Integer, IteratedThreadStatistics> pastThreads = new TreeMap<Integer, IteratedThreadStatistics>();
		
		
		for(IteratedThreadStatistics iteratedStats: iteratedStatsList){
			Date currDate = iteratedStats.getStatistics().getTimeLastMessage();
			if(currDate == null || iteratedStats.getStatistics().getNumMessages() < 2){
				continue;
			}
			
			Random random = new Random();
			int numTestUnknownThreads = 0;
			if(unknownAliveOrDeadPastThreads.size()>0){
				numTestUnknownThreads = random.nextInt(unknownAliveOrDeadPastThreads.size()+1);
			}
			
			ArrayList<Integer> testThreads = new ArrayList<Integer>();
			if(numTestUnknownThreads > 0){
				testThreads = new ArrayList<Integer>(unknownAliveOrDeadPastThreads);
				while(testThreads.size() > numTestUnknownThreads){
					int ignoredIndex = random.nextInt(testThreads.size());
					testThreads.remove(ignoredIndex);
				}
			}
			
			if(completedThreads.size() > 0 && testThreads.size() > 0){
				ArrayList<Integer> testCompletedThreads = new ArrayList<Integer>(completedThreads);
				while(testCompletedThreads.size() > numTestUnknownThreads){
					int ignoredIndex = random.nextInt(testThreads.size());
					testCompletedThreads.remove(ignoredIndex);
				}
				testThreads.addAll(testCompletedThreads);
			}
			
			
			
			for(Integer pastThreadID: testThreads){
				
				IteratedThreadStatistics stats = pastThreads.get(pastThreadID);
				
				double y = (completedThreads.contains(pastThreadID))? -1: +1;
				double[] vector = stats.getVector(currDate);//, subjectWordPositions);
				if(vector == null){
					continue;
				}
				
				String vectorStr = Arrays.toString(vector);
				xOut.write(vectorStr.substring(1, vectorStr.length()-1).replaceAll(", ", ","));
				xOut.newLine();
				
				yOut.write(""+y);
				yOut.newLine();
				
				if(completedThreads.contains(pastThreadID)){
					unknownAliveOrDeadPastThreads.remove(pastThreadID);
				}
			}
			
			
			
			if(iteratedStats.getStatistics().getNumMessages() >= 2){
				if(!unknownAliveOrDeadPastThreads.contains(iteratedStats.getThreadID())){
					unknownAliveOrDeadPastThreads.add(iteratedStats.getThreadID());
				}
				pastThreads.put(iteratedStats.getThreadID(), iteratedStats);
			}
			if(completingFiles.contains(iteratedStats.getPostLocation())){
				completedThreads.add(iteratedStats.getThreadID());
			}
		}
		
		xOut.flush();
		xOut.close();
		
		yOut.flush();
		yOut.close();
		System.out.println(iteratedStatsList.size());
	}
	
	public void writeBufferedVectoredData(int bufferSize, ArrayList<IteratedThreadStatistics> iteratedStatsList, 
			Map<Integer, File> threadToFinalFile, Map<String,Integer> subjectWordPositions,
			File xDest, File yDest) throws IOException{
		
		BufferedWriter xOut = new BufferedWriter(new FileWriter(xDest));
		BufferedWriter yOut = new BufferedWriter(new FileWriter(yDest));
		
		Set<Integer> completedThreads = new TreeSet<Integer>();
		Set<File> completingFiles = new TreeSet<File>(threadToFinalFile.values());
		ArrayList<Integer> pastThreadsBuffer = new ArrayList<Integer>();
		Map<Integer, IteratedThreadStatistics> pastThreads = new TreeMap<Integer, IteratedThreadStatistics>();
		
		
		for(IteratedThreadStatistics iteratedStats: iteratedStatsList){
			Date currDate = iteratedStats.getStatistics().getTimeLastMessage();
			if(currDate == null || iteratedStats.getStatistics().getNumMessages() < 2){
				continue;
			}
			
			for(Integer pastThreadID: pastThreadsBuffer){
				
				IteratedThreadStatistics stats = pastThreads.get(pastThreadID);
				
				double y = (completedThreads.contains(pastThreadID))? -1: +1;
				double[] vector = stats.getVector(currDate);//, subjectWordPositions);
				if(vector == null){
					continue;
				}
				
				String vectorStr = Arrays.toString(vector);
				xOut.write(vectorStr.substring(1, vectorStr.length()-1).replaceAll(", ", ","));
				xOut.newLine();
				
				yOut.write(""+y);
				yOut.newLine();
			}
			
			
			
			if(iteratedStats.getStatistics().getNumMessages() >= 2){
				pastThreadsBuffer.remove(iteratedStats.getThreadID());
				pastThreadsBuffer.add(iteratedStats.getThreadID());
				if(pastThreadsBuffer.size() > bufferSize){
					pastThreadsBuffer.remove(0);
				}
				pastThreads.put(iteratedStats.getThreadID(), iteratedStats);
			}
			if(completingFiles.contains(iteratedStats.getPostLocation())){
				completedThreads.add(iteratedStats.getThreadID());
			}
		}
		
		xOut.flush();
		xOut.close();
		
		yOut.flush();
		yOut.close();
		System.out.println(iteratedStatsList.size());
	}
	
	public void writeGaussianDistributions(Map<Integer, ThreadStatistics> finalThreadStatistics, ArrayList<IteratedThreadStatistics> iterStats,
		File msgCountDest, File totalTimeDest, File timeBetweenDest) throws IOException{
		
		GaussianFinder.writeDistribution(GaussianFinder.findGaussianThreadLength(finalThreadStatistics), msgCountDest);
		GaussianFinder.writeDistribution(GaussianFinder.findGaussianTotalThreadTime(finalThreadStatistics), totalTimeDest);
		GaussianFinder.writeDistribution(GaussianFinder.findGaussianTimeBetweenThreads(iterStats), timeBetweenDest);
		
	}
	
	public void sortIntoFolds(File xFile, File yFile, File destfolder, int k) throws IOException{
		
		destfolder.mkdirs();
		
		BufferedReader in = new BufferedReader(new FileReader(yFile));
		int entryCount = 0;
		String line = in.readLine();
		while(line != null){
			entryCount++;
			line = in.readLine();
		}
		in.close();
		
		int foldSize = entryCount/k;
		int modSize = entryCount%k;
		System.out.println(entryCount);
		int sum = 0;
		
		BufferedReader xIn = new BufferedReader(new FileReader(xFile));
		BufferedReader yIn = new BufferedReader(new FileReader(yFile));
		
		for(int i=1; i<=k; i++){
			
			File foldXFile = new File(destfolder, ""+i+"_X.csv");
			File foldYFile = new File(destfolder, ""+i+"_Y.csv");
			
			BufferedWriter xOut = new BufferedWriter(new FileWriter(foldXFile));
			BufferedWriter yOut = new BufferedWriter(new FileWriter(foldYFile));
			
			int numInFold = foldSize + ((i>modSize)?0:1);
			sum+= numInFold;
			
			for(int j=0; j<numInFold; j++){
				String xVal = xIn.readLine();
				String yVal = yIn.readLine();
				if(xVal == null || yVal == null){
					throw new RuntimeException("null values");
				}
				xOut.write(xVal);
				xOut.newLine();
				
				yOut.write(yVal);
				yOut.newLine();
			}
			
			xOut.flush();
			xOut.close();
			
			yOut.flush();
			yOut.close();
		}
		
		xIn.close();
		yIn.close();
		System.out.println(sum);
	}
	
	public static void test(){
		File testFolder = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\cleaned newsgroups");
	}
	
	public static void main(String[] args) throws IOException, MessagingException{
		
		PostLister lister = new PostLister();
		
		//File root = new File("/home/bartizzi/Workspaces/Machine Learning/data/cleaned newsgroups");
		//File precomputesLoc = new File("/home/bartizzi/Workspaces/Machine Learning/data/precomputes");
		File root = new File("C:\\Users\\Jacob\\Workspaces\\Machine Learning\\data\\cleaned newsgroups");
		File precomputesLoc = new File("C:\\Users\\Jacob\\Workspaces\\Machine Learning\\data\\precomputes");
		
		NewsgroupPrecomputeBuilder builder = new NewsgroupPrecomputeBuilder(root, precomputesLoc);
		ArrayList<IteratedThreadStatistics> iteratedThreadStats = builder.getIteratedThreadStatistics();
		Map<Integer, ArrayList<IteratedThreadStatistics>> sortedThreadStats = builder.getSortedIteratedThreadStatistics(iteratedThreadStats);
		
		DescriptiveStatistics threadSizes = new DescriptiveStatistics();
		DescriptiveStatistics responseTimes = new DescriptiveStatistics();
		for(ArrayList<IteratedThreadStatistics> threadPointSet: sortedThreadStats.values()) {
			Date timeLastMessage = null;
			if(threadPointSet.size() < 2) {
				continue;
			}
			threadSizes.addValue(threadPointSet.size());
			for(int i=0; i < threadPointSet.size(); i++) {
				IteratedThreadStatistics statsPoint = threadPointSet.get(i);
				if (timeLastMessage != null && statsPoint.getLatestDate() != null) {
					responseTimes.addValue(statsPoint.getLatestDate().getTime() - timeLastMessage.getTime());
				}
				timeLastMessage = statsPoint.getLatestDate();
			}
		}
		System.out.println("Thread size: " + (threadSizes.getMean()) + " stdev: "+threadSizes.getStandardDeviation());
		System.out.println("Average response time: " +
		(responseTimes.getMean()/1000/3600) + "hr" +
				" stdev:"+(responseTimes.getStandardDeviation()/1000/3600));
		
		//builder.savePrecomputes();
		//builder.getAllSubjectWordPositions();
		
		//Map<Integer, Collection<MimeMessage>> threadMessageCollections = builder.getThreadMessageCollections();
		
		
		
		
		/*File fileListLoc = new File("C:\\Users\\Jacob\\Workspaces\\Machine Learning\\data\\Cleaned Post List.txt");
		File threadIDsLoc = new File("C:\\Users\\Jacob\\Workspaces\\Machine Learning\\data\\Thread IDs.txt");
		
		ArrayList<File> fileList = lister.loadPostList(root, fileListLoc);
		Map<MimeMessage,File> postToFile = PostLoader.loadPostToFileMapping(fileList);
		Map<String, EmailThread> threads = NewsgroupThreadFinder.sortThreads(postToFile.keySet());
		Map<File, Integer> threadIds = lister.loadFileThreadAssociations(threadIDsLoc, root);
		NewsgroupThreadFinder.sameSubjectThreads(threads, postToFile, threadIds);*/
		//lister.writeFileThreadAssociations(fileListLoc, root, threadIDsLoc);
		
		
		/*System.out.println(fileList.size());
		Map<String, EmailThread> threadMap = NewsgroupThreadFinder.sortThreads(fileList);
		int threadCount = 0;
		int longThreadCount = 0;
		int sumThreadSize = 0;
		int sumLongThreadSize = 0;
		int maxThreadSize = 0;
		for(EmailThread thread: threadMap.values()){
			
			for(SummarizedMessage rootPost: thread.getRoots()){
				threadCount++;
				int threadSize = rootPost.getThreadSize();
				sumThreadSize += threadSize;
				if(threadSize > 1){
					longThreadCount++;
					sumLongThreadSize += threadSize;
				}
				if(threadSize > maxThreadSize){
					maxThreadSize = threadSize;
				}
			}
		}
		System.out.println("Threads: "+threadCount+ ", Long Threads: "+longThreadCount);
		System.out.println("Average thread size "+ ((double) sumThreadSize / (double) threadCount));
		System.out.println("Average long thread size "+ ((double) sumLongThreadSize / (double) longThreadCount));
		System.out.println("Max thread size " + maxThreadSize);*/
		/*for(File postLoc: fileList){
			MimeMessage message = PostLoader.getPost(postLoc);
			SummarizedMessage summarizedMsg = new SummarizedMessage(message);

		}*/
	}
}
