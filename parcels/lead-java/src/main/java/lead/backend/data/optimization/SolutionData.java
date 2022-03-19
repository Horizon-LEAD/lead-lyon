package lead.backend.data.optimization;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolutionData {
	@JsonProperty("runtime_s")
	public double runtime_s;

	@JsonProperty("cost")
	public double cost;

	@JsonProperty("routes")
	public List<RouteData> routes = new LinkedList<>();
}
