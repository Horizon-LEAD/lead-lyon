package lead.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectData {
	@JsonProperty("center")
	public LocationData center = null;

	@JsonProperty("zoom")
	public double zoom = 13.0;
}
