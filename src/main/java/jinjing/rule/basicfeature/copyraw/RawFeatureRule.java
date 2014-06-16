package jinjing.rule.basicfeature.copyraw;

import jinjing.dataimport.MessageData;
import jinjing.dataimport.ThreadData;
import jinjing.rule.FeatureRule;
import jinjing.rule.basicfeature.IBasicFeatureRule;

/**
 * Copy an attribute from message in thread data as feature
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class RawFeatureRule extends FeatureRule implements IBasicFeatureRule{
	/** k th message */
	protected int k;
	
	/** indicate message in earliest-first or latest-first order */	
	protected int inOrder;
	
	/** name of attribute to copy */
	protected String attrName;
	
	/** earliest-first order */
	public static final int ACCENDING = 0;
	
	/** latest-first order */
	public static final int DECENDING = 1;
	
	/**
	 * Create a feature copying rule
	 * Initialize listed variables
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param srcAttrName name of attribute to copy
	 * @param inOrder select message in earlist or latest order
	 * @param kth select kth message
	 */
	public RawFeatureRule(String destFeatureName, 
			String srcAttrName, 
			int inOrder, int kth){
		super(destFeatureName);
		
		this.k = kth;
		this.inOrder = inOrder;
		this.attrName = srcAttrName;
	}	
	

	/**
	 * Extracted feature value from a thread data
	 * 
	 * @param aThread the source thread data
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		MessageData msg; 
		switch(inOrder){
		case ACCENDING:
			msg = aThread.getKthEarlest(k);
			break;
		case DECENDING:
			msg = aThread.getKthLatest(k);
			break;
		default:
			throw new Exception("parameter of constructor is wrong");
		}
		
		if(msg==null){
			return null;
		}
		Object val = msg.getAttribute(attrName);
		this.checkValid(val);
		return val;
	}


	/**
	 * Check if an object is a valid feature value for corresponding feature rule
	 * 
	 * @param val object value
	 * @throws Exception when object value is not valid
	 */
	@Override
	public void checkValid(Object val) throws Exception {
	
	}

}
