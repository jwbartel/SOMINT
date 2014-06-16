package jinjing.rule.superfeature;

import jinjing.dataconvert.IntermediateData;
import jinjing.rule.IFeatureRule;

/**
 * Interface of all super feature extracting (from intermediate data) rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface ISuperFeatureRule extends IFeatureRule{
	
	/**
	 * Extracted feature value from an intermediate data instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	public Object extract(IntermediateData anInstData) throws Exception;

}
