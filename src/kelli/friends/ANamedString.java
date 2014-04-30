package kelli.friends;


public class ANamedString implements NamedString {
	String name;
	//Set<Integer> clique;
	String friendNames;
	//HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
//	public ANamedString(String theName, Set<Integer> theClique, HashMap<Integer, String> theUidNames) {
//		clique = theClique;
//		uidNames = theUidNames;
//		friendNames = toString(clique);
//		name = theName;
//	}
	public ANamedString(String theName, String theString) {
		friendNames = theString;
		name = theName;
	}
	
	
	
//	String toString(Set<Integer> s){
//			if (s == null) return "";
//		   String retVal = s.hashCode() + "(" + s.size() + ")";
//		   int numItemsInCurrentLine = 0;
//		   for (Integer element:s){
////			   if (numItemsInCurrentLine > 7) {
////				   numItemsInCurrentLine = 0;
////				   retVal += "\n";			   
////			   }
//			   
////			   retVal += ":" + uidNames.get(element)+"("+element+")";
//			   retVal +=  uidNames.get(element) + ":";
//
//		   }
//		   return retVal;
//	   }



	public String getName() {
		return name;
	}


//	public void setName(String name) {
//		this.name = name;
//	}

	@util.annotations.ComponentWidth(600)
	public String getFriendNames() {
		return friendNames;
	}

	

}
