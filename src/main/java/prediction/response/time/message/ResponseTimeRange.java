package prediction.response.time.message;

public class ResponseTimeRange {
	public final Double minResponseTime;
	public final Double maxResponseTime;

	public ResponseTimeRange(Double minResponseTime, Double maxResponseTime) {
		this.minResponseTime = minResponseTime;
		this.maxResponseTime = maxResponseTime;
	}

	@Override
	public String toString() {
		return "[" + minResponseTime + "," + maxResponseTime + "]";
	}
}