package lead.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleTypeData {
	@JsonProperty("id")
	public String id;

	@JsonProperty("name")
	public String name;

	@JsonProperty("capacity")
	public int capacity;

	@JsonProperty("cost_per_day")
	public double costPerDay = 0.0;

	@JsonProperty("cost_per_km")
	public double costPerKm = 0.0;

	@JsonProperty("co2_per_km")
	public double co2PerKm = 0.0;
}
