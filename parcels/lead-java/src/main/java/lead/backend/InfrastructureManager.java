package lead.backend;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import lead.backend.data.LocationData;

public class InfrastructureManager {
	private final double distanceFactor;
	private final double speed;

	private VehicleRoutingTransportCosts costs;
	private boolean needsUpdate = true;

	private final Map<String, Location> locations = new TreeMap<>();
	private final Map<String, LocationData> locationData = new TreeMap<>();

	private final GeodeticCalculator calculator;

	public InfrastructureManager(double distanceFactor, double speed)
			throws NoSuchAuthorityCodeException, FactoryException {
		this.distanceFactor = distanceFactor;
		this.speed = speed;
		this.calculator = new GeodeticCalculator(DefaultGeographicCRS.WGS84);
	}

	public Location getLocation(String locationId, LocationData data) {
		if (locations.containsKey(locationId)) {
			return locations.get(locationId);
		} else {
			Location location = Location.newInstance(locationId);
			locations.put(locationId, location);
			locationData.put(locationId, data);
			needsUpdate = true;
			return location;
		}
	}

	public LocationData getLocationData(Location location) {
		return Objects.requireNonNull(locationData.get(location.getId()));
	}

	public VehicleRoutingTransportCosts getRoutingCosts() {
		if (needsUpdate) {
			VehicleRoutingTransportCostsMatrix.Builder builder = VehicleRoutingTransportCostsMatrix.Builder
					.newInstance(false);

			for (Location origin : locations.values()) {
				LocationData originData = locationData.get(origin.getId());

				for (Location destination : locations.values()) {
					LocationData destinationData = locationData.get(destination.getId());

					calculator.setStartingGeographicPoint(originData.longitude, originData.latitude);
					calculator.setDestinationGeographicPoint(destinationData.longitude, destinationData.latitude);

					double distance = calculator.getOrthodromicDistance() * distanceFactor;
					double travelTime = distance / speed;

					builder.addTransportTime(origin.getId(), destination.getId(), travelTime);
					builder.addTransportDistance(origin.getId(), destination.getId(), distance);
				}
			}

			needsUpdate = false;
			costs = builder.build();
		}

		return costs;
	}
}
