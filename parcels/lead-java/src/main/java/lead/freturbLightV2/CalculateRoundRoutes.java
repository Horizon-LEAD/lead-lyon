package lead.freturbLightV2;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CalculateRoundRoutes {

    private static Random random = new Random(44555);
    private static PoissonDistribution poissonDistribution = new PoissonDistribution(12);
    static List<Double> scoreList = new ArrayList<>();

    /**
     * creates round trips from individual movements
     * @param movementList - a list of round movements
     * @return - a list of round trips
     */
    static List<RoundTrip> calculateRoundRoutes(List<Move> movementList) {
        Collections.shuffle(movementList, random);
        List<Move> plVehicle = new ArrayList<>();
        List<Move> vulVehicle = new ArrayList<>();
        for (Move move : movementList) {
            if (move.disVeh20.equals(DistributionV2.DistributionVehicleST20.VehicleST20.PL)) {
                plVehicle.add(move);
            } else {
                vulVehicle.add(move);
            }
        }
        List<RoundTrip> roundTrips = new ArrayList<>();
        System.out.println("Start PL vehicle round trips");
        buildTripV2(plVehicle, roundTrips);
        System.out.println("Start VUL vehicle round trips");
        buildTripV2(vulVehicle, roundTrips);

        System.out.println("Start building Trips");
        for (RoundTrip roundTrip : roundTrips) {
            roundTrip.buildTrip();
        }
        Collections.sort(roundTrips);
        return roundTrips;
    }

    private static List<RoundTrip> buildTripV2(List<Move> vehicleList, List<RoundTrip> roundTrips) {
        int amountRouts = vehicleList.size() / 12;
        System.out.println("" + amountRouts + " Routes are created");
        int roundsCreated = 0;
        while (!vehicleList.isEmpty() && vehicleList.size() > 2) {
            int tmpTripSize = poissonDistribution.sample();
            if (tmpTripSize < 3){
                continue;
            }
            if (tmpTripSize > vehicleList.size()) {
                tmpTripSize = vehicleList.size();
            }
            Move startMove = vehicleList.get(random.nextInt(vehicleList.size()));
            vehicleList.remove(startMove);
            int index = 1;
            RoundTrip roundTrip = new RoundTrip();
            roundTrip.addStop(startMove);
            List<Coord> visited = new ArrayList<>();
            visited.add(startMove.ownCoord);
            while (index < tmpTripSize - 1) {
                Move endMove = null;
                double bestScore = Double.MAX_VALUE;
                int stopTimeWaste = 0;
                for (Move possibleMove : vehicleList) {
                    if (!visited.contains(possibleMove.ownCoord)) {
                        double tmpScore = scoreConnection(startMove, possibleMove);
                        if (tmpScore < bestScore) {
                            bestScore = tmpScore;
                            endMove = possibleMove;
                        }
                    }
                    stopTimeWaste++;
                    if (stopTimeWaste == 1000) {
//                        continue;
                    }
                }
                if (endMove != null) {
                    visited.add(endMove.ownCoord);
                    roundTrip.addScore(bestScore);
                    roundTrip.addStop(endMove);
                    vehicleList.remove(endMove);
                    startMove = endMove;
                }
                index++;
            }
//            Move move = findBestBetweenTwo(roundTrip.tourPoints.get(roundTrip.tourPoints.size() - 1), roundTrip.tourPoints.get(0), vehicleList);
            Move bestMove = null;
            double bestScore = Double.MAX_VALUE;
            for (Move possibleMove : vehicleList) {
                if (!visited.contains(possibleMove.ownCoord)) {
                    double firstWay = scoreConnection(roundTrip.tourPoints.get(roundTrip.tourPoints.size() - 1), possibleMove);
                    double secondWay = scoreConnection(possibleMove, roundTrip.tourPoints.get(0));
                    if (bestScore > firstWay + secondWay) {
                        bestMove = possibleMove;
                        bestScore = firstWay + secondWay;
                    }
                }
            }
            if (bestMove != null) {
                roundTrip.addStop(bestMove);
                roundTrip.addScore(bestScore);
                vehicleList.remove(bestMove);
            }
            if (roundTrip.tourPoints.size() == tmpTripSize) {
                scoreList.add(roundTrip.score);
                roundsCreated++;
                roundTrips.add(roundTrip);
            } else {
                System.out.println("Round trips stops should be the same as the pulled trips stops: " + roundTrip.tourPoints.size() + "; " + tmpTripSize);
            }
            if (roundsCreated > 1 && roundsCreated % 1000 == 0) {
                System.out.println("" + roundsCreated + " Routes created");
            }
        }
        System.out.println("" + roundsCreated + " were created in the end");
        return roundTrips;
    }

    private static Move findBestBetweenTwo(Move endPoint, Move startPoint, List<Move> possibleMoves) {
        Move bestMove = null;
        double bestScore = Double.MAX_VALUE;
        for (Move possibleMove : possibleMoves) {
            double firstWay = scoreConnection(endPoint, possibleMove);
            double secondWay = scoreConnection(possibleMove, startPoint);
            if (bestScore > firstWay + secondWay) {
                bestMove = possibleMove;
                bestScore = firstWay + secondWay;
            }
        }
        return bestMove;
    }

    @Deprecated // contains a bug (start move never gets overwritten)
    private static List<RoundTrip> buildTrip(List<Move> vehicleList, List<RoundTrip> roundTrips) {
        int amountRouts = vehicleList.size() / 12;
//        int amountRouts = poissonDistribution.sample();
        System.out.println("" + amountRouts + " Routes are created");
        int roundsCreated = 0;
        while (!vehicleList.isEmpty()) {
            int tmpTripSize = poissonDistribution.sample();
            if (tmpTripSize < 3){
                continue;
            }
            if (tmpTripSize < vehicleList.size()) {
                Move startMove = vehicleList.get(random.nextInt(vehicleList.size()));
                vehicleList.remove(startMove);
                int index = 1;
                RoundTrip roundTrip = new RoundTrip();
                roundTrip.addStop(startMove);
                List<Coord> visited = new ArrayList<>();
                visited.add(startMove.ownCoord);
                while (index < tmpTripSize) {
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
                        startMove = endMove;
                    }
                    index++;
                }
                if (roundTrip.tourPoints.size() == tmpTripSize) {
                    scoreList.add(roundTrip.score);
                    roundTrips.add(roundTrip);
                } else {
                    System.out.println("mhhh     " + roundTrip.tourPoints.size() + "          " + tmpTripSize);
                }
                if (roundsCreated > 1 && roundsCreated % 1000 == 0) {
                    System.out.println("" + roundsCreated + " Routes created");
                }

            }else {
                Move startMove = vehicleList.get(random.nextInt(vehicleList.size()));
                vehicleList.remove(startMove);
                int index = 1;
                RoundTrip roundTrip = new RoundTrip();
                roundTrip.addStop(startMove);
                List<Coord> visited = new ArrayList<>();
                visited.add(startMove.ownCoord);
                while (index < tmpTripSize) {
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
                if (roundTrip.tourPoints.size() == tmpTripSize) {
                    scoreList.add(roundTrip.score);
                    roundTrips.add(roundTrip);
                } else {
                    System.out.println("mhhh     " + roundTrip.tourPoints.size());
                }
                if (roundsCreated > 1 && roundsCreated % 1000 == 0) {
                    System.out.println("" + roundsCreated + " Routes created");
                }
            }

            roundsCreated++;
        }
        System.out.println("" + roundsCreated + " were created in the end");
        return roundTrips;
    }

    static double scoreConnection(Move startMove, Move endMove) {
        if (startMove.ownCoord.equals(endMove.ownCoord)) {
            return Double.MAX_VALUE;
        }
        double distance = (CoordUtils.calcEuclideanDistance(startMove.ownCoord, endMove.ownCoord)/1000) * 1.4;
        double fistMoveDistance = startMove.travelDistance - distance;
        return Math.abs(fistMoveDistance);
//        return Math.abs(((startMove.travelDistance + endMove.travelDistance)/2) - distance);
    }

}
