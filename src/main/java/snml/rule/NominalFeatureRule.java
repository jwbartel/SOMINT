package snml.rule;

import java.util.ArrayList;

/**
 * Abstract, superclass of all rules extracting nominal features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NominalFeatureRule extends FeatureRule {

	/** domain of feature value */
	protected ArrayList<String> domain;
	
	/**
	   * Create an nominal feature extracting rule
	   * Initialize an empty feature value domain
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public NominalFeatureRule(String destFeatureName) {
		super(destFeatureName);
		domain = new ArrayList<String>();
	}
	
	/**
	   * Create an nominal feature extracting rule
	   * Initialize feature value domain
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param aDomain domain of feature value
	   */
	public NominalFeatureRule(String destFeatureName, ArrayList<String> aDomain) {
		super(destFeatureName);
		domain = aDomain;
	}
	
	/**
	   * Get the feature value domain
	   *
	   * @return domain of feature value
	   */
	public ArrayList<String> getDomain() {
		return domain;
	}
	
	/**
	   * Set the feature value domain
	   *
	   * @param aDomain domain of feature value
	   */
	public void setDomain(ArrayList<String> aDomain){
		domain = aDomain;
	}
	
	/**
	   * Check if a nominal value is in this rule's domain.
	   *
	   * @param val string value
	   */		
	@Override
	public void checkValid(Object val) throws Exception{
			
		if(val!=null){
			if(val instanceof String){
				for(int i=0; i < domain.size(); i++){
					if(domain.get(i).equals(val)){
						return;
					}
				}
			}
			throw new Exception("invalid attribute value");
		}
			
	}

		

}
