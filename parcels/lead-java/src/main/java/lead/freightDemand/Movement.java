package lead.freightDemand;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;


public class Movement {

    static private Map<String, MovementFunction> movementFunctionMap = createMovementFunctionMap();
//    static private Map<String, MovementFunction> movementFunctionMapOriginal = createMovementFunctionMapOriginal();
    static List<Movement> movementList = new ArrayList<>();

    private final static Distribution[] distributions = generateDistribution();
    private final static DistributionVehicleST20[] distributionVehicles = vehicleDistributionST20();
    private final static double TYPE_OF_MOVEMENT = 13.4637;
    private final static double MANAGEMENT_MODE = -1.4543;
    private final static double VEHICLE_TYPE = 3.6464;
    private final static double ACTIVITY_TYPE = 1.9224;
    private final static double DISTANCE = 0.4507;
    private final static int[] ACTIVITY_TYPE_ARRAY = {1, 1, 1, 0, 0, 0, 0, 1};

    DistributionMovement.Movements disMove;
    DistributionVehicle.Vehicle disVeh;
    DistributionManagement.Management disMan;
    DistributionVehicleST20.VehicleST20 disVeh20;
    RouteType routeType;
    double travelDistance;
    double centerDistance;
    int centerId;
    final String siret;
    final String siren;
    final Coord coord;
    final int st8;

    enum RouteType {direct, round}

    public Movement(double centerDistance, int centerId, FreightFacility freightFacility) {
        this.centerDistance = centerDistance;
        this.centerId = centerId;
        this.siret = freightFacility.siret;
        this.siren = freightFacility.siren;
        this.coord = freightFacility.getCoord();
        this.st8 = freightFacility.getSt8();
    }

//    static void calculateMovements(List<FreightFacility> freightFacilityList) throws Exception {
//        int count = 0;
//        for (FreightFacility freightFacility : freightFacilityList) {
//            try {
//                freightFacility.movements = selectCorrectFunction(movementFunctionMapOriginal.get(freightFacility.getSt45()), freightFacility.getEmployees());
//            } catch (Exception e) {
//                count++;
//                continue;
//            }
//        }
//        System.out.println("APE ist empty: " + count);
//    }

    static void calculateMovementsForST8(List<FreightFacility> freightFacilityList) throws Exception {
        for (FreightFacility freightFacility : freightFacilityList) {
            int count = 0;
            double movments = 0;
            for (String key : movementFunctionMap.keySet()) {
                if (key.split("\\.")[0].equals(String.valueOf(freightFacility.getSt8()))) {
                    movments += selectCorrectFunction(movementFunctionMap.get(key), freightFacility.getEmployees());
                    count++;
                }
            }
            if (count != 0) {
                freightFacility.movements = (int) Math.round(movments / (count));
            }
        }
    }


    private static double selectCorrectFunction(MovementFunction movementFunction, int employees) throws Exception {
        if (movementFunction.type.equals("RATIO_Constant_LAET")) {
            return movementFunction.x * employees;
        } else if (movementFunction.type.equals("FUNCTION_LIN")) {
            return movementFunction.x * employees + movementFunction.y;
        } else if (movementFunction.type.equals("FUNCTION_LOG")) {
            return movementFunction.x * Math.log(employees) + movementFunction.y;
        } else if (movementFunction.type.equals("FUNCTION_LOG_NO_CONS")) {
            return movementFunction.x * Math.log(employees);
        } else {
            throw new Exception("no function type for this establishment");
        }
    }

