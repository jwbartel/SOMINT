package jinjing.dataimport;

/**
 * A thread retriever to sort messages into conversations. 
 * It's usually called in a source data parser if the source data is not organized
 * in threads (conversations). 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ThreadRetriever {
	
	/** The thread data set to store sorted messages */
	public ThreadDataSet targetThreadDataSet;
	
	/** The message data config to define the message data's fields */
	public MsgDataConfig aMsgDataConfig;
	
	/**
	   * Create a thread retriever for given thread dataset
	   *
	   * @param aThreadDataSet the tread dataset to store sorted messages
	   */
	public ThreadRetriever(ThreadDataSet aThreadDataSet){
		this.targetThreadDataSet = aThreadDataSet;
	}
	
	/**
	   * Create a thread retriever for a new thread dataset
	   * 
	   */
	public ThreadRetriever(){
		this.targetThreadDataSet = new ThreadDataSet();
	}
	
	/**
	   * Sort a given message into the thread dataset.
	   * The destination thread id must be known in the message.
	   * 
	   * @param aMsgData the message to be sort
	   */
	public void sortMsgIntoThread(MessageData aMsgData){
		Object threadIdObj = aMsgData.getAttribute(MsgDataConfig.THREADID);
		if(threadIdObj==null){
			System.out.println("Error in ThreadRetriever.sortMsgIntoThread: "
					+ "no thread id available of message");
			return;
		}
		int threadId = (int)threadIdObj;
		this.targetThreadDataSet.addMsgData(threadId, aMsgData);
	}
	
	/**
	   * Trim the thread dataset to fit size
	   * 
	   */
	public void trimThreadDataSetToSize(){
		this.targetThreadDataSet.trimToSize();
	}
	
	
}
