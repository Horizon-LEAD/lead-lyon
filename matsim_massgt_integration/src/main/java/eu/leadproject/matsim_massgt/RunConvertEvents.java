package eu.leadproject.matsim_massgt;

import java.io.IOException;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import eu.leadproject.matsim_massgt.aggregation.RouteAggregator;
import eu.leadproject.matsim_massgt.aggregation.TravelTimeAggregator;
import eu.leadproject.matsim_massgt.writers.NetworkWriter;
import eu.leadproject.matsim_massgt.writers.RouteWriter;

public class RunConvertEvents {
	static public void main(String[] args) throws ConfigurationException, IOException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "events-path", "output-routes-path", "output-network-path", "crs") //
				.build();

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(cmd.getOptionStrict("network-path"));

		RouteAggregator routeAggregator = new RouteAggregator();
		TravelTimeAggregator travelTimeAggregator = new TravelTimeAggregator();

		EventsManager eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(routeAggregator);
		eventsManager.addHandler(travelTimeAggregator);

		eventsManager.initProcessing();
		new MatsimEventsReader(eventsManager).readFile(cmd.getOptionStrict("events-path"));
		eventsManager.finishProcessing();

		new RouteWriter(routeAggregator).write(cmd.getOptionStrict("output-routes-path"));
		new NetworkWriter(network, travelTimeAggregator, cmd.getOptionStrict("crs"))
				.write(cmd.getOptionStrict("output-network-path"));
	}
}
