package lead.freturbLightV2;

import java.util.List;

public class RunParallelizationRoutes extends Thread {

    int i;
    public RunParallelizationRoutes(int routesType) {
        this.i = routesType;
    }

    @Override
    public void run() {
        try {
            if (i == 0) {
                System.out.println("Start directTrips (Thread)");
                RunFreturbLightV2.directTrips = CalculateRoutes.findBetterSolutions(RunFreturbLightV2.directMoveList);
                System.out.println("Created " + RunFreturbLightV2.directTrips.size() + " directTrips (Thread)");
                Thread.sleep(50);
            } else if (i == 1) {
                System.out.println("Start roundTrips PL (Thread)");
                RunFreturbLightV2.roundTripsPL = CalculateRoundRoutes.calculateRoundRoutes(RunFreturbLightV2.roundMovePL);
                System.out.println("Created " + RunFreturbLightV2.roundTripsPL.size() + " roundTrips PL (Thread)");
                Thread.sleep(50);
            } else {
                System.out.println("Start roundTrips VUL (Thread)");
                RunFreturbLightV2.roundTripsVUl = CalculateRoundRoutes.calculateRoundRoutes(RunFreturbLightV2.roundMoveVUL);
                System.out.println("Created " + RunFreturbLightV2.roundTripsVUl.size() + " roundTrips VUL (Thread)");
                Thread.sleep(50);
            }
        } catch (InterruptedException  e) {
        }
    }

}
