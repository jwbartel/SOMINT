package pool.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tracer.Tracer;

public class ProcessPool {

	public static final String POOL_ADDRESS = "127.0.0.1";
	public static final int POOL_PORT = 1099;
	
	ActionQueueHandler queueHandler = new ActionQueueHandler(this);
	
	List<QueueableAction> actionsQueue = new LinkedList<QueueableAction>();
	List<QueueableAction> priorityActionsQueue = new LinkedList<QueueableAction>();
	
	Map<QueueableAction, ReturnValueHandler> actionToHandler = new HashMap<QueueableAction, ReturnValueHandler>();
	Map<SocketActionSender, QueueableAction> senderToAction = new HashMap<SocketActionSender, QueueableAction>();
	
	BlockingQueue<SocketActionSender> senderQueue = new LinkedBlockingQueue<SocketActionSender>();
	
	public ProcessPool(){
		init(POOL_PORT);
	}
	
	
	
	public ProcessPool(int port){
		init(port);
	}
	
	protected void init(int port){
		
		Tracer.info(this, "Starting SocketConnectionAceptor on port "+port);
		Thread connectionAcceptor = new Thread(new SocketConnectionAcceptor(this, port));
		connectionAcceptor.start();
		Tracer.info(this, "SocketConnectionAceptor started");
		
		
		Tracer.info(this, "Starting ActionQueueHandler in separate Thread");
		Thread asyncQueueHandler = new Thread(queueHandler);
		asyncQueueHandler.start();
	}
	
	public synchronized void queueAction(QueueableAction action, ReturnValueHandler handler){
		queueHandler.queueAction(action, handler);
		
	}
	
	public synchronized void queuePrioritizedAction(SocketActionSender failedSender, QueueableAction action, ReturnValueHandler handler){
		if(failedSender != null){
			senderToAction.remove(failedSender);
		}
		queueHandler.queuePrioritizedAction(action, handler);
	}
	
	public void senderMadeAvailable(SocketActionSender sender){
		synchronized(this){
			QueueableAction action = senderToAction.get(sender);
			if(action != null){
				senderToAction.remove(sender);
				actionToHandler.remove(action);
			}
		}
		
		senderQueue.add(sender);
		
	}
	
	
	public void giveActionToSender(QueueableAction action, ReturnValueHandler handler){
		SocketActionSender sender = null;
		while(sender == null){
			try {
				sender = senderQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		senderToAction.put(sender, action);
		sender.associateAction(action, handler);
	}
	
	public int getAvailableProcesses(){
		return senderQueue.size();
	}
	
}
