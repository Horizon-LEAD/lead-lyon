package lead.freightDemand;

import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;

public class DirectTour implements Trips {

    Movement startPoint;
    Movement entPoint;
    double score;
    double distance;
    int timeSlot = -1;

    DirectTour(Movement startPoint, Movement endPoint, double score) {
        this.startPoint = startPoint;
        this.entPoint = endPoint;
        this.score = score;
        this.distance = CoordUtils.calcEuclideanDistance(startPoint.coord, endPoint.coord);
    }

    @Override
    public String toString(){
        return "" + startPoint.coord.getX() + ";" + startPoint.coord.getY() + ";" + entPoint.coord.getX() + ";" + entPoint.coord.getY() + ";" + score + ";" + distance + ";LINESTRING (" + startPoint.coord.getX() + " " + startPoint.coord.getY() + ", " + entPoint.coord.getX() + " " + entPoint.coord.getY() + ")";
    }

    static List<DirectTour> generateDirectTour(List<Movement> movementList) {
        System.out.println(Math.round(movementList.size()/2.0) + " direct tours getting paired, with vehicle type " + movementList.get(0).disVeh20 + ", with management mode " + movementList.get(0).disMan);
        List<Movement> startPoints = new ArrayList<>();
        List<Movement> stopPoints = new ArrayList<>();

        for (Movement movement : movementList) {
            if (movement.disMove.equals(Movement.DistributionMovement.Movements.livraisons)) {
                stopPoints.add(movement);
            } else {
                startPoints.add(movement);
            }
        }
        int incorrect = 0;
        List<DirectTour> directTourList = new ArrayList<>();
        Collections.shuffle(startPoints, FreightDemand.random);
        List<Movement> noPartner = new ArrayList<>();
        while (!startPoints.isEmpty()) {
            Movement startPoint = startPoints.get(0);
            startPoints.remove(startPoint);
            Movement endPoint = null;
            double bestScore = Double.MAX_VALUE - 1;
            for (Movement movement : stopPoints) {
                double score = scoreConnection(startPoint, movement);
                if (bestScore > score) {
                    bestScore = score;
                    endPoint = movement;
                }
            }
            if (endPoint != null) {
                directTourList.add(new DirectTour(startPoint, endPoint, bestScore));
                stopPoints.remove(endPoint);
            } else {
                noPartner.add(startPoint);
            }
        }
        noPartner.addAll(startPoints);
        noPartner.addAll(stopPoints);
        Collections.shuffle(noPartner, FreightDemand.random);
        while (!noPartner.isEmpty()) {
            Movement startPoint = noPartner.get(0);
            noPartner.remove(startPoint);
            Movement endPoint = null;
            double bestScore = Double.MAX_VALUE;
            for (Movement movement : noPartner) {
                double score = scoreConnection(startPoint, movement);
                if (bestScore > score) {
                    bestScore = score;
                    endPoint = movement;
                }
            }
            if (endPoint != null) {
                incorrect++;
                directTourList.add(new DirectTour(startPoint, endPoint, bestScore));
                noPartner.remove(endPoint);
            }
        }
        System.out.println("paired " + (directTourList.size() - incorrect) + " direct tours, with vehicle type " + movementList.get(0).disVeh20 + ", with management mode " + movementList.get(0).disMan);
        System.out.println("paired " + incorrect + " incorrect direct tours, with vehicle type " + movementList.get(0).disVeh20 + ", with management mode " + movementList.get(0).disMan);
        return directTourList;
    }

    private static double scoreConnection(Movement startPoint, Movement movement) {
        if (startPoint.siret.equals(movement.siret)) {
            return Double.MAX_VALUE;
        }
        double distance = (CoordUtils.calcEuclideanDistance(startPoint.coord, movement.coord))/1000 * 1.4;
        return Math.abs(startPoint.travelDistance - distance) + Math.abs(movement.travelDistance - distance);
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.score, ((DirectTour) o).score);
    }
}
