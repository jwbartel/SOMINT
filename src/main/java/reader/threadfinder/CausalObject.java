package reader.threadfinder;

public interface CausalObject<T extends CausalObject> {

	public boolean isCauseOf(T object);
}
