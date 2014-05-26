package util.pool.process.test;

import java.util.ArrayList;

import tracer.ImplicitKeywordKind;
import tracer.Tracer;
import util.pool.process.ProcessPool;
import util.pool.process.QueueableAction;


public class PoolStarter {

	static long startTime = 0;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setTracing();
		ProcessPool processPool = new ProcessPool();
		
		int repetitions = 10;
		int[] runnerCounts = {1,2,4,8,16,32,64};
		
		int numRunners = 10;
		ArrayList<QueueableAction> runners = new ArrayList<QueueableAction>();
		
		for(int i=0; i<runnerCounts.length; i++){
			
			for(int reps=0; reps<repetitions; reps++){
				System.out.print(","+runnerCounts[i]);
			}
			
		}
		System.out.println();
		
		
		while(true){
			System.out.print(processPool.getAvailableProcesses()+",");
			
			for(int runnerCountPos=0; runnerCountPos<runnerCounts.length; runnerCountPos++){
				numRunners = runnerCounts[runnerCountPos];
				
				for(int reps=0; reps<repetitions; reps++){
					
					RepeaterHandler handler = new RepeaterHandler();
					
					for(int i=0; i<numRunners; i++){
						handler.incrementRunnersCount();
						runners.add(new Repeater("runner"+i));
					}
					


					handler.setStartTime(System.currentTimeMillis());
					
					for(int i=0; i<runners.size(); i++){
						processPool.queueAction(runners.get(i), handler);
					}
					
					synchronized(handler){
						while(!handler.finished()){
							try {
								handler.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					runners.clear();
				}
			}
			System.out.println();
		}
	}
	
	public static void setTracing(){
		Tracer.showInfo(false);
		Tracer.setKeyWordStatus(Tracer.ALL_KEYWORDS, false);
		Tracer.setImplicitKeywordKind(ImplicitKeywordKind.OBJECT_PACKAGE_NAME);
		Tracer.setKeyWordStatus(ProcessPool.class, true);
	}
}
