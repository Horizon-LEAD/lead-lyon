package lead.backend.data;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StateData {
	@JsonProperty("project")
	public ProjectData project = new ProjectData();

	@JsonProperty("senders")
	public List<SenderData> senders = new LinkedList<>();

	@JsonProperty("receivers")
	public List<ReceiverData> receivers = new LinkedList<>();

	@JsonProperty("vehicle_types")
	public List<VehicleTypeData> vehicleTypes = new LinkedList<>();

	@JsonProperty("uccs")
	public List<UCCData> uccs = new LinkedList<>();

	@JsonProperty("scenarios")
	public List<ScenarioData> scenarios = new LinkedList<>();

	@JsonProperty("osm_path")
	public String osmPath;

	@JsonProperty("crs")
	public String crs;
}
