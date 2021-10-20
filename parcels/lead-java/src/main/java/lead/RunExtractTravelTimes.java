package lead;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.zip.GZIPOutputStream;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.network.io.MatsimNetworkReader;

import lead.timing.RecordedTravelTime;

public class RunExtractTravelTimes {
	static public void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "events-path", "output-path") //
				.allowOptions("start-time", "end-time", "interval") //
				.build();

		String networkPath = cmd.getOptionStrict("network-path");
		String eventsPath = cmd.getOptionStrict("events-path");

		double startTime = cmd.getOption("start-time").map(Double::parseDouble).orElse(5.0 * 3600.0);
		double endTime = cmd.getOption("end-time").map(Double::parseDouble).orElse(23.0 * 3600.0);
		double interval = cmd.getOption("interval").map(Double::parseDouble).orElse(3600.0);

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(networkPath);

		Network roadNetwork = NetworkUtils.createNetwork();
		new TransportModeNetworkFilter(network).filter(roadNetwork, Collections.singleton("car"));

		RecordedTravelTime travelTime = RecordedTravelTime.readFromEvents(new File(eventsPath), roadNetwork, startTime,
				endTime, interval);

		GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(cmd.getOptionStrict("output-path")));
		RecordedTravelTime.writeBinary(outputStream, travelTime);
		outputStream.close();
	}
}
