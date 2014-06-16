package snml.rule.superfeature;

import java.util.ArrayList;

import snml.dataconvert.IntermediateData;
import snml.rule.BinaryFeatureRule;

/**
 * Decide if a sting feature contains given string tokens
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class TextBinaryFeatureRule extends BinaryFeatureRule implements ISuperFeatureRule{

	/** string feature name to extract new feature */
	protected String attrName;
	
	/** save string tokens */
	protected String[] seeIfContainTheseTokens;
		
	/**
	 * Create a feature copying rule
	 * Initialize listed variables
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param aDomain domain of the nominal value
	 * 			Leave aDomain null or set "yes" at pos 0, "no" at pos 1
	 * @param attrName name of attribute to copy
	 * @param seeIfContainTheseTokens tokens to decide
	 */
	public TextBinaryFeatureRule(String destFeatureName, 
			ArrayList<String> aDomain, 
			String attrName,
			String[] seeIfContainTheseTokens) {
		super(destFeatureName, aDomain);
		this.attrName = attrName;
		this.seeIfContainTheseTokens = seeIfContainTheseTokens;		
	}

	/**
	 * Extracted if a given sting feature contains given string tokens
	 * from given intermediate data
	 * 
	 * @param anInstData the source intermediate data
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		if(anInstData==null) return null;
		
		String attrValue = anInstData.getStringAttrValue(attrName);
		if(attrValue==null) return null;
		
		for(int i=0; i < this.seeIfContainTheseTokens.length; i++){
			if(attrValue.contains(this.seeIfContainTheseTokens[i])){
				return domain.get(0);
			}
		}
		return domain.get(1);
	}

}
