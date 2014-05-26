package util.pool.process;

import java.io.Serializable;

public interface QueueableAction extends Serializable{

	public ReturnValue run();
}
