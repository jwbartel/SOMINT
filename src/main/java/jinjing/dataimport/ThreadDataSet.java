package jinjing.dataimport;

import java.util.ArrayList;

/**
 * An thread data set contains a list of thread data
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ThreadDataSet{
	
	/** list of thread data */
	protected ArrayList<ThreadData> threads;
	
	/**
	   * Serialize the thread dataset into a string expression.
	   * The expression contains the string expression of each thread.
	   * 
	   * @return string expression of the thread dataset
	   */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<threads.size(); i++){
			if(threads.get(i)!=null){ 
				sb.append(threads.get(i).toString());
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	   * Create an empty thread dataset.
	   * Note threads must have continues integer id.
	   * 
	   */
	public ThreadDataSet(){
		threads = new ArrayList<ThreadData>();
	}
	
	/**
	   * Create an empty thread dataset with given thread num.
	   * Note threads must have continues integer id.
	   * 
	   * @param threadNum initial capacity of the thread dataset
	   */
	public ThreadDataSet(int threadNum){
		threads = new ArrayList<ThreadData>(threadNum+1);
	}
	
	/**
	   * Set a certain location with an empty thread data in the thread dataset,
	   * if that loc has no thread data.
	   * 
	   * @param threadId the dest location of the thread dataset
	   */
	public void setThreadData(int threadId){
		if(this.threads.get(threadId)==null){
			this.setThreadData(threadId, new ThreadData());
		}
	}
	
	/**
	   * Replace the thread data at certain location with given one 
	   * in the thread dataset.
	   * 
	   * @param threadId the dest location of the thread dataset
	   * @param aThread the thread data to be put in the thread set
	   */
	public void setThreadData(int threadId, ThreadData aThread){
		if(threadId >= threads.size()){
			threads.add(null);
		}
		threads.set(threadId, aThread);
	}
	
	/**
	   * Add thread data to the end of the thread dataset.
	   * 
	   * @param aThread the thread data to be put in the thread set
	   */
	public void addThreadData(ThreadData aThread){
		threads.add(aThread);
	}
	
	/**
	   * Get the thread data at certain location in the thread dataset.
	   * Set an empty thread at threadId only if it's not initialized yet.
	   * 
	   * @param threadId the dest location of the thread dataset
	   * @return thread data with given threadId, 
	   */	
	public ThreadData getDataInstance(int threadId){
		if(threadId >= threads.size() ){
			int i = threads.size();
			for(; i<=threadId; i++){
				threads.add(null);
			}
		}
		if(this.threads.get(threadId)==null){
			this.setThreadData(threadId, new ThreadData());
		}
		return threads.get(threadId);
	}
	
	/**
	   * Add a message to a certain thread data in the thread dataset.
	   * 
	   * @param threadId the id of dest thread
	   * @param aMsgData the message to be added
	   */
	public void addMsgData(int threadId, MessageData aMsgData){
		ThreadData thread = this.getDataInstance(threadId);
		thread.addMsgData(aMsgData);
	}
	
	/**
	   * Get the number of thread data in the thread set
	   * 
	   * @return number of thread data in the thread set
	   */
	public int size(){
		return threads.size();
	}
	
	/**
	   * Trim the thread set to fit size
	   */
	public void trimToSize(){
		threads.trimToSize();
	}
}
