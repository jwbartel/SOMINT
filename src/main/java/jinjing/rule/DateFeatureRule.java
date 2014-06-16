package jinjing.rule;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Superclass of all rules extracting date-type features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class DateFeatureRule extends FeatureRule {
	
	/** date formate of this feature */
	String dateFormat;

	/**
	   * Create an date feature extracting rule
	   * Initialize date feature
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param dateFormat formate of date it process
	   */
	public DateFeatureRule(String destFeatureName, String dateFormat) {
		super(destFeatureName);
		this.dateFormat = dateFormat;
	}

	/**
	   * Check if a date value is in this rule's format
	   *
	   * @param val a string value of date
	   */
	@Override
	public void checkValid(Object val) throws Exception{
		if(val==null) return;
		
		if(val instanceof String){
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			try {
				format.parse((String)val);
			} catch (ParseException e) {
				throw new Exception("Date format must be "+ dateFormat);
			}
		}else if(!(val instanceof Double)){
			throw new Exception("Date-type feature/attribute must be Double or String");
		}
	}

}
