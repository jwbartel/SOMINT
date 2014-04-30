package pool.process;

public class ActionHandlerCombo{
	private QueueableAction action;
	private ReturnValueHandler handler;
	
	public ActionHandlerCombo(QueueableAction action, ReturnValueHandler handler){
		this.action = action;
		this.handler = handler;
	}
	
	public QueueableAction getAction(){
		return action;
	}
	
	public ReturnValueHandler getHandler(){
		return handler;
	}
}
