package kelli.mergeAttempts.incremental;

import java.awt.Color;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JTextArea;

import kelli.FriendGrouper.Int.FindSocialGroups;
import kelli.friends.AFriendList;
import kelli.friends.AJoinableFriendListNameList;
import kelli.friends.ANamedString;
import kelli.friends.FriendListNameList;
import kelli.friends.JoinableFriendListNameList;
import kelli.mergeAttempts.RandomOrderMerge;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
import bus.uigen.attributes.AttributeNames;

public class CliqueDriver {
	private  String participantId = "2720658";
	private  String networkOutfile = "data/Kelli/FriendGrouperResults/"+participantId+"_MergedCliquesIntersection.txt";
	private  String subGroupOutfile = "data/Kelli/FriendGrouperResults/"+participantId+"_MergedCliquesSubGroup.txt";
	float percent = .9F;
	Collection<String> printedCliques = null;
	
	
	MergeKind mergeKind;
	CliqueMerger cliqueMerger;
	static Vector<ATrace> incrementalTrace = new Vector<ATrace>();
	public static Vector<ATrace> incrementalTrace(){
		return incrementalTrace;
	}
//	static Map<MergeKind, CliqueMerger> mergeKindToCliqueMerger = new HashMap();
//	static {
//		mergeKindToCliqueMerger.put(MergeKind.INTERSECTION_MERGE, new AnIntersectionMerger());
//	}
	public CliqueDriver() {
		setMergeKind (MergeKind.INTERSECTION_MERGE);
		
		
		
	}
	public static String compareTraces(){
		String comparison = ATrace.compareTraces(incrementalTrace(), nonIncrementalTrace());
		return comparison;
	}
	public static Vector<ATrace> nonIncrementalTrace(){
		return RandomOrderMerge.getMergeTrace();
	}
	public void doFindSocialGroups(){
		FindSocialGroups.init();
	}
	public MergeKind getMergeKind() {
		return mergeKind;
	}
	public void setMergeKind(MergeKind mergeKind) {
		this.mergeKind = mergeKind;
		cliqueMerger = CliqueMergerRegistry.getCliqueMerger(mergeKind);
	}
	public boolean preShowMergedCliques(){
		//return true;
		return incrementalMerger != null;
	}
	public void showMergedCliques() {
		//return incrementalMerger.printedCliques();
		
		//uiFrame frame = ObjectEditor.tableEdit(incrementalMerger.printedCliques());
		//uiFrame frame = ObjectEditor.tableEdit(incrementalMerger.getNamedCliques());
		uiFrame frame = ObjectEditor.edit(incrementalMerger.getNamedStringCliques());
		frame.setSize(600, 600);
	}
	public void showFriendListNames() {
		//return incrementalMerger.printedCliques();
		
		//uiFrame frame = ObjectEditor.tableEdit(incrementalMerger.printedCliques());
		//uiFrame frame = ObjectEditor.tableEdit(incrementalMerger.getNamedCliques());
		uiFrame frame = ObjectEditor.edit(incrementalMerger.getFriendsListManager().getFriendListNameList());
		frame.setSize(300, 650);
	}
	public void showJoinableFriendLists() {
		FriendListNameList source = incrementalMerger.getFriendsListManager().getFriendListNameList();
		JoinableFriendListNameList joinableList = new AJoinableFriendListNameList(source);
		uiFrame frame = ObjectEditor.edit(joinableList);
		frame.setSize(300, 650);
		
		
	}
//	public boolean preGetLargerClique() {
//		return preDoNextMergePass();
//	}
	@util.annotations.ComponentWidth(400)
	public String getLargerClique() {
		if (incrementalMerger == null)
			return "";
		return incrementalMerger.getLargerCliqueToString();	
			
	}
//	public boolean preGetSmallerClique() {
//		return preDoNextMergePass();
//	}
	@util.annotations.ComponentWidth(400)
	public String getSmallerClique() {
		if (incrementalMerger == null)
			return "";
		return incrementalMerger.getSmallerCliqueToString();	
			
	}
	
	public int getComparisonNumber() {
		if (incrementalMerger == null)
			return -1;
		return incrementalMerger.getComparisonNumber();
	}
	public int getMergeNumber() {
		if (incrementalMerger == null)
			return -1;
		return incrementalMerger.getMergeNumber();
	}
	public boolean preDoNextEffectiveMerge() {
		   return preDoNextMerge();
	 }
	 public int getEffectiveMergeNumber() {
		 if (incrementalMerger == null)
				return -1;
		   return incrementalMerger.getEffectiveMergeNumber();
	   }
	public int getPassNumber() {
		if (incrementalMerger == null)
			return -1;
		return incrementalMerger.getPassNumber();
	}
	
	
	private IncrementalMerger incrementalMerger;
	
	
	public float getPercent(){
		return percent;
	}
	public void setPercent(float percent){
		this.percent = percent;
	}
	
	@util.annotations.ComponentWidth(300)
	public String getInfile() {
		return participantId;
	}
	public void setInfile(String infile) {
		this.participantId = infile;
	}
	@util.annotations.ComponentWidth(300)
	public String getOutfile() {
		return networkOutfile;
	}
	public void setOutfile(String outfile) {
		this.networkOutfile = outfile;
	}
	public boolean preDoNextMergePass(){
		return incrementalMerger != null && incrementalMerger.mergeHappened();
	}
	boolean hasMoreComparisons() {
		return incrementalMerger.hasMoreComparisons();
	}
	public boolean preDoNextComparison() {
		return preDoNextMergePass() && hasMoreComparisons();
	}
	public void doNextComparison() {
		if (!hasMoreComparisons())
			return;
		incrementalMerger.doNextComparison();
		
	}
	
