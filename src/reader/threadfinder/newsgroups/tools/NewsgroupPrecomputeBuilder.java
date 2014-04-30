package reader.threadfinder.newsgroups.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.threadfinder.IteratedThreadStatistics;
import reader.threadfinder.ThreadStatistics;

public class NewsgroupPrecomputeBuilder {
	
	protected File messageRoot, precomputesFolder;
	PostLister lister = new PostLister();
	
	ArrayList<File> fileList;
	ArrayList<File> cleanedOrderedFileList;
	
	Map<File, Integer> fileToThreadID;
	Map<Integer, ThreadStatistics> finalThreadStatistics;
	Map<Integer, File> threadToFinalFile;
	ArrayList<IteratedThreadStatistics> iteratedThreadStatistics;
	
	Map<Integer, Integer> threadToVirtualThread;
	Map<File, Integer> fileToVirtualThreadID;
	Map<Integer, ThreadStatistics> finalVirtualThreadStatistics;
	Map<Integer, File> virtualThreadToFinalFile;
	ArrayList<IteratedThreadStatistics> iteratedVirtualThreadStatistics;
	
	Map<String,Integer> allSubjectWordPostions;
	Map<String,Integer> recipientPostions;
	
	public static final String FILE_LIST_NAME = "File List.txt";
	public static final String CLEANED_ORDERED_FILE_LIST_NAME = "Cleaned and Ordered File List.txt";
	public static final String THREAD_IDS_NAME = "Thread IDs.txt";
	public static final String LINKED_THREADS_NAME = "Linked Threads.txt";
	public static final String FINAL_THREADS_STATS = "Final Threads Stats.txt";
	public static final String ITERATED_THREADS_STATS = "Iterated Threads Stats.txt";
	public static final String THREAD_TERMINATION_POINTS = "Thread termination points.txt";
	public static final String SUBJECT_WORDS = "Subject words.txt";
	public static final String RECIPIENTS = "Recipients.txt";
	public static final String X_VECTOR = "X Vectors.csv";
	public static final String Y_VECTOR = "Y Vectors.csv";
	public static final String Z_VECTOR = "Z Vectors.csv";
	public static final String TRUNCATED_X_VECTOR = "Truncated X Vectors.csv";
	public static final String TRUNCATED_Y_VECTOR = "Truncated Y Vectors.csv";
	public static final String THREAD_SIZE_HISTOGRAM = "Thread size histogram.csv";
	public static final String THREAD_TOTAL_TIME_HISTOGRAM = "Thread total time histogram.csv";
	public static final String TIME_BETWEEN_THREADS_HISTOGRAM = "Time between threads histogram.csv";
	public static final String THREAD_SIZE_DISTRIB = "Thread size distrib.csv";
	public static final String THREAD_TOTAL_TIME_DISTRIB = "Thread total time distrib.csv";
	public static final String TIME_BETWEEN_THREADS_DISTRIB = "Time between threads distrib.csv";
	
	public static final String THREAD_TO_VIRTUAL_THREAD = "Thread to Virtual Thread.txt";
	public static final String VIRTUAL_THREAD_IDS_NAME = "Virtual Thread IDs.txt";
	public static final String FINAL_VIRTUAL_THREADS_STATS = "Final Virtual Threads Stats.txt";
	public static final String ITERATED_VIRTUAL_THREADS_STATS = "Iterated Virtual Threads Stats.txt";
	public static final String VIRTUAL_THREAD_TERMINATION_POINTS = "Virtual Thread termination points.txt";
	public static final String VIRTUAL_X_VECTOR = "Virtual X Vectors.csv";
	public static final String VIRTUAL_Y_VECTOR = "Virtual Y Vectors.csv";
	public static final String VIRTUAL_Z_VECTOR = "Virtual Z Vectors.csv";
	

	public static final String FOLDS_FOLDER = "folds";
	public static final String TRUNCATED_FOLDS_FOLDER = "truncated folds";
	
