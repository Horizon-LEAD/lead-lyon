package lead.parcelPopulation;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.FacilitiesWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FrightPopulation {

    private static Population freightPopulation;
    private static ActivityFacilities facilities;


    public static void main(String[] args) throws IOException {

        Config config = ConfigUtils.createConfig();
        config.network().setInputFile("C:/lead/Marc/lyon_merged_10pct_5i/output_network.xml.gz");
        createNewPlanAndFacilityFile(ScenarioUtils.loadScenario(config), "C:/vrp");
        new PopulationWriter(freightPopulation).write("frightPlans.xml");
        new FacilitiesWriter(facilities).write("freightFacilities.xml");
    }

    public static Scenario createNewPlanAndFacilityFile(Scenario scenario, String folder) throws IOException {
        Map<String, FreightAgent> freightAgents = new HashMap();
        Path dir = Paths.get(folder);
//        Path dir = Paths.get("C:/lead/lyon_parcel_demand/vrp");

        freightPopulation = scenario.getPopulation();
        facilities = scenario.getActivityFacilities();

        Files.walk(dir).forEach(path -> {readFiles(path.toFile(), freightAgents);});
        FrightPopulation.buildPopulation(freightAgents, scenario);
        return scenario;
    }

    /**
     *
     * creates and writes a plan file for the freight deliveries
     * creates and writes a facility file for the depots and the deliveries
     *
     * @param freightAgents - filled map with all vehicles and all there deliveries
     *                      key - vehicle id, composed of vehicle id and centre id
     *                      value - freight agent object
     * @param scenario - scenario with plans and facilities which are to be combined
     */
    private static void buildPopulation(Map<String, FreightAgent> freightAgents, Scenario scenario) {

        PopulationFactory factory = freightPopulation.getFactory();
        ActivityFacilitiesFactory facilityFactory = facilities.getFactory();

        Network network = scenario.getNetwork();

        int j = 0;
        int i = 1;

        for (FreightAgent freightAgent : freightAgents.values()) {            Person freightPerson = factory.createPerson(Id.createPersonId(freightAgent.id));
            Plan plan = factory.createPlan();

            ActivityFacility firstFacility = facilityFactory.createActivityFacility(Id.create("freight_loading_unloading" + "_" + i++, ActivityFacility.class), Id.createLinkId(freightAgent.startLocation));
            firstFacility.addActivityOption(facilityFactory.createActivityOption("freight_loading_unloading"));
            firstFacility.setCoord(network.getLinks().get(Id.createLinkId(freightAgent.startLocation)).getCoord());

            Activity firstActivity = factory.createActivityFromLinkId("freight_loading", Id.createLinkId(freightAgent.startLocation));
            firstActivity.setEndTime(freightAgent.time.get(0));
            firstActivity.setFacilityId(firstActivity.getFacilityId());
            firstActivity.setCoord(network.getLinks().get(Id.createLinkId(freightAgent.startLocation)).getCoord());
            facilities.addActivityFacility(firstFacility);
            plan.addActivity(firstActivity);

            Leg firstLeg = factory.createLeg("freight");
            plan.addLeg(firstLeg);

            int x = 1;
            for (String parcelDeliveries : freightAgent.deliveryLocations) {
                ActivityFacility facility = facilityFactory.createActivityFacility(Id.create("freight_delivery" + "_" + j++, ActivityFacility.class), Id.createLinkId(parcelDeliveries));
                facility.addActivityOption(facilityFactory.createActivityOption("freight_delivery"));
                facility.setCoord(network.getLinks().get(Id.createLinkId(parcelDeliveries)).getCoord());
                facilities.addActivityFacility(facility);

                Activity activity = factory.createActivityFromLinkId("freight_delivery", Id.createLinkId(parcelDeliveries));
                activity.setEndTime(freightAgent.time.get(x++));
                activity.setFacilityId(facility.getId());
                activity.setCoord(network.getLinks().get(Id.createLinkId(parcelDeliveries)).getCoord());
                plan.addActivity(activity);

                Leg leg = factory.createLeg("freight");
                plan.addLeg(leg);
            }

            Activity lastActivity = factory.createActivityFromLinkId("freight_loading", Id.createLinkId(freightAgent.startLocation));
            lastActivity.setFacilityId(firstActivity.getFacilityId());
            lastActivity.setCoord(network.getLinks().get(Id.createLinkId(freightAgent.startLocation)).getCoord());
            plan.addActivity(lastActivity);

            freightPerson.addPlan(plan);
            PopulationUtils.putSubpopulation(freightPerson, "freight");
            freightPopulation.addPerson(freightPerson);
        }

    }

    /**
     *
     * reads all solutions-files from jsprit
     *
     * @param file - path to the folder with the files
     * @param freightAgents - empty map with all vehicles and all there deliveries
     *                      key - vehicle id, composed of vehicle id and centre id
     *                      value - freight agent object
     */
    private static void readFiles(File file, Map<String, FreightAgent> freightAgents) {

        if (file.getAbsolutePath().contains("solution")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

                String line = null;
                List<String> header = null;
                String[] fileName = file.getName().split("_|\\.");
                String centerId = fileName[1];

                while ((line = br.readLine()) != null) {
                    List<String> row = Arrays.asList(line.split(";"));
                    if (header == null) {
                        header = row;
                    } else {

                        String vehicleId = row.get(header.indexOf("vehicle_id"));
                        String[] vehicle = vehicleId.split("_");
                        String fAId = centerId + "_" + vehicle[1];

                        if (freightAgents.containsKey(fAId)) {
                            FreightAgent freightAgent = freightAgents.get(fAId);
                            String kindOfAction = row.get(header.indexOf("name"));

                            if (kindOfAction.equals("deliverShipment")) {
                                freightAgent.time.add(Double.parseDouble(row.get(header.indexOf("arrival_time"))));
                                freightAgent.deliveryLocations.add(row.get(header.indexOf("location_id")));
                            } else if (kindOfAction.equals("end")) {
                                freightAgent.time.add(Double.parseDouble(row.get(header.indexOf("arrival_time"))));
                                freightAgent.startLocation = row.get(header.indexOf("location_id"));
                            }

                        } else {
                            freightAgents.put(fAId, new FreightAgent(fAId));
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *helping class to save information
     */
    static class FreightAgent {

        final String id;
        String startLocation;

        // time in seconds
        List<Double> time = new ArrayList<>();;
        List<String> deliveryLocations = new ArrayList<>();

        FreightAgent(String id) {
            this.id = id;
        }
    }

}