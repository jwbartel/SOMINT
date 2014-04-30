package bus.data.structures;

import java.util.Date;

public class DirectedEmailInteraction extends EmailInteraction{
	
	boolean received;
	
	public DirectedEmailInteraction(String emailLocation, Date date, boolean received) {
		super(emailLocation, date);
		this.received = received;
	}

	public boolean wasReceived(){
		return received;
	}
	
	public String toString(){
		String retVal =  ""+getDate().getTime()+" "+wasReceived();
		if(getEmailLocation() != null){
			retVal += getEmailLocation();
		}
		return retVal;
	}
	
	public boolean equals(Object object){
		if(object == null || ! (object instanceof DirectedEmailInteraction)){
			return false;
		}
		
		DirectedEmailInteraction interaction = (DirectedEmailInteraction) object;
		
		return received == interaction.wasReceived() 
				&& this.getDate().equals(interaction.getDate()) 
				&& ((this.getEmailLocation() == null && interaction.getEmailLocation() == null) || 
						(this.getEmailLocation() != null && this.getEmailLocation().equals(interaction.getEmailLocation()) ));
	}
}
