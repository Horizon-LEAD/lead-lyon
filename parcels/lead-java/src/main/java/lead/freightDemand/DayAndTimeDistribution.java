package lead.freightDemand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayAndTimeDistribution {

    static Double[] hourDistribution = {0.0, 0.3, 0.2, 0.4, 0.7, 1.4, 2.6, 4.5, 11.0, 16.7, 18.6, 12.9, 7.1, 3.5, 7.0, 6.5, 2.6, 1.7, 1.1, 0.5, 0.3, 0.2, 0.2, 0.2};
    static Double[] hourDistributionCommutative = {0.0, 0.003, 0.005, 0.009, 0.016, 0.03, 0.056, 0.101, 0.211, 0.378, 0.564, 0.693, 0.764, 0.799, 0.869, 0.934, 0.960, 0.977, 0.988, 0.993, 0.996, 0.998, 1.0, 1.0};

    static final double weekDay = 0.19;
    static double amountRoundTripsWeekDay;
    static double amountDirectTripsWeekDay;

    static List<Trips> generateDistribution(List<DirectTour> tripsDirectList, List<RoundTour> tripsRoundList) {
        filterWorstScore(tripsDirectList, tripsRoundList);
        amountDirectTripsWeekDay = tripsDirectList.size() * weekDay;
        amountRoundTripsWeekDay = tripsRoundList.size() * weekDay;
        List<Trips> allWeekDayTrips = dayDistribution(tripsDirectList, tripsRoundList);
        return timeDistribution(allWeekDayTrips);
    }

    private static List<Trips> dayDistribution(List<DirectTour> tripsDirectList, List<RoundTour> tripsRoundList) {
        List<Trips> allWeekDayTrips = new ArrayList<>();
        for (DirectTour directTrip : tripsDirectList) {
            if (FreightDemand.random.nextDouble() < amountDirectTripsWeekDay/tripsDirectList.size()) {
                allWeekDayTrips.add(directTrip);
            }
        }
        for (RoundTour trip : tripsRoundList) {
            if (FreightDemand.random.nextDouble() < amountRoundTripsWeekDay/tripsRoundList.size()) {
                allWeekDayTrips.add(trip);
            }
        }
        return allWeekDayTrips;
    }

    private static void filterWorstScore(List<DirectTour> tripsDirectList, List<RoundTour> tripsRoundList) {
        Collections.sort(tripsDirectList);
        Collections.sort(tripsRoundList);
        double maxDifferentDirectDistance = tripsDirectList.get(tripsDirectList.size() - (int) (tripsDirectList.size() * 0.05)).score;
        List<DirectTour> tmpDirectDirectTrips = new ArrayList<>();
        for (DirectTour directTour : tripsDirectList) {
            if (directTour.score > maxDifferentDirectDistance) {
                tmpDirectDirectTrips.add(directTour);
            }
        }
        tripsDirectList.removeAll(tmpDirectDirectTrips);
        double maxDifferentRoundDistance = tripsRoundList.get(tripsRoundList.size() - (int) (tripsRoundList.size() * 0.05)).score;
        List<RoundTour> tmpRoundTrips = new ArrayList<>();
        for (RoundTour trip : tripsRoundList) {
            if (trip.score > maxDifferentRoundDistance) {
                tmpRoundTrips.add(trip);
            }
        }
        tripsRoundList.removeAll(tmpRoundTrips);
    }

    private static List<Trips> timeDistribution(List<Trips> allWeekDayTrips) {
        for (Trips trip : allWeekDayTrips) {
            if (trip instanceof DirectTour) {
                double pick = FreightDemand.random.nextDouble();
                for (int i = hourDistributionCommutative.length; i > 1; i--) {
                    if (pick > hourDistributionCommutative[i-1] && ((DirectTour) trip).timeSlot == -1) {
                        ((DirectTour) trip).timeSlot = i * 3600 + (FreightDemand.random.nextInt(60) * 60) ;
                        break;
                    }
                }
            } else if (trip instanceof RoundTour) {
                RoundTour roundTrip = (RoundTour) trip;
                for (int i = 0; i < roundTrip.tourPoints.size(); i++) {
                    double pick = FreightDemand.random.nextDouble();
                    for (int j = hourDistributionCommutative.length; j > 0; j--) {
                        if (pick > hourDistributionCommutative[j-1] && roundTrip.timeSlots.size() != i) {
                            roundTrip.timeSlots.add(j * 3600 + (FreightDemand.random.nextInt(60) * 60));
                            break;
                        }
                    }
                }
                Collections.sort(roundTrip.timeSlots);
            }
        }
        return allWeekDayTrips;
    }

}
