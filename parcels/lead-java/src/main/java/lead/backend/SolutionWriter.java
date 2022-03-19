package lead.backend;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

public class SolutionWriter implements Closeable {
	private final BufferedWriter writer;
	private final InfrastructureManager infrastructure;

	public SolutionWriter(OutputStreamWriter outputStreamWriter, InfrastructureManager infrastructure)
			throws IOException {
		this.infrastructure = infrastructure;
		writer = new BufferedWriter(outputStreamWriter);

		writer.write(String.join(";", new String[] { //
				"carrier_id", //
				"vehicle_id", //
				"route_index", //
				"start_time", //
				"end_time", //
				"activity", //
				"location_id", //
				"distance", //
		}) + "\n");
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	public void write(String problemId, VehicleRoutingProblemSolution solution) throws IOException {
		int routeIndex = 0;

		for (VehicleRoute route : solution.getRoutes()) {
			List<TourActivity> activities = new LinkedList<>(route.getActivities());
			activities.add(0, route.getStart());
			activities.add(route.getEnd());

			double distance = 0.0;
			TourActivity previousActivity = null;

			for (TourActivity activity : activities) {
				if (previousActivity != null) {
					distance += infrastructure.getRoutingCosts().getDistance(previousActivity.getLocation(),
							activity.getLocation(), previousActivity.getEndTime(), route.getVehicle());
				}

				previousActivity = activity;

				writer.write(String.join(";", new String[] { //
						problemId, //
						route.getVehicle().getId(), //
						String.valueOf(routeIndex), //
						String.valueOf(activity.getArrTime()), //
						String.valueOf(activity.getEndTime()), //
						String.valueOf(activity.getName()), //
						String.valueOf(activity.getLocation().getId()), //
						String.valueOf(distance) //
				}) + "\n");
			}

			routeIndex++;
		}
	}
}