    private static Map<String, MovementFunction> createMovementFunctionMapOriginal() {
        Map<String, MovementFunction> movementFunctionMap = new HashMap<>();
        movementFunctionMap.put("1", new MovementFunction("FUNCTION_LOG_NO_CONS",2.396,0));
        movementFunctionMap.put("2-2", new MovementFunction("FUNCTION_LOG",1.933,14.307));
        movementFunctionMap.put("2-3", new MovementFunction("FUNCTION_LIN",3.132,-2.2));
        movementFunctionMap.put("2-4", new MovementFunction("FUNCTION_LIN",0.789,2.492));
        movementFunctionMap.put("26Ha", new MovementFunction("FUNCTION_LOG",6.416,0.195));
        movementFunctionMap.put("26Mi", new MovementFunction("FUNCTION_LIN",0.101,1.628));
        movementFunctionMap.put("26Mo", new MovementFunction("FUNCTION_LIN",0.14,5.059));
        movementFunctionMap.put("3", new MovementFunction("FUNCTION_LIN",1.536,3.077));
        movementFunctionMap.put("4-2", new MovementFunction("FUNCTION_LIN",1.172,6.893));
        movementFunctionMap.put("5-2", new MovementFunction("FUNCTION_LOG",1.045,13.342));
        movementFunctionMap.put("5-4", new MovementFunction("FUNCTION_LOG_NO_CONS",6.246,0));
        movementFunctionMap.put("5-5", new MovementFunction("FUNCTION_LIN",1.8,0.719));
        movementFunctionMap.put("4-6", new MovementFunction("RATIO_Constant_LAET",2.59,0));
        movementFunctionMap.put("4-7", new MovementFunction("FUNCTION_LIN",0.176,8.75));
        movementFunctionMap.put("34-2", new MovementFunction("FUNCTION_LIN",0.614,19.720));
        movementFunctionMap.put("34-3", new MovementFunction("FUNCTION_LIN",0.352,7.574));
        movementFunctionMap.put("7-2", new MovementFunction("FUNCTION_LOG",15.086,0.026));
        movementFunctionMap.put("8-2", new MovementFunction("RATIO_Constant_LAET",2.57,0));
        movementFunctionMap.put("9-2", new MovementFunction("RATIO_Constant_LAET",7.62,0));
        movementFunctionMap.put("7-3", new MovementFunction("RATIO_Constant_LAET",2.3,0));
        movementFunctionMap.put("8-3", new MovementFunction("RATIO_Constant_LAET",2.52,0));
        movementFunctionMap.put("9-3", new MovementFunction("FUNCTION_LOG",19.31,10.01));
        movementFunctionMap.put("10", new MovementFunction("FUNCTION_LIN",0.108,79.785));
        movementFunctionMap.put("11", new MovementFunction("FUNCTION_LIN",0.961,0.793));
        movementFunctionMap.put("12", new MovementFunction("FUNCTION_LOG_NO_CONS",10.626,0));
        movementFunctionMap.put("13", new MovementFunction("RATIO_Constant_LAET",1.57,0));
        movementFunctionMap.put("14", new MovementFunction("FUNCTION_LOG_NO_CONS",3.029,0));
        movementFunctionMap.put("15", new MovementFunction("FUNCTION_LOG",5.124,3.362));
        movementFunctionMap.put("16", new MovementFunction("RATIO_Constant_LAET",1.55,0));
        movementFunctionMap.put("17", new MovementFunction("FUNCTION_LOG",1.057,5.364));
        movementFunctionMap.put("18", new MovementFunction("FUNCTION_LIN",0.329,4.277));
        movementFunctionMap.put("19", new MovementFunction("FUNCTION_LOG",4.437,12.43));
        movementFunctionMap.put("20", new MovementFunction("FUNCTION_LOG",0.107,3.3347));
        movementFunctionMap.put("21", new MovementFunction("FUNCTION_LIN",1.413,0.685));
        movementFunctionMap.put("22", new MovementFunction("FUNCTION_LOG",4.998,16.764));
        movementFunctionMap.put("23", new MovementFunction("FUNCTION_LOG",3.304,2.748));
        movementFunctionMap.put("29", new MovementFunction("FUNCTION_LOG",-0.681,5.015));
        movementFunctionMap.put("6", new MovementFunction("FUNCTION_LOG",0.795,1.053));
        movementFunctionMap.put("25", new MovementFunction("FUNCTION_LIN",0.074,1.801));
        movementFunctionMap.put("27-2", new MovementFunction("RATIO_Constant_LAET",0.64,0));
        movementFunctionMap.put("27-3", new MovementFunction("FUNCTION_LOG_NO_CONS",4.657,0));
        movementFunctionMap.put("26Fa", new MovementFunction("FUNCTION_LIN",0.157,1.941));
        movementFunctionMap.put("30", new MovementFunction("RATIO_Constant_LAET",12.12,0));
        movementFunctionMap.put("28-2", new MovementFunction("RATIO_Constant_LAET",4.37,0));
        movementFunctionMap.put("28-3", new MovementFunction("FUNCTION_LIN",4.841,9.429));
        return movementFunctionMap;
    }

