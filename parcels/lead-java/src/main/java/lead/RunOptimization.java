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
import java.util.concurrent.atomic.AtomicInteger;

import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.listener.IterationEndsListener;
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

	static public void main(String[] args) throws NumberFormatException, IOException, ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("deliveries-path", "costs-path", "depot-node", "output-path") //
				.allowOptions("vehicle-speed", "vehicle-capacity", "vehicle-type", "max-iterations") //
				.build();

		String deliveriesPath = cmd.getOptionStrict("deliveries-path");
		String costsPath = cmd.getOptionStrict("costs-path");
		String depotNode = cmd.getOptionStrict("depot-node");
		String outputPath = cmd.getOptionStrict("output-path");

		double vehicleSpeed_m_s = cmd.getOption("vehicle-speed").map(Double::parseDouble).orElse(5.0) / 3.6;
		double pickupServiceTime_s = 60.0;
		double deliveryServiceTime_s = 300.0;
		int vehicleCapacity = cmd.getOption("vehicle-capacity").map(Integer::parseInt).orElse(4);
		int maximumIterations = cmd.getOption("max-iterations").map(Integer::parseInt).orElse(2000);
		int numberOfVehicles = 20;

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

					double travelTime = distance / vehicleSpeed_m_s;

					if (header.indexOf("travel_time") >= 0) {
						travelTime = Double.parseDouble(row.get(header.indexOf("travel_time")));
					}

					costBuilder.addTransportDistance(fromId, toId, distance);
					costBuilder.addTransportTime(fromId, toId, travelTime);
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

		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("robo");

		switch (cmd.getOption("vehicle-type").orElse("robo")) {
		case "robo":
			vehicleTypeBuilder //
					.addCapacityDimension(SIZE_INDEX, vehicleCapacity) //
					.setCostPerWaitingTime(0.0) //
					.setCostPerDistance(1.0) //
					.setCostPerTransportTime(0.0) //
					.setCostPerServiceTime(0.0) //
					.setFixedCost(0.0);
			break;
		case "van":
			vehicleTypeBuilder //
					.addCapacityDimension(SIZE_INDEX, vehicleCapacity) //
					.setCostPerWaitingTime(0.0) //
					.setCostPerDistance(0.4 * 1e-3) //
					.setCostPerTransportTime(0.0) //
					.setCostPerServiceTime(0.0) //
					.setFixedCost(4000.0 * 12.0 / 365.0);
			break;
		}

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
		algorithm.setMaxIterations(maximumIterations);

		AtomicInteger usedIterations = new AtomicInteger(0);
		algorithm.addListener(new IterationEndsListener() {
			@Override
			public void informIterationEnds(int i, VehicleRoutingProblem problem,
					Collection<VehicleRoutingProblemSolution> solutions) {
				usedIterations.incrementAndGet();
			}
		});

		long startTime = System.nanoTime();
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		long endTime = System.nanoTime();

		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
		double runtime = 1e-9 * (endTime - startTime);

		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));

			writer.write(String.join(";", new String[] { //
					"vehicle_id", //
					"activity_index", //
					"arrival_time", //
					"end_time", //
					"name", //
					"location_id", //
					"iterations", //
					"unassigned_jobs", //
					"runtime", //
					"distance", //
			}) + "\n");

			for (VehicleRoute route : bestSolution.getRoutes()) {
				List<TourActivity> activities = new LinkedList<>(route.getActivities());
				activities.add(0, route.getStart());
				activities.add(route.getEnd());

				double distance = 0.0;
				TourActivity previousActivity = null;

				for (TourActivity activity : activities) {
					if (previousActivity != null) {
						distance += costs.getDistance(previousActivity.getLocation(), activity.getLocation(),
								previousActivity.getEndTime(), route.getVehicle());
					}

					previousActivity = activity;

					writer.write(String.join(";", new String[] { //
							route.getVehicle().getId(), //
							String.valueOf(activity.getIndex()), //
							String.valueOf(activity.getArrTime()), //
							String.valueOf(activity.getEndTime()), //
							String.valueOf(activity.getName()), //
							String.valueOf(activity.getLocation().getId()), //
							String.valueOf(usedIterations.intValue()), //
							String.valueOf(bestSolution.getUnassignedJobs().size()), //
							String.valueOf(runtime), //
							String.valueOf(distance) //
					}) + "\n");
				}
			}

			writer.close();
		}

		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
	}
}
