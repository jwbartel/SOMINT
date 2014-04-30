package kelli.FriendGrouper;

import java.text.DecimalFormat;

public class AccuracyRecord {
	double costToCreateIdeal = 0;
	double costToInsert = 0;
	double kelliAccuracy = 0;
	double costToDelete = 0;
	
	public void setRecord(double createIdeal, double insert, double kAccuracy, double delete){
		costToCreateIdeal = createIdeal;
		costToInsert = insert;
		kelliAccuracy = kAccuracy;
		costToDelete = delete;
	}
	
	public String toString(){
		return costToCreateIdeal + ", " + costToInsert + ", " + kelliAccuracy;
	}
	public String toFormattedString(){
		DecimalFormat myFormatter = new DecimalFormat("#.###");
	    String createIdeal = myFormatter.format(costToCreateIdeal);
	    String insertIdeal = myFormatter.format(costToInsert);
	    String deleteIdeal = myFormatter.format(costToDelete);
	    String kAcc = myFormatter.format(kelliAccuracy);
	    return createIdeal + ", " + insertIdeal + ", " + deleteIdeal + ", "+ kAcc;
	}
}
