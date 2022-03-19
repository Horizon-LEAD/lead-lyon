package lead.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationData {
	@JsonProperty("lat")
	public double latitude;

	@JsonProperty("lng")
	public double longitude;

	static public LocationData of(double latitude, double longitude) {
		LocationData location = new LocationData();
		location.latitude = latitude;
		location.longitude = longitude;
		return location;
	}
}
