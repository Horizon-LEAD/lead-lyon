package eu.leadproject.matsim_massgt.aggregation;

import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

public class TravelTimeAggregator
		implements LinkEnterEventHandler, LinkLeaveEventHandler, VehicleLeavesTrafficEventHandler {
	private final IdMap<Link, Double> enterTimes = new IdMap<>(Link.class);

	private final IdMap<Link, Double> cumulativeTravelTimes = new IdMap<>(Link.class);
	private final IdMap<Link, Integer> numberOfTraversals = new IdMap<>(Link.class);

	@Override
	public void handleEvent(LinkEnterEvent event) {
		enterTimes.put(event.getLinkId(), event.getTime());
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		Double enterTime = enterTimes.remove(event.getLinkId());

		if (enterTime != null) {
			cumulativeTravelTimes.put(event.getLinkId(),
					cumulativeTravelTimes.getOrDefault(event.getLinkId(), 0.0) + event.getTime() - enterTime);
			numberOfTraversals.put(event.getLinkId(), numberOfTraversals.getOrDefault(event.getLinkId(), 0) + 1);
		}
	}

	@Override
	public void handleEvent(VehicleLeavesTrafficEvent event) {
		enterTimes.remove(event.getLinkId());
	}

	public IdMap<Link, Double> getTravelTimes(Network network) {
		IdMap<Link, Double> result = new IdMap<>(Link.class);

		for (Link link : network.getLinks().values()) {
			double cumulativeTravelTime = cumulativeTravelTimes.getOrDefault(link.getId(),
					link.getLength() / link.getFreespeed());
			int traversals = numberOfTraversals.getOrDefault(link.getId(), 1);
			result.put(link.getId(), cumulativeTravelTime / (double) traversals);
		}

		return result;
	}
}
