package jinjing.rule;

/**
 * Abstract, superclass of all feature extracting rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class FeatureRule implements IFeatureRule {
	
	/** name of extracted feature */
	protected String destFeatureName;	
	
	/**
	   * Create a feature extracting rule
	   * Initialize name for extracted feature
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public FeatureRule(String destFeatureName){
		this.destFeatureName = destFeatureName;
	}
	
	/**
	   * Get the extracted feature's name
	   *
	   * @return feature name
	   */
	@Override
	public String getDestFeatureName(){
		return this.destFeatureName;
	}
	
	/**
	 * Check if an object is a valid feature value for corresponding feature rule
	 * 
	 * @param val object value
	 * @throws Exception when object value is not valid
	 */
	public abstract void checkValid(Object val) throws Exception;
}
