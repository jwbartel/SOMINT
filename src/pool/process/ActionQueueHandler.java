package pool.process;

import java.util.LinkedList;
import java.util.List;

import tracer.Tracer;


public class ActionQueueHandler implements Runnable {
	
	ProcessPool parent;
	
	List<ActionHandlerCombo> actionsQueue = new LinkedList<ActionHandlerCombo>();
	List<ActionHandlerCombo> priorityActionsQueue = new LinkedList<ActionHandlerCombo>();
	
	public ActionQueueHandler(ProcessPool parent){
		this.parent = parent;
	}
	
	
	@Override
	public void run() {
		while(true){
			synchronized(this){
				while(actionsQueue.size() == 0 && priorityActionsQueue.size() == 0){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				ActionHandlerCombo combo = null;
				if(priorityActionsQueue.size() > 0){
					 combo = priorityActionsQueue.remove(0);
				}else{
					combo = actionsQueue.remove(0);
				}
				QueueableAction action = combo.getAction();
				ReturnValueHandler handler = combo.getHandler();
				
				parent.giveActionToSender(action, handler);
			}
		}
	}
	

	
	public synchronized void queueAction(QueueableAction action, ReturnValueHandler handler){
		actionsQueue.add(new ActionHandlerCombo(action, handler));
		Tracer.info(this, "Action queued in normal queue. Queue is now size "+actionsQueue.size() );
		notify();
	}
	
	public synchronized void queuePrioritizedAction(QueueableAction action, ReturnValueHandler handler){
		
		
		priorityActionsQueue.add(new ActionHandlerCombo(action, handler));
		Tracer.info(this, "Action queued in priority queue. Priority queue is now size "+priorityActionsQueue.size() );
		notify();
	}

}
