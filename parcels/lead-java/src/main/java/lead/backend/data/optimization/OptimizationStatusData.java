package lead.backend.data.optimization;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptimizationStatusData {
	public enum StatusType {
		idle, running, finished
	}

	@JsonProperty("status")
	public StatusType status = StatusType.idle;

	@JsonProperty("progress")
	public double progess = Double.NaN;
}