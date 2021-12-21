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
            if (score < minScore) {
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
        if (firstMove.id == possibleMove.id || firstMove.ownCoord.equals(possibleMove.ownCoord)) {
            return Double.MAX_VALUE;
        }
        if (!firstMove.logisticDirectMatch.contains(possibleMove.logisticDirectType) && !possibleMove.logisticDirectMatch.contains(firstMove.logisticDirectType)) {
            return Double.MAX_VALUE;
        }
        double distance = (CoordUtils.calcEuclideanDistance(firstMove.ownCoord, possibleMove.ownCoord)/1000) * 1.4;
        double fistMoveDistance = firstMove.travelDistance - distance;
        double possibleMoveDistance = possibleMove.travelDistance - distance;
        return Math.abs(fistMoveDistance) + Math.abs(possibleMoveDistance);
//        return Math.abs(((firstMove.travelDistance + possibleMove.travelDistance)/2) - distance);
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

    static class Trip extends Trips implements Comparable {
        Move startPoint;
        Move entPoint;
        double score;
        double bestScore;
        double distanceReal;
        double distanceTheoretically;
        int timeSlot = -1;

        public Trip(Move startPiont, Move entpoint, double score) {
            this.startPoint = startPiont;
            this.entPoint = entpoint;
            this.score = score;
            distanceReal = (CoordUtils.calcEuclideanDistance(startPiont.ownCoord, entpoint.ownCoord)/1000)*1.4;
            distanceTheoretically = (startPiont.travelDistance + entpoint.travelDistance)/2;
        }

        public Trip(Move startPiont, Move entpoint) {
            this.startPoint = startPiont;
            this.entPoint = entpoint;
            distanceReal = (CoordUtils.calcEuclideanDistance(startPiont.ownCoord, entpoint.ownCoord)/1000)*1.4;
            distanceTheoretically = (startPiont.travelDistance + entpoint.travelDistance)/2;
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

    static List<Trip> findBetterSolutions(List<Move> movementsList){
       List<Move> startMoves = new ArrayList<>();
       List<Move> endMoves = new ArrayList<>();

       for (Move move : movementsList) {
           if (move.disMove.equals(DistributionV2.DistributionMovement.Movement.livraisons)) {
               endMoves.add(move);
           } else {
               startMoves.add(move);
           }
       }

       Collections.shuffle(startMoves);
       Solution initialSolution = new Solution(endMoves);
       List<Trip> notOptimalTrips = new ArrayList<>();

       for (Move startMove : startMoves) {
           TreeSet<Trip> endPoints = findAllSolution(startMove, endMoves);
           Iterator<Trip> iterator = endPoints.iterator();
           Trip tmpTrip = iterator.next();
           boolean found = true;
           while (initialSolution.usedIds.contains(tmpTrip.entPoint.id) && found) {
               notOptimalTrips.add(tmpTrip);
               if (!iterator.hasNext()) {
                   found = false;
                   continue;
               }
               tmpTrip = iterator.next();
           }
           if (found) {
               initialSolution.addEndPoint(tmpTrip);
           } else {
               initialSolution.addPairLess(startMove);
           }
       }

//       for (Trip notOptimalTrip : notOptimalTrips) {
//           TreeSet<Trip> notOptimalTripEndPoints = findAllSolution(notOptimalTrip.startPiont, endMoves);
//           Trip optimalTrip = notOptimalTripEndPoints.first();
//           int idOT = optimalTrip.entpoint.id;
//           Move thief = initialSolution.solution.get(idOT);
//           if (thief == null) {
//               continue;
//           }
//           TreeSet<Trip> thiefEndPoints = findAllSolution(thief, endMoves);
//           Iterator<Trip> iterator = thiefEndPoints.iterator();
//           Trip tmpTrip = iterator.next();
//           boolean found = true;
//           while (initialSolution.usedIds.contains(tmpTrip.entpoint.id) && found) {
//               if (!iterator.hasNext()) {
//                   found = false;
//                   continue;
//               }
//               tmpTrip = iterator.next();
//           }
//           if (found && initialSolution.compareScore(optimalTrip, tmpTrip, thief, idOT, notOptimalTrip)) {
//               initialSolution.removeUsedId(notOptimalTrip.entpoint.id);
//               initialSolution.removeUsedId(thief.id);
//               initialSolution.addEndPoint(optimalTrip);
//               initialSolution.addEndPoint(tmpTrip);
//               System.out.println("Better");
//           }
//       }
//
//        System.out.println("Done");
        return initialSolution.getTrips();

    }

    static class Solution {
        double score = 0;
        List<Move> endMoves;
        List<Integer> usedIds = new ArrayList<>();
        Map<Integer, Move> solution = new HashMap<>();
        List<Move> pairLess = new ArrayList<>();

        public Solution(List<Move> endMoves) {
            this.endMoves = endMoves;
        }

        void addPairLess(Move move) {
            pairLess.add(move);
        }

        void addEndPoint(Trip trip) {
            solution.put(trip.entPoint.id,trip.startPoint);
            usedIds.add(trip.entPoint.id);
            increaseScore(trip.score);
        }

        void increaseScore(double score) {
            this.score += score;
        }

        void decreaseScore(double score) {
            this.score -= score;
        }

        void removeUsedId(int id) {
            Move move = solution.get(Integer.valueOf(id));
            Move finalEndMove = null;
            for (Move endMove : endMoves) {
                if (endMove.id == id) {
                    finalEndMove = endMove;
                }
            }
            decreaseScore(scoreConnection(move,finalEndMove));
            this.usedIds.remove(Integer.valueOf(id));
            this.solution.remove(Integer.valueOf(id));
        }

        public List<Trip> getTrips() {
            List<Trip> trips =  new ArrayList<>();
            for (Map.Entry<Integer, Move> entry : solution.entrySet()) {
                Move finalEndMove = null;
                for (Move endMove : endMoves) {
                    if (endMove.id == entry.getKey()) {
                        finalEndMove = endMove;
                    }
                }
                trips.add(new Trip(entry.getValue(), finalEndMove, scoreConnection(entry.getValue(), finalEndMove)));
            }
            return  trips;
        }

        public boolean compareScore(Trip optimalTrip, Trip tmpTrip, Move thief, int id, Trip notOptimalTrip) {
            double scoreOptimal = optimalTrip.score;
            double scoreTmp = tmpTrip.score;
            Move finalEndMove = null;
            for (Move endMove : endMoves) {
                if (endMove.id == id) {
                    finalEndMove = endMove;
                }
            }
            double oldScore = scoreConnection(thief, finalEndMove);
            double scoreNotOptimal = notOptimalTrip.score;
            return oldScore + scoreNotOptimal > scoreOptimal + scoreTmp;
        }
    }


    private static Move findMoveWhoTookBestMatch(int id, List<Trip> optimalSolution) {
        for (Trip trip : optimalSolution) {
            if (trip.entPoint.id == id) {
                return trip.startPoint;
            }
        }
        return null;
    }

    private static TreeSet<Trip> findAllSolution(Move move, List<Move> endPoints) {
        TreeSet<Trip> set = new TreeSet<>();
        for (Move possibleEndMove : endPoints) {
            double localScore = scoreConnection(move, possibleEndMove);
            Trip trip = new Trip(move, possibleEndMove, localScore);
            set.add(trip);
        }
        return set;
    }

}
