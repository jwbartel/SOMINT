package jinjing.dataimport.email;

import jinjing.dataimport.MsgDataConfig;

/**
 * An email message data configure to define fields name/type of an email message.
 * Any configure for particular email message data must extend this configure. 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class EmailDataConfig extends MsgDataConfig {
	
	/** message id */
	public static String ID			= "Id";
	
	/** message subject */
	public static String SUBJECT 	= "Subject";
	
	/** message attach file number */
	public static String ATTACHMENT_NUM 	= "Num_Attachments";
	
	/** message attach file names */
	public static String ATTACHMENTS		= "Attachments";
	
	/** message sender internal id */
	public static String FROM				= "From";
	
	/** message recipient ids */
	public static String RECIPIENTS			= "Recipients";
	
	/** message sending day of the week */
	public static String DAYOFWEEK			= "DayOfWeek";
	
	/** message sending time zone */
	public static String TIMEZONE			= "TimeZone";
	
	/**
	   * Create an email message data configure.
	   * Initialize attribute type map and attribute types.
	   *
	   */
	public EmailDataConfig(){
		super();
		
		attributeTypes.put(ID, INT);
		attributeTypes.put(SUBJECT, STRING);
		attributeTypes.put(ATTACHMENT_NUM, DOUBLE);
		attributeTypes.put(FROM, INT);
		attributeTypes.put(DAYOFWEEK, "{Mon, Tue, Wed, Thu, Fri, Sat, Sun}");
		attributeTypes.put(TIMEZONE, STRING);
		
		attributeTypes.put(ATTACHMENTS, "string array");
		attributeTypes.put(RECIPIENTS, "int array");		
	}
}
