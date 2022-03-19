package lead.backend;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lead.backend.data.ScenarioData;
import lead.backend.data.StateData;
import lead.backend.data.optimization.SolutionData;

public class RunSolverWithNetwork {
	static public void main(String[] args) throws JsonParseException, JsonMappingException, IOException,
			ConfigurationException, NoSuchAuthorityCodeException, FactoryException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("data-path", "scenario", "output-path") //
				.build();

		File dataPath = new File(cmd.getOptionStrict("data-path"));
		File outputPath = new File(cmd.getOptionStrict("output-path"));
		String scenarioId = cmd.getOptionStrict("scenario");

		StateData data = new ObjectMapper().readValue(dataPath, StateData.class);
		DataManager dataManager = new DataManager(data);

		Network network = new SupersonicOsmNetworkReader.Builder() //
				.setCoordinateTransformation(new GeotoolsTransformation("EPSG:4326", data.crs)) //
				.build() //
				.read(new File(data.osmPath).toPath());
		new NetworkCleaner().run(network);

		new NetworkWriter(network).write("output_network.xml.gz");

		InfrastructureManager infrastructure = new InfrastructureManager(1.3, 5.55);

		ScenarioSolver solver = new ScenarioSolver(dataManager, infrastructure, 10000);

		ScenarioData scenario = Objects.requireNonNull(dataManager.getScenarios().get(scenarioId));
		SolutionData solution = solver.solve(scenario);
		new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValue(outputPath, solution);
	}
}
