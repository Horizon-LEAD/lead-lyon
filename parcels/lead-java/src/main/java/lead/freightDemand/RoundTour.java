package lead.freightDemand;

import org.apache.commons.math3.distribution.GeometricDistribution;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RoundTour implements Trips {

    private final static int avgStops = 13;
    private static final GeometricDistribution geometricDistributionCA = new GeometricDistribution( 1/19.0);
    private static final GeometricDistribution geometricDistributionCPE = new GeometricDistribution( 1/11.0);
    private static final GeometricDistribution geometricDistributionCPD = new GeometricDistribution( 1/9.0);
    List<Integer> timeSlots = new ArrayList<>();

    Movement startPoint;
    List<Movement> tourPoints;
//    List<Movement> tourWithOrder = new ArrayList<>();
    double score;

    RoundTour(List<Movement> trip, double score) {
        this.tourPoints = trip;
        this.score = score;
//        buildTrip();
    }

//    private void buildTrip() {
//        findStartMove();
//        int index = tourPoints.indexOf(this.startPoint);
//        Iterator<Movement> listIterator = tourPoints.listIterator(index);
//        this.tourWithOrder.add(listIterator.next());
//        listIterator.remove();
//        while (listIterator.hasNext()){
//            this.tourWithOrder.add(listIterator.next());
//            listIterator.remove();
//        }
//        listIterator = tourPoints.listIterator();
//        while (listIterator.hasNext()) {
//            this.tourWithOrder.add(listIterator.next());
//        }
//        this.tourWithOrder.add(this.startPoint);
//        if (!tourWithOrder.get(0).equals(startPoint)){
//            System.out.println("first move ist not start move");
//        }
//        if (!tourWithOrder.get(tourWithOrder.size()-1).equals(startPoint)) {
//            System.out.println("last move ist not start move, " + tourWithOrder.size() + ", " + index);
//        }
//    }

    private void findStartMove() {
        double[] distance = new double[this.tourPoints.size()];
        Iterator<Movement> iterator = this.tourPoints.listIterator();
        Movement firstMove = iterator.next();
        int i = 0;
        while (iterator.hasNext()) {
            Movement nextMove = iterator.next();
            distance[i] = CoordUtils.calcEuclideanDistance(firstMove.coord, nextMove.coord);
            firstMove = nextMove;
            i++;
        }
        distance[i] = CoordUtils.calcEuclideanDistance(firstMove.coord, tourPoints.get(0).coord);
        double maxDistance = 0;
        for (int j = 0; j < distance.length; j++) {
            double tmpDistance = 0;
            if (j == 0) {
                tmpDistance = distance[0] + distance[distance.length-1];
            } else {
                tmpDistance = distance[j - 1] + distance[j];
            }
            if (tmpDistance > maxDistance) {
                maxDistance = tmpDistance;
                this.startPoint = tourPoints.get(j);
            }
        }
    }


    static List<RoundTour> generateRoundTours(List<Movement> movementList) {
        GeometricDistribution geometricDistribution;
        if (movementList.get(0).disMan.equals(Movement.DistributionManagement.Management.CA)) {
            geometricDistribution = geometricDistributionCA;
        } else if (movementList.get(0).disMan.equals(Movement.DistributionManagement.Management.CPE)) {
            geometricDistribution = geometricDistributionCPE;
        } else {
            geometricDistribution = geometricDistributionCPD;
        }
        System.out.println(Math.round(movementList.size()/geometricDistribution.getNumericalMean()) + " round tours getting paired, with vehicle type " + movementList.get(0).disVeh20 + ", management mode " + movementList.get(0).disMan);
        List<RoundTour> roundTourList = new ArrayList<>();
        Collections.shuffle(movementList, FreightDemand.random);
        while (!movementList.isEmpty()) {
            int tourSize = geometricDistribution.sample();
            if (tourSize < 3) {
                continue;
            }
            if (movementList.size() < 3) {
                break;
            }
            if (tourSize > movementList.size()) {
                tourSize = movementList.size();
            }
            Movement startPoint = movementList.get(0);
            List<Movement> trip = new ArrayList<>();
            List<String> siret = new ArrayList<>();
            trip.add(startPoint);
            siret.add(startPoint.siret);
            double overallScore = 0;
            while(trip.size() < tourSize) {
                Movement stop = null;
                double bestScore = Double.MAX_VALUE;
                for (Movement movement : movementList) {
                    if (!siret.contains(movement.siret)) {
                        double score = scoreConnection(startPoint, movement);
                        if (score < bestScore) {
                            bestScore = score;
                            stop = movement;
                        }
                    }
                }
                if (stop != null) {
                    trip.add(stop);
                    siret.add(stop.siret);
                    startPoint = stop;
                    overallScore += bestScore;
                } else {
                    movementList.removeAll(trip);
                    break;
                }
            }
            if (trip.size() == tourSize) {
                Movement lastStop = null;
                double bestScore = Double.MAX_VALUE;
                for (Movement movement : movementList) {
                    if (!siret.contains(movement.siret)) {
                        double firstTrip = scoreConnection(trip.get(0), movement);
                        double secondTrip = scoreConnection(movement, trip.get(trip.size()-1));
                        if (firstTrip + secondTrip < bestScore) {
                            bestScore = firstTrip + secondTrip ;
                            lastStop = movement;
                        }
                    }
                }
                if (lastStop != null) {
                    trip.add(lastStop);
                    overallScore += bestScore;
                } else {
                    movementList.removeAll(trip);
                    break;
                }
            }
            movementList.removeAll(trip);
            if (trip.size() == tourSize + 1) {
                roundTourList.add(new RoundTour(trip, overallScore));
            }
        }
        System.out.println("paired " + roundTourList.size() + " round tours, with vehicle type " + movementList.get(0).disVeh20 + ", management mode " + movementList.get(0).disMan);
        return roundTourList;
    }

    private static double scoreConnection(Movement startPoint, Movement movement) {
        double distance = (CoordUtils.calcEuclideanDistance(startPoint.coord, movement.coord)/1000) * 1.4;
        return Math.abs(startPoint.travelDistance - distance);
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.score, ((RoundTour) o).score);
    }
}
