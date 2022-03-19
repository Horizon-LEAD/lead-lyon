package lead.vroom;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

public class RunOptimizationVroom {
	static public void main(String[] args) throws NumberFormatException, IOException, ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("input-path", "output-path") //
				.allowOptions("threads", "random-seed", "iterations", "fleet-size") //
				.build();

		int numberOfThreads = cmd.getOption("threads").map(Integer::parseInt)
				.orElse(Runtime.getRuntime().availableProcessors());
		int randomSeed = cmd.getOption("random-seed").map(Integer::parseInt).orElse(0);
		int iterations = cmd.getOption("iterations").map(Integer::parseInt).orElse(10000);
		FleetSize fleetSize = FleetSize.valueOf(cmd.getOption("fleet-size").orElse("FINITE"));

		VroomProblemData vroomProblem = new ObjectMapper().readValue(new File(cmd.getOptionStrict("input-path")),
				VroomProblemData.class);

		// Define cost and duration

		VehicleRoutingTransportCostsMatrix.Builder costBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(true);

		for (int i = 0; i < vroomProblem.matrices.get("car").costs.size(); i++) {
			for (int j = 0; j < vroomProblem.matrices.get("car").costs.size(); j++) {
				costBuilder.addTransportDistance(String.valueOf(i), String.valueOf(j),
						vroomProblem.matrices.get("car").costs.get(i).get(j));
				costBuilder.addTransportTime(String.valueOf(i), String.valueOf(j),
						vroomProblem.matrices.get("car").durations.get(i).get(j));
			}
		}

		VehicleRoutingTransportCosts costs = costBuilder.build();

		// Define services

		List<Shipment> shipments = new LinkedList<>();

		for (VroomShipmentData vroomShipment : vroomProblem.shipments) {
			VroomShipmentStepData pickup = vroomShipment.pickup;
			VroomShipmentStepData delivery = vroomShipment.delivery;

			Shipment.Builder builder = Shipment.Builder.newInstance(String.valueOf(pickup.id)) //
					.setPickupLocation(Location.newInstance(pickup.location_index)) //
					.setDeliveryLocation(Location.newInstance(delivery.location_index)); //

			for (int i = 0; i < vroomShipment.amount.size(); i++) {
				builder.addSizeDimension(i, vroomShipment.amount.get(i));
			}

			shipments.add(builder.build());
		}

		// Vehicles

		List<VehicleImpl> vehicles = new LinkedList<>();

		for (VroomVehicleData vehicle : vroomProblem.vehicles) {
			VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance(String.valueOf(vehicle.id)) //
					.setCostPerWaitingTime(0.0) //
					.setCostPerDistance(1.0) //
					.setCostPerTransportTime(0.0) //
					.setCostPerServiceTime(0.0) //
					.setFixedCost(0.0); //

			if (fleetSize.equals(FleetSize.INFINITE)) {
				vehicleTypeBuilder.setFixedCost(vehicle.cost);
			}

			for (int i = 0; i < vehicle.capacity.size(); i++) {
				vehicleTypeBuilder.addCapacityDimension(i, vehicle.capacity.get(i));
			}

			VehicleTypeImpl vehicleType = vehicleTypeBuilder.build();

			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(String.valueOf(vehicle.id));
			vehicleBuilder.setStartLocation(Location.newInstance(vehicle.start_index));
			vehicleBuilder.setEndLocation(Location.newInstance(vehicle.end_index));
			vehicleBuilder.setReturnToDepot(true);
			vehicleBuilder.setType(vehicleType);

			if (vehicle.time_window != null) {
				vehicleBuilder.setEarliestStart(vehicle.time_window.get(0));
				vehicleBuilder.setLatestArrival(vehicle.time_window.get(1));
			}

			vehicles.add(vehicleBuilder.build());
		}

		VehicleRoutingProblem problem = VehicleRoutingProblem.Builder.newInstance() //
				.setRoutingCost(costs) //
				.setFleetSize(fleetSize) //
				.addAllJobs(shipments) //
				.addAllVehicles(vehicles) //
				.build();

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem) //
				.setRandom(new Random(randomSeed)) //
				.setExecutorService(executorService, numberOfThreads) //
				// .setProperty(Parameter.FIXED_COST_PARAM, "1.0") //
				.buildAlgorithm();

		algorithm.setMaxIterations(iterations);

		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
		executorService.shutdown();

		VroomSolutionData solution = new VroomSolutionData();
		solution.unassigned = bestSolution.getUnassignedJobs().stream().map(j -> j.getIndex())
				.collect(Collectors.toList());
		solution.summary.cost = bestSolution.getCost();

		for (VehicleRoute route : bestSolution.getRoutes()) {
			VroomRoute vroomRoute = new VroomRoute();
			solution.routes.add(vroomRoute);
			vroomRoute.vehicle = Integer.parseInt(route.getVehicle().getId());
		}

		new ObjectMapper().writeValue(new File(cmd.getOptionStrict("output-path")), solution);
	}
}
