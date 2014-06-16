package snml.dataimport.json;


import snml.dataimport.MsgDataConfig;

/**
 * An message data configure to define fields name/type of JSON-format message data.
 * Any configure for particular JSON-format message data must extend this configure. 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class JsonDataConfig extends MsgDataConfig{
	
	/** Type of a JSON component */
	public static final String JSON 		= "JSON";
	
	/** Type of a JSON component is a message */
	public static final String MESSAGE 		= "message";

	/**
	   * Create an JSON message data configure.
	   *
	   */
	public JsonDataConfig(){
		super();
	}
	
	/**
	   * Get the date format in files to read
	   * 
	   * @return the date format in files to read
	   */
	public abstract String getDateReadFormat();
	
}
