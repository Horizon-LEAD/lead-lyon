package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.List;
import java.util.Random;

import static lead.freturbLightV2.DistributionV2.DistributionManagement.Management.*;
import static lead.freturbLightV2.DistributionV2.DistributionMovement.Movement.*;
import static lead.freturbLightV2.DistributionV2.DistributionVehicle.Vehicle.*;

public class DistributionV2 {

    final int st8;
    final static double TYPE_OF_MOVEMENT = 13.4637;
    final static double MANAGEMENT_MODE = -1.4543;
    final static double VEHICLE_TYPE = 3.6464;
    final static double ACTIVITY_TYPE = 1.9224;
    final static double DISTANCE = 0.4507;
    final static int[] ACTIVITY_TYPE_ARRAY = {1, 1, 1, 0, 0, 0, 0, 1};

    final DistributionMovement disMove;
    final DistributionVehicle disVeh;
    final DistributionManagement disMan;

    static DistributionV2[] distributions = generateDistributionV2();
    static DistributionVehicleST20[] distributionVehicles = vehicleDistributionST20();

    DistributionV2(int st8, DistributionMovement disMove, DistributionManagement disMan, DistributionVehicle disVeh) {
        this.st8 = st8;
        this.disMove = disMove;
        this.disMan = disMan;
        this.disVeh = disVeh;
    }

    public static List<FirmDataV2> distributeLogistics(List<FirmDataV2> firms, List<Coord> centers, boolean oneCenter) {
        Random random = new Random(21656368);
        for (FirmDataV2 firm : firms) {
            double centerDistance = Double.MAX_VALUE;
            int centerId = 0;
            int count = 1;
            for (Coord centerCoord : centers) {
                double tmpDistance = CoordUtils.calcEuclideanDistance(firm.coord, centerCoord)/1000;
                if (tmpDistance < centerDistance) {
                    centerDistance = tmpDistance;
                    centerId = count;
                }
                count++;
            }
            if (oneCenter) {
                final Coord CENTER = new Coord(842443.74, 6519278.68);
                firm.centerDistance = CoordUtils.calcEuclideanDistance(firm.coord, CENTER)/1000;
            } else {
                firm.centerDistance = centerDistance;
            }
            Move[] moves = new Move[firm.movements];
            for (int i = 0; i < firm.movements; i++) {
                Move move = distributeLogistics(firm, random);
                move.setLogistic();
                moves[i] = move;
                move.st8 = firm.st8;
                move.centerId = centerId;
                Move.movementsList.add(move);
            }
            firm.moves = moves;
        }
        return firms;
    }

    private static Move distributeLogistics(FirmDataV2 firm, Random random) {
        int[] variable = new int[3];

        if(firm.st8 == 0) {
            return null;
        }

        Move move = new Move();
        move.ownCoord = firm.coord;

        DistributionV2 distribution = distributions[firm.st8-1];
        {
            double rnd = random.nextDouble();
            if (rnd < distribution.disMove.conjointes) {
                move.disMove = conjointes;
            } else if (rnd < distribution.disMove.conjointes + distribution.disMove.enlèvements) {
                move.disMove = enlèvements;
            } else {
                move.disMove = livraisons;
            }
        }
        {
            double rnd = random.nextDouble();
            if (rnd < distribution.disMan.CPD) {
                move.disMan = CPD;
            } else if (rnd < distribution.disMan.CPD + distribution.disMan.CPE) {
                move.disMan = CPE;
            } else {
                move.disMan = CA;
                variable[1] = 1;
            }
        }
        {
            double rnd = random.nextDouble();
            if (rnd < distribution.disVeh.Articulés) {
                move.disVeh = Articulés;
            } else if (rnd < distribution.disVeh.Articulés + distribution.disVeh.Porteurs) {
                move.disVeh = Porteurs;
            } else if (rnd < distribution.disVeh.Articulés + distribution.disVeh.Porteurs + distribution.disVeh.VUL) {
                move.disVeh = DistributionVehicle.Vehicle.VUL;
            } else {
                move.disVeh = roues2_3;
            }
            rnd = random.nextDouble();
            DistributionVehicleST20 distributionVehicleST20 = distributionVehicles[firm.st20];
            if (rnd < distributionVehicleST20.PL) {
                move.disVeh20 = DistributionVehicleST20.VehicleST20.PL;
                variable[2] = 1;
            } else {
                move.disVeh20 = DistributionVehicleST20.VehicleST20.VUL;
            }
        }

        variable[0] = getTypeOfMovement(random);
        if (variable[0] == 1) {
            move.routeType = Move.RouteType.direct;
        } else {
            move.routeType = Move.RouteType.round;
        }


        move.travelDistance = variable[0] * TYPE_OF_MOVEMENT + variable[1] * MANAGEMENT_MODE + ACTIVITY_TYPE_ARRAY[firm.st8 - 1] * ACTIVITY_TYPE + variable[2] * VEHICLE_TYPE + firm.centerDistance * DISTANCE;

        return move;
    }

