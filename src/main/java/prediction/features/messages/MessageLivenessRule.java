package prediction.features.messages;


import snml.dataimport.ThreadData;
import snml.rule.NumericFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * subclass of BinaryFeatureRule deciding if a thread has response message
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class MessageLivenessRule extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an rule for deciding where contains follow message
	   * Feature value domain is initialized as {y, n}
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public MessageLivenessRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Decide if given thread data has follow messages
	 * 
	 * @param aThread the source thread data
	 * @return whether the thread data has follow messages
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		if(aThread==null) return 0.0;
		double msgNum = aThread.size();
		if(msgNum >= 2){
			return 1.0;
		}else{
			return 0.0;
		}
	}

}
