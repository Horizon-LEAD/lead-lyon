package lead.freightDemand;

import java.util.List;

public class RunParallelizationRoutes extends Thread {

    List<Movement> movementList;

    public RunParallelizationRoutes(List<Movement> movementList) {
        this.movementList = movementList;
    }

    @Override
    public void run() {
        Movement movement = movementList.get(0);
        if (movement.routeType.equals(Movement.RouteType.direct)) {
            List<DirectTour> x = DirectTour.generateDirectTour(movementList);
            FreightDemand.tripsDirectList.addAll(x);
        } else {
            List<RoundTour> x = RoundTour.generateRoundTours(movementList);
            FreightDemand.tripsRoundList.addAll(x);
        }

    }

}
