package kelli.mergeAttempts.SimplifiedCliqueDriver;

import java.awt.Color;
import java.util.Collection;

import javax.swing.JTextArea;

import kelli.friends.AFriendList;
import kelli.friends.ANamedString;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
import bus.uigen.attributes.AttributeNames;

public class SimplifiedCliqueDriver {
	private  String participantId = "2720658";
	private  String networkOutfile = "data/Kelli/FriendGrouperResults/"+participantId+"_MergedCliquesIntersection.txt";
	//private  String subGroupOutfile = "data/Kelli/FriendGrouperResults/"+participantId+"_MergedCliquesSubGroup.txt";
	float percent = .9F;
	Collection<String> printedCliques = null;
	private String status = "";
	private String networksFound = "";
	private String subgroupsFound = "";
	
	public SimplifiedCliqueDriver() {
		//setMergeKind (MergeKind.INTERSECTION_MERGE);
	
	}
	@util.annotations.ComponentWidth(400)
//	public String getLargerClique() {
//		//if (incrementalMerger == null)
//			return "";
//	//	return incrementalMerger.getLargerCliqueToString();		
//	}
	//@util.annotations.ComponentWidth(400)
//	public String getSmallerClique() {
//	//	if (incrementalMerger == null)
//			return "";
////		return incrementalMerger.getSmallerCliqueToString();	
//			
//	}
	
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
	
	public void doHybridMerge(){
		MergeStatsRecord statsRecord = HybridMerge.init();
		setNetworksFound(statsRecord.getNumNetworks());
		setSubgroupsFound(statsRecord.getNumSubgroups());
		setOutfile(statsRecord.getOutfile());
		setStatus("merge completed");
	}
	
	public void doNetworkFind(){
		MergeStatsRecord statsRecord = NetworkFind.init();
		setNetworksFound(statsRecord.getNumNetworks());
		setSubgroupsFound(statsRecord.getNumSubgroups());
		setOutfile(statsRecord.getOutfile());
		setStatus("merge completed");
	}
	
	public void doSubgroupFind(){
		MergeStatsRecord statsRecord = SubgroupFind.init();
		setNetworksFound(statsRecord.getNumNetworks());
		setSubgroupsFound(statsRecord.getNumSubgroups());
		setOutfile(statsRecord.getOutfile());
		setStatus("merge completed");
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String newStatus){
		status = newStatus;
	}
	public String getNetworksFound(){
		return networksFound;
	}
	public void setNetworksFound(String numNetworksFound){
		networksFound  = numNetworksFound;
	}
	public String getSubgroupsFound(){
		return subgroupsFound;
	}
	public void setSubgroupsFound(String numSubgroupsFound){
		subgroupsFound = numSubgroupsFound;
	}
	
	public static void main(String[] args){
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
		ObjectEditor.setPreferredWidget(SimplifiedCliqueDriver.class, "LargerClique", JTextArea.class);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.COMPONENT_WIDTH, 400);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.COMPONENT_HEIGHT, 20);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "LargerClique", AttributeNames.SCROLLED, true);

		ObjectEditor.setPreferredWidget(SimplifiedCliqueDriver.class, "SmallerClique", JTextArea.class);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "SmallerClique", AttributeNames.COMPONENT_WIDTH, 400);

		ObjectEditor.setPropertyAttribute(SimplifiedCliqueDriver.class, "SmallerClique", AttributeNames.COMPONENT_HEIGHT, 20);
		//ObjectEditor.setPropertyAttribute(CliqueDriver.class, "SmallerClique", AttributeNames.SCROLLED, true);

		SimplifiedCliqueDriver cliqueDriver = new SimplifiedCliqueDriver();
		uiFrame cliqueFrame = ObjectEditor.edit(cliqueDriver);
		cliqueFrame.setSize(1000, 500);
		cliqueFrame.showToolBar();
		cliqueFrame.setAutoRefreshAll(true);
		//cliqueDriver.showMergedCliques();
		
	}
}
