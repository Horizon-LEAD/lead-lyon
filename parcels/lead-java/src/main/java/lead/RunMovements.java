package lead;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.common.base.Verify;

public class RunMovements {
	static public void main(String[] args) throws NumberFormatException, IOException {
		String networkPath = args[0];
		String nodesPath = args[1];
		String activitiesPath = args[2];

		Config config = ConfigUtils.createConfig();

		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory("movements_output");
		config.controler().setLastIteration(0);

		Scenario scenario = ScenarioUtils.createScenario(config);
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkPath);

		scenario.getNetwork().getLinks().values().forEach(link -> {
			link.setFreespeed(2.7);
		});

		// Find locations

		Map<String, Link> locations = new HashMap<>();

		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(nodesPath)));

			String line = null;

			List<String> header = null;
			List<String> row = null;

			while ((line = reader.readLine()) != null) {
				row = Arrays.asList(line.split(";"));

				if (header == null) {
					header = row;
				} else {
					String locationId = row.get(header.indexOf("location_id"));
					double x = Double.parseDouble(row.get(header.indexOf("x")));
					double y = Double.parseDouble(row.get(header.indexOf("y")));

					locations.put(locationId, NetworkUtils.getNearestLink(scenario.getNetwork(), new Coord(x, y)));
				}
			}

			reader.close();
		}

		// Read activities

		Map<String, Person> persons = new HashMap<>();
		PopulationFactory factory = scenario.getPopulation().getFactory();

		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(activitiesPath)));

			String line = null;

			List<String> header = null;
			List<String> row = null;

			while ((line = reader.readLine()) != null) {
				row = Arrays.asList(line.split(";"));

				if (header == null) {
					header = row;
				} else {
					String vehicleId = row.get(header.indexOf("vehicle_id"));
					String locationId = row.get(header.indexOf("location_id"));
					String activityName = row.get(header.indexOf("name"));
					double endTime = Double.parseDouble(row.get(header.indexOf("end_time")));

					if (!persons.containsKey(vehicleId)) {
						Person person = factory.createPerson(Id.createPersonId(vehicleId));
						scenario.getPopulation().addPerson(person);

						Plan plan = factory.createPlan();
						person.addPlan(plan);

						persons.put(vehicleId, person);
					}

					Link location = locations.get(locationId);
					Verify.verifyNotNull(location);

					Plan plan = persons.get(vehicleId).getSelectedPlan();

					if (plan.getPlanElements().size() > 0) {
						Leg leg = factory.createLeg("car");
						plan.addLeg(leg);
					}

					Activity nextActivity = factory.createActivityFromLinkId(activityName + "_" + vehicleId,
							location.getId());
					nextActivity.setCoord(location.getCoord());
					nextActivity.setEndTime(endTime);
					plan.addActivity(nextActivity);
				}
			}

			reader.close();
		}

		new PopulationWriter(scenario.getPopulation()).write("movement_population.xml");

		for (int i = 0; i < 4; i++) {
			{
				ActivityParams params = new ActivityParams("start_vehicle_" + i);
				params.setScoringThisActivityAtAll(false);
				config.planCalcScore().addActivityParams(params);
			}

			{
				ActivityParams params = new ActivityParams("end_vehicle_" + i);
				params.setScoringThisActivityAtAll(false);
				config.planCalcScore().addActivityParams(params);
			}

			{
				ActivityParams params = new ActivityParams("pickupShipment_vehicle_" + i);
				params.setScoringThisActivityAtAll(false);
				config.planCalcScore().addActivityParams(params);
			}

			{
				ActivityParams params = new ActivityParams("deliverShipment_vehicle_" + i);
				params.setScoringThisActivityAtAll(false);
				config.planCalcScore().addActivityParams(params);
			}
		}
		new Controler(scenario).run();
	}
}
