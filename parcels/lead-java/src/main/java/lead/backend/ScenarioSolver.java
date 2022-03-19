package lead.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit.Parameter;
import com.graphhopper.jsprit.core.algorithm.listener.IterationEndsListener;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;

import lead.backend.data.FlowData;
import lead.backend.data.ReceiverData;
import lead.backend.data.ScenarioData;
import lead.backend.data.SenderData;
import lead.backend.data.ShipmentType;
import lead.backend.data.UCCData;
import lead.backend.data.VehicleTypeData;
import lead.backend.data.optimization.RouteData;
import lead.backend.data.optimization.RouteData.CarrierType;
import lead.backend.data.optimization.SolutionData;

public class ScenarioSolver {
	private final static Logger logger = Logger.getLogger(ScenarioSolver.class);

	static final int SIZE_INDEX = 0;

	private final DataManager manager;
	private final InfrastructureManager infrastructure;

	private final int numberOfIterations;

	public ScenarioSolver(DataManager manager, InfrastructureManager infrastructure, int numberOfIterations) {
		this.manager = manager;
		this.infrastructure = infrastructure;
		this.numberOfIterations = numberOfIterations;
	}

	public SolutionData solve(ScenarioData scenario) throws IOException {
		return solve(scenario, (a, b, c, d) -> {
		});
	}

