package lead.backend;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lead.backend.data.ScenarioData;
import lead.backend.data.StateData;
import lead.backend.data.optimization.SolutionData;

public class RunSolver {
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

		InfrastructureManager infrastructure = new InfrastructureManager(1.6, 10.0 / 3.6);

		ScenarioSolver solver = new ScenarioSolver(dataManager, infrastructure, 10000);

		ScenarioData scenario = Objects.requireNonNull(dataManager.getScenarios().get(scenarioId));
		SolutionData solution = solver.solve(scenario);
		new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValue(outputPath, solution);
	}
}
