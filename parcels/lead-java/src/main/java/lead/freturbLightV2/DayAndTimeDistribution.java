package lead.freturbLightV2;

import java.util.*;

public class DayAndTimeDistribution {

    static Double[] hourDistribution = {0.0, 0.3, 0.2, 0.4, 0.7, 1.4, 2.6, 4.5, 11.0, 16.7, 18.6, 12.9, 7.1, 3.5, 7.0, 6.5, 2.6, 1.7, 1.1, 0.5, 0.3, 0.2, 0.2, 0.2};
    static Double[] hourDistributionCommutative = {0.0, 0.003, 0.005, 0.009, 0.016, 0.03, 0.056, 0.101, 0.211, 0.378, 0.564, 0.693, 0.764, 0.799, 0.869, 0.934, 0.960, 0.977, 0.988, 0.993, 0.996, 0.998, 1.0, 1.0};

    static final double weekDay = 0.19;
    static double maxDifferentDirectDistance = 100;
    static double maxDifferentRoundDistance = 100;
    static double maxDifferentScore = 0;

    static List<Trips> generateDistribution(List<CalculateRoutes.Trip> directTrips, List<RoundTrip> roundTrips) {
        filterWorstScore(directTrips, roundTrips);
        List<Trips> allWeekDayTrips = dayDistribution(directTrips, roundTrips);
        allWeekDayTrips= timeDistribution(allWeekDayTrips);
        return allWeekDayTrips;
    }

    static void filterWorstScore(List<CalculateRoutes.Trip> directTrips, List<RoundTrip> roundTrips) {
        Collections.sort(directTrips);
        maxDifferentDirectDistance = directTrips.get(directTrips.size() - (int) (directTrips.size() * 0.05)).score;
        List<CalculateRoutes.Trip> tmpDirectTrips = new ArrayList<>();
        for (CalculateRoutes.Trip trip : directTrips) {
            if (trip.score > maxDifferentDirectDistance) {
                tmpDirectTrips.add(trip);
            }
        }
        directTrips.removeAll(tmpDirectTrips);
        tmpDirectTrips.clear();
        System.out.println(directTrips.size() + " direct trips");

        Collections.sort(roundTrips);
        maxDifferentRoundDistance = roundTrips.get(roundTrips.size() - (int) (roundTrips.size() * 0.05)).score;
        List<RoundTrip> tmpRoundTrips = new ArrayList<>();
        for (RoundTrip trip : roundTrips) {
            if (trip.score > maxDifferentRoundDistance) {
                tmpRoundTrips.add(trip);
            }
        }
        roundTrips.removeAll(tmpRoundTrips);
        tmpRoundTrips.clear();
        System.out.println(roundTrips.size() + " round trips");
    }

    static List<Trips> dayDistribution(List<CalculateRoutes.Trip> directTrips, List<RoundTrip> roundTrips) {
        List<Trips> allWeekDayTrips = new ArrayList<>();
        Random random = new Random(2223);
        for (CalculateRoutes.Trip trip : directTrips) {
            if (random.nextDouble() < weekDay) {
                allWeekDayTrips.add(trip);
            }
        }
        for (RoundTrip trip : roundTrips) {
            if (random.nextDouble() < weekDay) {
                allWeekDayTrips.add(trip);
            }
        }
        return allWeekDayTrips;
    }

    static List<Trips> timeDistribution(List<Trips> allWeekDayTrips) {
        Random random = new Random(222);
        for (Trips trip : allWeekDayTrips) {
            if (trip instanceof CalculateRoutes.Trip) {
                double pick = random.nextDouble();
                for (int i = hourDistributionCommutative.length; i > 1; i--) {
                    if (pick > hourDistributionCommutative[i-1] && ((CalculateRoutes.Trip) trip).timeSlot == -1) {
                        ((CalculateRoutes.Trip) trip).timeSlot = i * 3600 + (random.nextInt(60) * 60) ;
                        continue;
                    }
                }
            } else if (trip instanceof RoundTrip) {
                RoundTrip roundTrip = (RoundTrip) trip;

                for (int i = 1; i < roundTrip.tourWithOrder.size(); i++) {
                    double pick = random.nextDouble();
                    for (int j = hourDistributionCommutative.length; j > 1; j--) {
                        if (pick > hourDistributionCommutative[j-1] && roundTrip.timeSlots.size() != i) {
                            roundTrip.timeSlots.add(j * 3600 + (random.nextInt(60) * 60));
                            continue;
                        }
                    }
                }
                Collections.sort(roundTrip.timeSlots);
            }
        }
        return allWeekDayTrips;
    }
}
