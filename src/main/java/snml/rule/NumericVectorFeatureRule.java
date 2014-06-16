package snml.rule;

import java.lang.reflect.Array;

/**
 * Abstract, superclass of all rules extracting an array 
 * of numeric features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class NumericVectorFeatureRule extends FeatureRule {

	/** vector length */
	public int length = 0;
	
	/**
	 * Create a numeric-type feature extracting rule
	 * Initialize name for extracted feature, and certain length of feature vector
	 * 
	 * @param featureName
	 * @param l
	 */
	public NumericVectorFeatureRule(String featureName, int l){
		super(featureName);
		length = l;
	}
	
	/**
	 * Check if an object is numeric vector with valid length
	 * 
	 * @param val object value
	 * @throws Exception when object value is not valid
	 */
	@Override
	public void checkValid(Object val) throws Exception{
		/*
		if(val==null){
			System.out.println("Error in NumericVectorSuperFeatureRule.isValid: " + 
					"parameter is null");
			return false;
		}
		*/
		if(val!=null){
			if(val instanceof double[] || val instanceof Double[]){
			
				if(Array.getLength(val)!=length){
					throw new Exception("vector length does not match");
				}
			}else{
				throw new Exception("only double vector allowed to be returned");		
			}
		}
	}

}
