package jinjing.rule.basicfeature;

import jinjing.dataimport.MessageData;
import jinjing.dataimport.ThreadData;
import jinjing.rule.NumericFeatureRule;

/**
 * Extract the average char-length of given string attribute of the response
 * messages in a thread
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ResponseAvgCharLength extends NumericFeatureRule implements IBasicFeatureRule{

	/** attribute name */
	String field;
	
	/**
	   * Create an ResponseAvgCharLength
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param responseField name of string attribute to count length
	   */
	public ResponseAvgCharLength(String destFeatureName, String responseField) {
		super(destFeatureName);
		this.field = responseField;
	}

	/**
	 * Extract the average char-length of given string attribute of the response
	 * messages in given thread
	 * 
	 * @param aThread the source thread data
	 * @return the average char-length of given string attribute of 
	 * 			the response messages
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		int length = 0;
		int responseNum = aThread.size()-1;
		for(int i=1; i<=responseNum; i++){
			MessageData msg = aThread.getKthEarlest(i);
			String ans = (String)msg.getAttribute(field);
			length += ans.length();
		}
		if(responseNum>=1){
			length /= responseNum;
		}
		return length;
	}

}
