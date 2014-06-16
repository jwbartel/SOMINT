package snml.rule;

/**
 * Abstract, superclass of all rules extracting string features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class StringFeatureRule extends FeatureRule {

	/**
	   * Create a string feature extracting rule
	   * Initialize name for extracted feature
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public StringFeatureRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Check if an object is string
	 * 
	 * @param val object value
	 * @throws Exception when object value is not valid
	 */
	@Override
	public void checkValid(Object val) throws Exception {
		if(val==null) return;
		
		if(!(val instanceof String)){
			throw new Exception("String type required");
		}

	}

}
