package jinjing.rule;

/**
 * Abstract, superclass of all rules extracting numeric features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NumericFeatureRule extends FeatureRule {

	/**
	   * Create a numeric-type feature extracting rule
	   * Initialize name for extracted feature
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public NumericFeatureRule(String destFeatureName) {
		super(destFeatureName);
	}
	
	/**
	 * Check if an object is numeric
	 * 
	 * @param val object value
	 * @throws Exception when object value is not valid
	 */
	@Override
	public void checkValid(Object val) throws Exception{
		if(val==null) return;
		
		if(!(val instanceof Double || val instanceof Integer)){
			throw new Exception("This attr value must be double/int");
		}
		if(val instanceof Integer){
			val = new Double(((Integer) val).doubleValue());
		}
	}

}
