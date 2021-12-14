package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RunFreturbLightV2 {

    public static void main(String[] args) throws Exception {

//        String sireneFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockEtablissement_utf8.csv";
        String sireneFile = "D:/Praktikum/StockEtablissement_utf8.csv";
//        String filterFile = "C:/lead/Marc/Freturb_Light/Filter/lyons_coords.csv";
        String filterFile = "D:/Praktikum/lyons_coords.csv";
//        String sirenFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockUniteLegale_utf8.csv";
        String sirenFile = "D:/Praktikum/StockUniteLegale_utf8.csv";
        final Coord CENTER = new Coord(842443.74, 6519278.68);

        List<FirmDataV2> firms = ReadSireneFileV2.readFile(sireneFile);

        FilterFirmsV2.filter(firms, filterFile, sirenFile);

        CategorisationV2.categorise(firms);

//        CreateMovementV2.calculateMovements(firms);
        CreateMovementV2.calculateMovementsWithCorrection(firms);

        DistributionV2.distributeLogistics(firms, CENTER);

        double co = 0;
        for (Move firmDataV2 : Move.movementsList) {
            co += firmDataV2.travelDistance;
        }

        double[] kilometer = new double[8];
        double[] vehicel = new double[8];
        for (Move move : Move.movementsList) {
            if (move.st8 != 0) {
                double km = move.travelDistance;
                kilometer[move.st8 -1] = kilometer[move.st8-1] + km;
            }
            if (move.disVeh20.equals(DistributionV2.DistributionVehicleST20.VehicleST20.VUL) && move.st8 != 0) {
                vehicel[move.st8-1] = vehicel[move.st8 -1] + 1;
            }
        }
        System.out.println(Arrays.toString(kilometer));
        System.out.println(Arrays.toString(vehicel  ));

        System.out.println("Get direct Movements");
        List<Move> directMoveList = new ArrayList<>();
        for (Move move : Move.movementsList) {
            if (move.routeType.equals(Move.RouteType.direct)) {
                directMoveList.add(move);
            }
        }
        List<Move> movementsList = directMoveList;

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
        System.out.println(Arrays.toString(movementsFirm));
        System.out.println(Arrays.toString(amountFirm));
        System.out.println(Arrays.toString(employeesFirm));

        firms.clear();

//        List<CalculateRoutes.Trip> trips = CalculateRoutes.calculateDirectRouts(movementsList);
        List<CalculateRoutes.Trip> trips = CalculateRoutes.calculateDirectRoutsV2(movementsList);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("movements.txt"))) {
            for (Move trip : movementsList){
                writer.write("" + trip.ownCoord.getX() + ";" + trip.ownCoord.getY() + ";" + trip.travelDistance);
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double distance = 0;
        double cal = 0;
        int moves = trips.size();
        for (CalculateRoutes.Trip trip : trips) {
            cal += (trip.startPiont.travelDistance + trip.entpoint.travelDistance) / 2;
            distance += CoordUtils.calcEuclideanDistance(trip.startPiont.ownCoord, trip.entpoint.ownCoord) * 1.4 /1000;
        }
        System.out.println(moves);
        System.out.println("pairing:" + distance);
        System.out.println("calculated:" + cal);



        try (BufferedWriter writer = new BufferedWriter(new FileWriter("directMovements.txt"))) {
            writer.write("startX;startY;endX;endY;score;linestring");
            writer.newLine();
            for (CalculateRoutes.Trip trip : trips){
                writer.write("" + trip.startPiont.ownCoord.getX() + ";" + trip.startPiont.ownCoord.getY() + ";" + trip.entpoint.ownCoord.getX() + ";" + trip.entpoint.ownCoord.getY() + ";" + trip.score + ";LINESTRING (" + trip.startPiont.ownCoord.getX() + " " + trip.startPiont.ownCoord.getY() + ", " + trip.entpoint.ownCoord.getX() + " " + trip.entpoint.ownCoord.getY() + ")");
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("directMovements10pct.txt"))) {
            writer.write("startX;startY;endX;endY;score;linestring");
            writer.newLine();
            Random r = new Random(123);
            for (CalculateRoutes.Trip trip : trips){
                if (r.nextDouble() < 0.1) {
                    writer.write("" + trip.startPiont.ownCoord.getX() + ";" + trip.startPiont.ownCoord.getY() + ";" + trip.entpoint.ownCoord.getX() + ";" + trip.entpoint.ownCoord.getY() + ";" + trip.score + ";LINESTRING (" + trip.startPiont.ownCoord.getX() + " " + trip.startPiont.ownCoord.getY() + ", " + trip.entpoint.ownCoord.getX() + " " + trip.entpoint.ownCoord.getY() + ")");
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("directMovements1pct.txt"))) {
            writer.write("startX;startY;endX;endY;score;linestring;length");
            writer.newLine();
            Random r = new Random(123);
            for (CalculateRoutes.Trip trip : trips){
                if (r.nextDouble() < 0.01) {
                    writer.write("" + trip.startPiont.ownCoord.getX() + ";" + trip.startPiont.ownCoord.getY() + ";" + trip.entpoint.ownCoord.getX() + ";" + trip.entpoint.ownCoord.getY() + ";" + trip.score + ";LINESTRING (" + trip.startPiont.ownCoord.getX() + " " + trip.startPiont.ownCoord.getY() + ", " + trip.entpoint.ownCoord.getX() + " " + trip.entpoint.ownCoord.getY() + ")");
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        System.out.println("Done");

    }


}