	public boolean preDoNextMerge() {
		return preDoNextComparison();
	}
	public void doNextMerge() {
		if (!hasMoreComparisons())
			return;
		incrementalMerger.doNextMerge();
		
	}
	public void doNextEffectiveMerge() {
		if (!hasMoreComparisons())
			return;
		incrementalMerger.doNextEffectiveMerge();
	}
	
//	public void doRemainingMerges(){
//		if (!preDoNextMergePass())
//			findAllCliques();
//		while (incrementalMerger.hasMoreComparisons()){
//			doNextComparison();
//		}
//		printAtEnd();
//	}
	
	/*public boolean preDoAllMerges(){
		return incrementalMerger != null;
	}*/
	public void doNextMergePass(){
		incrementalMerger.doNextMergePass();
	}
	@util.annotations.SeparateThread(true)
	public synchronized void doRemainingPasses(){
		if (!preDoNextMergePass())
			loadInitialCliques();
		while (incrementalMerger.mergeHappened()){
			doNextMergePass();
		}
		printAtEnd();
	}
	public void loadInitialCliques(){
		incrementalMerger = new IncrementalMerger(participantId, networkOutfile, percent, cliqueMerger);
	}
	public void printAtEnd(){
		incrementalMerger.printCliquesToFile();
	}
	public static void main(String[] args){
		//ObjectEditor.setPreferredWidget(String.class, VirtualLabel.class);
		//ObjectEditor.edit("hello world");
		
		//ObjectEditor.setAttribute(AFriendList.class, AttributeNames.VECTOR_NAVIGATOR, true);
		//ObjectEditor.setAttribute(AFriendList.class, AttributeNames.VECTOR_NAVIGATOR_SIZE, 9);
		//ObjectEditor.setDefaultAttribute(AttributeNames.HORIZONTAL_METHOD_GAP, 4);
		ObjectEditor.setDefaultAttribute(AttributeNames.HORIZONTAL_BOUND_GAP, 4);

		ObjectEditor.setPropertyAttribute(AFriendList.class, "*", AttributeNames.LABELLED, false);

		ObjectEditor.setAttribute(AFriendList.class, AttributeNames.SHOW_BUTTON, new Boolean(true) );
		ObjectEditor.setAttribute(AFriendList.class, AttributeNames.SHOW_UNBOUND_BUTTONS, new Boolean(true) );
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.VECTOR_NAVIGATOR, true);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.VECTOR_NAVIGATOR_SIZE, 12);
		//ObjectEditor.setPropertyAttribute(AFriendList.class, "Friends", AttributeNames.COMPONENT_COLOR, Color.GREEN);
		ObjectEditor.setPropertyAttribute(AFriendList.class, "Selected", AttributeNames.COMPONENT_BACKGROUND, Color.blue);
		ObjectEditor.setMethodAttribute(AFriendList.class, "SaveList", AttributeNames.COMPONENT_BACKGROUND, Color.blue);
		ObjectEditor.setMethodAttribute(AFriendList.class, "All", AttributeNames.COMPONENT_BACKGROUND, Color.blue);
		ObjectEditor.setMethodAttribute(AFriendList.class, "EditName", AttributeNames.COMPONENT_BACKGROUND, Color.blue);


		ObjectEditor.setAttribute(ANamedString.class, AttributeNames.LABEL_POSITION, AttributeNames.LABEL_IN_BORDER);
		ObjectEditor.setPreferredWidget(ANamedString.class, "FriendNames",  JTextArea.class);
		ObjectEditor.setPropertyAttribute(ANamedString.class, "FriendNames", AttributeNames.COMPONENT_HEIGHT, 80);
		//ObjectEditor.setPropertyAttribute(ANamedClique.class, "FriendNames", AttributeNames.SCROLLED, true);

		//ObjectEditor.setPropertyAttribute(ANamedClique.class, "FriendNames", AttributeNames.COMPONENT_HEIGHT, 70);
		ObjectEditor.setPreferredWidget(CliqueDriver.class, "LargerClique", JTextArea.class);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.COMPONENT_WIDTH, 400);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.COMPONENT_HEIGHT, 20);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.SCROLLED, true);

		ObjectEditor.setPreferredWidget(CliqueDriver.class, "SmallerClique", JTextArea.class);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "SmallerClique", AttributeNames.COMPONENT_WIDTH, 400);

		ObjectEditor.setPropertyAttribute(CliqueDriver.class, "SmallerClique", AttributeNames.COMPONENT_HEIGHT, 20);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "SmallerClique", AttributeNames.SCROLLED, true);

		CliqueDriver cliqueDriver = new CliqueDriver();
		uiFrame cliqueFrame = ObjectEditor.edit(cliqueDriver);
		cliqueFrame.setSize(1000, 500);
		cliqueFrame.showToolBar();
		cliqueFrame.setAutoRefreshAll(true);
		//cliqueDriver.showMergedCliques();
		
	}
}
