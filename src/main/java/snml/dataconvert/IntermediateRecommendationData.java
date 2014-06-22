package snml.dataconvert;


/**
 * Intermediate data interface for recommendations. 
 */
public abstract class IntermediateRecommendationData implements IntermediateData {
	
	private Integer startUserAttributes;
	private Integer startItemAttributes;
	private Integer startPreferenceAttributes;

	/**
	 * Sets where the user attributes start
	 * @param attribIndex The index of the first user attribute
	 */
	public void setUserAttribute(int attribIndex) {
		this.startUserAttributes = attribIndex;
	}
	
	/**
	 * Get the attribute parts associated with the user
	 * @return The attribute or array of attributes associated with the user
	 */
	public Object getUserAttribute() {
		return getAttrValue(startUserAttributes, startItemAttributes);
	}
	
	/**
	 * Get the attribute parts associated with the item
	 * @return The attribute or array of attributes associated with the item
	 */
	public Object getItemAttribute() {
		return getAttrValue(startItemAttributes, startPreferenceAttributes);
	}
	
	/**
	 * Get the attribute parts associated with the preference
	 * @return The attribute or array of attributes associated with the preference
	 */
	public Object getPreferenceAttribute() {
		return getAttrValue(startPreferenceAttributes, startPreferenceAttributes);
	}
	
	/**
	 * The length of attribute parts (i.e. max attribIndex + 1)
	 * @return the length
	 */
	public abstract int length();

	private Object getAttrValue(Integer startAttributeIndex, Integer endAttributeIndex) {
		if (startAttributeIndex == null || endAttributeIndex == null) {
			return null;
		}
		
		int length = startAttributeIndex - endAttributeIndex;
		if (length < 1) {
			return null;
		} else if (length > 1) {
			Object[] attributeItems = new Object[length];
			for (int i=0; i<length; i++) {
				attributeItems[i] = getAttrValue(startAttributeIndex + i);
			}
			return attributeItems;
		} else {
			return getAttrValue(startAttributeIndex);
		}
	}
	
	/**
	   * Get the value of an attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return value of the attribute
	   */
	public abstract Object getAttrValue(int attrIndex);
	
	/**
	 * Sets where the item attributes start
	 * @param attribIndex The index of the first item attribute
	 */
	public void setItemAttribute(int attribIndex) {
		this.startItemAttributes = attribIndex;
	}

	/**
	 * Sets where the preference attributes start
	 * @param attribIndex The index of the first preference attribute
	 */
	public void setPreferenceAttribute(int attribIndex) {
		this.startPreferenceAttributes = attribIndex;
	}
}
