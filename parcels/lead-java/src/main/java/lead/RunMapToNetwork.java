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

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.network.io.MatsimNetworkReader;

public class RunMapToNetwork {
	static public void main(String[] args) throws IOException, ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "input-path", "output-path") //
				.build();

		String networkPath = cmd.getOptionStrict("network-path");
		String inputPath = cmd.getOptionStrict("input-path");
		String outputPath = cmd.getOptionStrict("output-path");

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkPath);

		Network roadNetwork = NetworkUtils.createNetwork();
		new TransportModeNetworkFilter(network).filter(roadNetwork, Collections.singleton("car"));

		String line = null;
		List<String> header = null;

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));

		writer.write(String.join(";", new String[] { //
				"location_id", "link_id" //
		}) + "\n");

		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(";"));

			if (header == null) {
				header = row;
			} else {
				String parcelId = row.get(header.indexOf("location_id"));

				Coord coord = new Coord( //
						Double.parseDouble(row.get(header.indexOf("x"))), //
						Double.parseDouble(row.get(header.indexOf("y"))) //
				);

				Link closestLink = NetworkUtils.getNearestLink(roadNetwork, coord);
				writer.write(String.join(";", new String[] { //
						parcelId, closestLink.getId().toString() //
				}) + "\n");
			}
		}

		reader.close();
		writer.close();
	}
}
