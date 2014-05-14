package kelli.friends;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import util.misc.Message;
import util.models.ACheckedObject;
import util.models.ACheckedStringEnum;
import util.models.ADeletableStringInSets;
import util.models.AListenableVector;
import util.models.AStringInSets;
import util.models.CheckedObject;
import util.models.CheckedStringEnum;
import util.models.DeletableStringInSets;
import util.models.ListSelection;
import util.models.ListenableVector;
import util.models.StringInSets;

public class AFriendListManager implements FriendListManager{
	
	static final String ALL_FRIENDS = "Friends";
	Map<String, Set<Integer>>  nameToClique = new HashMap();
	//Map<String, ListenableVector<CheckedObject<String>>>  nameToCheckedObjectList = new HashMap();
	Map<String, FriendList>  nameToFriendList= new HashMap();
	Map<String, CompactFriendList>  nameToCompactFriendList= new HashMap();
	FriendListNameList friendListNameList;
	HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
	ListenableVector<String> names = new AListenableVector();
	private Collection<NamedString> namedCliques;
	Collection<Set<Integer>> mergedCliques;
	Set<Integer> allFriends = new HashSet();
//	List<Integer> allFriendsIdList = new Vector();
//	List<String> allFriendsNameList = new Vector();
	List<NameIDRecord> allFriendsList = new Vector();
	CompactFriendList<DeletableStringInSets> allFriendsCompactList;

	public AFriendListManager(Collection<Set<Integer>> theMergedCliques, HashMap<Integer, String> theUidNames) {
		mergedCliques = theMergedCliques;
		uidNames = theUidNames;
		processCliques(mergedCliques);
	}
	String toBeautifiedName (String name) {
		int spaceIndex = name.indexOf(' ');
		if (spaceIndex == -1 || spaceIndex + 1 >= name.length())
			return name;
		String retVal = name.substring(spaceIndex + 1);
		return  retVal;
	}
	String firstYes(ListenableVector<CheckedObject<String>> checkedObjects) {
		for (int i = 0; i < checkedObjects.size(); i++) {
			if (checkedObjects.get(i).getStatus())
				return checkedObjects.get(i).getObject();
		}
		return "";
	}
	
