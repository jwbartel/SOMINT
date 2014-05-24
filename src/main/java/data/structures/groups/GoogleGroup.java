package data.structures.groups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import data.structures.DirectedEmailInteraction;
import data.structures.EmailInteraction;

public class GoogleGroup extends Group{
	
	//static final int ONE_HOUR = 0;
	static final int ONE_DAY = 0;
	static final int ONE_WEEK = 1;
	static final int FOUR_WEEKS = 2;
	static final int SIX_MONTHS = 3;
	static final int ONE_YEAR = 4;

	static double w_out = 0.5;
	static double half_life = 100000;
	
	
	
	public static void setW_Out(double w_out){
		GoogleGroup.w_out = w_out;
	}
	
	public static void setHalfLife(int timeFrame){
		/*if(timeFrame == ONE_HOUR){
			half_life = 1000*3600;
		}else*/ if(timeFrame == ONE_DAY){
			half_life = 1000*3600*24;
		}else if(timeFrame == ONE_WEEK){
			half_life = 1000*3600*24*7;
		}else if(timeFrame == FOUR_WEEKS){
			half_life = 1000*3600*24*7*4;
		}else if(timeFrame == SIX_MONTHS){
			half_life = 1000*3600*24*7*26;
		}else if(timeFrame == ONE_YEAR){
			half_life = 1000*3600*24*7*52;
		}
		
	}
	
	public GoogleGroup(Set<String> addresses){
		super(addresses);
	}
	
	
	public void foundInteraction(String loc, Date date, boolean wasReceived){
		DirectedEmailInteraction interaction = new DirectedEmailInteraction(loc, date, wasReceived);
		if(!interactions.contains(interaction)){
			interactions.add(new DirectedEmailInteraction(loc, date, wasReceived));
		}
	}
	
	public double computeIR(Date currTime) throws IOException{
		return computeIR(currTime.getTime());
	}
	
	public double computeIR(long currTime) throws IOException{
		double outTotal = 0.0;
		double inTotal = 0.0;
		
		for(int i=0; i<interactions.size(); i++){
			DirectedEmailInteraction interaction = (DirectedEmailInteraction) interactions.get(i);
			
			Date pastDate = interaction.getDate();
			double exponent = ((double) (currTime-pastDate.getTime()))/half_life;
			double value = Math.pow(0.5, exponent);
			
			if(interaction.wasReceived()){
				inTotal += value;
			}else{
				outTotal += value;
			}
			
			
		}
		
		return w_out * outTotal + inTotal;
	}
	
	public ArrayList<EmailInteraction> getInteractions(){
		ArrayList<EmailInteraction> toReturn = new ArrayList<EmailInteraction>();
		
		for(int i=0; i<interactions.size(); i++){
			DirectedEmailInteraction interaction = (DirectedEmailInteraction) interactions.get(i);
			DirectedEmailInteraction toAdd = new DirectedEmailInteraction(interaction.getEmailLocation(), interaction.getDate(), interaction.wasReceived());
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}
	
	
	
}
