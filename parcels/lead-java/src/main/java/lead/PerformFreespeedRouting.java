package lead;

import java.io.IOException;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class PerformFreespeedRouting {
	static public void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "demand-path", "center-path") //
				.build();

		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(cmd.getOptionStrict("network-path"));

	}

	public static class DemandItem {
		public String operator;
		public String center_id;
		public double x;
		public double y;
	}

	public static class CenterItem {
		public String center_id;
		public double x;
		public double y;
	}

}
