package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunFreturbLightV2 {

    public static void main(String[] args) throws Exception {

        String sireneFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockEtablissement_utf8.csv";
        String filterFile = "C:/lead/Marc/Freturb_Light/Filter/lyons_coords.csv";
        String sirenFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockUniteLegale_utf8.csv";

//        String sireneFile = "D:/Praktikum/StockEtablissement_utf8.csv";
//        String filterFile = "D:/Praktikum/lyons_coords.csv";
//        String sirenFile = "D:/Praktikum/StockUniteLegale_utf8.csv";

        // if only one center then set to true
        boolean oneCenter = false;
        final List<Coord> CENTERS = Arrays.asList(new Coord(844819.280, 6517939.271), new Coord(913487.627, 6458394.690), new Coord(808804.412, 6484085.296), new Coord(783594.005, 6550352.652), new Coord(872190.579, 6569800.681));

        // reads in the sirene file and also filters the location, at the moment the filter file is hard coded and must be changed also in the class
        List<FirmDataV2> firms = ReadSireneFileV2.readFile(sireneFile);
        // filters after jurisdiction and shell companies
        FilterFirmsV2.filter(firms, filterFile, sirenFile);
        // categories the establishments in st8 and st20
        CategorisationV2.categorise(firms);

        // calculates the amount movements for each establishment, averaging of movements based on St8 classes
//        CreateMovementV2.calculateMovements(firms);
        // corrects the amount of movements so movements per employee ar similar to idf
        CreateMovementV2.calculateMovementsWithCorrection(firms);

        // distributes the logistic type
        DistributionV2.distributeLogistics(firms, CENTERS, oneCenter);

        double co = 0;
        for (Move firmDataV2 : Move.movementsList) {
            co += firmDataV2.travelDistance;
        }
        System.out.println("Total distance travelled: " + co + "km");

        double[] disQuest = new double[3];
        for (Move move : Move.movementsList) {
            if (move.disMan.equals(DistributionV2.DistributionManagement.Management.CPD)){
                disQuest[0] = disQuest[0] + 1;
            } else if (move.disMan.equals(DistributionV2.DistributionManagement.Management.CPE)) {
                disQuest[1] = disQuest[1] + 1;
            } else {
                disQuest[2] = disQuest[2] + 1;
            }
        }
        System.out.println("distribution question (CPD, CPE, CA)"  + Arrays.toString(disQuest));

        double[] kilometer = new double[8];
        for (Move move : Move.movementsList) {
            if (move.st8 != 0) {
                double km = move.travelDistance;
                kilometer[move.st8 -1] = kilometer[move.st8-1] + km;
            }
        }
        System.out.println("driven kilometer per class"  + Arrays.toString(kilometer));

        System.out.println("Get round Movements");
        List<Move> roundMoveList = new ArrayList<>();
        for (Move move : Move.movementsList) {
            if (move.routeType.equals(Move.RouteType.round)) {
                roundMoveList.add(move);
            }
        }
        // calculates round trips
        List<RoundTrip> roundTrips = CalculateRoundRoutes.calculateRoundRoutes(roundMoveList);
        System.out.println("Done");
        System.out.println("Get direct Movements");
        List<Move> directMoveList = new ArrayList<>();
        for (Move move : Move.movementsList) {
            if (move.routeType.equals(Move.RouteType.direct)) {
                directMoveList.add(move);
            }
        }
        // calculates direct trips
        List<DirectTrip> directTrips = CalculateRoutes.findBetterSolutions(directMoveList);
        System.out.println("Done");

        double co1 = 0;
        for (Move firmDataV2 : directMoveList) {
            co1 += firmDataV2.travelDistance;
        }
        System.out.println("Total direct distance travelled: " + co1 + "km");
        double co2 = 0;
        for (Move firmDataV2 : roundMoveList) {
            co2 += firmDataV2.travelDistance;
        }
        System.out.println("Total round distance travelled: " + co2 + "km");
        double co3 = 0;
        for (Move firmDataV2 : Move.movementsList) {
            if (firmDataV2.routeType.equals(Move.RouteType.direct)) {
                co3 += firmDataV2.travelDistance;
            }
        }
        System.out.println("Total direct travelled: " + co3 + "km");
        double stops = 0;
        for (RoundTrip roundTrip : roundTrips) {
            stops += roundTrip.tourWithOrder.size();
        }
        double avgStrops = stops/roundTrips.size();
        System.out.println("avg. Stops: " + avgStrops);

        int[] amountFirm = new int[8];
        int[] employeesFirm = new int[8];
        double[] movementsFirm = new double[8];
        for (FirmDataV2 firmData : firms) {
            if (firmData.st8 != 0) {
                int a = firmData.st8 - 1;
                int af = amountFirm[a] + 1;
                int ef = employeesFirm[a] + firmData.employees;
                double mf = movementsFirm[a] + firmData.movements;
                amountFirm[a] = af;
                employeesFirm[a] = ef;
                movementsFirm[a] = mf;
            }

        }
        System.out.println("Movements for st8 classes " + Arrays.toString(movementsFirm));
        System.out.println("Amount of establishment for st8 classes " + Arrays.toString(amountFirm));
        System.out.println("Employees for st8 classes" + Arrays.toString(employeesFirm));
        System.out.println("Done");
        firms.clear();

        System.out.println("Start day and time distribution");
        // splits the weekly information into daily information
        List<Trips> allTrips = DayAndTimeDistribution.generateDistribution(directTrips, roundTrips);
        // generates a MATSim population file from the trips
        FreightPopulation.generateMATSimFreightPopulation(allTrips);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scoreOverNumberDirect.txt"))) {
            writer.write("score");
            writer.newLine();
            for (Double score : CalculateRoutes.scoreList){
                writer.write(""+score);
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scoreOverNumberRound.txt"))) {
            writer.write("score");
            writer.newLine();
            for (Double score : CalculateRoundRoutes.scoreList){
                writer.write(""+score);
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        }
}