    private static Map<String, MovementFunction> createMovementFunctionMap() {
        Map<String, MovementFunction> movementFunctionMap = new HashMap<>();
        movementFunctionMap.put("1.1", new MovementFunction("FUNCTION_LOG_NO_CONS",2.396,0));
        movementFunctionMap.put("2.1", new MovementFunction("FUNCTION_LOG",1.933,14.307));
        movementFunctionMap.put("2.2", new MovementFunction("FUNCTION_LIN",3.132,-2.2));
        movementFunctionMap.put("2.3", new MovementFunction("FUNCTION_LIN",0.789,2.492));
        movementFunctionMap.put("2.4", new MovementFunction("FUNCTION_LOG",6.416,0.195));
        movementFunctionMap.put("2.5", new MovementFunction("FUNCTION_LIN",0.101,1.628));
        movementFunctionMap.put("2.6", new MovementFunction("FUNCTION_LIN",0.14,5.059));
        movementFunctionMap.put("3.1", new MovementFunction("FUNCTION_LIN",1.536,3.077));
        movementFunctionMap.put("3.2", new MovementFunction("FUNCTION_LIN",1.172,6.893));
        movementFunctionMap.put("3.3", new MovementFunction("FUNCTION_LOG",1.045,13.342));
        movementFunctionMap.put("3.4", new MovementFunction("FUNCTION_LOG_NO_CONS",6.246,0));
        movementFunctionMap.put("3.5", new MovementFunction("FUNCTION_LIN",1.8,0.719));
        movementFunctionMap.put("3.6", new MovementFunction("RATIO_Constant_LAET",2.59,0));
        movementFunctionMap.put("3.7", new MovementFunction("FUNCTION_LIN",0.176,8.75));
        movementFunctionMap.put("3.8", new MovementFunction("FUNCTION_LIN",0.614,19.720));
        movementFunctionMap.put("3.9", new MovementFunction("FUNCTION_LIN",0.352,7.574));
        movementFunctionMap.put("4.1", new MovementFunction("FUNCTION_LOG",15.086,0.026));
        movementFunctionMap.put("4.2", new MovementFunction("RATIO_Constant_LAET",2.57,0));
        movementFunctionMap.put("4.3", new MovementFunction("RATIO_Constant_LAET",7.62,0));
        movementFunctionMap.put("4.4", new MovementFunction("RATIO_Constant_LAET",2.3,0));
        movementFunctionMap.put("4.5", new MovementFunction("RATIO_Constant_LAET",2.52,0));
        movementFunctionMap.put("4.6", new MovementFunction("FUNCTION_LOG",19.31,10.01));
        movementFunctionMap.put("5.1", new MovementFunction("FUNCTION_LIN",0.108,79.785));
        movementFunctionMap.put("5.2", new MovementFunction("FUNCTION_LIN",0.961,0.793));
        movementFunctionMap.put("5.3",new MovementFunction("FUNCTION_LOG_NO_CONS",10.626,0));
        movementFunctionMap.put("13", new MovementFunction("RATIO_Constant_LAET",1.57,0));
        movementFunctionMap.put("6.1", new MovementFunction("FUNCTION_LOG_NO_CONS",3.029,0));
        movementFunctionMap.put("6.2", new MovementFunction("FUNCTION_LOG",5.124,3.362));
        movementFunctionMap.put("6.3", new MovementFunction("RATIO_Constant_LAET",1.55,0));
        movementFunctionMap.put("6.4", new MovementFunction("FUNCTION_LOG",1.057,5.364));
        movementFunctionMap.put("6.5", new MovementFunction("FUNCTION_LIN",0.329,4.277));
        movementFunctionMap.put("6.6", new MovementFunction("FUNCTION_LOG",4.437,12.43));
        movementFunctionMap.put("6.7", new MovementFunction("FUNCTION_LOG",0.107,3.3347));
        movementFunctionMap.put("6.8", new MovementFunction("FUNCTION_LIN",1.413,0.685));
        movementFunctionMap.put("6.9", new MovementFunction("FUNCTION_LOG",4.998,16.764));
        movementFunctionMap.put("6.10", new MovementFunction("FUNCTION_LOG",3.304,2.748));
        movementFunctionMap.put("6.11", new MovementFunction("FUNCTION_LOG",-0.681,5.015));
        movementFunctionMap.put("7.1", new MovementFunction("FUNCTION_LOG",0.795,1.053));
        movementFunctionMap.put("7.2", new MovementFunction("FUNCTION_LIN",0.074,1.801));
        movementFunctionMap.put("7.3", new MovementFunction("RATIO_Constant_LAET",0.64,0));
        movementFunctionMap.put("7.4", new MovementFunction("FUNCTION_LOG_NO_CONS",4.657,0));
        movementFunctionMap.put("7.5", new MovementFunction("FUNCTION_LIN",0.157,1.941));
        movementFunctionMap.put("8.1", new MovementFunction("RATIO_Constant_LAET",12.12,0));
        movementFunctionMap.put("8.2", new MovementFunction("RATIO_Constant_LAET",4.37,0));
        movementFunctionMap.put("8.3", new MovementFunction("FUNCTION_LIN",4.841,9.429));
        return movementFunctionMap;
    }

