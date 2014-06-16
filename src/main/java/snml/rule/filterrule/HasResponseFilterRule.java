package snml.rule.filterrule;

import snml.dataimport.ThreadData;

/**
 * rule to decide the subset of a thread dataset,
 * threads that have response messages would be put into the sub dataset
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class HasResponseFilterRule extends ThreadFilterRule {

	/**
	   * Create a thread data filtering rule
	   * 
	   * @param destFeatureName can be null
	   */
	public HasResponseFilterRule(String destFeatureName) {
		super(destFeatureName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Decide whether given thread has response messages
	 * 
	 * @param aThread the source thread data
	 * @return whether given thread has response messages
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		int n = aThread.size();		
		return n>=2;
	}

}
