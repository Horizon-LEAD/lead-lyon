package lead;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.IdSet;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;

public class RunFilterNetwork {
	static public void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "filter-path", "output-network-path", "output-links-path") //
				.build();

		String networkPath = cmd.getOptionStrict("network-path");
		String filterPath = cmd.getOptionStrict("filter-path");
		String outputNetworkPath = cmd.getOptionStrict("output-network-path");
		String outputLinksPath = cmd.getOptionStrict("output-links-path");

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkPath);

		Network roadNetwork = NetworkUtils.createNetwork();
		new TransportModeNetworkFilter(network).filter(roadNetwork, Collections.singleton("car"));

		List<Geometry> geometries = ShpGeometryUtils.loadGeometries(new File(filterPath).toURI().toURL());
		IdSet<Link> removeIds = new IdSet<>(Link.class);
		IdSet<Link> validIds = new IdSet<>(Link.class);

		for (Link link : roadNetwork.getLinks().values()) {
			if (!ShpGeometryUtils.isCoordInGeometries(link.getCoord(), geometries)) {
				// removeIds.add(link.getId());
				link.setFreespeed(1e-6);
			} else {
				validIds.add(link.getId());
			}
		}

		// removeIds.forEach(roadNetwork::removeLink);

		// new NetworkCleaner().run(roadNetwork);

		new NetworkWriter(roadNetwork).write(outputNetworkPath);

		BufferedWriter writer = IOUtils.getBufferedWriter(outputLinksPath);
		writer.write(validIds.stream().map(id -> id.toString()).collect(Collectors.joining(";")));
		writer.close();
	}
}
