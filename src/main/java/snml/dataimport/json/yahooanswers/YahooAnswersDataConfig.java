package snml.dataimport.json.yahooanswers;

import snml.dataimport.json.JsonDataConfig;

/**
 * Define common fields name/type of YahooAnswers data.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class YahooAnswersDataConfig extends JsonDataConfig{
	
	/** attribute name of Content of a message */
	public static String CONTENT 			= "Content";
	
	/** attribute name of timestamp of a message */
	public static String TIMESTAMP 			= "Timestamp";
	
	/** attribute name of author id of a message */
	public static String USERID				= "UserId";
	
	/** attribute name of author nick of a message */
	public static String USERNICK 			= "UserNick";
	
	/** Yahoo post date format */
	public static final String DATEFORMAT_YAHOO 	= "yyyy-MM-dd HH:mm:ss";
	
	/**
	   * Create an YahooAnswer message data configure.
	   * Initialize attribute type map and attribute types.
	   *
	   */
	public YahooAnswersDataConfig(){
		super();
		
		attributeTypes.put(CONTENT, STRING);
		
		attributeTypes.put(TIMESTAMP, LONG);
		attributeTypes.put(USERID, STRING);
		attributeTypes.put(USERNICK, STRING);
	}
	
	/**
	   * Get reading data format
	   *
	   * @return data format for reading data
	   */
	@Override
	public String getDateReadFormat() {
		return DATEFORMAT_YAHOO;
	}
	
}
