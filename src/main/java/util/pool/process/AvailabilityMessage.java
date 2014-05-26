package util.pool.process;

import java.io.Serializable;

public class AvailabilityMessage implements Serializable {
	boolean isAvailable;
	
	public AvailabilityMessage(boolean isAvailable){
		this.isAvailable = isAvailable;
	}
	
	public boolean isAvailable(){
		return isAvailable;
	}
}