	String lastYes(ListenableVector<CheckedObject<String>> checkedObjects) {
		for (int i = checkedObjects.size() -1; i >= 0; i--) {
			if (checkedObjects.get(i).getStatus())
				return checkedObjects.get(i).getObject();
		}
		return "";
	}
	String getUniqueName(ListenableVector<CheckedObject<String>> checkedObjects, String prefix, String suffix) {
		String retVal = prefix;
		for (int i = checkedObjects.size() -2; i >= 0; i--) {
			if (checkedObjects.get(i).getStatus()) {
				retVal = prefix + toBeautifiedName(checkedObjects.get(i).getObject()) + suffix;
				if (!names.contains(retVal))
					return retVal;
			}				
		}
		return "";
	}
	void processCliques (Collection<Set<Integer>> sets){
		    namedCliques = new AListenableVector<NamedString>();
		    names.clear();
		    //names.add(ALL_FRIENDS);
		   //namedCliques.clear();
		    for(Set<Integer> set: sets){
		    	allFriends.addAll(set);
		    }
		    
		    initializeAllFriendsList();
		   
		   for(Set<Integer> set: sets){	
			   ListenableVector<CheckedObject<String>> checkedObjects = createCheckedObjectList(set);
//			   String friend1Name = toBeautifiedName(checkedObjects.get(0).getObject());
//			   String friend2Name = toBeautifiedName(checkedObjects.get(checkedObjects.size() -1).getObject());
			   String friend1Name = toBeautifiedName(firstYes(checkedObjects));
			   String friend2Name = toBeautifiedName(lastYes(checkedObjects));
//			   
//			   Integer[] template = {0};
//			   Integer[] elements = set.toArray(template);
//			   String friend1Name = toBeautifiedName(uidNames.get(elements[0]));
//			   String friend2Name = toBeautifiedName(uidNames.get(elements[elements.length-1])) ;
			   
//			   String name = uidNames.get(elements[0]) + "..." + uidNames.get(elements[elements.length-1]) +
//			   "(" + set.size() + ")";
			   String prefix = friend1Name + "...";
			   String suffix =  "(" + set.size() + ")";
			   String name = prefix + friend2Name +
			   suffix;
			   
//			   if (names.contains(name)) {
//				   name += "(" + set.size() + ")";
				   if (names.contains(name)) {
					   name = getUniqueName(checkedObjects, prefix, suffix);
					   //name += "(" + set.hashCode() + ")";
				   }
			   //}
			   nameToClique.put(name, set);
			   names.add(name);
			   //ListenableVector<CheckedObject<String>> checkedObjects = createCheckedObjectList(set);
			   FriendList friendList = new AFriendList(name, checkedObjects, this);
			   //NamedString namedClique = new ANamedString(name, set, uidNames);
			   NamedString namedClique = new ANamedString(name, toString(set));
			   //nameToCheckedObjectList.put(name, checkedObjects);
			   nameToFriendList.put(name, friendList);
			   namedCliques.add(namedClique);
			   
		   }	 
		   friendListNameList = new AFriendListNameList(this);
		   createAllFriendsCompactList();
		   createCompactFriendLists();
		   //ObjectEditor.edit(allFriendsCompactList);
		   //reloadNames();

	}
	public FriendList createEmptyFriendList() {
		//return new AFriendList("", createAllUncheckedObjectList(), this);		
		return new ANewFriendList(createAllUncheckedObjectList(), this);		
	}
	ListenableVector<CheckedObject<String>> createCheckedObjectList(Set<Integer> set) {
		ListenableVector retVal = new AListenableVector();
		//for (Integer id: allFriends) {
		for (NameIDRecord friend: allFriendsList) {
			//String name = uidNames.get(id);
			Integer id = friend.getId();
			String name = friend.getName();
			CheckedObject checkedObject = new ACheckedObject(name, set.contains(id), friend);
			retVal.add(checkedObject);
		}
		return retVal;		
	}
	ListenableVector<CheckedObject<String>> createAllUncheckedObjectList() {
		ListenableVector retVal = new AListenableVector();
		//for (Integer id: allFriends) {
		for (NameIDRecord friend: allFriendsList) {
			//String name = uidNames.get(id);
			Integer id = friend.getId();
			String name = friend.getName();
			CheckedObject checkedObject = new ACheckedObject(name, false, friend);
			retVal.add(checkedObject);
		}
		return retVal;		
	}
	
