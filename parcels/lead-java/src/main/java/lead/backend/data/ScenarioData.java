package lead.backend.data;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScenarioData {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("name")
	public String name;

	@JsonProperty("flows")
	public List<FlowData> flows = new LinkedList<>();
}
