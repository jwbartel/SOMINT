package prediction.features.messages;

import snml.dataimport.MsgDataConfig;

/**
 * A message data configure to define fields name/type of a message.
 * Any configure for particular message data must extend this configure. 
 */
public class MessageDataConfig extends MsgDataConfig {
	
	/** message title */
	public static String TITLE 	= "Title";
	
	/** words in the message title */
	public static String TITLE_WORDS 	= "Title_Words";
	
	/** message attach file number */
	public static String ATTACHMENT_NUM 	= "Num_Attachments";
	
	/** message attach file names */
	public static String ATTACHMENTS		= "Attachments";
	
	/** message creators internal ids */
	public static String CREATORS				= "Creators";
	
	/** message collaborator ids */
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
		
		attributeTypes.put(TITLE, STRING);
		attributeTypes.put(TITLE_WORDS, "string array");
		attributeTypes.put(ATTACHMENT_NUM, DOUBLE);
		attributeTypes.put(DAYOFWEEK, "{Mon, Tue, Wed, Thu, Fri, Sat, Sun}");
		attributeTypes.put(TIMEZONE, STRING);

		attributeTypes.put(CREATORS, "int array");
		attributeTypes.put(COLLABORATORS, "int array");
		attributeTypes.put(ATTACHMENTS, "string array");	
	}
}
