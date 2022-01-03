package lead.freturbLightV2;

import org.matsim.core.utils.geometry.CoordUtils;

class DirectTrip implements Trips, Comparable {
    Move startPoint;
    Move entPoint;
    double score;
    double bestScore;
    double distanceReal;
    double distanceTheoretically;
    int timeSlot = -1;

    public DirectTrip(Move startPiont, Move entpoint, double score) {
        this.startPoint = startPiont;
        this.entPoint = entpoint;
        this.score = score;
        distanceReal = (CoordUtils.calcEuclideanDistance(startPiont.ownCoord, entpoint.ownCoord) / 1000) * 1.4;
        distanceTheoretically = (startPiont.travelDistance + entpoint.travelDistance) / 2;
    }

    public DirectTrip(Move startPiont, Move entpoint) {
        this.startPoint = startPiont;
        this.entPoint = entpoint;
        distanceReal = (CoordUtils.calcEuclideanDistance(startPiont.ownCoord, entpoint.ownCoord) / 1000) * 1.4;
        distanceTheoretically = (startPiont.travelDistance + entpoint.travelDistance) / 2;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.score, ((DirectTrip) o).score);
    }

    @Override
    public String toString() {
        return "Score: " + score;
    }
}
