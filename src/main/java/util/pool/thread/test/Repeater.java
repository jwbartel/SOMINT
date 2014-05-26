package util.pool.thread.test;

import util.pool.process.ObjectReturnValue;
import util.pool.process.QueueableAction;
import util.pool.process.ReturnValue;

public class Repeater implements QueueableAction {
	static final int printCount = 200000;
	
	String name;
	
	public Repeater(String name){
		this.name = name;
	}

	@Override
	public ReturnValue run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 return new ObjectReturnValue(name+ " finished at:" +System.currentTimeMillis());
	}

}
