package data.structures;

import java.util.Date;


public class EmailInteraction {
	
	String emailLocation;
	Date date;
	
	
	public EmailInteraction(String emailLocation, Date date){
		this.emailLocation = emailLocation;
		this.date = date;
	}
	
	public String getEmailLocation(){
		return emailLocation;
	}
	
	public Date getDate(){
		return date;
	}
	
	
	
}
