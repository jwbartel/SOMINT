package reader.threadfinder.synthetic.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import reader.threadfinder.ThreadStatistics;
import reader.threadfinder.newsgroups.tools.NewsgroupPrecomputeBuilder;
import reader.threadfinder.newsgroups.tools.PostLister;

public class SyntheticDataGenerator extends NewsgroupPrecomputeBuilder{

	Random random = new Random();
	int numThreads;
	
	double meanThreadLength;
	double meanThreadTotalTime;
	double meanTimeBetweenThreads;

	double stdevThreadLength;
	double stdvThreadTotalTime;
	double stdevTimeBetweenThreads;
	
	public SyntheticDataGenerator(File precomputesFolder, int numThreads,
			double mLength, double sThreadLength,
			double mTotalTime, double sTotalTime,
			double mTimeBetween, double sTimeBetween){
		super(null, precomputesFolder);
		
		this.numThreads = numThreads;
		
		this.meanThreadLength = mLength;
		this.meanThreadTotalTime = mTotalTime;
		this.meanTimeBetweenThreads = mTimeBetween;
		
		this.stdevThreadLength = sThreadLength;
		this.stdvThreadTotalTime = sTotalTime;
		this.stdevTimeBetweenThreads = sTimeBetween;
		
	}
	
	protected double getAdjustedGaussian(double mean, double stdev){
		double val = random.nextGaussian() * stdev;
		val = Math.abs(val);
		val += mean;
		return val;
	}
	
	public ArrayList<SyntheticThread> createSyntheticThreads(){
		 
		ArrayList<SyntheticThread> retVal = new ArrayList<SyntheticThread>(numThreads);
		
		Long prevStart= null;
		
		for(int i=0; i<numThreads; i++){
			long currStart = 0;
			if(prevStart != null){
				long deltaStart = (long) getAdjustedGaussian(meanTimeBetweenThreads, stdevTimeBetweenThreads);
				currStart = prevStart + deltaStart;
			}
			prevStart = currStart;
			
			int numMessages = (int) Math.round(getAdjustedGaussian(meanThreadLength, stdevThreadLength));
			long totalTime = (long) getAdjustedGaussian(meanThreadTotalTime, stdvThreadTotalTime);
		
			retVal.add(new SyntheticThread(currStart, numMessages, totalTime));
		}
		
		return retVal;
		
	}
	
	public ArrayList<SyntheticMessage> createSyntheticMessages(ArrayList<SyntheticThread> syntheticThreads){
		Map<Long, ArrayList<SyntheticMessage>> messages = new TreeMap<Long, ArrayList<SyntheticMessage>>();
		
		int threadID = 1;
		for(SyntheticThread thread: syntheticThreads){
			thread.setThreadID(threadID);
			
			ArrayList<SyntheticMessage> threadMessages = thread.getMessages();
			for(SyntheticMessage message: threadMessages){
				message.setThreadID(threadID);
				
				Long time = message.getTime();
				ArrayList<SyntheticMessage> seenMessages = messages.get(time);
				if(seenMessages == null){
					seenMessages = new ArrayList<SyntheticMessage>();
					messages.put(time, seenMessages);
				}
				seenMessages.add(message);
			}
			threadID++;
		}
		
		ArrayList<SyntheticMessage> retVal = new ArrayList<SyntheticMessage>();
		for(Entry<Long,ArrayList<SyntheticMessage>> entry: messages.entrySet()){
			for(SyntheticMessage message: entry.getValue()){
				retVal.add(message);
			}
		}
		return retVal;
	}
	
