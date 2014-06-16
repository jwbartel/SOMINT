package jinjing.dataconvert;

/**
 * Feature extractor interface. All schemes for feature extraction in
 * SoMMinT implement this interface. Note that a feature extractor for 
 * particular type of dataset should define its own extracting method
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class IFeatureExtractor {
	
	/** An initializer for output dataset */
	protected IntermediateDataInitializer destDatasetInit;
	
	/**
	   * Initialize an feature extractor with a given output 
	   * dataset initializer
	   * 
	   * @param init the initializer of output dataset
	   */
	public IFeatureExtractor(IntermediateDataInitializer init){
		destDatasetInit = init;
	}


}
