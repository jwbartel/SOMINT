package prediction.features.messages;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the char-length of starting email's subject
 */
public class MessageTitleLengthRule extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an EmailSubjectLengthRule
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public MessageTitleLengthRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the char-length of starting email's subject
	 * from given thread
	 * 
	 * @param aThread the source thread data
	 * @return the char-length of starting message's subject 
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		
		MessageData msg = aThread.getKthEarlest(0);
		String subj = (String)msg.getAttribute("Subject");
		if(subj==null) return 0;
		
		double length = subj.length();
		
		return length;
	}

}
