package reader.threadfinder.stackoverflow.tools.eventflow;

public class ThreadItem implements Comparable<ThreadItem> {

	String type;
	Long time;
	Integer ownerId;

	public ThreadItem(String type, Long time, Integer ownerId) {
		this.type = type;
		this.time = time;
		this.ownerId = ownerId;
	}

	@Override
	public int compareTo(ThreadItem t) {
		if (time != null && time.compareTo(t.time) != 0) {
			return time.compareTo(t.time);
		}

		if (type != null && type.compareTo(t.type) != 0) {
			return type.compareTo(t.type);
		}

		if (ownerId == null) {
			if (t.ownerId == null) {
				return 0;
			} else {
				return -1;
			}
		}
		return ownerId.compareTo(t.ownerId);
	}

	@Override
	public String toString() {
		return "" + type + "," + ownerId + "," + time;
	}

}