	public NewsgroupPrecomputeBuilder(File messageRoot, File precomputesFolder){
		this.messageRoot = messageRoot;
		this.precomputesFolder = precomputesFolder;
		
		if(!precomputesFolder.exists()){
			precomputesFolder.mkdirs();
		}
	}
	
	public ArrayList<File> getFileList() throws IOException, MessagingException{
		if(fileList == null){
			File fileListLoc = new File(precomputesFolder, FILE_LIST_NAME);
			if(!fileListLoc.exists()){
				savePrecomputes();
			}
			fileList = lister.loadPostList(messageRoot, fileListLoc);
		}
		return fileList;
	}
	
	public ArrayList<File> getCleanedOrderedFileList() throws IOException, MessagingException{
		if(cleanedOrderedFileList == null){
			File fileListLoc = new File(precomputesFolder, CLEANED_ORDERED_FILE_LIST_NAME);
			if(!fileListLoc.exists()){
				savePrecomputes();
			}

			cleanedOrderedFileList = lister.loadPostList(messageRoot, fileListLoc);
		}
		return cleanedOrderedFileList;
	}
	
	public Map<File, Integer> getFileToThreadIDs() throws IOException, MessagingException{
		if(fileToThreadID == null){
			File threadIDLoc = new File(precomputesFolder, THREAD_IDS_NAME);
			if(!threadIDLoc.exists()){
				savePrecomputes();
			}
			fileToThreadID = lister.loadFileThreadAssociations(threadIDLoc, messageRoot);
		}
		return fileToThreadID;
	}
	
	public Map<Integer, ThreadStatistics> getFinalThreadStatistics() throws IOException, MessagingException{
		if(finalThreadStatistics == null){
			File finalThreadsStatsLoc = new File(precomputesFolder, FINAL_THREADS_STATS);
			if(!finalThreadsStatsLoc.exists()){
				savePrecomputes();
			}
			finalThreadStatistics = lister.loadFinalThreadStatistics(finalThreadsStatsLoc, messageRoot);
		}
		return finalThreadStatistics;
	}
	
	public ArrayList<IteratedThreadStatistics> getIteratedThreadStatistics() throws IOException, MessagingException{
		if(iteratedThreadStatistics == null){
			File iteratedThreadsStatsLoc = new File(precomputesFolder, ITERATED_THREADS_STATS);
			if(!iteratedThreadsStatsLoc.exists()){
				savePrecomputes();
			}
			iteratedThreadStatistics = lister.loadIteratedThreadStatistics(iteratedThreadsStatsLoc, messageRoot);
		}
		return iteratedThreadStatistics;
	}
	
	public Map<Integer,ArrayList<IteratedThreadStatistics>> getSortedIteratedThreadStatistics(
			ArrayList<IteratedThreadStatistics> iteratedThreadStatistics){
		
		Map<Integer, ArrayList<IteratedThreadStatistics>> retVal = new TreeMap<Integer, ArrayList<IteratedThreadStatistics>>();
		for(IteratedThreadStatistics stats: iteratedThreadStatistics){
			
			ArrayList<IteratedThreadStatistics> threadsStats = retVal.get(stats.getThreadID());
			if(threadsStats == null){
				threadsStats = new ArrayList<IteratedThreadStatistics>();
				retVal.put(stats.getThreadID(), threadsStats);
			}
			
			boolean wasAdded = false;
			for(int i=0; i<threadsStats.size(); i++){
				IteratedThreadStatistics threadStats = threadsStats.get(i);
				if(threadStats.getPostLocation().equals(stats.getPostLocation())){
					wasAdded = true;
					break;
				}
				if(threadStats.getLatestDate().after(stats.getLatestDate())){
					threadsStats.add(i, stats);
					wasAdded = true;
					break;
				}
			}
			if(!wasAdded){
				threadsStats.add(stats);
			}
			
		}
		return retVal;
	}
	
