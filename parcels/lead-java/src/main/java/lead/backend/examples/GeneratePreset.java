package lead.backend.examples;

import java.io.File;
import java.io.IOException;

import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lead.backend.data.FlowData;
import lead.backend.data.LocationData;
import lead.backend.data.ReceiverData;
import lead.backend.data.ScenarioData;
import lead.backend.data.SenderData;
import lead.backend.data.ShipmentType;
import lead.backend.data.StateData;
import lead.backend.data.UCCData;
import lead.backend.data.VehicleTypeData;

public class GeneratePreset {
	static public void main(String[] args)
			throws ConfigurationException, JsonGenerationException, JsonMappingException, IOException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("output-path") //
				.build();

		StateData data = new StateData();

		VehicleTypeData vanType = new VehicleTypeData();
		vanType.id = "van";
		vanType.capacity = 15;
		vanType.costPerKm = 0.15;
		vanType.costPerDay = 85.0;
		vanType.co2PerKm = 132.0;
		data.vehicleTypes.add(vanType);

		VehicleTypeData bikeType = new VehicleTypeData();
		bikeType.id = "cargobike";
		bikeType.capacity = 5;
		bikeType.costPerKm = 0.05;
		bikeType.costPerDay = 60.0;
		bikeType.co2PerKm = 0.0;
		data.vehicleTypes.add(bikeType);

		ReceiverData receiver = new ReceiverData();
		receiver.id = "receiverA";
		receiver.vehicleTypeIds.add(vanType.id);
		receiver.demand.add(LocationData.of(841292, 6517484));
		receiver.demand.add(LocationData.of(841318, 6517116));
		receiver.demand.add(LocationData.of(841571, 6517257));
		receiver.demand.add(LocationData.of(841292, 6517014));
		receiver.demand.add(LocationData.of(841657, 6517289));
		receiver.demand.add(LocationData.of(841118, 6517119));
		receiver.demand.add(LocationData.of(841591, 6517182));
		receiver.demand.add(LocationData.of(841625, 6517064));
		receiver.demand.add(LocationData.of(841625, 6517064));
		receiver.demand.add(LocationData.of(841314, 6517086));
		data.receivers.add(receiver);

		SenderData sender = new SenderData();
		sender.id = "senderA";
		sender.vehicleTypeIds.add(vanType.id);
		sender.location = LocationData.of(842004.0, 6516363.0);
		data.senders.add(sender);

		UCCData ucc = new UCCData();
		ucc.id = "uccA";
		ucc.vehicleTypeIds.add(bikeType.id);
		ucc.location = LocationData.of(841704.0, 6517432.0);
		data.uccs.add(ucc);

		{
			ScenarioData baselineScenario = new ScenarioData();
			baselineScenario.id = "baseline";
			data.scenarios.add(baselineScenario);

			FlowData flow = new FlowData();
			flow.receiverId = receiver.id;
			flow.senderId = sender.id;
			flow.shipmentType = ShipmentType.pickup;
			baselineScenario.flows.add(flow);
		}

		{
			ScenarioData uccScenario = new ScenarioData();
			uccScenario.id = "ucc";
			data.scenarios.add(uccScenario);

			FlowData flow = new FlowData();
			flow.receiverId = receiver.id;
			flow.senderId = sender.id;
			flow.shipmentType = ShipmentType.pickup;
			flow.uccId = ucc.id;
			flow.consolidationType = ShipmentType.delivery;
			uccScenario.flows.add(flow);
		}

		{
			ScenarioData deliveryScenario = new ScenarioData();
			deliveryScenario.id = "delivery";
			data.scenarios.add(deliveryScenario);

			FlowData flow = new FlowData();
			flow.receiverId = receiver.id;
			flow.senderId = sender.id;
			flow.shipmentType = ShipmentType.delivery;
			flow.uccId = ucc.id;
			flow.consolidationType = ShipmentType.delivery;
			deliveryScenario.flows.add(flow);
		}

		data.osmPath = "scenario.osm.pbf";
		data.crs = "EPSG:2154";

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(cmd.getOptionStrict("output-path")), data);
	}
}
