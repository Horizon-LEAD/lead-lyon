package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CalculateRoundRoutes {

    static Random random = new Random(44555);

    public static List<RoundTrip> calculateRoundRoutes(List<Move> movementlist) {
        Collections.shuffle(movementlist, random);
        List<Move> plVehicle = new ArrayList<>();
        List<Move> vulVehicle = new ArrayList<>();
        for (Move move : movementlist) {
            if (move.disVeh20.equals(DistributionV2.DistributionVehicleST20.VehicleST20.PL)) {
                plVehicle.add(move);
            } else {
                vulVehicle.add(move);
            }
        }
        List<RoundTrip> roundTrips = new ArrayList<>();
        System.out.println("Start PL vehicle round trips");
        buildTrip(plVehicle, roundTrips);
        System.out.println("Start VUL vehicle round trips");
        buildTrip(vulVehicle, roundTrips);

        System.out.println("Start building Trips");
        int x = 0;
        for (RoundTrip roundTrip : roundTrips) {
            roundTrip.buildTrip();
        }
        Collections.sort(roundTrips);
        return roundTrips;
    }

    private static List<RoundTrip> buildTrip(List<Move> vehicleList, List<RoundTrip> roundTrips) {
        int amountRouts = vehicleList.size() / 12;
        System.out.println("" + amountRouts + " Routes are created");
        for (int i = 0; i < amountRouts; i++) {
            Move startMove = vehicleList.get(random.nextInt(vehicleList.size()));
            vehicleList.remove(startMove);
            int index = 1;
            RoundTrip roundTrip = new RoundTrip();
            roundTrip.addStop(startMove);
            List<Coord> visited = new ArrayList<>();
            visited.add(startMove.ownCoord);
            while (index < 12) {
                Move endMove = null;
                double bestScore = Double.MAX_VALUE;
                for (Move move : vehicleList) {
                    double tpmSCore = scoreConnection(startMove, move);
                    if (tpmSCore < bestScore && !visited.contains(move.ownCoord)) {
                        bestScore = tpmSCore;
                        endMove = move;
                    }
                }
                if (endMove != null) {
                    visited.add(endMove.ownCoord);
                    roundTrip.addScore(bestScore);
                    roundTrip.addStop(endMove);
                    vehicleList.remove(endMove);
                }
                index++;
            }
            if (roundTrip.tourPoints.size() == 12) {
                roundTrips.add(roundTrip);
            }
            if (i > 1 && i % 1000 == 0) {
                System.out.println("" + i + " Routes created");
            }
        }
        return roundTrips;
    }

    static double scoreConnection(Move startMove, Move endMove) {
        if (startMove.ownCoord.equals(endMove.ownCoord)) {
            return Double.MAX_VALUE;
        }
        double distance = (CoordUtils.calcEuclideanDistance(startMove.ownCoord, endMove.ownCoord)/1000) * 1.4;
        double fistMoveDistance = startMove.travelDistance - distance;
        double possibleMoveDistance = endMove.travelDistance - distance;
        return Math.abs(fistMoveDistance) + Math.abs(possibleMoveDistance);
    }

}
