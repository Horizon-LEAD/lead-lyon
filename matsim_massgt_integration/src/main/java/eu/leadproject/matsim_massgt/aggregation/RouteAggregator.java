package eu.leadproject.matsim_massgt.aggregation;

import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

public class RouteAggregator implements PersonDepartureEventHandler, PersonArrivalEventHandler, LinkEnterEventHandler {
	private final List<Route> routes = new LinkedList<>();
	private final IdMap<Person, Route> currentRoute = new IdMap<>(Person.class);

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		if (event.getLegMode().equals("car")) {
			int tripIndex = 0;

			if (currentRoute.containsKey(event.getPersonId())) {
				tripIndex = currentRoute.get(event.getPersonId()).tripIndex + 1;
			}

			Route route = new Route(event.getPersonId(), tripIndex, event.getTime());
			currentRoute.put(event.getPersonId(), route);
			routes.add(route);
		}
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		Id<Person> personId = Id.create(event.getVehicleId(), Person.class);
		Route route = currentRoute.get(personId);

		if (route != null) {
			route.route.add(event.getLinkId());
		}
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		Route route = currentRoute.get(event.getPersonId());

		if (route != null) {
			route.arrivalTime = event.getTime();
		}
	}

	public List<Route> getRoutes() {
		return routes;
	}

	static public class Route {
		final public Id<Person> personId;
		final public int tripIndex;

		final public double departureTime;
		public double arrivalTime = Double.NaN;

		final public List<Id<Link>> route = new LinkedList<>();

		Route(Id<Person> personId, int tripIndex, double departureTime) {
			this.personId = personId;
			this.tripIndex = tripIndex;
			this.departureTime = departureTime;
		}
	}
}
