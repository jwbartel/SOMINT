package bus.tools;

import java.io.File;

import recommendation.groups.seedless.hybrid.IOFunctions;

public class FileFinder {
	
	static final String FRIENDS_NAME_ID_LIST_FOLDER = "data/Kelli/FriendshipData/2010Study/";
	static final String IDEAL_GROUPS_FOLDER = "data/Jacob/Ideal/";

	static final String HYBRID_PREDICTION_RESULTS_FOLDER = "data/Jacob/Hybrid/"; //"data/Kelli/CompareResults/";
	static final String SNAP_HYBRID_SUBSETS_FOLDER = "data/Stanford_snap/facebook/substeps/";
	
	static final String EVOLUTION_FILE_PREFIX_DEFAULT = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/data/";
	

	static final String SNAP_EVOLUTION_FILE_PREFIX_DEFAULT = "data/Stanford_snap/facebook/scaled_evolution/data/";
	static final String SNAP_IDEAL_GROUPS_FOLDER = "data/Stanford_snap/facebook/";
	static final String SNAP_EVOLUTION_NEW_MEMBERSHIP_FOLDER = "data/Stanford_snap/facebook/New Membership/";
	static final String SNAP_EVOLUTION_NEW_MEMBERS_FOLDER = "data/Stanford_snap/facebook/new members/";
	static final String SNAP_EVOLUTION_OLD_GROUPS_FOLDER = "data/Stanford_snap/facebook/old groups/";
	static final String SNAP_EVOLUTION_TUPLE_FOLDER = "data/Stanford_snap/facebook/scaled_evolution/prediction tuples/";

	static final String EVOLUTION_NEW_MEMBERSHIP_FOLDER = "data/Jacob/Test Data/New Membership/";
	static final String EVOLUTION_NEW_MEMBERS_FOLDER = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/new members/";
	static final String EVOLUTION_OLD_GROUPS_FOLDER = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/old groups/";
	static final String EVOLUTION_OLD_TO_IDEAL_FOLDER = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/old to ideal/";
	static final String EVOLUTION_TUPLE_FOLDER = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/prediction tuples/";
	static final String EVOLUTION_MULTIPLE_MATCHES_FOLDER = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/multiple matches/";
	static String evolutionFilePrefix = "";
	static String snapEvolutionFilePrefix = "";
	static String evolutionFileSuffix;
	
	static {
		evolutionFilePrefix = EVOLUTION_FILE_PREFIX_DEFAULT;
		snapEvolutionFilePrefix = SNAP_EVOLUTION_FILE_PREFIX_DEFAULT;
	}

	public static void selectType(String prefix, String suffix) {
		evolutionFileSuffix = " "+suffix;
		evolutionFilePrefix += prefix+"_";
		snapEvolutionFilePrefix += prefix+"_";
	}
	
	public static void selectHybrid(){
		selectType("hybrid groups", "hybrid");
	}
	
	public static void selectExpectedScaling(){
		selectType("expected scaling", "expected scaling");
	}
	
	public static void selectJaccardCoefficient(){
		selectType("jaccardian", "jaccardian");
	}
	
	//Select suffixes
	public static void selectSinglePredictionSingleIdealChoosing(){
		selectType("singlePrediction singleIdeal", "singlePrediction singleIdeal");
	}
	
	public static void selectSinglePredictionMultiIdealChoosing(){
		selectType("singlePrediction multiIdeal", "singlePrediction multiIdeal");
	}
	
	public static void selectMultiPredictionSingleIdealChoosing(){
		selectType("multiPrediction singleIdeal", "multiPrediction singleIdeal");
	}
	
	public static void selectMultiPredictionMultiIdealChoosing(){
		selectType("multiPrediction multiIdeal", "multiPrediction multiIdeal");
	}
	
	/*
	 * Extracted data locations
	 */
	
	public static String getFriendNameAndIdFileName(int participant){
		return FRIENDS_NAME_ID_LIST_FOLDER + participant + "_People.txt";
	}
	
	public static String getSnapIdealFile(int participant) {
		return "data/Stanford_snap/facebook/"+participant+".circles";
	}
	
	public static String getIdealFile(int participant){
		return IDEAL_GROUPS_FOLDER+participant+"_ideal.txt";
	}
	
	/*
	 * Hybrid full group prediction locations
	 */
	
	public static <V> String getHybridSubcliquesFileName(IOFunctions<V> ioHelp, int participant){
		String folder = (ioHelp != null)? ioHelp.getSubStepsFolder() : null;
		if (folder == null) {
			folder = HYBRID_PREDICTION_RESULTS_FOLDER;
		}
		return folder + participant + "_Subcliques.txt";
	}
	
	public static <V> String getSnapHybridSubcliquesFileName(IOFunctions<V> ioHelp, int participant){
		String folder = (ioHelp != null)? ioHelp.getSubStepsFolder() : null;
		if (folder == null) {
			folder = HYBRID_PREDICTION_RESULTS_FOLDER;
		}
		return folder + "0_Subcliques.txt";
	}
	
	public static <V> String getHybridNetworksFileName(IOFunctions<V> ioHelp, int participant){
		String folder = (ioHelp != null)? ioHelp.getSubStepsFolder() : null;
		if (folder == null) {
			folder = HYBRID_PREDICTION_RESULTS_FOLDER;
		}
		return folder + participant+ "_LargeGroups.txt";
	}
	
