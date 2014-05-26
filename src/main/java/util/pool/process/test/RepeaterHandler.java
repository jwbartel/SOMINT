package util.pool.process.test;

import util.pool.process.ReturnValue;
import util.pool.process.ReturnValueHandler;

public class RepeaterHandler implements ReturnValueHandler {

	long startTime = 0;
	int runnersCount = 0;
	
	public synchronized void handleReturnValue(ReturnValue value) {
		if(runnersCount > 0){
			runnersCount--;
		}

		if(runnersCount == 0){
			System.out.print(""+(System.currentTimeMillis() - startTime)+" ms,");
			notifyAll();
		}
	}
	
	public synchronized void incrementRunnersCount(){
		runnersCount++;
	}
	
	public void setStartTime(long time){
		startTime = time;
	}
	
	public boolean finished(){
		return runnersCount == 0;
	}

}
