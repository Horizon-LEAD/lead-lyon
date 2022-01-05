package lead.freturbLightV2;

import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoundTrip implements Trips, Comparable {

    private final static int avgStops = 13;
    private final static int avgCAStops = 19;
    private final static int avgCPEStops = 11;
    private final static int avgCPDStops = 9;
    List<Integer> timeSlots = new ArrayList<>();
    double worstConnection = 0;

    Move startMove;
    List<Move> tourPoints = new ArrayList<>();
    List<Move> tourWithOrder = new ArrayList<>();
    double score = 0;

    void addStop(Move move) {
        tourPoints.add(move);
    }

    void findStartMove() {
        double[] distance = new double[this.tourPoints.size()];
        Iterator<Move> iterator = this.tourPoints.listIterator();
        Move firstMove = iterator.next();
        int i = 0;
        while (iterator.hasNext()) {
            Move nextMove = iterator.next();
            distance[i] = CoordUtils.calcEuclideanDistance(firstMove.ownCoord, nextMove.ownCoord);
            firstMove = nextMove;
            i++;
        }
        distance[i] = CoordUtils.calcEuclideanDistance(firstMove.ownCoord, tourPoints.get(0).ownCoord);
        double maxDistance = 0;
        for (int j = 0; j < distance.length; j++) {
            double tmpDistance;
            if (j == 0) {
                tmpDistance = distance[0] + distance[distance.length-1];
            } else {
                tmpDistance = distance[j - 1] + distance[j];
            }
            if (tmpDistance > maxDistance) {
                maxDistance = tmpDistance;
                this.startMove = tourPoints.get(j);
            }
        }
    }

    double scoreConnection(Move m1, Move m2){
        double distance = (CoordUtils.calcEuclideanDistance(m1.ownCoord, m2.ownCoord)/1000) * 1.4;
        double fistMoveDistance = m1.travelDistance - distance;
        double possibleMoveDistance = m2.travelDistance - distance;
        return Math.abs(fistMoveDistance) + Math.abs(possibleMoveDistance);
    }

    private void scoreLastFirstConnection(Move m1, Move m2) {
        this.score += scoreConnection(m1, m2);
    }

    void addScore(double score) {
        this.score += score;
    }

    void buildTrip(){
        scoreLastFirstConnection(tourPoints.get(0), tourPoints.get(tourPoints.size() - 1));
        findStartMove();
        int index = tourPoints.indexOf(this.startMove);
        Iterator<Move> listIterator = tourPoints.listIterator(index);
        this.tourWithOrder.add(listIterator.next());
        listIterator.remove();
        while (listIterator.hasNext()){
            this.tourWithOrder.add(listIterator.next());
            listIterator.remove();
        }
        listIterator = tourPoints.listIterator();
        while (listIterator.hasNext()) {
            this.tourWithOrder.add(listIterator.next());
        }
        this.tourWithOrder.add(startMove);
        if (!tourWithOrder.get(0).equals(startMove)){
            System.out.println("first move ist not start move");
        }
        if (!tourWithOrder.get(tourWithOrder.size()-1).equals(startMove)) {
            System.out.println("last move ist not start move, " + tourWithOrder.size() + ", " + index);
        }
        findWorstConnection();
    }

    private void findWorstConnection() {
        Iterator<Move> iterator = this.tourWithOrder.iterator();
        Move firstMove = iterator.next();
        while (iterator.hasNext()) {
            Move secondMove = iterator.next();
            double score = scoreConnection(firstMove, secondMove);
            if (worstConnection < score) {
                worstConnection = score;
            }
            firstMove = secondMove;
        }
    }

//    @Override
//    public int compareTo(Object o) {
//        return Double.compare(this.score, ((RoundTrip) o).score);
//    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.worstConnection, ((RoundTrip) o).worstConnection);
    }

    @Override
    public String toString(){
        return "" + startMove.ownCoord.getX() + ";" + startMove.ownCoord.getY() + ";" + score + generateLineStringWKT();
    }

    private String generateLineStringWKT() {
        String out = ";LINESTRING (";
        for (Move move : tourWithOrder) {
            out = out + move.ownCoord.getX() + " " +move.ownCoord.getY() + ", ";
        }
        out = out + startMove.ownCoord.getX() + " " + startMove.ownCoord.getY() + ")";
        return out;
    }
}