	public SolutionData solve(ScenarioData scenario, ProgressObserver observer) throws IOException {
		List<PartialProblem> problems = new LinkedList<>();

		// Find flows by receivers, senders, and UCCs
		Map<String, List<FlowData>> flowsBySender = new HashMap<>();
		Map<String, List<FlowData>> flowsByReceiver = new HashMap<>();
		Map<String, List<FlowData>> flowsByUcc = new HashMap<>();

		for (FlowData flow : scenario.flows) {
			flowsBySender.computeIfAbsent(flow.senderId, sid -> new LinkedList<>()).add(flow);
			flowsByReceiver.computeIfAbsent(flow.receiverId, rid -> new LinkedList<>()).add(flow);

			if (flow.uccId != null) {
				flowsByUcc.computeIfAbsent(flow.uccId, uid -> new LinkedList<>()).add(flow);
			}
		}

		// Construct optimization problem per sender
		for (String senderId : flowsBySender.keySet()) {
			SenderData sender = Objects.requireNonNull(manager.getSenders().get(senderId));
			Location hubLocation = infrastructure.getLocation(senderLocationId(sender), sender.location);

			List<Location> deliveryLocations = new LinkedList<>();

			for (FlowData flow : flowsBySender.get(senderId)) {
				if (flow.uccId == null && flow.shipmentType.equals(ShipmentType.delivery)) {
					ReceiverData receiver = Objects.requireNonNull(manager.getReceivers().get(flow.receiverId));

					for (int i = 0; i < receiver.demand.size(); i++) {
						deliveryLocations.add(
								infrastructure.getLocation(receiverLocationId(receiver, i), receiver.demand.get(i)));
					}
				}

				if (flow.uccId != null && flow.consolidationType.equals(ShipmentType.delivery)) {
					UCCData ucc = Objects.requireNonNull(manager.getUCCs().get(flow.uccId));
					ReceiverData receiver = Objects.requireNonNull(manager.getReceivers().get(flow.receiverId));

					for (int i = 0; i < receiver.demand.size(); i++) {
						deliveryLocations.add(infrastructure.getLocation(uccLocationid(ucc), ucc.location));
					}
				}
			}

			if (deliveryLocations.size() > 0) {
				String problemId = "sender:" + sender.id;

				List<VehicleTypeData> vehicleTypes = sender.vehicleTypeIds.stream()
						.map(vtid -> Objects.requireNonNull(manager.getVehicleTypes().get(vtid)))
						.collect(Collectors.toList());

				problems.add(new PartialProblem(problemId, generateProblem(problemId, hubLocation,
						Collections.emptyList(), deliveryLocations, vehicleTypes), CarrierType.sender, sender.id));
			}
		}

		// Construct optimization problem per receiver
		for (String receiverId : flowsByReceiver.keySet()) {
			ReceiverData receiver = Objects.requireNonNull(manager.getReceivers().get(receiverId));

			for (int receiverIndex = 0; receiverIndex < receiver.demand.size(); receiverIndex++) {
				Location hubLocation = infrastructure.getLocation(receiverLocationId(receiver, receiverIndex),
						receiver.demand.get(receiverIndex));
				List<Location> pickupLocations = new LinkedList<>();

				for (FlowData flow : flowsByReceiver.get(receiverId)) {
					if (flow.uccId == null && flow.shipmentType.equals(ShipmentType.pickup)) {
						SenderData sender = Objects.requireNonNull(manager.getSenders().get(flow.senderId));
						pickupLocations.add(infrastructure.getLocation(senderLocationId(sender), sender.location));
					}

					if (flow.uccId != null && flow.shipmentType.equals(ShipmentType.pickup)) {
						UCCData ucc = Objects.requireNonNull(manager.getUCCs().get(flow.uccId));
						pickupLocations.add(infrastructure.getLocation(uccLocationid(ucc), ucc.location));
					}
				}

				if (pickupLocations.size() > 0) {
					String problemId = "receiver:" + receiver.id + "." + receiverIndex;

					List<VehicleTypeData> vehicleTypes = receiver.vehicleTypeIds.stream()
							.map(vtid -> Objects.requireNonNull(manager.getVehicleTypes().get(vtid)))
							.collect(Collectors.toList());

					problems.add(new PartialProblem(problemId, generateProblem(problemId, hubLocation, pickupLocations,
							Collections.emptyList(), vehicleTypes), CarrierType.receiver, receiver.id));
				}
			}
		}

		// Construct optimization problem per UCC
		for (String uccId : flowsByUcc.keySet()) {
			UCCData ucc = Objects.requireNonNull(manager.getUCCs().get(uccId));
			Location hubLocation = infrastructure.getLocation(uccLocationid(ucc), ucc.location);

			List<Location> pickupLocations = new LinkedList<>();
			List<Location> deliveryLocations = new LinkedList<>();

			for (FlowData flow : flowsByUcc.get(uccId)) {
				ReceiverData receiver = Objects.requireNonNull(manager.getReceivers().get(flow.receiverId));

				if (flow.shipmentType.equals(ShipmentType.delivery)) {
					for (int i = 0; i < receiver.demand.size(); i++) {
						deliveryLocations.add(
								infrastructure.getLocation(receiverLocationId(receiver, i), receiver.demand.get(i)));
					}
				}

				if (flow.consolidationType.equals(ShipmentType.pickup)) {
					SenderData sender = Objects.requireNonNull(manager.getSenders().get(flow.senderId));

					for (int i = 0; i < receiver.demand.size(); i++) {
						pickupLocations.add(infrastructure.getLocation(senderLocationId(sender), sender.location));
					}
				}
			}

			if (pickupLocations.size() > 0 || deliveryLocations.size() > 0) {
				String problemId = "ucc:" + ucc.id;

				List<VehicleTypeData> vehicleTypes = ucc.vehicleTypeIds.stream()
						.map(vtid -> Objects.requireNonNull(manager.getVehicleTypes().get(vtid)))
						.collect(Collectors.toList());

				problems.add(new PartialProblem(problemId,
						generateProblem(problemId, hubLocation, pickupLocations, deliveryLocations, vehicleTypes),
						CarrierType.ucc, ucc.id));
			}
		}

		logger.info("Done generating VRPs (" + problems.size() + " in total)");

		SolutionData solutionData = new SolutionData();
		double solutionStartTime = System.nanoTime();

		int totalProblems = problems.size();
		int totalIterations = numberOfIterations * totalProblems;

		final AtomicInteger currentProblems = new AtomicInteger(0);
		final AtomicInteger currentIterations = new AtomicInteger(0);

		Collections.sort(problems, (a, b) -> {
			return -Integer.compare(a.vrp.getNuActivities(), b.vrp.getNuActivities());
		});

		for (PartialProblem problem : problems) {
			logger.info("Solving VRP " + problem.problemId);

			VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem.vrp) //
					// .setRandom(new Random(randomSeed)) //
					// .setExecutorService(executorService, numberOfThreads) //
					.setProperty(Parameter.FIXED_COST_PARAM, "1.0") //
					.setProperty(Parameter.ITERATIONS, String.valueOf(numberOfIterations)) //
					.buildAlgorithm();

			algorithm.addListener(new IterationEndsListener() {
				@Override
				public void informIterationEnds(int i, VehicleRoutingProblem problem,
						Collection<VehicleRoutingProblemSolution> solutions) {
					currentIterations.incrementAndGet();
					observer.update(currentProblems.get(), totalProblems, currentIterations.get(), totalIterations);
				}
			});

			long startTime = System.nanoTime();
			Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
			long endTime = System.nanoTime();

			logger.info("  Finished after " + (1e-9 * (endTime - startTime)) + " seconds");

			VehicleRoutingProblemSolution solution = Solutions.bestOf(solutions);
			for (VehicleRoute route : solution.getRoutes()) {
				RouteData routeData = new RouteData();
				solutionData.routes.add(routeData);

				routeData.carrierType = problem.carrierType;
				routeData.carrierId = problem.carrierId;
				routeData.vehicle_type = route.getVehicle().getVehicleTypeIdentifier().type;

				List<TourActivity> activities = new LinkedList<>(route.getActivities());
				activities.add(0, route.getStart());
				activities.add(route.getEnd());

				TourActivity previousActivity = null;
				Location previousLocation = null;

				for (TourActivity activity : activities) {
					if (previousActivity != null) {
						routeData.distance_km += infrastructure.getRoutingCosts().getDistance(
								previousActivity.getLocation(), activity.getLocation(), previousActivity.getEndTime(),
								route.getVehicle()) * 1e-3;
					}

					if (activity.getLocation() != previousLocation) {
						routeData.trajectory.add(infrastructure.getLocationData(activity.getLocation()));
					}

					previousActivity = activity;
					previousLocation = activity.getLocation();
				}
			}

			synchronized (solutionData) {
				solutionData.cost += solution.getCost();
			}

			currentProblems.incrementAndGet();
			observer.update(currentProblems.get(), totalProblems, currentIterations.get(), totalIterations);
		}