	void initializeAllFriendsList() {		
		for (Integer id: allFriends) {
			
			String name = uidNames.get(id);
			if (name == null) {
				Message.warning("No NAME FOR USER ID:" + id + " allFriends iterator id: " + allFriendsList.size());
				//System.out.println("Null name in initializeAllFriendsList");
				//continue;
				name = "Anonymous: index:" + allFriendsList.size() + " id:" + id;
			} 
			NameIDRecord nameIDRecord = new ANameIdRecord(id, name);
			allFriendsList.add(nameIDRecord);		
		}
		Collections.sort(allFriendsList);
	}
	
//	void createCompactFriendLists() {
//		for (int i = 0; i < names.size(); i++) {
//			String listName = names.get(i);
//			ListenableVector<DeletableStringInSets> stringInSetsList = createStringInSetsList(listName, nameToFriendList.get(listName));
//			CompactFriendList compactFriendList = new ACompactFriendList(listName, stringInSetsList, this);
//			nameToCompactFriendList.put(listName, compactFriendList);
//		}
//	}
	void createCompactFriendLists() {
		for (int i = 0; i < names.size(); i++) {
			String listName = names.get(i);
//			ListenableVector<StringInSets> stringInSetsList = createStringInSetsList(listName, nameToFriendList.get(listName));
//			CompactFriendList compactFriendList = new ACompactFriendList(listName, stringInSetsList, this);
			CompactFriendList compactFriendList = createCompactFriendList(listName, nameToFriendList.get(listName));
			nameToCompactFriendList.put(listName, compactFriendList);
		}
	}
	CheckedStringEnum createCheckedStringEnum(int index) {
		List<CheckedObject<String>> choices = new Vector();
		ListenableVector<String> names = this.getNames();
		for (int i = 0; i < names.size(); i++) {
			String tempListName = names.get(i);
			CheckedObject<String>  friend = this.getFriend(tempListName, index);
			choices.add(new ACheckedObject<String>(tempListName, friend.getStatus(), friend.getUserData()));
			
		}
		CheckedStringEnum retVal = new ACheckedStringEnum(choices);
		return retVal;		
	}
//	CheckedStringEnum createFullyCheckedStringEnum(int index) {		
//		ListenableVector<String> names = this.getNames();
//		for (int friendIndex = 0; friendIndex < allFriends.size(); friendIndex++) {
//			Integer friendId = allFriendsList.get(friendIndex);
//			String friendName = uidNames.get(friendId);	
//			List<CheckedObject<String>> choices = new Vector();
//			for (int nameIndex = 0; nameIndex < names.size(); nameIndex++) {
//				String tempListName = names.get(nameIndex);
//				CheckedObject<String>  friend = this.getFriend(tempListName, index);
//				choices.add(new ACheckedObject<String>(tempListName, friend.getStatus(), friend.getUserData()));				
//			}
//			CheckedStringEnum retVal = new ACheckedStringEnum(choices);
//		}
//		
//		return retVal;		
//	}
	
//	ListenableVector<DeletableStringInSets>  createStringInSetsList(String listName, FriendList friendList) {
//		ListenableVector<DeletableStringInSets>  compactFriends = new AListenableVector();
//		for (int i = 0; i < friendList.getFriends().size(); i++) {
//			ListenableVector<String> names = this.getNames();
//			CheckedObject<String> friend = this.getFriend(listName, i);
//			if (!friend.getStatus())
//				continue;
//			DeletableStringInSets stringInSets = createStringInSets(friend, compactFriends);
//			compactFriends.add(stringInSets);			
//		}	
//		return compactFriends;
//	}
	ListenableVector<StringInSets>  createStringInSetsList(String listName, FriendList friendList) {
		ListenableVector<StringInSets>  compactFriends = new AListenableVector();
		for (int i = 0; i < friendList.getFriends().size(); i++) {
			ListenableVector<String> names = this.getNames();
			CheckedObject<String> friend = this.getFriend(listName, i);
			if (!friend.getStatus())
				continue;
			StringInSets stringInSets = createStringInSets(friend);
			compactFriends.add(stringInSets);			
		}	
		return compactFriends;
	}
	CompactFriendList<DeletableStringInSets> createCompactFriendList(String listName, FriendList friendList) {
		ListenableVector<DeletableStringInSets>  stringInSetsList = new AListenableVector();
		for (int i = 0; i < friendList.getFriends().size(); i++) {
			//CheckedObject<String> friend = this.getFriend(listName, i);
			ListSelection<CheckedObject<String>, String> selectedFriends = friendList.getFriends();
			 
			CheckedObject<String> friend =  selectedFriends.get(i);
			if (!friend.getStatus())
				continue;
			stringInSetsList.add(allFriendsCompactList.getCompactFriends().get(i));
		}
		return new ACompactFriendList<DeletableStringInSets>(listName, stringInSetsList, this);
	}
	void createAllFriendsCompactList() {
		ListenableVector<DeletableStringInSets>  stringInSetsList = new AListenableVector();
		ListenableVector<String> names = this.getNames();
		for (int friendIndex = 0; friendIndex < allFriends.size(); friendIndex++) {
			NameIDRecord nameIDRecord = allFriendsList.get(friendIndex);
			Integer friendId = nameIDRecord.getId();
			String friendName = nameIDRecord.getName();
			CheckedStringEnum checkedStringEnum = createCheckedStringEnum(friendIndex);
			DeletableStringInSets stringInSets = new ADeletableStringInSets(friendName,checkedStringEnum, stringInSetsList );
			stringInSetsList.add(stringInSets);
		}
		allFriendsCompactList = new ACompactFriendList<DeletableStringInSets>(ALL_FRIENDS, stringInSetsList, this);
	}
	
	
	public CompactFriendList<DeletableStringInSets> getAllFriendsCompactList() {
		return allFriendsCompactList;
	}
	
//	DeletableStringInSets createStringInSets (CheckedObject<String> checkedObject, List<DeletableStringInSets> theContainer) {
//		String name = checkedObject.getObject();
//		Integer id = (Integer) checkedObject.getUserData();
//		Integer index = this.getFriendIndex(id);
//		CheckedStringEnum checkedStringEnum = createCheckedStringEnum(index);
//		DeletableStringInSets retVal = new ADeletableStringInSets(name,checkedStringEnum, theContainer );
//		return  retVal;	
//		
//	}
	
