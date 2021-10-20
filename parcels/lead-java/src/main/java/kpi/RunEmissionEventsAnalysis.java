package kpi;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.EmissionUtils;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer.GridType;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup.HbefaRoadTypeSource;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriter;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.utils.objectattributes.attributable.Attributes;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.Vehicles;
import org.opengis.feature.simple.SimpleFeature;

public class RunEmissionEventsAnalysis {
	static public void main(String[] args) {
		Config config = ConfigUtils.createConfig();

		config.network().setInputFile("output_network.xml.gz");
		config.vehicles().setVehiclesFile("output_vehicles.xml.gz");

		EmissionsConfigGroup emissionsConfig = new EmissionsConfigGroup();
		config.addModule(emissionsConfig);

		// emissionsConfig.setHbefaRoadTypeSource(HbefaRoadTypeSource.fromOsm);
		emissionsConfig.setHbefaRoadTypeSource(HbefaRoadTypeSource.fromLinkAttributes);
		emissionsConfig.setDetailedVsAverageLookupBehavior(
				EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
		emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.abort);

		emissionsConfig.setAverageColdEmissionFactorsFile("sample_41_EFA_ColdStart_vehcat_2020average.txt");
		emissionsConfig.setAverageWarmEmissionFactorsFile("sample_41_EFA_HOT_vehcat_2020average.txt");

		Scenario scenario = ScenarioUtils.loadScenario(config);
		EventsManager eventsManager = EventsUtils.createEventsManager();

		Vehicles vehicles = scenario.getVehicles();

		VehicleType averageType = vehicles.getVehicleTypes().values().iterator().next();
		Attributes vehicleTypeAttributes = averageType.getEngineInformation().getAttributes();

		vehicleTypeAttributes.putAttribute("HbefaVehicleCategory", "PASSENGER_CAR");
		vehicleTypeAttributes.putAttribute("HbefaTechnology", "average");
		vehicleTypeAttributes.putAttribute("HbefaSizeClass", "average");
		vehicleTypeAttributes.putAttribute("HbefaEmissionsConcept", "average");

		for (Link link : scenario.getNetwork().getLinks().values()) {
			EmissionUtils.setHbefaRoadType(link, "URB/Local/50");
		}

		com.google.inject.Injector injector = Injector.createInjector(config, new AbstractModule() {
			@Override
			public void install() {
				bind(Scenario.class).toInstance(scenario);
				bind(EventsManager.class).toInstance(eventsManager);
				bind(EmissionModule.class);
			}
		});

		injector.getInstance(EmissionModule.class);

		EventWriter eventWriter = new EventWriterXML("emissions_events.xml.gz");
		eventsManager.addHandler(eventWriter);

		eventsManager.initProcessing();
		new MatsimEventsReader(eventsManager).readFile("output_events.xml.gz");
		eventsManager.finishProcessing();

		eventWriter.closeFile();

		SimpleFeature analysisFeature = ShapeFileReader.getAllFeatures("analysis_area.shp").iterator().next();
		Geometry analysisGeometry = (Geometry) analysisFeature.getDefaultGeometry();

		/*new EmissionGridAnalyzer.Builder() //
				.withBounds(analysisGeometry) //
				.withNetwork(scenario.getNetwork()) //
				.withCountScaleFactor(1e3) //
				.withGridSize(50) //
				.withSmoothingRadius(100) //
				.withTimeBinSize(3600) //
				.withGridType(GridType.Hexagonal) //
				.build() //
				.processToJsonFile("emissions_events.xml.gz", "emissions.json");*/

		// TODO EmissionGridAnalyzer.Builder() //
		// .build();
	}
}
