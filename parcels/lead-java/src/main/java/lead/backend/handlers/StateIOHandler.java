package lead.backend.handlers;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import lead.backend.data.StateData;

public class StateIOHandler implements Handler {
	private final File statePath;

	public StateIOHandler(File statePath) {
		this.statePath = statePath;
	}

	@Override
	public void handle(Context ctx) throws Exception {
		if (ctx.method().equals("GET")) {
			StateData data = new ObjectMapper().readValue(statePath, StateData.class);
			ctx.json(data);
		} else if (ctx.method().equals("PUT")) {
			StateData data = new ObjectMapper().readValue(ctx.bodyAsInputStream(), StateData.class);
			new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValue(statePath, data);
		} else {
			throw new IllegalStateException(ctx.method());
		}
	}
}
