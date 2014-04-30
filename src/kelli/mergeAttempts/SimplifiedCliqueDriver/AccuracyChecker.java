package kelli.mergeAttempts.SimplifiedCliqueDriver;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;

//FriendGrouperResults/2010Study/744355117


//not LevenshteinDistance, which treats insertions and deletions (and substitutions, which we don't use) as the same cost.
public class AccuracyChecker {
	static String participantId = "10";
	static ArrayList<FriendList> recommendedLists = new ArrayList<FriendList>();
	static ArrayList<FriendList> idealLists = new ArrayList<FriendList>();
	static ArrayList<FriendList> joinedLists;
	static AccuracyRecord accuracyMatrix1[][];
	static HashMap<String, String> rlToBestList = new HashMap<String, String>();
	static HashMap<String, String> rlToBestListKAcc = new HashMap<String, String>();
	static HashMap<String, ArrayList<String>> ilToRL = new HashMap<String, ArrayList<String>>();
	static ArrayList<String> dontJoinList = new ArrayList<String>();
	//static ArrayList<Float><Float> thing;
	//make into record?  when i have more things
	public static void main(String args[]){
		//read in lists
		setUpLists();
		accuracyMatrix1 = new AccuracyRecord[recommendedLists.size()][idealLists.size()];
		double costMatrix[][] = new double[recommendedLists.size()][idealLists.size()];
		double kAccMatrix[][] = new double[recommendedLists.size()][idealLists.size()];
		double savingsMatrix[][] = new double[recommendedLists.size()][idealLists.size()];
		//compare lists
		compareLists(recommendedLists, savingsMatrix, costMatrix, kAccMatrix);
		//output results
		printkAccMatrix(kAccMatrix, recommendedLists);
		printRLtoBestListsKAcc(recommendedLists);
		printCostMatrix(costMatrix, recommendedLists);
		printJoinLists();
		joinLists();
		System.out.println("~~~~~~~~~~~~~~~~~~ AFTER JOIN ~~~~~~~~~~~~~~~~~~~~~~~~~~");
		double cost2[][] = new double[joinedLists.size()][idealLists.size()];
		double kAcc2[][] = new double[joinedLists.size()][idealLists.size()];
		double save2[][] = new double[joinedLists.size()][idealLists.size()];
		compareLists(joinedLists, save2, cost2, kAcc2);
		printkAccMatrix(kAcc2, joinedLists);
		printCostMatrix(cost2, joinedLists);
	}
	private static void findRLtoBestCostLists(ArrayList<FriendList> recLists, double[][] costMatrix){
		for(int rl=0; rl<recLists.size(); rl++){
			String bestLists = " ";
			for(int il=0; il<idealLists.size(); il++){
				DecimalFormat myFormatter = new DecimalFormat("0.000");
			    String formatCost = myFormatter.format(costMatrix[rl][il]);
				if(costMatrix[rl][il] < 1){
				   bestLists = bestLists.concat(idealLists.get(il).name+" ("+formatCost+")"+", ");
				}
			}
			rlToBestList.put(recLists.get(rl).name, bestLists);
		}
	}
	private static void printRLtoBestCostLists(ArrayList<FriendList> recLists, double[][] costMatrix) {
		System.out.println("~~~~~~~~~~~~Recommended Lists to Best Lists~~~~~~~~~~~~~~~~");
		System.out.println("Recommended List maps to Ideal List (cost)");
		
		if (rlToBestList.isEmpty())
			findRLtoBestCostLists(recLists, costMatrix);
		
		for(FriendList recList:recLists){
			System.out.println(recList.name+": "+rlToBestList.get(recList.name));
		}
	}
	private static void printRLtoBestListsKAcc(ArrayList<FriendList> recLists) {
		System.out.println("~~~~~~~~~~~~Recommended Lists to Best Lists~~~~~~~~~~~~~~~~");
		System.out.println("~~~~~~~~~~~~              kAcc             ~~~~~~~~~~~~~~~~");
		System.out.println("Recommended List maps to Ideal List (kAcc)");
		for(FriendList recList:recLists){
			System.out.println(recList.name+": "+rlToBestListKAcc.get(recList.name));
		}
	}
	static void setUpLists(){
	   setUpIdeal();
	   setUpRecommendedNetworks();
	   setUpRecommendedSubcliques();
	}
	static int findInsert(FriendList rl, FriendList il){  //IL - RL
		int insert = 0;
		for(String s: il.members){
			if(!rl.members.contains(s)){
				insert++;
			}
		}
		return insert;
	}
	static int findDelete(FriendList rl, FriendList il){  //RL - IL
		int delete = 0;
		for(String s: rl.members){
			if(!il.members.contains(s)){
				delete++;
			}
		}
		return delete;
	}
	