	public void writeVectoredValues(ArrayList<SyntheticThread> threads, ArrayList<SyntheticMessage> messages,
			File xDest, File yDest) throws IOException{
		
		
		Set<Integer> seenThreads = new TreeSet<Integer>();
		Map<Integer, Integer> totalMessagesForThread = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> seenMessagesForThread = new TreeMap<Integer, Integer>();
		Map<Integer, Long> threadEndTime = new TreeMap<Integer, Long>();
		Map<Integer, Long> threadStartTime = new TreeMap<Integer, Long>();
		Set<Integer> completedThreads = new TreeSet<Integer>();
		
		for(SyntheticThread thread: threads){
			totalMessagesForThread.put(thread.getThreadID(), thread.getNumMessages());
			seenMessagesForThread.put(thread.getThreadID(), 0);
		}
		
		BufferedWriter xOut = new BufferedWriter(new FileWriter(xDest));
		BufferedWriter yOut = new BufferedWriter(new FileWriter(yDest));
		
		for(SyntheticMessage message: messages){
			
			long currTime = message.getTime();
			
			for(Integer seenThread: seenThreads){
				
				int numMessages = seenMessagesForThread.get(seenThread);
				if(numMessages < 2) continue;
				
				long startTime = threadStartTime.get(seenThread);
				long endTime = threadEndTime.get(seenThread);
				
				
				double timeScale = 1000*60*60;
				double[] vector = new double[4];
				vector[0] = ((double) currTime - endTime)/timeScale;
				vector[1] = numMessages;
				vector[2] = (((double) (endTime-startTime))/numMessages)/timeScale; 
				vector[3] = ((double)endTime - startTime)/timeScale;
				
				String vectorStr = Arrays.toString(vector);
				xOut.write(vectorStr.substring(1, vectorStr.length()-1).replaceAll(", ", ","));
				xOut.newLine();
				
				double y = (completedThreads.contains(seenThread))? -1: +1;
				yOut.write(""+y);
				yOut.newLine();
			}
			
			if(!seenThreads.contains(message.getThreadID())){
				seenThreads.add(message.getThreadID());
				threadStartTime.put(message.getThreadID(), message.getTime());
			}
			
			seenMessagesForThread.put(message.getThreadID(), seenMessagesForThread.get(message.getThreadID())+1);
			threadEndTime.put(message.getThreadID(), message.getTime());
			
			if(((int) seenMessagesForThread.get(message.getThreadID())) >= totalMessagesForThread.get(message.getThreadID())){
				completedThreads.add(message.getThreadID());
			}
			
		}
		
		xOut.flush();
		xOut.close();
		
		yOut.flush();
		yOut.close();
	}
	
	public void writeFinalThreadStatistics(ArrayList<SyntheticThread> threads, File dest) throws IOException{
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		for(SyntheticThread thread: threads){
			out.write(""+thread.getThreadID());
			out.newLine();
			out.write("\tmessage count:"+thread.getNumMessages());
			out.newLine();
			out.write("\tlast message:"+new Date(thread.getStartTime() + thread.getTotalTime()));
			out.newLine();
			out.write("\taverage time between messages:"+( ((double) thread.getTotalTime())/thread.getNumMessages()));
			out.newLine();
			out.write("\ttotal life time:"+thread.getTotalTime());
			out.newLine();
			out.write("\tsubject word frequencies:");
			out.newLine();
		}
		
		out.close();
	}
	
	public void savePrecomputes() throws IOException, MessagingException{


		File finalThreadsStatsDest = new File(precomputesFolder, FINAL_THREADS_STATS);
		File xVectorDest = new File(precomputesFolder, X_VECTOR);
		File yVectorDest = new File(precomputesFolder, Y_VECTOR);
		if(!finalThreadsStatsDest.exists() || !xVectorDest.exists() || !yVectorDest.exists()){
			
			ArrayList<SyntheticThread> threads = createSyntheticThreads();
			ArrayList<SyntheticMessage> messages = createSyntheticMessages(threads);
			
			writeFinalThreadStatistics(threads, finalThreadsStatsDest);
			writeVectoredValues(threads, messages, xVectorDest, yVectorDest);
		}

		
		File foldsFolder = new File(precomputesFolder, FOLDS_FOLDER);
		if(!foldsFolder.exists()){
			PostLister lister = new PostLister();
			lister.sortIntoFolds(xVectorDest, yVectorDest, foldsFolder, 10);
		}
	}
	
	public static void main(String[] args) throws IOException, MessagingException{
		
		File syntheticsFolder = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\synthetic");
		//File syntheticsFolder = new File("/home/bartizzi/Workspaces/Machine Learning/data/synthetic");
		
		SyntheticDataGenerator gen = new SyntheticDataGenerator(syntheticsFolder, 20,
				2, 1.240033, 0, 193055550.892379, 1000, 17184173.64);
		gen.savePrecomputes();
		
	}
}
