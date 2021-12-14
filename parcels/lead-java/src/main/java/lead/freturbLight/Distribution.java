package lead.freturbLight;

import java.util.Random;

import static lead.freturbLight.FirmData.Move;

public class Distribution {

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

    static Distribution[] distributions = generateDistribution();
    static DistributionVehicleST20[] distributionVehicles = vehicleDistributionST20();

    public static Move distributeLogistics(Move move, int st8, int st20, double centerDistance, Random random) {
        int[] variable = new int[3];
        if (st8 == 0) {
            return move;
        }
        Distribution distribution = distributions[st8-1];
        {
            double rnd = random.nextDouble();
            if (rnd < distribution.disMove.conjointes) {
                move.disMove = DistributionMovement.Movement.conjointes;
            } else if (rnd < distribution.disMove.conjointes + distribution.disMove.enlèvements) {
                move.disMove = DistributionMovement.Movement.enlèvements;
            } else {
                move.disMove = DistributionMovement.Movement.livraisons;
            }
        }
        {
            double rnd = random.nextDouble();
            if (rnd < distribution.disVeh.CPD) {
                move.disVeh = DistributionVehicle.Vehicle.CPD;
            } else if (rnd < distribution.disVeh.CPD + distribution.disVeh.CPE) {
                move.disVeh = DistributionVehicle.Vehicle.CPE;
            } else {
                move.disVeh = DistributionVehicle.Vehicle.CA;
                variable[1] = 1;
            }
        }
        {
            double rnd = random.nextDouble();
            if (rnd < distribution.disMan.Articulés) {
                move.disMan = DistributionManagement.Management.Articulés;
            } else if (rnd < distribution.disMan.Articulés + distribution.disMan.Porteurs) {
                move.disMan = DistributionManagement.Management.Porteurs;
            } else if (rnd < distribution.disMan.Articulés + distribution.disMan.Porteurs + distribution.disMan.VUL) {
                move.disMan = DistributionManagement.Management.VUL;
            } else {
                move.disMan = DistributionManagement.Management.roues2_3;
            }
            if (st20 != 0) {
                rnd = random.nextDouble();
                DistributionVehicleST20 distributionVehicleST20 = distributionVehicles[st20 - 1];
                if (rnd < distributionVehicleST20.LCV) {
                    move.disVeh20 = DistributionVehicleST20.VehicleST20.PL;
                    variable[2] = 1;
                } else {
                    move.disVeh20 = DistributionVehicleST20.VehicleST20.LCV;
                }
            }
        }
        move.travelDistance = variable[0] * TYPE_OF_MOVEMENT + variable[1] * MANAGEMENT_MODE + ACTIVITY_TYPE_ARRAY[st8 - 1] * VEHICLE_TYPE + variable[2] * ACTIVITY_TYPE + centerDistance * DISTANCE;
        return move;
    }

    private static Distribution[] generateDistribution() {
        Distribution[] distributions = new Distribution[8];
        distributions[0] = new Distribution(1,
                new DistributionMovement(.26,.34, .4),
                new DistributionVehicle(.427,.482,.091),
                new DistributionManagement(.005, .084, .911,.0));
        distributions[1] = new Distribution(2,
                new DistributionMovement(.15,.22, .63),
                new DistributionVehicle(.222,.364,.414),
                new DistributionManagement(.093, .171, .697,.04));
        distributions[2] = new Distribution(3,
                new DistributionMovement(.08,.42, .5),
                new DistributionVehicle(.1,.366,.534),
                new DistributionManagement(.074, .355, .508,.063));
        distributions[3] = new Distribution(4,
                new DistributionMovement(.04,.63, .33),
                new DistributionVehicle(.196,.274,.53),
                new DistributionManagement(.13, .438, .415,.016));
        distributions[4] = new Distribution(5,
                new DistributionMovement(.24,.17, .6),
                new DistributionVehicle(.083,.166,.751),
                new DistributionManagement(.331, .33, .338,.001));
        distributions[5] = new Distribution(6,
                new DistributionMovement(.14,.13, .73),
                new DistributionVehicle(.202,.448,.35),
                new DistributionManagement(.023, .293, .675,.009));
        distributions[6] = new Distribution(7,
                new DistributionMovement(.14,.28, .58),
                new DistributionVehicle(.068,.296,.636),
                new DistributionManagement(.016, .204, .685,.096));
        distributions[7] = new Distribution(8,
                new DistributionMovement(.04,.59, .27),
                new DistributionVehicle(.125,.219,.656),
                new DistributionManagement(.36, .493, .147,0));
        return distributions;
    }

    Distribution(int st8, DistributionMovement disMove, DistributionVehicle disVeh, DistributionManagement disMan) {
        this.st8 = st8;
        this.disMove = disMove;
        this.disVeh = disVeh;
        this.disMan = disMan;
    }

    static class DistributionMovement{
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

    static class DistributionVehicle{
        final double CPD;
        final double CPE;
        final double CA;
        enum  Vehicle {CPD, CPE, CA}

        public DistributionVehicle(double CPD, double CPE, double CA) {
            this.CPD = CPD;
            this.CPE = CPE;
            this.CA = CA;
        }
    }

    static class DistributionManagement{
        final double Articulés;
        final double Porteurs;
        final double VUL;
        final double roues2_3;
        enum Management {Articulés, Porteurs, VUL, roues2_3}

        public DistributionManagement(double articulés, double porteurs, double VUL, double roues2_3) {
            Articulés = articulés;
            Porteurs = porteurs;
            this.VUL = VUL;
            this.roues2_3 = roues2_3;
        }
    }

    static class DistributionVehicleST20{
        final double PL;
        final double LCV;
        enum  VehicleST20 {PL,LCV}

        public DistributionVehicleST20(double PL, double LCV) {
            this.PL = PL;
            this.LCV = LCV;
        }
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

}