	// 1 - (insert + .5*delete)/|IL|
	static double findCostToCreateIdeal(FriendList rl, FriendList il){
		double retVal;
		double in = findInsert(rl,il);
		double del = findDelete(rl,il);
		double cost = in + .5*del;
		retVal = Math.abs((cost)/il.getSize());
		NumberFormat f = NumberFormat.getInstance();
		return retVal;
	}
	static double findKAccuracy(FriendList rl, FriendList il){
		double numSame = 0;
		double retVal;
		for (String name: rl.members){
			if (il.members.contains(name)) numSame++;
		}
		if(rl.getSize() >= il.getSize()){
			retVal = numSame / rl.getSize();
		} else retVal = numSame/il.getSize();
		/**    if (RL(rl).size > IL(il).size)
		 * 			#RL in IL / RL(rl).size
		 * 		else #RL in IL / IL(il).size
		 */		
		return retVal;
	}
	static void compareLists(ArrayList<FriendList> recLists, double[][] sMatrix, double[][] costMatrix, double[][] kAccMatrix){
	   System.out.println("comparisons done here");
	   int insert;  //IL - RL
	   int delete;  //RL - IL
	   System.out.println("equation 1");
	   //AccuracyRecord record = new AccuracyRecord();
	   for(int rl = 0; rl < recLists.size(); rl++){
		  FriendList recList = recLists.get(rl);
		  for(int il = 0; il < idealLists.size(); il++){
			  //record = new AccuracyRecord();
			  FriendList idealList = idealLists.get(il);
			  double costForIdeal = findCostToCreateIdeal(recList,idealList); 
			  double costInsert = findCostToInsert(recList,idealList);
			  double costKAcc = findKAccuracy(recList, idealList);
			  			  double costDelete = findCostToDelete(recList,idealList);
			  //double costDelete = findDelete(recList,idealList);
			  AccuracyRecord record = new AccuracyRecord(costForIdeal, costInsert, costKAcc, costDelete);
			  double savings = findSavings(recList, idealList);
			  accuracyMatrix1[rl][il] = record;
			  costMatrix[rl][il] = costForIdeal;
			  if(costMatrix[rl][il] == 0){
				  String R = recLists.get(rl).name;
				  String I = idealLists.get(il).name;
				  dontJoinList.add(recLists.get(rl).name);
			  }
			  kAccMatrix[rl][il] = costKAcc;
			  sMatrix[rl][il] = savings;
		  }
	   }
		//how do we handle join/split?  if 1 IL -> 2+ RL, RL should be joined.  
		//if 1 RL -> 2+ IL, RL should be split
	}
	private static double findSavings(FriendList rl, FriendList il){
		double retVal = 0;
		retVal = il.getSize() - (findInsert(rl,il) + .5*findDelete(rl,il));
		return retVal;
	}
	// Cost to insert
	// 1 - insert/|IL|
	private static double findCostToInsert(FriendList rl, FriendList il) {
		double retVal;
		int in = findInsert(rl,il);
		retVal = in/il.getSize();
		return retVal;
	}
	private static double findCostToDelete(FriendList rl, FriendList il) {
		double retVal;
		int del = findDelete(rl,il);
		retVal = (del*.5)/il.getSize();
		return retVal;
	}
	static void printMatrix(double[][] costMatrix, double[][] kAccMatrix){
		System.out.println("Recommended v Ideal");
		System.out.print("      ");
		for (int il = 0; il < idealLists.size(); il++){
			System.out.print(idealLists.get(il).name+" - ");
		}
		System.out.println();
		for(int rl=0; rl<recommendedLists.size(); rl++){
			System.out.print(recommendedLists.get(rl).name+"  ");
			for(int il=0; il<idealLists.size(); il++){
				System.out.print(accuracyMatrix1[rl][il].toFormattedString()+"  ");
			}
			System.out.println();
		}
		for (int idealListIndex = 0; idealListIndex < idealLists.size(); idealListIndex++) {
			ObjectEditor.setPropertyAttribute(accuracyMatrix1.getClass(), "*." + idealListIndex, AttributeNames.LABEL, idealLists.get(idealListIndex).name);
			ObjectEditor.setPropertyAttribute(costMatrix.getClass(), "*." + idealListIndex, AttributeNames.LABEL, idealLists.get(idealListIndex).name);
			ObjectEditor.setPropertyAttribute(kAccMatrix.getClass(), "*." + idealListIndex, AttributeNames.LABEL, idealLists.get(idealListIndex).name);
		}
		for (int recListIndex = 0; recListIndex < recommendedLists.size(); recListIndex++) {
			ObjectEditor.setPropertyAttribute(accuracyMatrix1.getClass(), "" + recListIndex, AttributeNames.LABEL, recommendedLists.get(recListIndex).name);
			ObjectEditor.setPropertyAttribute(costMatrix.getClass(), "" + recListIndex, AttributeNames.LABEL, recommendedLists.get(recListIndex).name);
			ObjectEditor.setPropertyAttribute(kAccMatrix.getClass(), "" + recListIndex, AttributeNames.LABEL, recommendedLists.get(recListIndex).name);
		}
		

		ObjectEditor.setAttribute(Float.class, AttributeNames.COMPONENT_WIDTH, 60);
		// comment the following line to see the full record
		ObjectEditor.setAttribute(AccuracyRecord.class, AttributeNames.DEFAULT_EXPANDED,  false);
		ObjectEditor.setAttribute(AccuracyRecord.class, AttributeNames.ELIDE_STRING_IS_TOSTRING,  true);
		ObjectEditor.edit(accuracyMatrix1).setSize(800, 300);
		//ObjectEditor.edit(costMatrix).setSize(800, 300);
		//ObjectEditor.edit(kAccMatrix).setSize(800, 300);
	}
	static void printCostMatrix(double[][] costMatrix, ArrayList<FriendList> recLists){
		System.out.println("Recommended v Ideal: Cost Matrix");
		System.out.print("   ");
		for (int il = 0; il < idealLists.size(); il++){
			String ilName = idealLists.get(il).name;
			if(ilName.length() >= 3)
				System.out.print("  " +ilName.substring(0, 3) + "  -");
			else if (ilName.length() >= 2)
				System.out.print("   "+ilName + "  -");
			else if (ilName.length() >= 1)
			  System.out.print("   "+ilName + "   -");
		}
		System.out.println();
		for(int rl=0; rl<recLists.size(); rl++){
			System.out.print(recLists.get(rl).name+"  ");
			for(int il=0; il<idealLists.size(); il++){
				DecimalFormat myFormatter = new DecimalFormat("0.000");
			    String formatCost = myFormatter.format(costMatrix[rl][il]);
				if(costMatrix[rl][il] < 1){
				   System.out.print(formatCost+" | ");
				   //System.out.println(bestLists);
				}
				else  System.out.print("      | ");
			}
			System.out.println();
		}
	}

