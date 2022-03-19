package lead.backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.speedy.SpeedyALTFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import lead.backend.data.LocationData;

public class InfrastructureManagerWithNetwork {
	private final Network network;
	private final QuadTree<Node> spatialIndex;

	private final Set<Node> relevantNodes = new HashSet<>();

	private VehicleRoutingTransportCosts costs;
	private boolean needsUpdate = true;

	private GeotoolsTransformation wgsToEuclidean;
	private GeotoolsTransformation euclideanToWgs;

	public InfrastructureManagerWithNetwork(Network network) {
		double dimensions[] = NetworkUtils.getBoundingBox(network.getNodes().values());

		this.network = network;
		this.spatialIndex = new QuadTree<>(dimensions[0], dimensions[1], dimensions[2], dimensions[3]);

		for (Node node : network.getNodes().values()) {
			spatialIndex.put(node.getCoord().getX(), node.getCoord().getY(), node);
		}
	}

	public VehicleRoutingTransportCosts getRoutingCosts() {
		if (needsUpdate) {
			List<Node> nodeList = new ArrayList<>(relevantNodes);
			SpeedyALTFactory routerFactory = new SpeedyALTFactory();

			// TODO: May also have more complex cost structure here
			TravelTime travelTime = new FreeSpeedTravelTime();
			LeastCostPathCalculator router = routerFactory.createPathCalculator(network,
					new OnlyTimeDependentTravelDisutility(travelTime), travelTime);

			VehicleRoutingTransportCostsMatrix.Builder builder = VehicleRoutingTransportCostsMatrix.Builder
					.newInstance(false);

			for (int i = 0; i < nodeList.size(); i++) {
				for (int j = 0; j < nodeList.size(); j++) {
					Node originNode = nodeList.get(i);
					Node destinationNode = nodeList.get(j);

					Path path = router.calcLeastCostPath(originNode, destinationNode, 0.0, null, null);

					builder.addTransportTime(originNode.getId().toString(), destinationNode.getId().toString(),
							path.travelTime);
					builder.addTransportDistance(originNode.getId().toString(), destinationNode.getId().toString(),
							RouteUtils.calcDistance(path));
				}
			}

			needsUpdate = false;
			costs = builder.build();
		}

		return costs;
	}

	public Location getLocation(LocationData data) {
		Coord coord = wgsToEuclidean.transform(new Coord(data.longitude, data.latitude));
		Node node = spatialIndex.getClosest(coord.getX(), coord.getY());
		relevantNodes.add(node);
		needsUpdate = true;
		return Location.newInstance(node.getId().toString());
	}

	public LocationData getLocationData(Location location) {
		Node node = network.getNodes().get(Id.createNodeId(location.getId()));
		Coord lonlat = euclideanToWgs.transform(new Coord(node.getCoord().getX(), node.getCoord().getY()));
		return LocationData.of(lonlat.getY(), lonlat.getX());
	}
}
