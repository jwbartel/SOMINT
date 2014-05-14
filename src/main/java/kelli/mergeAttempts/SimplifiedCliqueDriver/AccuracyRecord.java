package kelli.mergeAttempts.SimplifiedCliqueDriver;

import java.text.DecimalFormat;

public class AccuracyRecord {
	double costToCreateIdeal = 0;
	double costToInsert = 0;
	double kelliAccuracy = 0;
	double costToDelete = 0;
	final double DELETE_THRESHOLD = 0.5;
	public  AccuracyRecord(double createIdeal, double insert, double kAccuracy, double delete){
		setRecord(createIdeal, insert, kAccuracy, delete);
	}
	
	 void setRecord(double createIdeal, double insert, double kAccuracy, double delete){
		costToCreateIdeal = createIdeal;
		costToInsert = insert;
		kelliAccuracy = kAccuracy;
		costToDelete = delete;
	}
	public double getIdealCost() {
		return costToCreateIdeal;
	}
	public double getInsertCost() {
		return costToInsert;
	}
	public double getKelliAccuracy() {
		return kelliAccuracy;
	}
	public double getDeleteCost() {
		return costToDelete;
	}

	
	public String toString(){
		//return costToCreateIdeal + ", " + costToInsert + ", " + kelliAccuracy;
		if (costToDelete > DELETE_THRESHOLD)
			return "Too costly";
		return toFormattedString();
	}
	public String toFormattedString(){
		DecimalFormat myFormatter = new DecimalFormat("#.###");
	    String createIdeal = myFormatter.format(costToCreateIdeal);
	    String insertIdeal = myFormatter.format(costToInsert);
	    String deleteIdeal = myFormatter.format(costToDelete);
	    String kAcc = myFormatter.format(kelliAccuracy);
	    return "T:"+createIdeal + ", I: " + insertIdeal + ", D: " + deleteIdeal + ", K:"+ kAcc;
	}
}
