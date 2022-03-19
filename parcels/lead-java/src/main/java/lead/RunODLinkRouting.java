package lead;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.IdSet;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.speedy.SpeedyALTFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
import org.matsim.core.utils.misc.Time;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;

import com.google.common.util.concurrent.AtomicDouble;

import lead.timing.RecordedTravelTime;

public class RunODLinkRouting {
	static public void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "input-path", "output-path") //
				.allowOptions("travel-times-path", "departure-time", "threads", "area-path", "track-routes") //
				.build();

		String networkPath = cmd.getOptionStrict("network-path");
		String inputPath = cmd.getOptionStrict("input-path");
		String outputPath = cmd.getOptionStrict("output-path");
		double departureTime = cmd.getOption("departure-time").map(Double::parseDouble).orElse(8.5 * 3600.0);
		int numberOfThreads = cmd.getOption("threads").map(s -> Integer.parseInt(s))
				.orElse(Runtime.getRuntime().availableProcessors());
		boolean trackRoutes = cmd.getOption("track-routes").map(s -> Boolean.parseBoolean(s)).orElse(false);

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

		final Function<Link, Boolean> isInArea;

		if (cmd.hasOption("area-path")) {
			List<Geometry> geometries = ShpGeometryUtils
					.loadGeometries(new File(cmd.getOptionStrict("area-path")).toURI().toURL());

			GeometryFactory geometryFactory = new GeometryFactory();
			Geometry geometry = geometries.get(0);

			IdSet<Link> areaLinks = new IdSet<>(Link.class);

			for (Link link : network.getLinks().values()) {
				if (geometry.covers(
						geometryFactory.createPoint(new Coordinate(link.getCoord().getX(), link.getCoord().getY())))) {
					areaLinks.add(link.getId());
				}
			}

			isInArea = link -> areaLinks.contains(link.getId());
		} else {
			isInArea = link -> true;
		}

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
		LeastCostPathCalculatorFactory routerFactory = new SpeedyALTFactory();

		int totalCount = originIds.size() * destinationIds.size();
		AtomicInteger currentCount = new AtomicInteger();

		double startTime = System.nanoTime() * 1e-9;
		AtomicDouble nextTime = new AtomicDouble();

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));

		List<String> header = Arrays.asList("origin_id", "destination_id", "travel_time", "distance", "area_distance");

		if (trackRoutes) {
			header = new ArrayList<>(header);
			header.add("route");
		}

		writer.write(String.join(";", header) + "\n");

		List<Thread> threads = new LinkedList<>();

		for (int i = 0; i < numberOfThreads; i++) {
			threads.add(new Thread(() -> {
				LeastCostPathCalculator router = routerFactory.createPathCalculator(roadNetwork,
						new OnlyTimeDependentTravelDisutility(travelTime), travelTime);

				while (true) {
					Id<Link> originId = null;

					synchronized (originIds) {
						if (originIds.size() > 0) {
							originId = originIds.remove(0);
						} else {
							return;
						}
					}

					List<Double> travelTimes = new LinkedList<>();
					List<Double> distances = new LinkedList<>();
					List<Double> areaDistances = new LinkedList<>();
					List<IdSet<Link>> routes = new LinkedList<>();

					for (Id<Link> destinationId : destinationIds) {
						Link originLink = roadNetwork.getLinks().get(originId);
						Link destinationLink = roadNetwork.getLinks().get(destinationId);

						if (originLink != destinationLink) {
							Path path = router.calcLeastCostPath(originLink.getToNode(), destinationLink.getToNode(),
									departureTime, null, null);

							double pathDistance = 0.0;
							double areaDistance = 0.0;
							IdSet<Link> route = new IdSet<>(Link.class);

							for (Link link : path.links) {
								pathDistance += link.getLength();
								route.add(link.getId());

								if (isInArea.apply(link)) {
									areaDistance += link.getLength();
								}
							}

							distances.add(pathDistance);
							areaDistances.add(areaDistance);
							travelTimes.add(path.travelTime);
							routes.add(route);
						} else {
							distances.add(0.0);
							areaDistances.add(0.0);
							travelTimes.add(0.0);
							routes.add(new IdSet<>(Link.class));
						}

						synchronized (currentCount) {
							currentCount.incrementAndGet();

							double currentTime = System.nanoTime() * 1e-9;

							if (currentTime > nextTime.get()) {
								nextTime.set(currentTime + 5.0);

								double secondsPerRoute = (currentTime - startTime) / currentCount.get();
								double remainingTime = secondsPerRoute * (totalCount - currentCount.get());

								System.out.println(String.format("Progress %d/%d (ETA %s)", currentCount.get(),
										totalCount, Time.writeTime(remainingTime)));
							}
						}
					}

					synchronized (writer) {
						for (int k = 0; k < destinationIds.size(); k++) {
							Id<Link> destinationId = destinationIds.get(k);

							try {
								List<String> columns = Arrays.asList( //
										originId.toString(), destinationId.toString(), //
										String.valueOf(travelTimes.get(k)), String.valueOf(distances.get(k)),
										String.valueOf(areaDistances.get(k)) //
								);

								if (trackRoutes) {
									columns = new ArrayList<>(columns);
									columns.add(routes.get(k).stream().map(id -> id.toString())
											.collect(Collectors.joining(",")));
								}

								writer.write(String.join(";", columns) + "\n");
							} catch (IOException e) {
								throw new IllegalSelectorException();
							}
						}
					}
				}
			}));
		}

		threads.forEach(t -> t.start());

		for (Thread thread : threads) {
			thread.join();
		}

		writer.close();
	}
}
