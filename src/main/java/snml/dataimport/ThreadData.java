package snml.dataimport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * An thread data stands for a conversation.
 * It contains a chronological sequence of message data.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ThreadData{
	/*	messageDatas in this thread
	 * 	Sorted by date&time
	 */
	
	/** Id of the thread */
	private int threadId;
	
	/** Collection of message data sorted by field "Date" */
	private TreeSet<MessageData> msgDatas;
	
	/**
	   * Get id of the thread.
	   * 
	   * @return id of the thread
	   */
	public int getThreadId(){
		return threadId;
	}
	
	/**
	   * Set id of the thread.
	   * 
	   * @param id id of the thread
	   */
	public void setThreadId(int id){
		threadId = id;
	}
	
	/**
	   * Serialize the thread into a string expression.
	   * The expression contains only date of messages.
	   * 
	   * @return string expression of the thread
	   */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator<MessageData> iter = msgDatas.iterator();
		while(iter.hasNext()){
			sb.append(iter.next().getAttribute("Date")).append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
		
	/**
	   * Create a thread data with a comparator of message data.
	   * The comparator compare messages by their date fields.
	   * 
	   */
	public ThreadData(){
		msgDatas = new TreeSet<MessageData>(new Comparator<MessageData>(){
            public int compare(MessageData a, MessageData b){
            	Object dateObjA = a.getAttribute(MsgDataConfig.DATE_DEFAULT);
            	Object dateObjB = b.getAttribute(MsgDataConfig.DATE_DEFAULT);
            	if(dateObjA==null || dateObjB==null){
            		return 0;
            	}
            	
            	String dateA = (String)dateObjA;
            	String dateB = (String)dateObjB;
            	SimpleDateFormat dateFormat = new SimpleDateFormat(MsgDataConfig.DATEFORMAT_DEFAULT);
            	
				try {
					Date dA = dateFormat.parse(dateA);
					Date dB = dateFormat.parse(dateB);
					
					int cmp = dA.compareTo(dB);
					if(cmp==0) cmp = 1;
	            	return cmp;
	            	
				} catch (ParseException e) {
					e.printStackTrace();
					return 0;
				}          	
            }
		});
        
	}
	
	/**
	   * Add a message data to the thread
	   * 
	   * @param aMsgData the message data to be added to the thread.
	   */
	public void addMsgData(MessageData aMsgData){
		msgDatas.add(aMsgData);
	}
	
	/**
	   * Get the number of message data in the thread
	   * 
	   * @return number of message data in the thread
	   */
	public int size(){
		return msgDatas.size();
	}
	
	/**
	   * Get the message data with Kth earliest Date.
	   * K = 0, 1, ..., thread size.
	   * 
	   * @param k the Kth earliest
	   * @return message data with Kth earlest Date.
	   * @throws Exception if k is invalid
	   */
	public MessageData getKthEarlest(int k) throws Exception{
		int msgNum = size();
		if(k >= msgNum){
			return null;
			//throw new Exception("Error in ThreadData.getKthEarlest: request out of boundary");
		}
		
		Iterator<MessageData> iter = msgDatas.iterator();
		for(int i=0; i < k && iter.hasNext(); i++){
			iter.next();
		}
		return iter.next();		
	}
	
	/**
	   * Get the message data with Kth latest Date.
	   * K = 0, 1, ..., thread size.
	   * 
	   * @param k the Kth latest
	   * @return message data with Kth latest Date.
	   * @throws Exception if k is invalid
	   */
	public MessageData getKthLatest(int k) throws Exception{
		int msgNum = size();
		if(k >= msgNum){
			return null;
			//throw new Exception("Error in ThreadData.getKthLatest: request out of boundary");
		}
		
		Iterator<MessageData> iter = msgDatas.descendingIterator();
		for(int i=0; i < k && iter.hasNext(); i++);
		return iter.next();		
	}
}