    private static int getTypeOfMovement(Random random) {
        double rnd = random.nextDouble();
        if (rnd > 0.75) {
            return 1;
        }
        return 0;
    }

    private static DistributionV2[] generateDistributionV2() {
        DistributionV2[] distributions = new DistributionV2[8];
        distributions[0] = new DistributionV2(1,
                new DistributionMovement(.26,.34, .4),
                new DistributionManagement(.43,.18,.09),
                new DistributionVehicle(.005, .084, .911,.0));
        distributions[1] = new DistributionV2(2,
                new DistributionMovement(.15,.22, .63),
                new DistributionManagement(.25,.28,.46),
                new DistributionVehicle(.06, .17, .63,.04));
        distributions[2] = new DistributionV2(3,
                new DistributionMovement(.08,.42, .5),
                new DistributionManagement(.08,.34,.58),
                new DistributionVehicle(.04, .35, .54,.07));
        distributions[3] = new DistributionV2(4,
                new DistributionMovement(.04,.63, .33),
                new DistributionManagement(.18,.27,.56),
                new DistributionVehicle(.13, .4, .46,.03));
        distributions[4] = new DistributionV2(5,
                new DistributionMovement(.24,.17, .6),
                new DistributionManagement(.11,.15,.74),
                new DistributionVehicle(.3, .35, .34,.01));
        distributions[5] = new DistributionV2(6,
                new DistributionMovement(.14,.13, .73),
                new DistributionManagement(.19,.45,.37),
                new DistributionVehicle(.02, .30, .67,.01));
        distributions[6] = new DistributionV2(7,
                new DistributionMovement(.14,.28, .58),
                new DistributionManagement(.07,.37,.56),
                new DistributionVehicle(.05, .27, .58,.10));
        distributions[7] = new DistributionV2(8,
                new DistributionMovement(.04,.59, .27),
                new DistributionManagement(.11,.27,.62),
                new DistributionVehicle(.35, .53, .08,.04));
        return distributions;
    }

    static DistributionVehicleST20[] vehicleDistributionST20(){
        DistributionVehicleST20[] vehicleST20s = new DistributionVehicleST20[20];
        vehicleST20s[0] = new DistributionVehicleST20(.09, .91);
        vehicleST20s[1] = new DistributionVehicleST20(.08, .92);
        vehicleST20s[2] = new DistributionVehicleST20(.23, .77);
        vehicleST20s[3] = new DistributionVehicleST20(.45, .55);
        vehicleST20s[4] = new DistributionVehicleST20(.22, .78);
        vehicleST20s[5] = new DistributionVehicleST20(.29, .71);
        vehicleST20s[6] = new DistributionVehicleST20(.55, .45);
        vehicleST20s[7] = new DistributionVehicleST20(.25, .75);
        vehicleST20s[8] = new DistributionVehicleST20(.68, .32);
        vehicleST20s[9] = new DistributionVehicleST20(.75, .25);
        vehicleST20s[10] = new DistributionVehicleST20(.42,.58);
        vehicleST20s[11] = new DistributionVehicleST20(.71,.29);
        vehicleST20s[12] = new DistributionVehicleST20(.16, .84);
        vehicleST20s[13] = new DistributionVehicleST20(.32, .68);
        vehicleST20s[14] = new DistributionVehicleST20(.6, .4);
        vehicleST20s[15] = new DistributionVehicleST20(.11, .89);
        vehicleST20s[16] = new DistributionVehicleST20(.17, .83);
        vehicleST20s[17] = new DistributionVehicleST20(.5, .5);
        vehicleST20s[18] = new DistributionVehicleST20(.8, .2);
        vehicleST20s[19] = new DistributionVehicleST20(.87, .13);
        return vehicleST20s;
    }

    static class DistributionMovement {
        final double conjointes;
        final double enlèvements;
        final double livraisons;
        enum Movement {conjointes, enlèvements, livraisons}

        public DistributionMovement(double conjointes, double enlèvements, double livraisons) {
            this.conjointes = conjointes;
            this.enlèvements = enlèvements;
            this.livraisons = livraisons;
        }
    }

     static class DistributionManagement {
        final double CPD;
        final double CPE;
        final double CA;
        enum  Management {CPD, CPE, CA}

        public DistributionManagement(double CPD, double CPE, double CA) {
            this.CPD = CPD;
            this.CPE = CPE;
            this.CA = CA;
        }
    }

    static class DistributionVehicle {
        final double Articulés;
        final double Porteurs;
        final double VUL;
        final double roues2_3;
        enum Vehicle {Articulés, Porteurs, VUL, roues2_3}

        public DistributionVehicle(double articulés, double porteurs, double VUL, double roues2_3) {
            Articulés = articulés;
            Porteurs = porteurs;
            this.VUL = VUL;
            this.roues2_3 = roues2_3;
        }
    }

    static class DistributionVehicleST20{
        final double PL;
        final double VUL;
        enum  VehicleST20 {PL, VUL}

        public DistributionVehicleST20(double PL, double VUL) {
            this.PL = PL;
            this.VUL = VUL;
        }
    }

}