    private static Distribution[] generateDistribution() {
        Distribution[] distributions = new Distribution[8];
        distributions[0] = new Distribution(1,
                new DistributionMovement(.26,.34, .4),
                new DistributionManagement(.43,.18,.09),
                new DistributionVehicle(.005, .084, .911,.0));
        distributions[1] = new Distribution(2,
                new DistributionMovement(.15,.22, .63),
                new DistributionManagement(.25,.28,.46),
                new DistributionVehicle(.06, .17, .63,.04));
        distributions[2] = new Distribution(3,
                new DistributionMovement(.08,.42, .5),
                new DistributionManagement(.08,.34,.58),
                new DistributionVehicle(.04, .35, .54,.07));
        distributions[3] = new Distribution(4,
                new DistributionMovement(.04,.63, .33),
                new DistributionManagement(.18,.27,.56),
                new DistributionVehicle(.13, .4, .46,.03));
        distributions[4] = new Distribution(5,
                new DistributionMovement(.24,.17, .6),
                new DistributionManagement(.11,.15,.74),
                new DistributionVehicle(.3, .35, .34,.01));
        distributions[5] = new Distribution(6,
                new DistributionMovement(.14,.13, .73),
                new DistributionManagement(.19,.45,.37),
                new DistributionVehicle(.02, .30, .67,.01));
        distributions[6] = new Distribution(7,
                new DistributionMovement(.14,.28, .58),
                new DistributionManagement(.07,.37,.56),
                new DistributionVehicle(.05, .27, .58,.10));
        distributions[7] = new Distribution(8,
                new DistributionMovement(.04,.59, .27),
                new DistributionManagement(.11,.27,.62),
                new DistributionVehicle(.35, .53, .08,.04));
        return distributions;
    }

