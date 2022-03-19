package lead.backend.data.optimization;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lead.backend.data.LocationData;

public class RouteData {
	@JsonProperty("vehicle_type")
	public String vehicle_type;

	@JsonProperty("carrier")
	public String carrierId;

	@JsonProperty("carrier_type")
	public CarrierType carrierType;

	public enum CarrierType {
		sender, receiver, ucc
	}

	@JsonProperty("distance_km")
	public double distance_km;

	@JsonProperty("trajectory")
	public List<LocationData> trajectory = new LinkedList<>();
}