	public Map<Integer, File> getThreadToFinalFile() throws IOException, MessagingException{
		if(threadToFinalFile == null){
			File threadTerminationPointsLoc = new File(precomputesFolder, THREAD_TERMINATION_POINTS);
			if(!threadTerminationPointsLoc.exists()){
				savePrecomputes();
			}
			threadToFinalFile = lister.loadThreadToLastFile(threadTerminationPointsLoc, messageRoot);
		}
		return threadToFinalFile;
	}
	
	public Map<Integer,Integer> getThreadToVirtualThread() throws IOException, MessagingException {
		if(threadToVirtualThread == null){
			File threadToVirtualThreadLoc = new File(precomputesFolder, THREAD_TO_VIRTUAL_THREAD);
			if(!threadToVirtualThreadLoc.exists()){
				savePrecomputes();
			}
			threadToVirtualThread = lister.loadThreadsToVirtualThreads(threadToVirtualThreadLoc);
		}
		return threadToVirtualThread;
	}
	
	public Map<String, Integer> getAllSubjectWordPositions() throws IOException, MessagingException{
		if(allSubjectWordPostions == null){
			File allSubjectWordsLoc = new File(precomputesFolder, SUBJECT_WORDS);
			if(!allSubjectWordsLoc.exists()){
				savePrecomputes();
			}
			allSubjectWordPostions = lister.loadWordPositions(allSubjectWordsLoc);
		}
		return allSubjectWordPostions;
	}
	
	public Map<String, Integer> getAllRecipientPositions() throws IOException, MessagingException{
		if(recipientPostions == null){
			File recipientsLoc = new File(precomputesFolder, RECIPIENTS);
			if(!recipientsLoc.exists()){
				savePrecomputes();
			}
			recipientPostions = lister.loadWordPositions(recipientsLoc);
		}
		return recipientPostions;
	}
	
	public Map<File, Integer> getFileToVirtualThreadIDs() throws IOException, MessagingException{
		if(fileToVirtualThreadID == null){
			File virtualThreadIDLoc = new File(precomputesFolder, VIRTUAL_THREAD_IDS_NAME);
			if(!virtualThreadIDLoc.exists()){
				savePrecomputes();
			}
			fileToVirtualThreadID = lister.loadFileThreadAssociations(virtualThreadIDLoc, messageRoot);
		}
		return fileToVirtualThreadID;
	}
	
	public Map<Integer, ThreadStatistics> getFinalVirtualThreadStatistics() throws IOException, MessagingException{
		if(finalVirtualThreadStatistics == null){
			File finalVirtualThreadsStatsLoc = new File(precomputesFolder, FINAL_VIRTUAL_THREADS_STATS);
			if(!finalVirtualThreadsStatsLoc.exists()){
				savePrecomputes();
			}
			finalVirtualThreadStatistics = lister.loadFinalThreadStatistics(finalVirtualThreadsStatsLoc, messageRoot);
		}
		return finalVirtualThreadStatistics;
	}
	
	public ArrayList<IteratedThreadStatistics> getIteratedVirtualThreadStatistics() throws IOException, MessagingException{
		if(iteratedVirtualThreadStatistics == null){
			File iteratedVirtualThreadsStatsLoc = new File(precomputesFolder, ITERATED_VIRTUAL_THREADS_STATS);
			if(!iteratedVirtualThreadsStatsLoc.exists()){
				savePrecomputes();
			}
			iteratedVirtualThreadStatistics = lister.loadIteratedThreadStatistics(iteratedVirtualThreadsStatsLoc, messageRoot);
		}
		return iteratedVirtualThreadStatistics;
	}
	
	public Map<Integer, File> getVirtualThreadToFinalFile() throws IOException, MessagingException{
		if(virtualThreadToFinalFile == null){
			File virtualThreadTerminationPointsLoc = new File(precomputesFolder, VIRTUAL_THREAD_TERMINATION_POINTS);
			if(!virtualThreadTerminationPointsLoc.exists()){
				savePrecomputes();
			}
			virtualThreadToFinalFile = lister.loadThreadToLastFile(virtualThreadTerminationPointsLoc, messageRoot);
		}
		return virtualThreadToFinalFile;
	}
	
