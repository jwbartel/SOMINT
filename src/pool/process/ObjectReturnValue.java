package pool.process;

public class ObjectReturnValue implements ReturnValue {
	Object value;
	
	public ObjectReturnValue(Object value){
		this.value = value;
	}
	
	public Object getValue(){
		return value;
	}
}
