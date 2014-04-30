package kelli.friends;

public class ANameIdRecord implements NameIDRecord {
	Integer id;
	String name;
	public ANameIdRecord (Integer theID, String theName) {
		id = theID;
		name = theName;
	}
	public Integer getId() {
		return id;
	}
//	public void setId(String id) {
//		this.id = id;
//	}
	public String getName() {
		return name;
	}
	public String toString() {
		return name;
	}
//	public void setName(String name) {
//		this.name = name;
//	}
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return toString().compareTo(arg0.toString());
	}
	

}
