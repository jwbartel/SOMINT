package snml.dataimport;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An message data configure to define fields name/type of a message data.
 * Any configure for particular message data, such as email or posts, must 
 * extend this configure. 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class MsgDataConfig {
	
	/** Type string */
	public static final String STRING 				= "string";
	
	/** Type int */
	public static final String INT 					= "int";
	
	/** Type long */
	public static final String LONG 				= "long";
	
	/** Type double */
	public static final String DOUBLE 				= "double";
	
	/** Type date */
	public static final String TYPEDATE				= "date";
	
	/** Default date format */
	public static final String DATEFORMAT_DEFAULT 	= "yyyy-MM-dd HH:mm:ss";
	
	/** map <attribute name, attribute type> to define attributes' types */
	public HashMap<String, String> attributeTypes;
	
	/** The id of thread a message data belongs to */
	public static final String THREADID 			= "ThreadId";
	
	/** The key field to sort messages in chronological order in threads */
	public static final String DATE_DEFAULT 		= "Date";
	
	/**
	   * Create a message data configure, initialize attribute type map.
	   * Initialize thread id as int
	   *
	   */
	public MsgDataConfig(){
		attributeTypes = new HashMap<String, String>();
		attributeTypes.put(THREADID, INT);
		attributeTypes.put(DATE_DEFAULT, TYPEDATE);
	}
	
	/**
	   * Get entry set of attribute type map.
	   *
	   */
	public Set<Entry<String, String>> attrTypeEntries(){
		return attributeTypes.entrySet();
	}
	
	/**
	   * Get type of a given attribute
	   * 
	   * @param attrName the name of an attribute
	   */
	public String getAttributeType(String attrName){
		return attributeTypes.get(attrName);
	}
	
	/**
	   * Check if the type of a given attribute is defined
	   * 
	   * @param attrName the name of an attribute
	   * @return true if the attribute's type is defined, otherwise false
	   */
	public boolean verifyAttribute(String attrName){
		return attributeTypes.containsKey(attrName);
	}
	
	/**
	   * Get number of defined attribute in this configure.
	   * 
	   * @return number of attributes in the type map
	   */
	public int attributeNum(){
		return attributeTypes.size();
	}

}
