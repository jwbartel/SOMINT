package snml.dataimport;

import java.util.HashMap;

/**
 * An message data stands for a message or a post.
 * It can have any number/type of attributes.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class MessageData {
	
	/** attribute map <attribute name, attribute value> of the message */
	protected HashMap<String, Object> attributes;
	
	/**
	   * Create a message data with empty attribute map
	   *
	   */
	public MessageData(){
		attributes = new HashMap<String, Object>();
	}
	
	/**
	   * Create a message data with empty attribute map
	   * with given number of attributes 
	   *
	   * @param attributeNum number of attributes the message will have
	   */
	public MessageData(int attributeNum){
		attributes = new HashMap<String, Object>(attributeNum+1, 1);
	}
	
	/**
	   * Add a new attribute to the message, 
	   * or replace the attribute with the same name. 
	   *
	   * @param attrName name of the new attribute
	   * @param attrVal value of the new attribute
	   */
	public void addAttribute(String attrName, Object attrVal){
		attributes.put(attrName, attrVal);
	}
	
	/**
	   * Get the value of an attribute with given name.
	   * Return null if the given name is invalid.
	   *
	   * @param attrName name of the attribute
	   * @return value of the attribute
	   */
	public Object getAttribute(String attrName){
		return attributes.get(attrName);
	}
}