	static void printkAccMatrix(double[][] kAccMatrix, ArrayList<FriendList> recLists){
		System.out.println("Recommended v Ideal: kAcc Matrix");
		System.out.print("   ");
		for (int il = 0; il < idealLists.size(); il++){
			String ilName = idealLists.get(il).name;
			if(ilName.length() >= 3)
				System.out.print("  " +ilName.substring(0, 3) + "  -");
			else if (ilName.length() >= 2)
				System.out.print("   "+ilName + "  -");
			else if (ilName.length() >= 1)
			  System.out.print("   "+ilName + "   -");
		}
		System.out.println();
		for(int rl=0; rl<recLists.size(); rl++){
			System.out.print(recLists.get(rl).name+"  ");
			String bestLists = " ";
			for(int il=0; il<idealLists.size(); il++){
				ArrayList<String> joinLists = new ArrayList<String>(); 
				if(ilToRL.containsKey(idealLists.get(il).name))
					joinLists = ilToRL.get(idealLists.get(il).name);
				DecimalFormat myFormatter = new DecimalFormat("0.000");
			    String formatCost = myFormatter.format(kAccMatrix[rl][il]);
				if(kAccMatrix[rl][il] > .5){
				   System.out.print(formatCost+" | ");
				   bestLists = bestLists.concat(idealLists.get(il).name+" ("+formatCost+")"+", ");
				   joinLists.add(recLists.get(rl).name);
				   ilToRL.put(idealLists.get(il).name, joinLists);
				   //System.out.println(bestLists);
				}
				else  System.out.print("      | ");
			}
			rlToBestListKAcc.put(recLists.get(rl).name, bestLists);
			System.out.println();
		}
	}

