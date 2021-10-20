package kpi;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.noise.MergeNoiseCSVFile;
import org.matsim.contrib.noise.NoiseConfigGroup;
import org.matsim.contrib.noise.NoiseOfflineCalculation;
import org.matsim.contrib.noise.ProcessNoiseImmissions;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class RunNoiseAnalysis {
	static public void main(String[] args) {
		Config config = ConfigUtils.createConfig();

		// config.global().setCoordinateSystem("EPSG:2154");

		config.network().setInputFile("output_network.xml.gz");
		// config.network().setInputCRS("EPSG:2154");
		config.vehicles().setVehiclesFile("output_vehicles.xml.gz");
		config.plans().setInputFile("output_plans.xml.gz");
		config.controler().setOutputDirectory("");

		NoiseConfigGroup noiseConfig = new NoiseConfigGroup();
		noiseConfig.setReceiverPointsCSVFileCoordinateSystem(TransformationFactory.ATLANTIS);
		noiseConfig.setReceiverPointsCSVFile("grid.csv");
		noiseConfig.setScaleFactor(10.0);
		config.addModule(noiseConfig);

		// noiseConfig.setReceiverPointGap(12345789.);

		Scenario scenario = ScenarioUtils.loadScenario(config);

		String outputDirectory = "noise_output/";
		NoiseOfflineCalculation noiseCalculation = new NoiseOfflineCalculation(scenario, "noise_output");
		noiseCalculation.run();

		String outputFilePath = outputDirectory + "noise-analysis/";
		ProcessNoiseImmissions process = new ProcessNoiseImmissions(outputFilePath + "immissions/",
				outputFilePath + "receiverPoints/receiverPoints.csv", noiseConfig.getReceiverPointGap());
		process.run();

		final String[] labels = { "immission", "consideredAgentUnits", "damages_receiverPoint" };
		final String[] workingDirectories = { outputFilePath + "/immissions/",
				outputFilePath + "/consideredAgentUnits/", outputFilePath + "/damages_receiverPoint/" };

		MergeNoiseCSVFile merger = new MergeNoiseCSVFile();
		merger.setReceiverPointsFile(outputFilePath + "receiverPoints/receiverPoints.csv");
		merger.setOutputDirectory(outputFilePath);
		merger.setTimeBinSize(noiseConfig.getTimeBinSizeNoiseComputation());
		merger.setWorkingDirectory(workingDirectories);
		merger.setLabel(labels);
		merger.run();
	}
}
