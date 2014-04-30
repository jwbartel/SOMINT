package pool.process;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import tracer.Tracer;

public class SocketActionReceiver implements Runnable{

	Socket socket;
	
	public SocketActionReceiver(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
		ObjectInputStream in;
		ObjectOutputStream out;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while(true){
			try {
				Tracer.info(this, "Sending message that process is available to receive actions");
				out.writeObject(new AvailabilityMessage(true));

				Tracer.info(this, "Waiting on action");
				QueueableAction action = (QueueableAction)in.readObject();
				Tracer.info(this, "Action:"+action+" received. Sending message that process is no longer availabe");
				out.writeObject(new AvailabilityMessage(false));
				Tracer.info(this, "Running action");
				ReturnValue retVal = action.run();
				Tracer.info(this, "Sending return value "+retVal);
				out.writeObject(retVal);
				
			} catch(SocketException e){
				System.out.println("Socket closed.");
				System.exit(0);
			}catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
