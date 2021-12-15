package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
        final List<Coord> CENTERS = Arrays.asList(new Coord(844819.280, 6517939.271), new Coord(913487.627, 6458394.690), new Coord(808804.412, 6484085.296), new Coord(783594.005, 6550352.652), new Coord(872190.579, 6569800.681));

        List<FirmDataV2> firms = ReadSireneFileV2.readFile(sireneFile);
        FilterFirmsV2.filter(firms, filterFile, sirenFile);
        CategorisationV2.categorise(firms);
        CreateMovementV2.calculateMovementsWithCorrection(firms);
        DistributionV2.distributeLogistics(firms, CENTERS);

        System.out.println("Get direct Movements");
        List<Move> directMoveList = new ArrayList<>();
        for (Move move : Move.movementsList) {
            if (move.routeType.equals(Move.RouteType.direct)) {
                directMoveList.add(move);
            }
        }
        List<Move> movementsList = directMoveList;
        firms.clear();
        List<CalculateRoutes.Trip> bestTrips = CalculateRoutes.calculateBestDirectRoutsV2(movementsList);
        List<CalculateRoutes.Trip> trips = CalculateRoutes.calculateDirectRoutsV2(movementsList);

        for (CalculateRoutes.Trip trip : trips) {
            for (CalculateRoutes.Trip bestTrip : bestTrips) {
                if (bestTrip.startPiont.id == trip.startPiont.id) {
                    trip.bestScore = bestTrip.bestScore;
                }
            }
        }
        bestTrips.clear();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("directMovementsMultipleCenters.txt"))) {
            writer.write("startX;startY;endX;endY;score;bestScore;startId;endId;linestring");
            writer.newLine();
            for (CalculateRoutes.Trip trip : trips){
                writer.write("" + trip.startPiont.ownCoord.getX() + ";" + trip.startPiont.ownCoord.getY() + ";" + trip.entpoint.ownCoord.getX() + ";" + trip.entpoint.ownCoord.getY() + ";" + trip.score + ";" + trip.bestScore + ";" + trip.startPiont.centerId + ";" + trip.entpoint.centerId + ";LINESTRING (" + trip.startPiont.ownCoord.getX() + " " + trip.startPiont.ownCoord.getY() + ", " + trip.entpoint.ownCoord.getX() + " " + trip.entpoint.ownCoord.getY() + ")");
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