	private static void printJoinLists(){
		System.out.println("************** Print Join Lists ******************");
		ArrayList<String> joinLists;
		for(FriendList i: idealLists){
			joinLists = ilToRL.get(i.name);
			if(joinLists.size() >= 2){
				System.out.print("idealList "+i.name+" ");
				for(String listName: joinLists){
					if(!dontJoinList.contains(listName)){
						System.out.print(listName+ ", ");
					}
				}
				System.out.println();
			}
		}
	}
	private static void joinLists(){
		System.out.println("************** Join Lists ******************");
		joinedLists = new ArrayList<FriendList>(recommendedLists);
		ArrayList<String> joinLists;
		ArrayList<FriendList> removeLists = new ArrayList<FriendList>();
		for(FriendList ideal: idealLists){
			joinLists = ilToRL.get(ideal.name);
			if(joinLists.size() >= 2){
				FriendList mergedList = new FriendList(joinLists.get(0)+"j");
				//System.out.print("idealList "+i.name+" ");
//				for(String listName: joinLists){
//					if(!dontJoinList.contains(listName)){
//						for(FriendList j: joinedLists){
//							if(j.name.equalsIgnoreCase(listName)){
//								merge(mergedList, j);
//								removeLists.add(j);
//								break;
//							}
//						}
//					}
//				}
				for(int i = 0; i < joinLists.size(); i++){
					String listName = joinLists.get(i);
					if(!dontJoinList.contains(listName)){
						for(FriendList j: joinedLists){
							if(j.name.equalsIgnoreCase(listName)){
								merge(mergedList, j);
								removeLists.add(j);
								break;
							}
						}
					} else if (joinLists.size() == 2){
						String cancelName = mergedList.name + "cancel";
						mergedList.changeName(cancelName);
						removeLists.add(mergedList);
					}
				}
				joinedLists.add(mergedList);
				//System.out.println();
			}
		}
		for(FriendList r: removeLists){
			joinedLists.remove(r);
		}
	}
	
