package lead.vroom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VroomProblemData {
	public List<VroomShipmentData> shipments = new LinkedList<>();
	public List<VroomVehicleData> vehicles = new LinkedList<>();
	public Map<String, VroomMatricesData> matrices = new HashMap<>();
}
