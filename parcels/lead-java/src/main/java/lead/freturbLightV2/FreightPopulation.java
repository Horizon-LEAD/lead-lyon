package lead.freturbLightV2;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FreightPopulation {

    /**
     * writes a MATSim populations file with the trips as input
     * @param allWeekDayTrips - a daily time distributed list with trips (direct and round)
     */
    static void generateMATSimFreightPopulation(List<Trips> allWeekDayTrips) {

        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        PopulationFactory pf = scenario.getPopulation().getFactory();
        Population population = scenario.getPopulation();

        Random random = new Random(233455);
        int personId = 0;

        for (Trips trip : allWeekDayTrips) {
            Person person = pf.createPerson(Id.createPersonId("truck_" + personId));
            PopulationUtils.putSubpopulation(person, "freight");
            Plan plan = pf.createPlan();
            if (trip instanceof DirectTrip) {
                DirectTrip directTrip = (DirectTrip) trip;
                Activity activity = pf.createActivityFromCoord("truck_operation", directTrip.startPoint.ownCoord);
                plan.addActivity(activity);
                Activity activity2 = pf.createActivityFromCoord("truck_operation", directTrip.entPoint.ownCoord);
                activity2.setStartTime(directTrip.timeSlot);
                plan.addActivity(activity2);
                Activity activity3 = pf.createActivityFromCoord("truck_operation", directTrip.startPoint.ownCoord);
                plan.addActivity(activity3);
            } else if (trip instanceof RoundTrip) {
                RoundTrip roundTrip = (RoundTrip) trip;
                Iterator<Integer> iterator = roundTrip.timeSlots.iterator();
                boolean first = true;
                for (Move move : roundTrip.tourWithOrder) {
                    if (move.equals(roundTrip.startMove)) {
                        Activity activity = pf.createActivityFromCoord("truck_operation", roundTrip.startMove.ownCoord);
                        if (first) {
                            first = false;
                            activity.setEndTime(0);
                            plan.addActivity(activity);
                            Leg leg = pf.createLeg("freight");
                            plan.addLeg(leg);
                        } else {
//                            activity.setEndTime(23*3600 + 59*60);
                            plan.addActivity(activity);
                        }
                    } else {
                        Activity activity2 = pf.createActivityFromCoord("truck_operation", move.ownCoord);
                        activity2.setEndTime(iterator.next());
                        plan.addActivity(activity2);
                        Leg leg = pf.createLeg("freight");
                        plan.addLeg(leg);
                    }
                }
            }
            personId++;
            person.addPlan(plan);
            population.addPerson(person);
        }

        new PopulationWriter(population).write("test.xml");
    }

}
