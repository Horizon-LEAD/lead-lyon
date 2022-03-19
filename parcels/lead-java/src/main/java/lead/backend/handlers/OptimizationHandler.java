package lead.backend.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.opengis.referencing.FactoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpCode;
import lead.backend.DataManager;
import lead.backend.InfrastructureManager;
import lead.backend.ScenarioSolver;
import lead.backend.ScenarioSolver.ProgressObserver;
import lead.backend.data.ScenarioData;
import lead.backend.data.StateData;
import lead.backend.data.optimization.OptimizationStatusData;
import lead.backend.data.optimization.OptimizationStatusData.StatusType;
import lead.backend.data.optimization.SolutionData;

public class OptimizationHandler implements Handler {
	private final Map<String, Thread> threads = new HashMap<>();
	private final Map<String, Optimization> optimizations = new HashMap<>();

	@Override
	public void handle(Context ctx) throws Exception {
		String subject = ctx.pathParam("subject");
		String scenario = ctx.pathParam("scenario");

		if (threads.containsKey(scenario)) {
			Thread thread = threads.get(scenario);

			if (!thread.isAlive()) {
				threads.remove(scenario);
			}
		}

		if (subject.equals("start") && ctx.method().equals("POST")) {
			StateData stateData = new ObjectMapper().readValue(ctx.bodyAsInputStream(), StateData.class);
			Optimization optimization = optimizations.get(scenario);

			if (optimization == null || optimization.finished()) {
				optimization = new Optimization(stateData, scenario);
				optimizations.put(scenario, optimization);

				Thread thread = new Thread(optimization);
				threads.put(scenario, thread);

				thread.start();
				ctx.status(HttpCode.OK);
				return;
			} else {
				ctx.status(HttpCode.BAD_REQUEST);
				return;
			}
		}

		if (subject.equals("abort")) {
			Optimization optimization = optimizations.get(scenario);

			if (optimization != null && !optimization.finished()) {
				Thread thread = threads.get(scenario);

				if (thread != null) {
					thread.interrupt();
					threads.remove(scenario);
				}

				optimizations.remove(scenario);

				ctx.status(HttpCode.OK);
				return;
			} else {
				ctx.status(HttpCode.BAD_REQUEST);
				return;
			}
		}

		if (subject.equals("solution")) {
			Optimization optimization = optimizations.get(scenario);

			if (optimization != null && optimization.finished()) {
				ctx.json(optimization.solutionData);
				ctx.status(HttpCode.OK);
				return;
			} else {
				ctx.status(HttpCode.BAD_REQUEST);
				return;
			}
		}

		if (subject.equals("status")) {
			OptimizationStatusData status = new OptimizationStatusData();

			if (optimizations.containsKey(scenario)) {
				Optimization optimization = optimizations.get(scenario);
				status.status = optimization.finished() ? StatusType.finished : StatusType.running;
				status.progess = optimization.progress;
			}

			ctx.json(status);
			return;
		}

		throw new IllegalStateException(subject);
	}

	static private class Optimization implements Runnable {
		private final StateData stateData;
		private final String scenarioId;

		private SolutionData solutionData;
		private double progress = 0.0;

		Optimization(StateData stateData, String scenarioId) {
			this.stateData = stateData;
			this.scenarioId = scenarioId;
		}

		@Override
		public void run() {
			try {
				DataManager dataManager = new DataManager(stateData);
				InfrastructureManager infrastructure = new InfrastructureManager(1.3, 5.55);

				ScenarioSolver solver = new ScenarioSolver(dataManager, infrastructure, 10000);

				ScenarioData scenario = Objects.requireNonNull(dataManager.getScenarios().get(scenarioId));
				solutionData = solver.solve(scenario, new Observer(this));
			} catch (FactoryException | IOException e) {
				e.printStackTrace();
			}
		}

		boolean finished() {
			return solutionData != null;
		}
	}

	static private class Observer implements ProgressObserver {
		private Optimization optimization;

		Observer(Optimization optimization) {
			this.optimization = optimization;
		}

		@Override
		public void update(int currentProblems, int totalProblems, int currentIterations, int totalIterations) {
			optimization.progress = (double) currentIterations / (double) totalIterations;
		}
	}
}
