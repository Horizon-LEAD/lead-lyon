package eu.leadproject.matsim_massgt.writers;

import java.util.Collection;
import java.util.LinkedList;

import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.PolylineFeatureFactory;
import org.matsim.core.utils.gis.ShapeFileWriter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.leadproject.matsim_massgt.aggregation.TravelTimeAggregator;

public class NetworkWriter {
	private final Network network;
	private final TravelTimeAggregator travelTimeAggregator;
	private final String crs;

	public NetworkWriter(Network network, TravelTimeAggregator travelTimeAggregator, String crs) {
		this.network = network;
		this.travelTimeAggregator = travelTimeAggregator;
		this.crs = crs;
	}

	public void write(String path) {
		CoordinateReferenceSystem crs = MGC.getCRS(this.crs);
		Collection<SimpleFeature> features = new LinkedList<>();

		PolylineFeatureFactory linkFactory = new PolylineFeatureFactory.Builder() //
				.setCrs(crs).setName("link") //
				.addAttribute("link", String.class) //
				.addAttribute("travtime", Double.class) //
				.addAttribute("freespeed", Double.class) //
				.addAttribute("length", Double.class) //
				.create();

		IdMap<Link, Double> travelTimes = travelTimeAggregator.getTravelTimes(network);

		for (Link link : network.getLinks().values()) {
			if (link.getAllowedModes().contains("car")) {
				Coordinate fromCoordinate = new Coordinate(link.getFromNode().getCoord().getX(),
						link.getFromNode().getCoord().getY());
				Coordinate toCoordinate = new Coordinate(link.getToNode().getCoord().getX(),
						link.getToNode().getCoord().getY());

				SimpleFeature feature = linkFactory.createPolyline( //
						new Coordinate[] { fromCoordinate, toCoordinate }, //
						new Object[] { //
								link.getId().toString(), //
								travelTimes.get(link.getId()), //
								link.getFreespeed(), //
								link.getLength() //
						}, null);

				features.add(feature);
			}
		}

		ShapeFileWriter.writeGeometries(features, path);
	}
}