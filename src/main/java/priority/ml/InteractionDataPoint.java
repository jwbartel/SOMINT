package priority.ml;

import java.util.Collection;

public class InteractionDataPoint {

	private int threadID;
	private String type;
	private Integer totalInteractions;
	private Long totalTime;
	private Double averageTimeBetweenInteractions;
	private Long timeSinceLastInteraction;
	private Collection<String> tags;
	private String body;
	private Double timeToNext = Double.POSITIVE_INFINITY;
	
	public InteractionDataPoint(int thread, String interactionType, Integer interactions, Long lifetime,
			Double averageTimeBetween, Long timeSinceLast, Collection<String> interactionTags, 
			String interactionBody) {
		
		this.threadID = thread;
		this.type = interactionType;
		this.totalInteractions = interactions;
		this.totalTime = lifetime;
		this.averageTimeBetweenInteractions = averageTimeBetween;
		this.timeSinceLastInteraction = timeSinceLast;
		this.tags = interactionTags;
		this.body = interactionBody;
		
	}

	public int getThreadID() {
		return threadID;
	}

	public String getType() {
		return type;
	}

	public Integer getTotalInteractions() {
		return totalInteractions;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public Double getAverageTimeBetweenInteractions() {
		return averageTimeBetweenInteractions;
	}

	public Long getTimeSinceLastInteraction() {
		return timeSinceLastInteraction;
	}

	public Collection<String> getTags() {
		return tags;
	}

	public String getBody() {
		return body;
	}
	
	public Double getTimeToNext() {
		return timeToNext;
	}

	public void setTimeToNext(Double timeToNext) {
		this.timeToNext = timeToNext;
	}

	public String toString(){
		String str = "Thread ID:" + threadID + "\n";
		str += "Type:" + type + "\n";
		str += "Seen Interactions:" + totalInteractions + "\n";
		str += "Time so far:" + totalTime + "\n";
		str += "Average time between interactions:" + averageTimeBetweenInteractions + "\n";
		str += "Time since last interaction:" + timeSinceLastInteraction + "\n";
		str += "Tags:" + tags + "\n";
		str += "Time to next:" + timeToNext +"\n"; 
		str += "Body:" + body;
		
		return str;
	}

}
