package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunFreturbLightClean {

    public static void main(String[] args) throws Exception {

        String sireneFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockEtablissement_utf8.csv";
//        String sireneFile = "D:/Praktikum/StockEtablissement_utf8.csv";
        String filterFile = "C:/lead/Marc/Freturb_Light/Filter/lyons_coords.csv";
//        String filterFile = "D:/Praktikum/lyons_coords.csv";
        String sirenFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockUniteLegale_utf8.csv";
//        String sirenFile = "D:/Praktikum/StockUniteLegale_utf8.csv";
        final Coord CENTER = new Coord(842443.74, 6519278.68);
        boolean oneCenter = false;
        final List<Coord> CENTERS = Arrays.asList(new Coord(844819.280, 6517939.271), new Coord(913487.627, 6458394.690), new Coord(808804.412, 6484085.296), new Coord(783594.005, 6550352.652), new Coord(872190.579, 6569800.681));

        List<FirmDataV2> firms = ReadSireneFileV2.readFile(sireneFile);
        FilterFirmsV2.filter(firms, filterFile, sirenFile);
        CategorisationV2.categorise(firms);
//        CreateMovementV2.calculateMovementsWithCorrection(firms);
        DistributionV2.distributeLogistics(firms, CENTERS, oneCenter);

        System.out.println("Get round Movements");
        List<Move> roundMoveList = new ArrayList<>();
        for (Move move : Move.movementsList) {
            if (move.routeType.equals(Move.RouteType.round)) {
                roundMoveList.add(move);
            }
        }

        List<RoundTrip> roundTrips = CalculateRoundRoutes.calculateRoundRoutes(roundMoveList);
        System.out.println("Done");

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("roundMovementsMultipleCenters1pct.txt"))) {
//            writer.write("startX;startY;score;linestring");
//            writer.newLine();
//            Random random = new Random();
//
//            for (RoundTrip trip : roundTrips) {
//                if (random.nextDouble() > .99) {
//                    writer.write(trip.toString());
//                    writer.newLine();
//                    writer.flush();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("roundMovementsMultipleCenters.txt"))) {
//            writer.write("startX;startY;score;linestring");
//            writer.newLine();
//            Random random = new Random();
//            for (RoundTrip trip : roundTrips) {
//                writer.write(trip.toString());
//                writer.newLine();
//                writer.flush();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("Get direct Movements");
        List<Move> directMoveList = new ArrayList<>();
        for (Move move : Move.movementsList) {
            if (move.routeType.equals(Move.RouteType.direct)) {
                directMoveList.add(move);
            }
        }

        List<Move> movementsList = directMoveList;
        firms.clear();

//        List<CalculateRoutes.Trip> bestTrips = CalculateRoutes.calculateBestDirectRoutsV2(movementsList);
//        List<CalculateRoutes.Trip> trips = CalculateRoutes.calculateDirectRoutsV2(movementsList);
        List<DirectTrip> directTrips = CalculateRoutes.findBetterSolutions(movementsList);

//        for (CalculateRoutes.Trip trip : trips) {
//            for (CalculateRoutes.Trip bestTrip : bestTrips) {
//                if (bestTrip.startPoint.id == trip.startPoint.id) {
//                    trip.bestScore = bestTrip.bestScore;
//                }
//            }
//        }
//        bestTrips.clear();
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("directMovementsMultipleCenters.txt"))) {
//            writer.write("startX;startY;endX;endY;score;bestScore;distanceReal;distanceTheoretically;startId;endId;linestring");
//            writer.newLine();
//            for (CalculateRoutes.Trip trip : trips){
//                writer.write("" + trip.startPoint.ownCoord.getX() + ";" + trip.startPoint.ownCoord.getY() + ";" + trip.entPoint.ownCoord.getX() + ";" + trip.entPoint.ownCoord.getY() + ";" + trip.score + ";" + trip.bestScore + ";" + trip.distanceReal + ";" + trip.distanceTheoretically + ";" + trip.startPoint.centerId + ";" + trip.entPoint.centerId + ";LINESTRING (" + trip.startPoint.ownCoord.getX() + " " + trip.startPoint.ownCoord.getY() + ", " + trip.entPoint.ownCoord.getX() + " " + trip.entPoint.ownCoord.getY() + ")");
//                writer.newLine();
//                writer.flush();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("Start day and time distribution");
        List<Trips> allTrips = DayAndTimeDistribution.generateDistribution(directTrips, roundTrips);
        FreightPopulation.generateMATSimFreightPopulation(allTrips);

    }
}
