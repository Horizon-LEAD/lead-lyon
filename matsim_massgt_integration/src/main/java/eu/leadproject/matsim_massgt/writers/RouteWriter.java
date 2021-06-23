package eu.leadproject.matsim_massgt.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

import eu.leadproject.matsim_massgt.aggregation.RouteAggregator;
import eu.leadproject.matsim_massgt.aggregation.RouteAggregator.Route;

public class RouteWriter {
	private final RouteAggregator aggregator;

	public RouteWriter(RouteAggregator aggregator) {
		this.aggregator = aggregator;
	}

	public void write(String path) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path))));

		writer.write(String.join(";", new String[] { //
				"person_id", "trip_index", //
				"departure_time", "arrival_time", //
				"route" //
		}) + "\n");

		for (Route route : aggregator.getRoutes()) {
			writer.write(String.join(";", new String[] { //
					route.personId.toString(), String.valueOf(route.tripIndex), //
					String.valueOf(route.departureTime), String.valueOf(route.arrivalTime), //
					route.route.stream().map(String::valueOf).collect(Collectors.joining(":")) //
			}) + "\n");
		}

		writer.close();
	}
}
