package reader.threadfinder.synthetic.tools;

public class SyntheticMessage {
	
	public long time;
	public Integer threadID;
	
	public SyntheticMessage(long time){
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
	
	public void setThreadID(Integer threadID){
		this.threadID=threadID;
	}
	
	public Integer getThreadID(){
		return threadID;
	}
	
	public String toString(){
		return "threadID:"+threadID+"\ttime:"+time;
	}

}