    private static DistributionVehicleST20[] vehicleDistributionST20(){
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

    public static void distributeProperties(List<FreightFacility> freightFacilityList, List<Coord> centers) {
        for (FreightFacility freightFacility : freightFacilityList) {
            int centerId = 0;
            int count = 1;
            double centerDistance = Double.MAX_VALUE;
            for (Coord centerCoord : centers) {
                double tmpDistance = CoordUtils.calcEuclideanDistance(freightFacility.getCoord(), centerCoord)/1000;
                if (tmpDistance < centerDistance) {
                    freightFacility.centerDistance = centerDistance;
                    centerDistance = tmpDistance;
                    centerId = count;
                }
                count++;
            }
            for (int i = 0; i < freightFacility.movements; i++) {
                Movement movement = new Movement(centerDistance, centerId, freightFacility);
                movement.distributeLogistics(freightFacility.getEmployees());
                movementList.add(movement);
            }
        }
    }

    private void distributeLogistics(int employees) {
        int[] variable = new int[3];
        Distribution distribution = distributions[st8-1];
        {
            double rnd = FreightDemand.random.nextDouble();
            if (rnd < distribution.disMove.conjointes) {
                this.disMove = DistributionMovement.Movements.conjointes;
            } else if (rnd < distribution.disMove.conjointes + distribution.disMove.enlèvements) {
                this.disMove = DistributionMovement.Movements.enlèvements;
            } else {
                this.disMove = DistributionMovement.Movements.livraisons;
            }
        }
        {
            double rnd = FreightDemand.random.nextDouble();
            if (rnd < distribution.disMan.CPD) {
                this.disMan = DistributionManagement.Management.CPD;
            } else if (rnd < distribution.disMan.CPD + distribution.disMan.CPE) {
                this.disMan = DistributionManagement.Management.CPE;
            } else {
                this.disMan = DistributionManagement.Management.CA;
                variable[1] = 1;
            }
        }
        {
            double rnd = FreightDemand.random.nextDouble();
            if (rnd < distribution.disVeh.Articulés) {
                this.disVeh = DistributionVehicle.Vehicle.Articulés;
            } else if (rnd < distribution.disVeh.Articulés + distribution.disVeh.Porteurs) {
                this.disVeh = DistributionVehicle.Vehicle.Porteurs;
            } else if (rnd < distribution.disVeh.Articulés + distribution.disVeh.Porteurs + distribution.disVeh.VUL) {
                this.disVeh = DistributionVehicle.Vehicle.VUL;
            } else {
                this.disVeh = DistributionVehicle.Vehicle.roues2_3;
            }
            rnd = FreightDemand.random.nextDouble();
            DistributionVehicleST20 distributionVehicleST20 = distributionVehicles[generateST20Map(st8, employees)];
            if (rnd < distributionVehicleST20.PL) {
                this.disVeh20 = DistributionVehicleST20.VehicleST20.PL;
                variable[2] = 1;
            } else {
                this.disVeh20 = DistributionVehicleST20.VehicleST20.VUL;
            }
        }
        variable[0] = getTypeOfMovement();
        if (variable[0] == 1) {
            this.routeType = RouteType.direct;
        } else {
            this.routeType = RouteType.round;
        }

        double tmpTravelDistance = variable[0] * TYPE_OF_MOVEMENT + variable[1] * MANAGEMENT_MODE + ACTIVITY_TYPE_ARRAY[st8 - 1] * ACTIVITY_TYPE + variable[2] * VEHICLE_TYPE + centerDistance * DISTANCE;
        this.travelDistance = getSample(tmpTravelDistance);;
    }

    private static double getSample(double tmpTravelDistance) {
        double sample = FreightDemand.random.nextDouble();
        return tmpTravelDistance * -Math.log(sample);
    }

    private static int getTypeOfMovement() {
        double rnd = FreightDemand.random.nextDouble();
        if (rnd > 0.75) {
            return 1;
        }
        return 0;
    }

    static private int generateST20Map(int ST8, int employee) {
        Map<String, String> st8Map = new HashMap<>();
        // fill  the map, data from Adrian Beziat
        {
            if (ST8 == 1) {
                return 0;
            } else if (ST8 == 2 && (employee == 2 || employee == 1)) {
                return 1;
            } else if (ST8 == 2 && employee > 2 && employee < 7) {
                return 2;
            } else if (ST8 == 2 && employee > 6) {
                return 3;
            } else if (ST8 == 3 && employee > 0 && employee < 5) {
                return 4;
            } else if (ST8 == 3 && employee > 4 && employee < 11) {
                return 5;
            } else if (ST8 == 3 && employee > 10) {
                return 6;
            } else if (ST8 == 4 && employee > 0 && employee < 7) {
                return 7;
            } else if (ST8 == 4 && employee > 6 && employee < 20) {
                return 8;
            } else if (ST8 == 4 && employee > 19) {
                return 9;
            } else if (ST8 == 5 && employee > 0 && employee < 71) {
                return 10;
            } else if (ST8 == 5 && employee > 70) {
                return 11;
            } else if (ST8 == 6 && employee > 0 && employee < 3) {
                return 12;
            } else if (ST8 == 6 && employee > 2 && employee < 6) {
                return 13;
            } else if (ST8 == 6 && employee > 5) {
                return 14;
            } else if (ST8 == 7 && employee > 0 && employee < 4) {
                return 15;
            } else if (ST8 == 7 && employee > 4 && employee < 19) {
                return 16;
            } else if (ST8 == 7 && employee > 18) {
                return 17;
            } else if (ST8 == 8 && employee > 0 && employee < 15) {
                return 18;
            } else if (ST8 == 8 && employee > 14) {
                return 19;
            }
        }
        return 0;
    }

    private static class MovementFunction {

        String type;
        double x;
        double y;

        MovementFunction(String type, double x, double y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

    }

    static class Distribution {

        int st8;
        DistributionMovement disMove;
        DistributionVehicle disVeh;
        DistributionManagement disMan;

        Distribution(int st8, DistributionMovement disMove, DistributionManagement disMan, DistributionVehicle disVeh) {
            this.st8 = st8;
            this.disMove = disMove;
            this.disMan = disMan;
            this.disVeh = disVeh;
        }

    }

    static class DistributionMovement {
        final double conjointes;
        final double enlèvements;
        final double livraisons;
        enum Movements {conjointes, enlèvements, livraisons}

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
