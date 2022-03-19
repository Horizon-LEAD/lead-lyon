package lead.backend;

import java.util.HashMap;
import java.util.Map;

import lead.backend.data.StateData;
import lead.backend.data.ReceiverData;
import lead.backend.data.ScenarioData;
import lead.backend.data.SenderData;
import lead.backend.data.UCCData;
import lead.backend.data.VehicleTypeData;

public class DataManager {
	private final Map<String, ScenarioData> scenarios = new HashMap<>();
	private final Map<String, VehicleTypeData> vehicleTypes = new HashMap<>();
	private final Map<String, ReceiverData> receivers = new HashMap<>();
	private final Map<String, SenderData> senders = new HashMap<>();
	private final Map<String, UCCData> uccs = new HashMap<>();

	public DataManager(StateData data) {
		data.scenarios.forEach(s -> this.scenarios.put(s.id, s));
		data.vehicleTypes.forEach(vt -> this.vehicleTypes.put(vt.id, vt));
		data.receivers.forEach(r -> this.receivers.put(r.id, r));
		data.senders.forEach(s -> this.senders.put(s.id, s));
		data.uccs.forEach(u -> this.uccs.put(u.id, u));
	}

	public Map<String, ScenarioData> getScenarios() {
		return scenarios;
	}

	public Map<String, VehicleTypeData> getVehicleTypes() {
		return vehicleTypes;
	}

	public Map<String, ReceiverData> getReceivers() {
		return receivers;
	}

	public Map<String, SenderData> getSenders() {
		return senders;
	}

	public Map<String, UCCData> getUCCs() {
		return uccs;
	}
}