	private static void merge(FriendList mergedList, FriendList friendList) {
		for(String s: friendList.members){
			if(!mergedList.members.contains(s)){
				mergedList.addMember(s);
			}
		}
		
	}
	static void printSavingsMatrix(double[][] savingsMatrix){
		System.out.println("Recommended v Ideal : Savings Matrix");
		System.out.print("   ");
		for (int il = 0; il < idealLists.size(); il++){
			String ilName = idealLists.get(il).name;
			if(ilName.length() >= 3)
				System.out.print("  " +ilName.substring(0, 3) + "  -");
			else if (ilName.length() >= 2)
				System.out.print("   "+ilName + "  -");
			else if (ilName.length() >= 1)
			  System.out.print("   "+ilName + "   -");
		}
		System.out.println();
		for(int rl=0; rl<recommendedLists.size(); rl++){
			System.out.print(recommendedLists.get(rl).name+"  ");
			String bestLists = " ";
			for(int il=0; il<idealLists.size(); il++){
				DecimalFormat myFormatter = new DecimalFormat("0.000");
			    String formatCost = myFormatter.format(savingsMatrix[rl][il]);
				if(savingsMatrix[rl][il] > 0){
				   System.out.print(formatCost+" | ");
				   //bestLists = bestLists.concat(idealLists.get(il).name+" ("+formatCost+")"+", ");
				   //System.out.println(bestLists);
				}
				else  System.out.print("      | ");
			}
			//rlToBestListKAcc.put(recommendedLists.get(rl).name, bestLists);
			System.out.println();
		}
	}

	
	static void setUpIdeal(){
	   String idealInput = "data/Kelli/FriendshipData/ideal/"+participantId+"_ideal.txt";
	   String inLine = null;
	   FriendList inList = null;
	   boolean newListFlag = false;
	   try{
		  DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
		  while(in.available()!=0){
			 inLine = in.readLine();
			 if(/*newListFlag && */inLine.contains("*")){
				String listName = inLine.substring(1);
				inList = new FriendList(listName);
				newListFlag = false;
			 } else if (inLine.isEmpty()){
				newListFlag = true;
				idealLists.add(inList);
			 } else{
				inList.addMember(inLine);
			 }
		  }
	   } catch(Exception e){
		  System.out.println("!!!setUpLists IDEAL ERROR: "+e.getMessage());
	   }
	}
	
	@SuppressWarnings("deprecation")
	static void setUpRecommendedNetworks(){
	   String idealInput = "data/Kelli/FriendGrouperResults/2010Study/Returned/"+participantId+"_LargeGroups.txt";
	   String inLine = null;
	   FriendList inList = null;
	   boolean newListFlag = false;
	   try{
		 DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
		 while(in.available()!=0){
			inLine = in.readLine();
			if(/*newListFlag && */inLine.contains("Clique")){
			   String listName = inLine.substring(7);
			   inList = new FriendList(listName);
			   newListFlag = false;
			   if(in.available()!=0) inLine = in.readLine();
			} else if (inLine.isEmpty()){
			   newListFlag = true;
			   recommendedLists.add(inList);
			} else{
			   inList.addMember(inLine);
			}
		 }
	   } catch(Exception e){
		   System.out.println("!!!setUpLists RECOMMENDED ERROR: "+e.getMessage());
	   }
	}
	@SuppressWarnings("deprecation")
	static void setUpRecommendedSubcliques(){
	   String idealInput = "data/Kelli/FriendGrouperResults/2010Study/Returned/"+participantId+"_Subcliques.txt";
	   String inLine = null;
	   FriendList inList = null;
	   boolean newListFlag = false;
	   try{
		 DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
		 while(in.available()!=0){
			inLine = in.readLine();
			if(/*newListFlag && */inLine.contains("Clique")){
			   String listName = inLine.substring(8);
			   inList = new FriendList(listName+ "s");
			   newListFlag = false;
			   if(in.available()!=0) inLine = in.readLine();
			} else if (inLine.isEmpty()){
			   newListFlag = true;
			   recommendedLists.add(inList);
			} else{
			   inList.addMember(inLine);
			}
		 }
	   } catch(Exception e){
		   System.out.println("!!!setUpLists RECOMMENDED ERROR: "+e.getMessage());
	   }
	}
		
}
