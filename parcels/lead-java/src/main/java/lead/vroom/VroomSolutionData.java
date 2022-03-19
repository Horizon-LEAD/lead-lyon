package lead.vroom;

import java.util.LinkedList;
import java.util.List;

public class VroomSolutionData {
	public List<Integer> unassigned = new LinkedList<>();

	public VroomSummaryData summary = new VroomSummaryData();

	public List<VroomRoute> routes = new LinkedList<>();
}