		double solutionEndTime = System.nanoTime();
		solutionData.runtime_s = 1e-9 * (solutionEndTime - solutionStartTime);

		return solutionData;
	}

	private VehicleRoutingProblem generateProblem(String id, Location hubLocation, List<Location> pickupLocations,
			List<Location> deliveryLocations, List<VehicleTypeData> vehicleTypes) {
		// Generate shipments
		List<Shipment> shipments = new ArrayList<>(pickupLocations.size() + deliveryLocations.size());

		for (int i = 0; i < pickupLocations.size(); i++) {
			shipments.add(Shipment.Builder.newInstance(id + ":p:" + i) //
					.setPickupLocation(pickupLocations.get(i)) //
					.setDeliveryLocation(hubLocation) //
					.addSizeDimension(SIZE_INDEX, 1) //
					.build());
		}

		for (int i = 0; i < deliveryLocations.size(); i++) {
			shipments.add(Shipment.Builder.newInstance(id + ":d:" + i) //
					.setPickupLocation(hubLocation) //
					.setDeliveryLocation(deliveryLocations.get(i)) //
					.build());
		}

		// Generate vehicles
		List<VehicleImpl> vehicles = new LinkedList<>();

		for (VehicleTypeData vehicleTypeData : vehicleTypes) {
			VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance(vehicleTypeData.id) //
					.addCapacityDimension(SIZE_INDEX, vehicleTypeData.capacity) //
					.setCostPerWaitingTime(0.0) //
					.setCostPerDistance(vehicleTypeData.costPerKm * 1e-3) //
					.setCostPerTransportTime(0.0) //
					.setCostPerServiceTime(0.0) //
					.setFixedCost(vehicleTypeData.costPerDay) //
					.build();

			vehicles.add(VehicleImpl.Builder.newInstance(vehicleType.getTypeId()) //
					.setStartLocation(hubLocation) //
					.setEndLocation(hubLocation) //
					.setReturnToDepot(true) //
					.setType(vehicleType) //
					.build());
		}

		logger.info("Generating VRP Problem " + id + " with " + shipments.size() + " shipments and " + vehicles.size()
				+ " vehicle types");

		return VehicleRoutingProblem.Builder.newInstance() //
				.setRoutingCost(infrastructure.getRoutingCosts()) //
				.setFleetSize(FleetSize.INFINITE) //
				.addAllJobs(shipments) //
				.addAllVehicles(vehicles) //
				.build();
	}

	static private class PartialProblem {
		VehicleRoutingProblem vrp;

		String problemId;
		CarrierType carrierType;
		String carrierId;

		PartialProblem(String problemId, VehicleRoutingProblem vrp, CarrierType carrierType, String carrierId) {
			this.vrp = vrp;
			this.problemId = problemId;
			this.carrierId = carrierId;
			this.carrierType = carrierType;
		}
	}

	private String receiverLocationId(ReceiverData receiver, int index) {
		return String.format("r:%s:%d", receiver.id, index);
	}

	private String senderLocationId(SenderData sender) {
		return String.format("s:%s", sender.id);
	}

	private String uccLocationid(UCCData ucc) {
		return String.format("u:%s", ucc.id);
	}

	static public interface ProgressObserver {
		void update(int currentProblems, int totalProblems, int currentIterations, int totalIterations);
	}
}