	public Map<Integer, Collection<MimeMessage>> getThreadMessageCollections(Map<File, Integer> fileToThreadIDs) throws IOException, MessagingException {
		
		Map<Integer, Collection<MimeMessage>> threadToMessages = new TreeMap<Integer, Collection<MimeMessage>>();
		for(Entry<File,Integer> entry: fileToThreadIDs.entrySet()){
			Integer threadID = entry.getValue();
			Collection<MimeMessage> collection = threadToMessages.get(threadID);
			if(collection == null){
				collection = new ArrayList<MimeMessage>();
				threadToMessages.put(threadID, collection);
			}
			collection.add(PostLoader.getPost(entry.getKey()));
		}
		return threadToMessages;
	}
	
	public void savePrecomputes() throws IOException, MessagingException{
		System.out.print("Saving precomputes...");
		File fileListDest = new File(precomputesFolder, FILE_LIST_NAME);
		if(!fileListDest.exists()){
			ArrayList<File> fileList = lister.buildList(messageRoot);
			lister.writeFileList(fileList, messageRoot, fileListDest);
		}
		ArrayList<File> fileList = getFileList();
		
		File cleanedFileListDest = new File(precomputesFolder, CLEANED_ORDERED_FILE_LIST_NAME);
		if(!cleanedFileListDest.exists()){
			ArrayList<File> cleanedFileList = lister.buildOrderedNonRepeatingList(messageRoot);
			lister.writeFileList(cleanedFileList, messageRoot, cleanedFileListDest);
		}
		ArrayList<File> cleanedFileList = getCleanedOrderedFileList();
		
		File threadIDsDest = new File(precomputesFolder, THREAD_IDS_NAME);
		if(!threadIDsDest.exists()){
			lister.writeFileThreadAssociations(cleanedFileListDest, messageRoot, threadIDsDest);
		}
		
		Map<File,Integer> fileToThreadID = getFileToThreadIDs();
		File linkedThreadsDest = new File(precomputesFolder, LINKED_THREADS_NAME);
		if(!linkedThreadsDest.exists()){
			lister.writeSameSubjectThreads(cleanedFileListDest, fileToThreadID, messageRoot, linkedThreadsDest);
		}

		File finalThreadsStatsDest = new File(precomputesFolder, FINAL_THREADS_STATS);
		if(!finalThreadsStatsDest.exists()){
			Map<File, Integer> fileToThreadIDs = getFileToThreadIDs();
			Map<Integer, Collection<MimeMessage>> threadMessageCollections = getThreadMessageCollections(fileToThreadIDs);
			lister.writeFinalThreadStatistics(threadMessageCollections, finalThreadsStatsDest);
		}

		File iteratedThreadsStatsDest = new File(precomputesFolder, ITERATED_THREADS_STATS);
		if(!iteratedThreadsStatsDest.exists()){
			lister.writeIteratedThreadStatistics(cleanedFileList, getFileToThreadIDs(), messageRoot, iteratedThreadsStatsDest);
		}
		
		File threadTerminationPointsDest = new File(precomputesFolder, THREAD_TERMINATION_POINTS);
		if(!threadTerminationPointsDest.exists()){
			lister.writeLastFileOFThreads(cleanedFileList, getFileToThreadIDs(), messageRoot, threadTerminationPointsDest);
		}
		
		File subjectWordsDest = new File(precomputesFolder, SUBJECT_WORDS);
		if(!subjectWordsDest.exists()){
			Map<Integer, ThreadStatistics> finalThreadStats = getFinalThreadStatistics();
			lister.writeAllSubjectWords(finalThreadStats, subjectWordsDest);
		}
		
		File recipientsDest = new File(precomputesFolder, RECIPIENTS);
		if(!recipientsDest.exists()){
			ArrayList<IteratedThreadStatistics> iteratedThreadStats = getIteratedThreadStatistics();
			lister.writeAllRecipients(iteratedThreadStats, recipientsDest);
		}
		
		File xVectorDest = new File(precomputesFolder, X_VECTOR);
		File yVectorDest = new File(precomputesFolder, Y_VECTOR);
		File zVectorDest = new File(precomputesFolder, Z_VECTOR);
		if(!xVectorDest.exists() || !yVectorDest.exists() || !zVectorDest.exists()){
			ArrayList<IteratedThreadStatistics> iteratedStatsList = getIteratedThreadStatistics();
			Map<Integer, ArrayList<IteratedThreadStatistics>> sortedIteratedStats = getSortedIteratedThreadStatistics(iteratedStatsList);
			Map<String, Integer> subjectWordPositions = getAllSubjectWordPositions();
			Map<String, Integer> recipientPositions = getAllRecipientPositions();
			lister.writeVectoredData(sortedIteratedStats, subjectWordPositions, recipientPositions, xVectorDest, yVectorDest, zVectorDest);
		}
		
		File truncatedXVectorDest = new File(precomputesFolder, TRUNCATED_X_VECTOR);
		File truncatedYVectorDest = new File(precomputesFolder, TRUNCATED_Y_VECTOR);
		if(!truncatedXVectorDest.exists() && !truncatedYVectorDest.exists()){
			ArrayList<IteratedThreadStatistics> iteratedStatsList = getIteratedThreadStatistics();
			Map<Integer, File> threadToFinalFile = getThreadToFinalFile();
			Map<String, Integer> subjectWordPositions = getAllSubjectWordPositions();
			lister.writeTruncatedVectoredData(5, iteratedStatsList, threadToFinalFile, subjectWordPositions, truncatedXVectorDest, truncatedYVectorDest);
		}
		
		File threadSizeHistDest = new File(precomputesFolder, THREAD_SIZE_HISTOGRAM);
		if(!threadSizeHistDest.exists()){
			Map<Integer, ThreadStatistics> finalThreadStats = getFinalThreadStatistics();
			lister.writeThreadSizeHistogram(finalThreadStats, threadSizeHistDest);
		}
		
		File threadTotalTimeHistDest = new File(precomputesFolder, THREAD_TOTAL_TIME_HISTOGRAM);
		if(!threadTotalTimeHistDest.exists()){
			Map<Integer, ThreadStatistics> finalThreadStats = getFinalThreadStatistics();
			lister.writeThreadTotalTimeHistogram(finalThreadStats, threadTotalTimeHistDest);
		}
		
		File timeBetweenThreadsHistDest = new File(precomputesFolder, TIME_BETWEEN_THREADS_HISTOGRAM);
		if(!timeBetweenThreadsHistDest.exists()){
			ArrayList<IteratedThreadStatistics> iteratedStats = getIteratedThreadStatistics();
			lister.writeTimeBetweenThreadsHistogram(iteratedStats, timeBetweenThreadsHistDest);
		}
		
		File threadSizeDistrDest = new File(precomputesFolder, THREAD_SIZE_DISTRIB);
		File threadTotalTimeDistrDest = new File(precomputesFolder, THREAD_TOTAL_TIME_DISTRIB);
		File timeBetweenThreadsDistrDest = new File(precomputesFolder, TIME_BETWEEN_THREADS_DISTRIB);
		if(!threadSizeDistrDest.exists() || !threadTotalTimeDistrDest.exists() || !timeBetweenThreadsDistrDest.exists()){
			Map<Integer, ThreadStatistics> finalThreadStats = getFinalThreadStatistics();
			ArrayList<IteratedThreadStatistics> iteratedStats = getIteratedThreadStatistics();
			lister.writeGaussianDistributions(finalThreadStats, iteratedStats, threadSizeDistrDest, threadTotalTimeDistrDest, timeBetweenThreadsDistrDest);
		}
		
		File foldsFolder = new File(precomputesFolder, FOLDS_FOLDER);
		if(!foldsFolder.exists()){
			lister.sortIntoFolds(xVectorDest, yVectorDest, foldsFolder, 500);
		}
		
		File truncatedFoldsFolder = new File(precomputesFolder, TRUNCATED_FOLDS_FOLDER);
		if(!truncatedFoldsFolder.exists()){
			lister.sortIntoFolds(truncatedXVectorDest, truncatedYVectorDest, truncatedFoldsFolder, 500);
		}
		
		
		
		
		File threadsToVirtualThreadsDest = new File(precomputesFolder, THREAD_TO_VIRTUAL_THREAD);
		if(!threadsToVirtualThreadsDest.exists()){
			Map<Integer, File> threadTerminationPoints = getThreadToFinalFile();
			lister.writeThreadsToVirtualThreads(cleanedFileList, threadTerminationPoints, fileToThreadID, messageRoot, threadsToVirtualThreadsDest);
		}

		File virtualThreadIDsDest = new File(precomputesFolder, VIRTUAL_THREAD_IDS_NAME);
		if(!virtualThreadIDsDest.exists()){
			Map<Integer, Integer> threadToVirtualThread = getThreadToVirtualThread();
			lister.writeVirtualThreadIDs(fileToThreadID, threadToVirtualThread, messageRoot, virtualThreadIDsDest);
		}

		File finalVirtualThreadsStatsDest = new File(precomputesFolder, FINAL_VIRTUAL_THREADS_STATS);
		if(!finalVirtualThreadsStatsDest.exists()){
			Map<File, Integer> fileToVirtualThreadIDs = getFileToVirtualThreadIDs();
			Map<Integer, Collection<MimeMessage>> virtualThreadMessageCollections = getThreadMessageCollections(fileToVirtualThreadIDs);
			lister.writeFinalThreadStatistics(virtualThreadMessageCollections, finalVirtualThreadsStatsDest);
		}

		File iteratedVirtualThreadsStatsDest = new File(precomputesFolder, ITERATED_VIRTUAL_THREADS_STATS);
		if(!iteratedVirtualThreadsStatsDest.exists()){
			lister.writeIteratedThreadStatistics(cleanedFileList, getFileToVirtualThreadIDs(), messageRoot, iteratedVirtualThreadsStatsDest);
		}

		File virtualThreadTerminationPointsDest = new File(precomputesFolder, VIRTUAL_THREAD_TERMINATION_POINTS);
		if(!virtualThreadTerminationPointsDest.exists()){
			lister.writeLastFileOFThreads(cleanedFileList, getFileToVirtualThreadIDs(), messageRoot, virtualThreadTerminationPointsDest);
		}
		
		File virtualXVectorDest = new File(precomputesFolder, VIRTUAL_X_VECTOR);
		File virtualYVectorDest = new File(precomputesFolder, VIRTUAL_Y_VECTOR);
		File virtualZVectorDest = new File(precomputesFolder, VIRTUAL_Z_VECTOR);
		if(!virtualXVectorDest.exists() && !virtualYVectorDest.exists()){
			ArrayList<IteratedThreadStatistics> iteratedStatsList = getIteratedVirtualThreadStatistics();
			Map<Integer, ArrayList<IteratedThreadStatistics>> sortedIteratedStats = getSortedIteratedThreadStatistics(iteratedStatsList);
			Map<String, Integer> subjectWordPositions = getAllSubjectWordPositions();
			Map<String, Integer> recipientPositions = getAllRecipientPositions();
			lister.writeVectoredData(sortedIteratedStats, subjectWordPositions, recipientPositions, virtualXVectorDest, virtualYVectorDest, virtualZVectorDest);
		}

		
		System.out.println("done.");
		
	}
	
	
}
