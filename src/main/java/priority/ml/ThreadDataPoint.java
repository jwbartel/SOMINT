package priority.ml;

import java.sql.Timestamp;
import java.util.Collection;

public class ThreadDataPoint {
	
	private int id;
	private Collection<String> tags;
	private Timestamp timeThreadStart;
	private Integer totalInteractions;
	
	public ThreadDataPoint(int threadID, Collection<String> threadTags, Timestamp threadStart, Integer interactions) {
		this.id = threadID;
		this.timeThreadStart = threadStart;
		this.tags = threadTags;
		this.totalInteractions = interactions;
	}
	
	public int getId() {
		return id;
	}



	public Collection<String> getTags() {
		return tags;
	}



	public Timestamp getTimeThreadStart() {
		return timeThreadStart;
	}



	public Integer getTotalInteractions() {
		return totalInteractions;
	}



	public String toString() {
		String str = "(" + id;
		for (String tag: tags) {
			str += "," + tag;
		}
		str += "," + timeThreadStart.getTime() + "," + totalInteractions + ")";
		return str;
	}
}
