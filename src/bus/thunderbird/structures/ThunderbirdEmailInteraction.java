package bus.thunderbird.structures;

import java.util.Date;

import bus.data.structures.DirectedEmailInteraction;

public class ThunderbirdEmailInteraction extends DirectedEmailInteraction{
	
	String sender;
	
	public ThunderbirdEmailInteraction(String sender, Date date, boolean wasReceived){
		super("", date, wasReceived);
		this.sender  = sender;
	}
	
	public String getSender(){
		return sender;
	}
	
	/*public String toString(){
		return ""+super.getDate().getTime()+" "+super.wasReceived()+" "+sender;
	}*/

}
