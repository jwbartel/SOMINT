package snml.rule;

/**
 * Basic interface of all feature extracting rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IFeatureRule {

	/**
	   * Get the extracted feature's name
	   *
	   * @return feature name
	   */
	public String getDestFeatureName();
	
	/**
	 * Check if an object is a valid feature value for corresponding feature rule
	 * 
	 * @param val object value
	 * @throws Exception when object value is not valid
	 */
	public void checkValid(Object val) throws Exception;

}