	public static <V> String getSnapHybridNetworksFileName(IOFunctions<V> ioHelp, int participant){
		String folder = (ioHelp != null)? ioHelp.getSubStepsFolder() : null;
		if (folder == null) {
			folder = HYBRID_PREDICTION_RESULTS_FOLDER;
		}
		return folder + "0_LargeGroups.txt";
	}
	
	public static <V> String getHybridMaximalCliquesFileName(IOFunctions<V> ioHelp, int participant){
		String folder = (ioHelp != null)? ioHelp.getSubStepsFolder() : null;
		if (folder == null) {
			folder = HYBRID_PREDICTION_RESULTS_FOLDER;
		}
		return folder + participant+ "_NetworkMaximalCliques.txt";
	}
	
	public static <V> File getHybridMaximalCliquesFile(IOFunctions<V> ioHelp, int participant){
		return new File(getHybridMaximalCliquesFileName(ioHelp, participant));
	}
	
	/*
	 * Group evolution prediction locations
	 */
	
	public static String getSnapHybridSubsetsFolder(int participant) {
		return SNAP_HYBRID_SUBSETS_FOLDER + participant + "/";
	}
	
	public static String getEvolutionEffortFileName(){
		return evolutionFilePrefix + "efforts.csv";
	}
	
	public static File getEvolutionEffortFile(){
		return new File(getEvolutionEffortFileName());
	}
	
	public static String getSnapEvolutionEffortFileName(){
		return snapEvolutionFilePrefix + "efforts.csv";
	}
	
	public static File getSnapEvolutionEffortFile(){
		return new File(getSnapEvolutionEffortFileName());
	}
	
	public static String getEvolutionNewGroupsFileName(){
		return evolutionFilePrefix + "new groups.csv";
	}
	
	public static String getSnapEvolutionNewGroupsFileName(){
		return snapEvolutionFilePrefix + "new groups.csv";
	}
	
	public static File getEvolutionNewGroupsFile(){
		return new File(getEvolutionNewGroupsFileName());
	}
	
	public static File getSnapEvolutionNewGroupsFile(){
		return new File(getSnapEvolutionNewGroupsFileName());
	}
	
	public static File getSnapNewMembershipFile(int participant, double percentNew, int testNum){
		return new File(SNAP_EVOLUTION_NEW_MEMBERSHIP_FOLDER+percentNew+"/participant"+participant+"_test"+testNum+".txt");
	}
	
	public static File getNewMembershipFile(int participant, double percentNew, int testNum){
		return new File(EVOLUTION_NEW_MEMBERSHIP_FOLDER+percentNew+"/participant"+participant+"_test"+testNum+evolutionFileSuffix+".txt");
	}
	
	public static File getNewMembershipFile(int participant, double percentNew){
		return getNewMembershipFile(participant, percentNew, 0);
	}
	
	public static String getNewMembersFileName(int participant, double percentNew){
		return EVOLUTION_NEW_MEMBERS_FOLDER+"participant "+participant+"/percent_new"+percentNew+evolutionFileSuffix+".txt";
	}
	
	public static String getSnapNewMembersFileName(int participant, double percentNew){
		return SNAP_EVOLUTION_NEW_MEMBERS_FOLDER+"participant "+participant+"/percent_new"+percentNew+evolutionFileSuffix+".txt";
	}
	
	public static String getOldGroupFileName(int participant, int newIndividuals){
		return EVOLUTION_OLD_GROUPS_FOLDER+"participant "+participant+"/new_individuals_"+newIndividuals+evolutionFileSuffix+".txt";
	}
	
	public static String getSnapOldGroupFileName(int participant, int newIndividuals){
		return SNAP_EVOLUTION_OLD_GROUPS_FOLDER+"participant "+participant+"/new_individuals_"+newIndividuals+evolutionFileSuffix+".txt";
	}
	
	public static File getOldGroupFile(int participant, int newIndividuals){
		return new File(getOldGroupFileName(participant, newIndividuals));
	}
	
	public static String getOldToIdealFileName(int participant, int newIndividuals){
		return EVOLUTION_OLD_TO_IDEAL_FOLDER+"/participant "+participant+"/new_individuals_"+newIndividuals+evolutionFileSuffix+".txt";
	}
	
	public static File getOldToIdealFile(int participant, int newIndividuals){
		return new File(getOldToIdealFileName(participant, newIndividuals));
	}
	
	public static File getSnapOldGroupFile(int participant, double percentNew, int testNum){
		return new File(SNAP_EVOLUTION_OLD_GROUPS_FOLDER+percentNew+"/participant"+participant+"_test"+testNum);
	}
	
	public static File getOldGroupFile(int participant, double percentNew, int testNum){
		return new File(EVOLUTION_OLD_GROUPS_FOLDER+percentNew+"/participant"+participant+"_test"+testNum);
	}
	
	public static String getSnapTupleFileName() {
		return SNAP_EVOLUTION_TUPLE_FOLDER + evolutionFileSuffix + ".csv";
	}
	
	public static String getTupleFileName(){
		return EVOLUTION_TUPLE_FOLDER + evolutionFileSuffix + ".csv";
	}
	
	public static File getSnapTupleFile(){
		return new File(getSnapTupleFileName());
	}
	
	public static File getTupleFile(){
		return new File(getTupleFileName());
	}
	
	public static String getMultipleMatchesFileName(){
		return EVOLUTION_MULTIPLE_MATCHES_FOLDER + evolutionFileSuffix + ".csv";
	}
	
	public static File getMultipleMatchesFile(){
		return new File(getMultipleMatchesFileName());
	}

}
