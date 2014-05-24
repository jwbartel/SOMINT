package recommendation.recipients.old.predictionchecking;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;

import data.structures.ComparableSet;
import data.structures.groups.GoogleGroupTracker;
import recommendation.recipients.old.contentbased.AdaptedAgedContentAccount;
import bus.accounts.Account;


public abstract class PredictionMaker extends AdaptedAgedContentAccount{
	


	public static final String groups_list = Account.ADAPTED_GOOGLE_GROUPS_LIST;
	public static final String individuals_list = Account.INDIVIDUALS_GOOGLE_LIST;
	
	//Group ranking schemes
	public static final int SUBSET_GROUP_COUNT = 0;
	public static final int SUBSET_GROUP_SCORE = 1;
	public static final int SUBSET_WEIGHTED_SCORE = 2;
	
	public static final int INTERSECTION_GROUP_COUNT = 3;
	public static final int INTERSECTION_GROUP_SCORE = 4;
	public static final int INTERSECTION_WEIGHTED_SCORE = 5;
	public static final int TOP_CONTACT_SCORE = 6;
	
	public static final int COMBINED_GROUP_COUNT = 7;
	public static final int COMBINED_GROUP_SCORE = 8;
	public static final int COMBINED_WEIGHTED_SCORE = 9;
	
	//Half Life Values
	public static final int ONE_HOUR = 0;
	public static final int ONE_DAY = 1;
	public static final int ONE_WEEK = 2;
	public static final int FOUR_WEEKS = 3;
	public static final int SIX_MONTHS = 4;
	public static final int ONE_YEAR = 5;
	public static final int TWO_YEARS = 6;
	
	public static boolean predictIndividuals = true;

	public static boolean useTextContent = false;
	public static boolean useSocialConnections = true;
	
	public static boolean isAged = true;
	public static boolean useHalfLives = true;

	public static int group_algorithm = 5;
	public static long half_life = 0;
	public static double w_out = 1.0;
	public static double threshold = 0.0;
	
	public static double content_importance = 0.0;
	public static double connection_importance = 1.0;
	
	public static double relative_intersection_importance = 0.001;
	
	public abstract Map<String, Double> getIndividualPredictions();
	public abstract Map<ComparableSet<String>, Double> getGroupPredictions();
	
	protected static GoogleGroupTracker groupTracker;
	
	public PredictionMaker(){
	}
	
	public PredictionMaker(File accountFolder) throws IOException, MessagingException {
		super(accountFolder);
	}
	
	public PredictionMaker(String accountFolder) throws IOException, MessagingException {
		super(accountFolder);
	}
	
	public static void setHalfLife(int timeFrame){
		long val = 1000;
		val *= 3600;
		if(timeFrame == ONE_HOUR){
			half_life = val;
			return;
		}
		
		val *= 24;
		if(timeFrame == ONE_DAY){
			half_life = val;
			return;
		}
		
		val *= 7;
		if(timeFrame == ONE_WEEK){
			half_life = val;
			return;
		}
		
		if(timeFrame == FOUR_WEEKS){
			val *= 4;
			half_life = val;
			return;
		}
		
		val *= 26;
		if(timeFrame == SIX_MONTHS){
			half_life = val;
			return;
		}

		val *= 2;
		if(timeFrame == ONE_YEAR){
			half_life = val;
			return;
		}
		
		val *= 2;
		if(timeFrame == TWO_YEARS){
			half_life = val;
			return;
		}
		
	}
	
	public static void clearMsgLists(){
		if(individualMsgs != null){
			individualMsgs.clear();
			individualMsgs = null;
		}
		if(groupTracker != null){
			groupTracker.clear();
			groupTracker = null;
		}
	}
	
	public static String getGroupAlgorithmName(int group_algorithm){
		if(group_algorithm == SUBSET_GROUP_COUNT){
			return "Subset Group Count";
		}else if(group_algorithm == SUBSET_GROUP_SCORE){
			return "Subset Group Score";
		}else if(group_algorithm == SUBSET_WEIGHTED_SCORE){
			return "Subset Weighted Score";
		}else if(group_algorithm == INTERSECTION_GROUP_COUNT){
			return "Intersection Group Count";
		}else if(group_algorithm == INTERSECTION_GROUP_SCORE){
			return "Intersection Group Score";
		}else if(group_algorithm == INTERSECTION_WEIGHTED_SCORE){
			return "Intersection Weighted Score";
		}else if(group_algorithm == TOP_CONTACT_SCORE){
			return "Top Contact Score";
		}else{
			return "ERROR!";
		}
	}
	
	public static String getHalfLifeTitle(int half_life){
		if(half_life == ONE_HOUR){
			return "one hour";
		}else if(half_life == ONE_DAY){
			return "one day";
		}else if(half_life == ONE_WEEK){
			return "one week";
		}else if(half_life == FOUR_WEEKS){
			return "four weeks";
		}else if(half_life == SIX_MONTHS){
			return "six months";
		}else if(half_life == ONE_YEAR){
			return "one year";
		}else if(half_life == TWO_YEARS){
			return "two years";
		}else{
			return "ERROR!";
		}
	}
}
