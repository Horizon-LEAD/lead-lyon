package lead.freturbLightV2;

import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;

public class CalculateRoutes {


    public static List<Trip> calculateDirectRouts(List<Move> movementsList) {
        Random rnd = new Random(3146464);

        List<Trip> tripsList = new ArrayList<>();
        List<Move> startPointList = new ArrayList<>();
        List<Move> endPointList = new ArrayList<>();

        for (Move move : movementsList) {
            if (move.disMove.equals(DistributionV2.DistributionMovement.Movement.enlèvements)) {
                startPointList.add(move);
            } else if (move.disMove.equals(DistributionV2.DistributionMovement.Movement.livraisons)) {
                endPointList.add(move);
            } else {
                startPointList.add(move);
                endPointList.add(move);
            }
        }

        movementsList = null;

        Collections.shuffle(startPointList, rnd);
        Collections.sort(endPointList);

        List<Double> checkDouble = new ArrayList<>();

        double diffDistance = Double.MAX_VALUE;
        Iterator<Move> iteratorStart = startPointList.iterator();
        while (iteratorStart.hasNext()) {
            int index = 0;
            Move startMove = iteratorStart.next();
            Move endMove = null;
            if (checkDouble.contains(startMove.travelDistance)) {
                continue;
            }
            for (Move move : endPointList) {
                if ((move.disMove.equals(DistributionV2.DistributionMovement.Movement.conjointes) || move.disMove.equals(DistributionV2.DistributionMovement.Movement.livraisons))
                        && startMove.disVeh20.equals(move.disVeh20) && startMove.disMan.equals(move.disMan)
                        && startMove.travelDistance != move.travelDistance) {
                    if (Math.abs(move.travelDistance - startMove.travelDistance) <= diffDistance) {
                        diffDistance = Math.abs(move.travelDistance - startMove.travelDistance);
                    } else if (Math.abs(move.travelDistance - startMove.travelDistance) >= diffDistance) {
                        endMove = endPointList.get(index);
                        checkDouble.add(endMove.travelDistance);
                        continue;
                    }
                }
                index++;
            }
            tripsList.add(new Trip(startMove, endMove, 2));
            iteratorStart.remove();
            endPointList.remove(endMove);
        }
        return tripsList;
    }

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
        if (!firstMove.logisticMatch.contains(possibleMove.logisticType) && firstMove.id != possibleMove.id) {
            return Double.MAX_VALUE;
        }
        double distance = (CoordUtils.calcEuclideanDistance(firstMove.ownCoord, possibleMove.ownCoord)/1000) * 1.4;
        double fistMoveDistance = firstMove.travelDistance - distance;
        double possibleMoveDistance = possibleMove.travelDistance - distance;
        return Math.abs(fistMoveDistance) + Math.abs(possibleMoveDistance);
    }

    static class Trip implements Comparable{
        Move startPiont;
        Move entpoint;
        double score;

        public Trip(Move startPiont, Move entpoint, double score) {
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
