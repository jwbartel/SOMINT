package prediction.features.messages;

import snml.dataimport.MsgDataConfig;

/**
 * A message data configure to define fields name/type of a message.
 * Any configure for particular message data must extend this configure. 
 */
public class MessageDataConfig extends MsgDataConfig {
	
	/** message id */
	public static String ID			= "Id";
	
	/** message subject */
	public static String TITLE 	= "Title";
	
	/** message attach file number */
	public static String ATTACHMENT_NUM 	= "Num_Attachments";
	
	/** message attach file names */
	public static String ATTACHMENTS		= "Attachments";
	
	/** message sender internal id */
	public static String CREATORS				= "Creators";
	
	/** message recipient ids */
	public static String COLLABORATORS			= "Collaborators";
	
	/** message sending day of the week */
	public static String DAYOFWEEK			= "DayOfWeek";
	
	/** message sending time zone */
	public static String TIMEZONE			= "TimeZone";
	
	/**
	   * Create an message data configure.
	   * Initialize attribute type map and attribute types.
	   *
	   */
	public MessageDataConfig(){
		super();
		
		attributeTypes.put(ID, INT);
		attributeTypes.put(TITLE, STRING);
		attributeTypes.put(ATTACHMENT_NUM, DOUBLE);
		attributeTypes.put(DAYOFWEEK, "{Mon, Tue, Wed, Thu, Fri, Sat, Sun}");
		attributeTypes.put(TIMEZONE, STRING);

		attributeTypes.put(CREATORS, "int array");
		attributeTypes.put(COLLABORATORS, "int array");	
		attributeTypes.put(ATTACHMENTS, "string array");	
	}
}
