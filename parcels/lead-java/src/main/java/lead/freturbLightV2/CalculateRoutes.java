package lead.freturbLightV2;

import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;

public class CalculateRoutes {

    public static List<Trip> calculateDirectRoutsV2(List<Move> movementsList) {
        int movementsSize = movementsList.size();
        System.out.println(movementsSize + " direct Movements getting paired");
        Random rnd = new Random(31464645);
        List<Trip> tripsList = new ArrayList<>();
        int size = movementsList.size();
        for (int i = 1; i < size; i++) {
            Trip trip = getPair(movementsList, rnd);
            if (trip != null) {
                tripsList.add(trip);
            }
            if (i % 10000 == 0) {
                System.out.println("Done " + i);
            }
        }
        System.out.println("No Match for " + (movementsSize-(tripsList.size() * 2)) + " movements");
        Collections.sort(tripsList);
        System.out.println(tripsList.get(0));
        System.out.println(tripsList.get(tripsList.size() -1 ));
        return tripsList;
    }

    private static Trip getPair(List<Move> movementsList, Random rnd) {
        if (movementsList.size() == 0) {
            return null;
        }
        Move firstMove = movementsList.get(rnd.nextInt(movementsList.size()));
        Move bestFit = null;
        double minScore = Double.MAX_VALUE;
        Trip trip = null;
        for (Move move : movementsList) {
            double score = scoreConnection(firstMove, move);
            if (minScore > score) {
                minScore = score;
                bestFit = move;
            }
        }
        if (minScore != Double.MAX_VALUE) {
            movementsList.remove(bestFit);
            movementsList.remove(firstMove);
            trip = new Trip(firstMove, bestFit, minScore);
        }
        return trip;
    }

    private static double scoreConnection(Move firstMove, Move possibleMove) {
        if (!firstMove.logisticMatch.contains(possibleMove.logisticType) || firstMove.id == possibleMove.id) {
            return Double.MAX_VALUE;
        }
        double distance = (CoordUtils.calcEuclideanDistance(firstMove.ownCoord, possibleMove.ownCoord)/1000) * 1.4;
        double fistMoveDistance = firstMove.travelDistance - distance;
        double possibleMoveDistance = possibleMove.travelDistance - distance;
        return Math.abs(fistMoveDistance) + Math.abs(possibleMoveDistance);
    }

    public static List<Trip> calculateBestDirectRoutsV2(List<Move> movementsList) {
        int movementsSize = movementsList.size();
        System.out.println(movementsSize + " best direct Movements getting paired");
        List<Trip> tripsList = new ArrayList<>();
        for (Move startMove : movementsList) {
            double minScore = Double.MAX_VALUE;
            Move bestFit = null;
            for (Move possibleMove : movementsList) {
                double score = scoreConnection(startMove, possibleMove);
                if (minScore > score) {
                    minScore = score;
                    bestFit = possibleMove;
                }
            }
            Trip trip = new Trip(startMove, bestFit);
            trip.bestScore = minScore;
            tripsList.add(trip);
        }
        return tripsList;
    }

    static class Trip implements Comparable{
        Move startPiont;
        Move entpoint;
        double score;
        double bestScore;

        public Trip(Move startPiont, Move entpoint, double score) {
            this.startPiont = startPiont;
            this.entpoint = entpoint;
            this.score = score;
        }

        public Trip(Move startPiont, Move entpoint) {
            this.startPiont = startPiont;
            this.entpoint = entpoint;
            this.score = score;
        }

        @Override
        public int compareTo(Object o) {
            return Double.compare(this.score, ((Trip) o).score);
        }

        @Override
        public String toString() {
            return "Score: " + score;
        }
    }

}
