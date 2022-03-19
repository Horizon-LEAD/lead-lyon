package lead.backend.data;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceiverData {
	@JsonProperty("id")
	public String id;

	@JsonProperty("name")
	public String name;

	@JsonProperty("demand")
	public List<LocationData> demand = new LinkedList<>();

	@JsonProperty("vehicle_types")
	public List<String> vehicleTypeIds = new LinkedList<>();
}
