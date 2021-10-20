package lead;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.speedy.SpeedyALTFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
import org.matsim.core.utils.misc.Time;

import lead.timing.RecordedTravelTime;

public class RunODLinkRouting {
	static public void main(String[] args) throws IOException, ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "input-path", "output-path") //
				.allowOptions("travel-times-path", "departure-time") //
				.build();

		String networkPath = cmd.getOptionStrict("network-path");
		String inputPath = cmd.getOptionStrict("input-path");
		String outputPath = cmd.getOptionStrict("output-path");
		double departureTime = cmd.getOption("departure-time").map(Double::parseDouble).orElse(8.5 * 3600.0);

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkPath);

		Network roadNetwork = NetworkUtils.createNetwork();
		new TransportModeNetworkFilter(network).filter(roadNetwork, Collections.singleton("car"));

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));

		List<Id<Link>> originIds = Arrays.asList(reader.readLine().split(";")).stream().map(Id::createLinkId)
				.collect(Collectors.toList());
		List<Id<Link>> destinationIds = Arrays.asList(reader.readLine().split(";")).stream().map(Id::createLinkId)
				.collect(Collectors.toList());

		reader.close();

		if (cmd.hasOption("travel-times-path")) {
			GZIPInputStream inputStream = new GZIPInputStream(
					new FileInputStream(cmd.getOptionStrict("travel-times-path")));
			RecordedTravelTime recordedTravelTime = RecordedTravelTime.readBinary(inputStream);

			for (Link link : roadNetwork.getLinks().values()) {
				link.setFreespeed(
						link.getLength() / recordedTravelTime.getLinkTravelTime(link, departureTime, null, null));
			}
		}

		TravelTime travelTime = new FreeSpeedTravelTime();
		LeastCostPathCalculator router = new SpeedyALTFactory().createPathCalculator(roadNetwork,
				new OnlyTimeDependentTravelDisutility(travelTime), travelTime);

		int totalCount = originIds.size() * destinationIds.size();
		int currentCount = 0;

		double startTime = System.nanoTime() * 1e-9;
		double nextTime = 0;

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));

		writer.write(String.join(";", new String[] { //
				"origin_id", "destination_id", "travel_time", "distance" //
		}) + "\n");

		for (Id<Link> originId : originIds) {
			for (Id<Link> destinationId : destinationIds) {
				Link originLink = roadNetwork.getLinks().get(originId);
				Link destinationLink = roadNetwork.getLinks().get(destinationId);

				Path path = router.calcLeastCostPath(originLink.getToNode(), destinationLink.getFromNode(),
						departureTime, null, null);

				double distance = RouteUtils.calcDistance(path);

				writer.write(String.join(";", new String[] { //
						originId.toString(), destinationId.toString(), //
						String.valueOf(path.travelTime), String.valueOf(distance) //
				}) + "\n");

				currentCount++;

				double currentTime = System.nanoTime() * 1e-9;

				if (currentTime > nextTime) {
					nextTime = currentTime + 5.0;

					double secondsPerRoute = (currentTime - startTime) / currentCount;
					double remainingTime = secondsPerRoute * (totalCount - currentCount);

					System.out.println(String.format("Progress %d/%d (ETA %s)", currentCount, totalCount,
							Time.writeTime(remainingTime)));
				}
			}
		}

		writer.close();
	}
}
