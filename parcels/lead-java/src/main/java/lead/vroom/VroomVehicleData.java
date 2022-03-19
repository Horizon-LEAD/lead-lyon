package lead.vroom;

import java.util.LinkedList;
import java.util.List;

public class VroomVehicleData {
	public int id;
	public String profile = "car";
	public int start_index;
	public int end_index;
	public List<Integer> capacity = new LinkedList<>();
	public List<Integer> time_window = null;
	public double cost = 0.0;
}