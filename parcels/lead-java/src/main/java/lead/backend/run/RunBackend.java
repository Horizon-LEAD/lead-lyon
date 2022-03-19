package lead.backend.run;

import java.io.File;
import java.io.IOException;

import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.javalin.Javalin;
import lead.backend.data.LocationData;
import lead.backend.data.StateData;
import lead.backend.handlers.OptimizationHandler;
import lead.backend.handlers.StateIOHandler;

public class RunBackend {
	public static void main(String[] args)
			throws ConfigurationException, JsonParseException, JsonMappingException, IOException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("state-path") //
				.allowOptions("port") //
				.build();

		File statePath = new File(cmd.getOptionStrict("state-path"));

		int port = cmd.getOption("port").map(Integer::parseInt).orElse(9000);

		Javalin app = Javalin.create(config -> {
			config.enableCorsForAllOrigins();
		}).start(port);
		app.get("/state", new StateIOHandler(statePath));
		app.put("/state", new StateIOHandler(statePath));

		OptimizationHandler optimizationHandler = new OptimizationHandler();
		app.get("/optimization/{scenario}/{subject}", optimizationHandler);
		app.post("/optimization/{scenario}/{subject}", optimizationHandler);
	}

	static private StateData createDefaultState() {
		StateData state = new StateData();

		state.project.center = LocationData.of(45.73841707992852, 4.82402801513672);
		state.project.zoom = 13.0;

		return state;
	}
}
