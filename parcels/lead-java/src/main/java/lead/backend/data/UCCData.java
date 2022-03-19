package lead.backend.data;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UCCData {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("name")
	public String name;

	@JsonProperty("location")
	public LocationData location = null;

	@JsonProperty("vehicle_types")
	public List<String> vehicleTypeIds = new LinkedList<>();
}
