package jinjingdataconvert;


/**
 * Intermediate data interface. All schemes of intermediate data in
 * SoMMinT implement this interface. 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IntermediateData{
	
	/**
	   * Set the value of a string/numeric/nominal attribute. 
	   * The attribute value must match the type of the attribute. 
	   * Must not change the dataset in any way.
	   *
	   * @param dataset associate dataset of current instance
	   * @param attrIndex index of attribute whose value is to be set
	   * @param val value to set to the attribute
	   * @return next attribute index
	   */
	public int setAttrValue(IntermediateDataSet dataset, int attrIndex, Object val);
	
	/**
	   * Set the value of a date attribute. 
	   * The attribute value must match the type of the attribute. 
	   * Must not change the dataset in any way.
	   *
	   * @param dataset associate dataset of current instance
	   * @param attrIndex index of attribute whose value is to be set
	   * @param val value to set to the attribute
	   * @return next attribute index
	   */
	public int setDateAttrValue(IntermediateDataSet dataset, int attrIndex, Object val);
	
	/**
	   * Set the value of an attribute if the value is missing. 
	   * Must not change the dataset in any way.
	   *
	   * @param dataset associate dataset of current instance
	   * @param attrIndex index of attribute whose value is to be set
	   * @return next attribute index
	   */
	public int setMissing(IntermediateDataSet dataset, int attrIndex);
	
	/**
	   * Get the string value of an attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return string value of the attribute
	   */
	public String getStringAttrValue(int attrIndex);
	
	/**
	   * Get the string value of an attribute.
	   *
	   * @param attrName name of target attribute
	   * @return string value of the attribute
	   */
	public String getStringAttrValue(String attrName);
	
	/**
	   * Get the numeric(double) value of an attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return double value of the attribute
	   */
	public double getNumericAttrValue(int attrIndex);
	
	/**
	   * Get the numeric(double) value of an attribute.
	   *
	   * @param attrName name of target attribute
	   * @return double value of the attribute
	   */
	public double getNumericAttrValue(String attrName);
	
	/**
	   * Get the value of an date attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return string value of the date attribute
	   */
	public String getDateAttrValue(int attrIndex);
	
	/**
	   * Get the value of an date attribute.
	   *
	   * @param attrName name of target attribute
	   * @return string value of the date attribute
	   */
	public String getDateAttrValue(String attrName);
	

}
