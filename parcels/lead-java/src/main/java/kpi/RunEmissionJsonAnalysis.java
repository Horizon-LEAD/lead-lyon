package kpi;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer.GridType;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

public class RunEmissionJsonAnalysis {
	static public void main(String[] args) {
		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile("output_network.xml.gz");

		SimpleFeature analysisFeature = ShapeFileReader.getAllFeatures("analysis_area.shp").iterator().next();
		Geometry analysisGeometry = (Geometry) analysisFeature.getDefaultGeometry();

		new EmissionGridAnalyzer.Builder() //
				.withBounds(analysisGeometry) //
				.withNetwork(network) //
				.withCountScaleFactor(10.0) //
				.withGridSize(25) //
				.withSmoothingRadius(50) //
				.withTimeBinSize(3600) //
				.withGridType(GridType.Square) //
				.build() //
				.processToJsonFile("emissions_events.xml.gz", "emissions.json");
	}
}
