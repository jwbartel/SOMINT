package recommendation.groups.comparison;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;




public class AccuracyChecker {
	

	String participantId;
	String outFile;
	
	ArrayList<FriendList> idealLists = new ArrayList<FriendList>();
	HashMap<String, ArrayList<String>> ilToRL = new HashMap<String, ArrayList<String>>();
	HashMap<String, String> rlToBestListKAcc = new HashMap<String, String>();
	ArrayList<String> dontJoinList = new ArrayList<String>();
	
	public AccuracyChecker(String id, String accuracyCompareOutFile) {
		participantId = id;
		outFile = accuracyCompareOutFile;
	}
	
	public void setUpIdeal(){
		
		String idealInput = "data/Jacob/Ideal/"+participantId+"_ideal.txt";
		String inLine = null;
		FriendList inList = null;
		try{
			DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
			while(in.available()!=0){
				inLine = in.readLine();
				if(/*newListFlag && */inLine.contains("*")){
					String listName = inLine.substring(1);
					inList = new FriendList(listName);
				} else if (inLine.isEmpty()){
					idealLists.add(inList);
				} else{
					inList.addMember(inLine);
				}
			}
		} catch(Exception e){
			System.out.println("!!!setUpLists IDEAL ERROR: "+e.getMessage());
		}
	}
	
	public void compareLists(ArrayList<FriendList> recLists, double[][] costMatrix, double[][] kAccMatrix) {
		System.out.println("comparisons done here");
		for(int rl = 0; rl < recLists.size(); rl++){
			FriendList recList = recLists.get(rl);
			for(int il = 0; il < idealLists.size(); il++){
				//record = new AccuracyRecord();
				FriendList idealList = idealLists.get(il);
				double costForIdeal = findCostToCreateIdeal(recList,idealList); 
				double costKAcc = findKAccuracy(recList, idealList);
				costMatrix[rl][il] = costForIdeal;
				if(costMatrix[rl][il] == 0){
					String R = recLists.get(rl).getName();
					dontJoinList.add(R);
				}
				kAccMatrix[rl][il] = costKAcc;
			}
		}
	}
	
	double findKAccuracy(FriendList rl, FriendList il){
		double numSame = 0;
		double retVal;
		for (String name: rl.getMembers()){
			if (il.getMembers().contains(name)) numSame++;
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
	
	static double findCostToCreateIdeal(FriendList rl, FriendList il){
		double retVal;
		double in = findInsert(rl,il);
		double del = findDelete(rl,il);
		double cost = in + del;//.5*del;
		retVal = Math.abs((cost)/il.getSize());
		NumberFormat f = NumberFormat.getInstance();
		return retVal;
	}
	
	static int findInsert(FriendList rl, FriendList il){  //IL - RL
		int insert = 0;
		for(String s: il.getMembers()){
			if(!rl.getMembers().contains(s)){
				insert++;
			}
		}
		return insert;
	}
	
	static int findDelete(FriendList rl, FriendList il){  //RL - IL
		int delete = 0;
		for(String s: rl.getMembers()){
			if(!il.getMembers().contains(s)){
				delete++;
			}
		}
		return delete;
	}

	public void printStatus(String message) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outFile, true));
			pw.println(message);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	ArrayList<FriendList> setUpHybrid(){
		ArrayList<FriendList> recommendedLists;
		ArrayList<FriendList> returnList = new ArrayList<FriendList>();
		recommendedLists = setUpRecommendedNetworks();
		returnList = setUpRecommendedSubcliques(recommendedLists);
		return returnList;
	}
	

	@SuppressWarnings("deprecation")
	ArrayList<FriendList> setUpRecommendedNetworks(){
	   ArrayList<FriendList> recommendedNetworks = new ArrayList<FriendList>();
	   String idealInput = "data/Jacob/Hybrid/"+participantId+"_LargeGroups.txt";
	   String inLine = null;
	   FriendList inList = null;
	   try{
		 DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
		 while(in.available()!=0){
			inLine = in.readLine();
			if(inLine.contains("Clique")){
			   String listName = inLine.substring(7);
			   inList = new FriendList(listName);
			   if(in.available()!=0) inLine = in.readLine();
			} else if (inLine.isEmpty()){
				recommendedNetworks.add(inList);
			} else{
			   inList.addMember(inLine);
			}
		 }
	   } catch(Exception e){
		   System.out.println("!!!setUpLists RECOMMENDED ERROR: "+e.getMessage());
	   }
	   return recommendedNetworks;
	}
	
	@SuppressWarnings("deprecation")
	ArrayList<FriendList> setUpRecommendedSubcliques(ArrayList<FriendList> recommendedLists){
		ArrayList<FriendList> retList = new ArrayList<FriendList>(recommendedLists);
		String idealInput = "data/Jacob/Hybrid/"+participantId+"_Subcliques.txt";
		String inLine = null;
		FriendList inList = null;
		try{
			DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
			while(in.available()!=0){
				inLine = in.readLine();
				if(inLine.contains("Clique")){
					String listName = inLine.substring(8);
					inList = new FriendList(listName+ "s");
					if(in.available()!=0) inLine = in.readLine();
				} else if (inLine.isEmpty()){
					retList.add(inList);
				} else{
				   inList.addMember(inLine);
				}
			}
		} catch(Exception e){
			System.out.println("!!!setUpLists RECOMMENDED SUBCLIQUES ERROR: "+e.getMessage());
		}
		return retList;
	}
	
