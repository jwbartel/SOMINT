package snml.rule.superfeature.copy;

import snml.dataconvert.IntermediateData;
import snml.rule.DateFeatureRule;
import snml.rule.superfeature.ISuperFeatureRule;

/**
 * Copy date feature from intermediate data
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class DateCopySuperFeatureRule extends DateFeatureRule implements
		ISuperFeatureRule {

	/** name of src feature to be copied */
	protected String srcAttrName;
	
	/**
	   * Create an date super feature copying rule
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param srcAttrName name of src feature to be copied 
	   * @param dateFormat formate of date it process
	   */
	public DateCopySuperFeatureRule(String destFeatureName, String srcAttrName, String dateFormat) {
		super(destFeatureName, dateFormat);
		
		this.srcAttrName = srcAttrName;
	}

	/**
	 * Copy date feature value from an intermediate data instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		return anInstData.getDateAttrValue(srcAttrName);
	}
	



}