	StringInSets createStringInSets (CheckedObject<String> checkedObject) {
		String name = checkedObject.getObject();
		NameIDRecord friend = (NameIDRecord) checkedObject.getUserData();
		Integer index = this.getFriendIndex(friend);
		CheckedStringEnum checkedStringEnum = createCheckedStringEnum(index);
		StringInSets retVal = new AStringInSets(name,checkedStringEnum );
		return  retVal;	
		
	}
	
	String toString(Set<Integer> s){
		if (s == null) return "";
	   String retVal = s.hashCode() + "(" + s.size() + ")";
	   int numItemsInCurrentLine = 0;
	   for (Integer element:s){
//		   if (numItemsInCurrentLine > 7) {
//			   numItemsInCurrentLine = 0;
//			   retVal += "\n";			   
//		   }
		   
//		   retVal += ":" + uidNames.get(element)+"("+element+")";
		   retVal +=  uidNames.get(element) + ":";

	   }
	   return retVal;
   }
	  public Collection<NamedString> getNamedStringCliques() {
//		   if (namedCliques == null)
//			   processCliques(mergedCliques);
		   return namedCliques;
	   }
	  public ListenableVector<String> getNames() {
		  return names;
	  }
	  public FriendListNameList getFriendListNameList() {
		  return friendListNameList;
	  }
	  public FriendList getFriendList(String name) {
		  return nameToFriendList.get(name);
	  }
	  public int getFriendIndex(NameIDRecord friend) {
		  return allFriendsList.indexOf(friend);
		  
	  }
	  public CheckedObject<String> getFriend(String listName, int index) {
		  FriendList friendList = nameToFriendList.get(listName);
		  ListSelection<CheckedObject<String>, String> selectedFriends = friendList.getFriends();
		 
		return selectedFriends.get(index);
			  
	  }
	  public void setFriendStatus(String listName, int index, boolean status) {
		  CheckedObject<String> friend = getFriend(listName, index);
		  friend.setStatus(status);
			  
	  }
	public void addName(String name) {
		// TODO Auto-generated method stub
		
	}
	public void changeName(String originalName, String newName) {		
		FriendList friendList = nameToFriendList.get(originalName);
		if (friendList == null) return;
		//ListSelection<CheckedObject<String>, String> checkedList = friendList.getFriends();
		//if (friendList != null)
		nameToFriendList.remove(originalName);
		nameToFriendList.put(newName, friendList);
		int nameIndex = names.indexOf(originalName);
		names.set(nameIndex, newName);
		
		
	}
	public void addFriendList(String name, FriendList friendList) {
		nameToFriendList.put(name, friendList);
		names.add(name);
		CompactFriendList compactFriendList = createCompactFriendList(name, friendList);
		nameToCompactFriendList.put(name, compactFriendList);
	}
	void reloadNames() {
		names.clear();
		for (String name: nameToFriendList.keySet())
			names.add(name);
	}
	public void removeList(String name) {
		names.remove(name);
		
	}
	public CompactFriendList getCompactFriendList(String name) {
		// TODO Auto-generated method stub
		return nameToCompactFriendList.get(name);
	}
	
}
