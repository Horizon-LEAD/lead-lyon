package lead;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

public class RunOptimization {
	static final int SIZE_INDEX = 0;

	static public void main(String[] args) throws NumberFormatException, IOException {
		String deliveriesPath = args[0];
		String costsPath = args[1];
		String depotNode = args[2];
		String outputPath = args[3];

		double vehicleSpeed_m_s = 2.7;
		double pickupServiceTime_s = 60.0;
		double deliveryServiceTime_s = 300.0;
		int vehicleCapacity = 5;
		int numberOfVehicles = 10;

		// Define costs

		VehicleRoutingTransportCostsMatrix.Builder costBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(true); //

		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(costsPath)));

			String line = null;

			List<String> header = null;
			List<String> row = null;

			while ((line = reader.readLine()) != null) {
				row = Arrays.asList(line.split(";"));

				if (header == null) {
					header = row;
				} else {
					String fromId = row.get(header.indexOf("from_node"));
					String toId = row.get(header.indexOf("to_node"));
					double distance = Double.parseDouble(row.get(header.indexOf("distance")));

					costBuilder.addTransportDistance(fromId, toId, distance);
					costBuilder.addTransportTime(fromId, toId, distance / vehicleSpeed_m_s);
				}
			}

			reader.close();
		}

		VehicleRoutingTransportCosts costs = costBuilder.build();

		// Define services

		Map<String, Shipment.Builder> shipmentBuilders = new HashMap<>();

		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(deliveriesPath)));

			String line = null;

			List<String> header = null;
			List<String> row = null;

			while ((line = reader.readLine()) != null) {
				row = Arrays.asList(line.split(";"));

				if (header == null) {
					header = row;
				} else {
					String householdId = row.get(header.indexOf("household_id"));

					if (!shipmentBuilders.containsKey(householdId)) {
						shipmentBuilders.put(householdId, Shipment.Builder.newInstance(householdId));
						shipmentBuilders.get(householdId).addSizeDimension(SIZE_INDEX, 1);

						String locationId = row.get(header.indexOf("location_id"));
						shipmentBuilders.get(householdId).setDeliveryLocation(Location.newInstance(locationId));
						shipmentBuilders.get(householdId).setPickupLocation(Location.newInstance(depotNode));
						shipmentBuilders.get(householdId).setPickupServiceTime(pickupServiceTime_s);
						shipmentBuilders.get(householdId).setDeliveryServiceTime(deliveryServiceTime_s);
					}

					double startTime = Double.parseDouble(row.get(header.indexOf("start_time")));
					double endTime = Double.parseDouble(row.get(header.indexOf("end_time")));
					shipmentBuilders.get(householdId).addDeliveryTimeWindow(startTime, endTime);
				}
			}

			reader.close();
		}

		List<Shipment> shipments = new LinkedList<>();

		for (Shipment.Builder builder : shipmentBuilders.values()) {
			shipments.add(builder.build());
		}

		// Vehicles

		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("robo") //
				.addCapacityDimension(SIZE_INDEX, vehicleCapacity) //
				.setCostPerWaitingTime(0.0) //
				.setCostPerDistance(1.0) //
				.setCostPerTransportTime(0.0) //
				.setCostPerServiceTime(0.0) //
				.setFixedCost(0.0);
		VehicleTypeImpl vehicleType = vehicleTypeBuilder.build();

		List<VehicleImpl> vehicles = new LinkedList<>();

		for (int i = 0; i < numberOfVehicles; i++) {
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle_" + i);
			vehicleBuilder.setStartLocation(Location.newInstance(depotNode));
			vehicleBuilder.setEndLocation(Location.newInstance(depotNode));
			vehicleBuilder.setReturnToDepot(true);
			vehicleBuilder.setType(vehicleType);
			vehicles.add(vehicleBuilder.build());
		}

		VehicleRoutingProblem problem = VehicleRoutingProblem.Builder.newInstance() //
				.setRoutingCost(costs) //
				.setFleetSize(FleetSize.FINITE) //
				.addAllJobs(shipments) //
				.addAllVehicles(vehicles) //
				.build();

		VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));

			writer.write(String.join(";", new String[] { //
					"vehicle_id", //
					"activity_index", //
					"arrival_time", //
					"end_time", //
					"name", //
					"location_id" //
			}) + "\n");

			for (VehicleRoute route : bestSolution.getRoutes()) {
				List<TourActivity> activities = new LinkedList<>(route.getActivities());
				activities.set(0, route.getStart());
				activities.add(route.getEnd());

				for (TourActivity activity : activities) {
					writer.write(String.join(";", new String[] { //
							route.getVehicle().getId(), //
							String.valueOf(activity.getIndex()), //
							String.valueOf(activity.getArrTime()), //
							String.valueOf(activity.getEndTime()), //
							String.valueOf(activity.getName()), //
							String.valueOf(activity.getLocation().getId()) }) + "\n");
				}
			}

			writer.close();
		}

		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
	}
}
