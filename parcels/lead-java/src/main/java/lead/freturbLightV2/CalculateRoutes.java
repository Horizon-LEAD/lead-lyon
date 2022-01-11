package lead.freturbLightV2;

import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;

public class CalculateRoutes {

    static List<Double> scoreList = new ArrayList<>();

    static List<DirectTrip> calculateDirectRoutsV2(List<Move> movementsList) {
        int movementsSize = movementsList.size();
        System.out.println(movementsSize + " direct Movements getting paired");
        Random rnd = new Random(31464645);
        List<DirectTrip> tripsList = new ArrayList<>();
        int size = movementsList.size();
        for (int i = 1; i < size; i++) {
            DirectTrip directTrip = getPair(movementsList, rnd);
            if (directTrip != null) {
                tripsList.add(directTrip);
            }
            if (i % 10000 == 0) {
                System.out.println("Done " + i);
            }
        }
        System.out.println("No Match for " + (movementsSize-(tripsList.size() * 2)) + " movements");
        Collections.sort(tripsList);
        return tripsList;
    }

    private static DirectTrip getPair(List<Move> movementsList, Random rnd) {
        if (movementsList.size() == 0) {
            return null;
        }
        Move firstMove = movementsList.get(rnd.nextInt(movementsList.size()));
        Move bestFit = null;
        double minScore = Double.MAX_VALUE;
        DirectTrip directTrip = null;
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
            directTrip = new DirectTrip(firstMove, bestFit, minScore);
        }
        return directTrip;
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

     static List<DirectTrip> calculateBestDirectRoutsV2(List<Move> movementsList) {
        int movementsSize = movementsList.size();
        System.out.println(movementsSize + " best direct Movements getting paired");
        List<DirectTrip> tripsList = new ArrayList<>();
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
            DirectTrip directTrip = new DirectTrip(startMove, bestFit);
            directTrip.bestScore = minScore;
            tripsList.add(directTrip);
        }
        return tripsList;
    }

    /**
     * creates direct trips from individual movements
     * @param movementsList - a list of direct movements
     * @return - a list of direct trips
     */
    static List<DirectTrip> findBetterSolutions(List<Move> movementsList){
       List<Move> startMoves = new ArrayList<>();
       List<Move> endMoves = new ArrayList<>();

       System.out.println((movementsList.size()/2) + " direct Routes are created");

       for (Move move : movementsList) {
           if (move.disMove.equals(DistributionV2.DistributionMovement.Movement.livraisons)) {
               endMoves.add(move);
           } else {
               startMoves.add(move);
           }
       }

       Collections.shuffle(startMoves);
       Solution initialSolution = new Solution(endMoves);
       List<DirectTrip> notOptimalDirectTrips = new ArrayList<>();

       int directsCreated = 0;

       for (Move startMove : startMoves) {
           TreeSet<DirectTrip> endPoints = findAllSolution(startMove, endMoves);
           Iterator<DirectTrip> iterator = endPoints.iterator();
           DirectTrip tmpDirectTrip = iterator.next();
           boolean found = true;
           while (initialSolution.usedIds.contains(tmpDirectTrip.entPoint.id) && found) {
               notOptimalDirectTrips.add(tmpDirectTrip);
               if (!iterator.hasNext()) {
                   found = false;
                   continue;
               }
               tmpDirectTrip = iterator.next();
           }
           if (found) {
               scoreList.add(tmpDirectTrip.score);
               initialSolution.addEndPoint(tmpDirectTrip);
               directsCreated++;
           } else {
               initialSolution.addPairLess(startMove);
           }
           if (directsCreated > 1 && directsCreated % 1000 == 0) {
               System.out.println("" + directsCreated + " Routes created");
           }
       }
       System.out.println("" + directsCreated + " were created in the end");
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

        void addEndPoint(DirectTrip directTrip) {
            solution.put(directTrip.entPoint.id, directTrip.startPoint);
            usedIds.add(directTrip.entPoint.id);
            increaseScore(directTrip.score);
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

        public List<DirectTrip> getTrips() {
            List<DirectTrip> directTrips =  new ArrayList<>();
            for (Map.Entry<Integer, Move> entry : solution.entrySet()) {
                Move finalEndMove = null;
                for (Move endMove : endMoves) {
                    if (endMove.id == entry.getKey()) {
                        finalEndMove = endMove;
                    }
                }
                directTrips.add(new DirectTrip(entry.getValue(), finalEndMove, scoreConnection(entry.getValue(), finalEndMove)));
            }
            return directTrips;
        }

        public boolean compareScore(DirectTrip optimalDirectTrip, DirectTrip tmpDirectTrip, Move thief, int id, DirectTrip notOptimalDirectTrip) {
            double scoreOptimal = optimalDirectTrip.score;
            double scoreTmp = tmpDirectTrip.score;
            Move finalEndMove = null;
            for (Move endMove : endMoves) {
                if (endMove.id == id) {
                    finalEndMove = endMove;
                }
            }
            double oldScore = scoreConnection(thief, finalEndMove);
            double scoreNotOptimal = notOptimalDirectTrip.score;
            return oldScore + scoreNotOptimal > scoreOptimal + scoreTmp;
        }
    }


    private static Move findMoveWhoTookBestMatch(int id, List<DirectTrip> optimalSolution) {
        for (DirectTrip directTrip : optimalSolution) {
            if (directTrip.entPoint.id == id) {
                return directTrip.startPoint;
            }
        }
        return null;
    }

    private static TreeSet<DirectTrip> findAllSolution(Move move, List<Move> endPoints) {
        TreeSet<DirectTrip> set = new TreeSet<>();
        int stopTimeWaste = 0;
        for (Move possibleEndMove : endPoints) {
            double localScore = scoreConnection(move, possibleEndMove);
            DirectTrip directTrip = new DirectTrip(move, possibleEndMove, localScore);
            set.add(directTrip);
            stopTimeWaste++;
            if (stopTimeWaste == 1000) {
//                continue;
            }
        }
        return set;
    }

}
