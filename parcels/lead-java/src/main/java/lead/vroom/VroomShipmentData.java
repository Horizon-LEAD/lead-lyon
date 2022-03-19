package lead.vroom;

import java.util.LinkedList;
import java.util.List;

public class VroomShipmentData {
	public VroomShipmentStepData pickup = null;
	public VroomShipmentStepData delivery = null;
	public List<Integer> amount = new LinkedList<>();
}
