package jinjingdataconvert;

import jinjing.dataimport.ThreadData;
import jinjing.dataimport.ThreadDataSet;
import jinjing.rule.filterrule.ThreadFilterRule;

/**
 * Thread data set filter which create a subset from given ThreadDataSet.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ThreadDataFilter {
	
	/**
	   * Create a subset from a given ThreadDataSet.
	   * Whether the instances are in the subset is decided by a given thread 
	   * filter rule
	   * 
	   * @param threads the input dataset to select instances from
	   * @param rule a filtering rule for selecting instances
	   * @return the created output dataset to store the features
	   * @throws Exception if invalid operation appears
	   */
	public ThreadDataSet filt(ThreadDataSet threads, ThreadFilterRule rule) 
			throws Exception{
		
		ThreadDataSet newThreads = new ThreadDataSet();
		newThreads.addThreadData(null);
		for(int i=1; i<threads.size(); i++){
			ThreadData thread = threads.getDataInstance(i);
			if((Boolean)(rule.extract(thread))==true){
				newThreads.addThreadData(thread);
			}
		}
		newThreads.trimToSize();
		return newThreads;
	}

}
