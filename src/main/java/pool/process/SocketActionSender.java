package pool.process;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketActionSender implements Runnable{

	ProcessPool parent;
	Socket socket;
	BlockingQueue<QueueableAction> actionQueue = new ArrayBlockingQueue<QueueableAction>(1); //Solely to wait for next action available
	BlockingQueue<ReturnValueHandler> handlerQueue = new ArrayBlockingQueue<ReturnValueHandler>(1); //Solely to wait for next action available
	
	public SocketActionSender(ProcessPool parent, Socket socket){
		this.parent = parent;
		this.socket = socket;
	}
	
	public void run(){
		
		ObjectInputStream in;
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		AvailabilityMessage message = null;
		while(true){
			try {
				if(message == null || !message.isAvailable()){
					message = (AvailabilityMessage) in.readObject();
				}
				
				if(message != null){
					if(message.isAvailable()){
						parent.senderMadeAvailable(this);
					}else{
						throw new ProcessPoolException("Out of order availability message");
					}
				}else{
					continue;
				}
				
				QueueableAction action;
				while(true){
					try {
						action = actionQueue.take();
					} catch (InterruptedException e) {
						continue;
					}
					break;
				}
				
				ReturnValueHandler handler;
				while(true){
					try {
						handler = handlerQueue.take();
					} catch (InterruptedException e) {
						continue;
					}
					break;
				}
				
				out.writeObject(action);
				
				message = (AvailabilityMessage) in.readObject();
				if(message == null || message.isAvailable()){
					parent.queuePrioritizedAction(this, action, handler);
					throw new ProcessPoolException("Out of order availability message");
				}
				
				ReturnValue retVal = (ReturnValue) in.readObject();
				handler.handleReturnValue(retVal);
				
				//TODO: alert parent of received availability of false (throw exception if availability = true)
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void associateAction(QueueableAction action, ReturnValueHandler handler){
		actionQueue.add(action);
		handlerQueue.add(handler);
	}
	
}