	void printkAccMatrix(double[][] kAccMatrix, ArrayList<FriendList> recLists){
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outFile, true));
			pw.println("Recommended v Ideal: kAcc Matrix");
			pw.print(",");
			//pw.print("   ");
			for (int il = 0; il < idealLists.size(); il++){
				String ilName = idealLists.get(il).getName();
				if(ilName.length() >= 3)
					pw.print(ilName.substring(0, 3) + ",");
					//pw.print("  " +ilName.substring(0, 3) + "  -");
				else if (ilName.length() >= 2)
					pw.print(ilName + ",");
					//pw.print("   "+ilName + "  -");
				else if (ilName.length() >= 1)
					pw.print(ilName + ",");
					//pw.print("   "+ilName + "   -");
			}
			pw.println();
			for(int rl=0; rl<recLists.size(); rl++){
				pw.print(recLists.get(rl).getName()+",");
				//pw.print(recLists.get(rl).getName()+"  ");
				String bestLists = " ";
				for(int il=0; il<idealLists.size(); il++){
					ArrayList<String> joinLists = new ArrayList<String>(); 
					if(ilToRL.containsKey(idealLists.get(il).getName()))
						joinLists = ilToRL.get(idealLists.get(il).getName());
					DecimalFormat myFormatter = new DecimalFormat("0.000");
	        		String formatCost = myFormatter.format(kAccMatrix[rl][il]);
	        		if(kAccMatrix[rl][il] > .5){
	        			pw.print(formatCost+",");
	        			//pw.print(formatCost+" | ");
	        			bestLists = bestLists.concat(idealLists.get(il).getName()+" ("+formatCost+")"+", ");
	        			joinLists.add(recLists.get(rl).getName());
	        			ilToRL.put(idealLists.get(il).getName(), joinLists);
	        			//System.out.println(bestLists);
	        		}
	        		else  pw.print(" ,");//pw.print("      | ");
				}
				rlToBestListKAcc.put(recLists.get(rl).getName(), bestLists);
				pw.println();
			}
			pw.close();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	void printCostMatrix(double[][] costMatrix, ArrayList<FriendList> recLists){
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outFile, true));
			pw.println("Recommended v Ideal: Cost Matrix");
			pw.print(",");
			//pw.print("   ");
			for (int il = 0; il < idealLists.size(); il++){
				String ilName = idealLists.get(il).getName();
				if(ilName.length() >= 3)
					pw.print(ilName.substring(0, 3) + ",");
					//pw.print("  " +ilName.substring(0, 3) + "  -");
				else if (ilName.length() >= 2)
					pw.print(ilName + ",");
					//pw.print("   "+ilName + "  -");
				else if (ilName.length() >= 1)
					pw.print(ilName + ",");
					//pw.print("   "+ilName + "   -");
			}
			pw.println();
			for(int rl=0; rl<recLists.size(); rl++){
				pw.print(recLists.get(rl).getName()+",");
				//pw.print(recLists.get(rl).getName()+"  ");
				for(int il=0; il<idealLists.size(); il++){
					DecimalFormat myFormatter = new DecimalFormat("0.000");
					String formatCost = myFormatter.format(costMatrix[rl][il]);
					if(costMatrix[rl][il] < 1){
						pw.print(formatCost+",");
						//pw.print(formatCost+" | ");
					}
					else  pw.print(" ,");//pw.print("      | ");
				}
				pw.println();
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	ArrayList<FriendList> joinLists(ArrayList<FriendList> recommendedLists){
		System.out.println("************** Join Lists ******************");
		ArrayList<FriendList> joinedLists = new ArrayList<FriendList>(recommendedLists);
		ArrayList<String> joinLists;
		ArrayList<FriendList> removeLists = new ArrayList<FriendList>();
		for(FriendList ideal: idealLists){
			joinLists = ilToRL.get(ideal.getName());
			if(joinLists != null && joinLists.size() >= 2){
				FriendList mergedList = new FriendList(joinLists.get(0)+"j");
				for(int i = 0; i < joinLists.size(); i++){
					String listName = joinLists.get(i);
					if(!dontJoinList.contains(listName)){
						for(FriendList j: joinedLists){
							if(j.getName().equalsIgnoreCase(listName)){
								merge(mergedList, j);
								removeLists.add(j);
								break;
							}
						}
					} else if (joinLists.size() == 2){
						String cancelName = mergedList.getName() + "cancel";
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
		return joinedLists;
	}
	
	private static void merge(FriendList mergedList, FriendList friendList) {
		for(String s: friendList.getMembers()){
			if(!mergedList.getMembers().contains(s)){
				mergedList.addMember(s);
			}
		}	
	}
	
	ArrayList<FriendList> setUpLCMA(double threshold){
		ArrayList<FriendList> LCMALists = new ArrayList<FriendList>();
		String idealInput = "data/Jacob/LCMA/"+participantId+"_GroupsWithThreshold_"+threshold+".txt";
		String inLine = null;
		FriendList inList = null;
		try{
			DataInputStream in = new DataInputStream(new FileInputStream(idealInput));
			while(in.available()!=0){
				inLine = in.readLine();
				if(inLine.contains("Clique")){
					String listName = inLine.substring(8);
					inList = new FriendList(listName);
					if(in.available()!=0) inLine = in.readLine();
				} else if (inLine.isEmpty()){
					LCMALists.add(inList);
				} else{
					inList.addMember(inLine);
				}
			}
		} catch(Exception e){
			System.out.println("!!!setUpLists LCMA ERROR: "+e.getMessage());
		}
		return LCMALists;
	}
	
	void printStatus(String message, boolean append){
		try {
			File file = new File(outFile);
			if(!file.exists()){
				file.createNewFile();
			}
			
			PrintWriter pw = new PrintWriter(new FileWriter(outFile, append));
			pw.println(message);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	static void hybridCompare(AccuracyChecker ac){
		ac.printStatus("~~~~~~~~~~~~~~~~~~ HYBRID ~~~~~~~~~~~~~~~~~~");
		int idealSize = ac.idealLists.size();
		ArrayList<FriendList> hybridLists = ac.setUpHybrid();
		double costMatrix[][] = new double[hybridLists.size()][idealSize];
		double kAccMatrix[][] = new double[hybridLists.size()][idealSize];
		ac.compareLists(hybridLists, costMatrix, kAccMatrix);
		ac.printkAccMatrix(kAccMatrix, hybridLists);
		ac.printCostMatrix(costMatrix, hybridLists);
		ArrayList<FriendList> joinedLists = ac.joinLists(hybridLists);
		ac.printStatus("~~~~~~~~~~~~~~~~ AFTER JOIN ~~~~~~~~~~~~~~~~~");
		double cost2[][] = new double[joinedLists.size()][idealSize];
		double kAcc2[][] = new double[joinedLists.size()][idealSize];
		ac.compareLists(joinedLists, cost2, kAcc2);
		ac.printkAccMatrix(kAcc2, joinedLists);
		ac.printCostMatrix(cost2, joinedLists);
	}
	
	private static void LCMACompare(AccuracyChecker ac, double threshold) {
		   ac.printStatus("~~~~~~~~~~~~~~~~~~ LCMA ~~~~~~~~~~~~~~~~~~");
		   int idealSize = ac.idealLists.size();
		   ArrayList<FriendList> LCMALists = ac.setUpLCMA(threshold);
		   double costMatrix[][] = new double[LCMALists.size()][idealSize];
		   double kAccMatrix[][] = new double[LCMALists.size()][idealSize];
		   ac.compareLists(LCMALists, costMatrix, kAccMatrix);
	       ac.printkAccMatrix(kAccMatrix, LCMALists);
		   ac.printCostMatrix(costMatrix, LCMALists);
	}
	
	
	
	private static void accuracyCheck(){
		
		int[] participants = {10,12, 13,16, 17, 19, 21, 22, 23, 24, 25, 8};
		

		double[] thresholds = {1.0, 0.95, 0.9, 0.85, 0.8, 0.75, 0.7, 0.6, 0.65, 0.6, 0.55, 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0.0};
		
		/*for(int participantsPos = 0; participantsPos < participants.length; participantsPos++){
			String participantID = ""+participants[participantsPos];
			
			System.out.println("****PARTICIPANT "+participantID+"****");
			
			String accuracyCompareOutFile = "data/Jacob/Stats/seedless/CompareResults/Hybrid/"+participantID+"_Accuracy.csv";
			AccuracyChecker ac = new AccuracyChecker(participantID, accuracyCompareOutFile);
			ac.printStatus("Accuracy Check for Participant "+participantID, false);
			ac.setUpIdeal();
			hybridCompare(ac);
			//LCMACompare(ac);
		
		}*/
		
		for(int participantsPos = 0; participantsPos < participants.length; participantsPos++){
			String participantID = ""+participants[participantsPos];
			

			System.out.println("****PARTICIPANT "+participantID+"****");
			for(int thresholdPos=0; thresholdPos < thresholds.length; thresholdPos++){
				double threshold = thresholds[thresholdPos];

				
				System.out.println("****threshold "+threshold+"****");
				
				String accuracyCompareOutFile = "data/Jacob/Stats/seedless/CompareResults/LCMA/"+threshold+"/"+participantID+"_Accuracy.csv";
				File parent = (new File(accuracyCompareOutFile)).getParentFile();
				if(!parent.exists()){
					parent.mkdirs();
				}
				AccuracyChecker ac = new AccuracyChecker(participantID, accuracyCompareOutFile);
				ac.printStatus("Accuracy Check for Participant "+participantID, false);
				ac.setUpIdeal();
				//hybridCompare(ac);
				LCMACompare(ac, threshold);
				
			}
		
		}
	}
	
	public static void main(String[] args){
		accuracyCheck();
	}
	
	
	
}
