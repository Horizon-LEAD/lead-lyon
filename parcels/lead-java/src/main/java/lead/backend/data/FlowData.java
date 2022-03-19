package lead.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlowData {
	@JsonProperty("sender")
	public String senderId;

	@JsonProperty("receiver")
	public String receiverId;

	@JsonProperty("shipment_type")
	public ShipmentType shipmentType = ShipmentType.delivery;

	@JsonProperty("consolidation_type")
	public ShipmentType consolidationType = ShipmentType.delivery;

	@JsonProperty("ucc")
	public String uccId;
}
