package lead.freightDemand;

import org.matsim.api.core.v01.Coord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class FreightDemand {

    static Random random = new Random(123);
    static List<RoundTour> tripsRoundList = new ArrayList<>();
    static List<DirectTour> tripsDirectList = new ArrayList<>();

    private final String ETABLISSEMENT_FILE;
    private final String ACTIVITY_CLASSES;
    private final String AREA_File;
    private final List<Coord> CENTERS;

    static String outputLocation = "";

    public FreightDemand (String etablissementFile, String activityClasses, String areaFile, List<Coord> centers, String output) {
        this.ETABLISSEMENT_FILE = etablissementFile;
        this.AREA_File = areaFile;
        this.CENTERS = centers;
        this.ACTIVITY_CLASSES = activityClasses;
        this.outputLocation = output;
    }

    public void run() throws Exception {
        List<FreightFacility> freightFacilityList = ReadFacilitiesFile.read(ETABLISSEMENT_FILE, AREA_File);
//        ActivityClasses.setActivityClassesOriginal(ACTIVITY_CLASSES, freightFacilityList);
        ActivityClasses.setActivityClasses(freightFacilityList);
        Movement.calculateMovements(freightFacilityList);
//        Movement.calculateMovementsForST8(freightFacilityList);
        Movement.distributeProperties(freightFacilityList, CENTERS);
        printEmployees();
        printClasses(freightFacilityList);
//        writeAll();
        fixDistribution();
        creatingToursPreparation();
        writeFiles(tripsDirectList, tripsRoundList);
        List<Trips> weekDayTours = DayAndTimeDistribution.generateDistribution(tripsDirectList, tripsRoundList);
        FreightMatsimPopulation.generateMATSimFreightPopulation(weekDayTours);
        System.out.println("Done");
    }

    private void fixDistribution() {
        List<Movement> movementListRound_Not_LIV_PL_CA = new ArrayList<>();
        List<Movement> movementListRound_Not_LIV_PL_CPD = new ArrayList<>();
        List<Movement> movementListRound_Not_LIV_PL_CPE = new ArrayList<>();
        List<Movement> movementListRound_Not_LIV_VUL_CA = new ArrayList<>();
        List<Movement> movementListRound_Not_LIV_VUL_CPD = new ArrayList<>();
        List<Movement> movementListRound_Not_LIV_VUL_CPE = new ArrayList<>();
        List<Movement> movementListDirect_LIV_PL_CA = new ArrayList<>();
        List<Movement> movementListDirect_LIV_PL_CPD = new ArrayList<>();
        List<Movement> movementListDirect_LIV_PL_CPE = new ArrayList<>();
        List<Movement> movementListDirect_LIV_VUL_CA = new ArrayList<>();
        List<Movement> movementListDirect_LIV_VUL_CPD = new ArrayList<>();
        List<Movement> movementListDirect_LIV_VUL_CPE = new ArrayList<>();
        List<Movement> movementListRound_LIV_PL_CA = new ArrayList<>();
        List<Movement> movementListRound_LIV_PL_CPD = new ArrayList<>();
        List<Movement> movementListRound_LIV_PL_CPE = new ArrayList<>();
        List<Movement> movementListRound_LIV_VUL_CA = new ArrayList<>();
        List<Movement> movementListRound_LIV_VUL_CPD = new ArrayList<>();
        List<Movement> movementListRound_LIV_VUL_CPE = new ArrayList<>();
        List<Movement> movementListDirect_Not_LIV_PL_CA = new ArrayList<>();
        List<Movement> movementListDirect_Not_LIV_PL_CPD = new ArrayList<>();
        List<Movement> movementListDirect_Not_LIV_PL_CPE = new ArrayList<>();
        List<Movement> movementListDirect_Not_LIV_VUL_CA = new ArrayList<>();
        List<Movement> movementListDirect_Not_LIV_VUL_CPD = new ArrayList<>();
        List<Movement> movementListDirect_Not_LIV_VUL_CPE = new ArrayList<>();
        for (Movement movement : Movement.movementList) {
            if (movement.routeType.equals(Movement.RouteType.round) && !movement.disMove.equals(Movement.DistributionMovement.Movements.livraisons)) {
                if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListRound_Not_LIV_PL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListRound_Not_LIV_PL_CPD.add(movement);
                    } else {
                        movementListRound_Not_LIV_PL_CPE.add(movement);
                    }
                } else {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListRound_Not_LIV_VUL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListRound_Not_LIV_VUL_CPD.add(movement);
                    } else {
                        movementListRound_Not_LIV_VUL_CPE.add(movement);
                    }
                }
            } else if (movement.routeType.equals(Movement.RouteType.direct) && movement.disMove.equals(Movement.DistributionMovement.Movements.livraisons)) {
                if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListDirect_LIV_PL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListDirect_LIV_PL_CPD.add(movement);
                    } else {
                        movementListDirect_LIV_PL_CPE.add(movement);
                    }
                } else {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListDirect_LIV_VUL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListDirect_LIV_VUL_CPD.add(movement);
                    } else {
                        movementListDirect_LIV_VUL_CPE.add(movement);
                    }
                }
            }
            if (movement.routeType.equals(Movement.RouteType.round) && movement.disMove.equals(Movement.DistributionMovement.Movements.livraisons)) {
                if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListRound_LIV_PL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListRound_LIV_PL_CPD.add(movement);
                    } else {
                        movementListRound_LIV_PL_CPE.add(movement);
                    }
                } else {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListRound_LIV_VUL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListRound_LIV_VUL_CPD.add(movement);
                    } else {
                        movementListRound_LIV_VUL_CPE.add(movement);
                    }
                }
            } else if (movement.routeType.equals(Movement.RouteType.direct) && !movement.disMove.equals(Movement.DistributionMovement.Movements.livraisons)) {
                if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListDirect_Not_LIV_PL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListDirect_Not_LIV_PL_CPD.add(movement);
                    } else {
                        movementListDirect_Not_LIV_PL_CPE.add(movement);
                    }
                } else {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListDirect_Not_LIV_VUL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListDirect_Not_LIV_VUL_CPD.add(movement);
                    } else {
                        movementListDirect_Not_LIV_VUL_CPE.add(movement);
                    }
                }
            }
        }

        if (Movement.directDiff_PL_CA < 0) {
            var diff = Math.abs(Movement.directDiff_PL_CA);
            while (diff > 1) {
                var movement1 = movementListDirect_LIV_PL_CA.get(random.nextInt(movementListDirect_LIV_PL_CA.size()));
                var movement2 = movementListRound_Not_LIV_PL_CA.get(random.nextInt(movementListRound_Not_LIV_PL_CA.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_LIV_PL_CA.remove(movement1);
                movementListRound_Not_LIV_PL_CA.remove(movement2);
                diff -= 2;
            }
        } else if (Movement.directDiff_PL_CA > 0) {
            var diff = Movement.directDiff_PL_CA;
            while (diff > 1) {
                var movement1 = movementListDirect_Not_LIV_PL_CA.get(random.nextInt(movementListDirect_Not_LIV_PL_CA.size()));
                var movement2 = movementListRound_LIV_PL_CA.get(random.nextInt(movementListRound_LIV_PL_CA.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_Not_LIV_PL_CA.remove(movement1);
                movementListRound_LIV_PL_CA.remove(movement2);
                diff -= 2;
            }
        }
        if (Movement.directDiff_PL_CPD < 0) {
            var diff = Math.abs(Movement.directDiff_PL_CPD);
            while (diff > 1) {
                var movement1 = movementListDirect_LIV_PL_CPD.get(random.nextInt(movementListDirect_LIV_PL_CPD.size()));
                var movement2 = movementListRound_Not_LIV_PL_CPD.get(random.nextInt(movementListRound_Not_LIV_PL_CPD.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_LIV_PL_CPD.remove(movement1);
                movementListRound_Not_LIV_PL_CPD.remove(movement2);
                diff -= 2;
            }
        } else if (Movement.directDiff_PL_CPD > 0) {
            var diff = Math.abs(Movement.directDiff_PL_CPD);
            while (diff > 1) {
                var movement1 = movementListDirect_Not_LIV_PL_CPD.get(random.nextInt(movementListDirect_Not_LIV_PL_CPD.size()));
                var movement2 = movementListRound_LIV_PL_CPD.get(random.nextInt(movementListRound_LIV_PL_CPD.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_Not_LIV_PL_CPD.remove(movement1);
                movementListRound_LIV_PL_CPD.remove(movement2);
                diff -= 2;
            }
        }
        if (Movement.directDiff_PL_CPE < 0) {
            var diff = Math.abs(Movement.directDiff_PL_CPE);
            while (diff > 1) {
                var movement1 = movementListDirect_LIV_PL_CPE.get(random.nextInt(movementListDirect_LIV_PL_CPE.size()));
                var movement2 = movementListRound_Not_LIV_PL_CPE.get(random.nextInt(movementListRound_Not_LIV_PL_CPE.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_LIV_PL_CPE.remove(movement1);
                movementListRound_Not_LIV_PL_CPE.remove(movement2);
                diff -= 2;
            }
        } else if (Movement.directDiff_PL_CPE > 0) {
            var diff = Math.abs(Movement.directDiff_PL_CPE);
            while (diff > 1) {
                var movement1 = movementListDirect_Not_LIV_PL_CPE.get(random.nextInt(movementListDirect_Not_LIV_PL_CPE.size()));
                var movement2 = movementListRound_LIV_PL_CPE.get(random.nextInt(movementListRound_LIV_PL_CPE.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_Not_LIV_PL_CPE.remove(movement1);
                movementListRound_LIV_PL_CPE.remove(movement2);
                diff -= 2;
            }
        }
        if (Movement.directDiff_VUL_CA < 0) {
            var diff = Math.abs(Movement.directDiff_VUL_CA);
            while (diff > 1) {
                var movement1 = movementListDirect_LIV_VUL_CA.get(random.nextInt(movementListDirect_LIV_VUL_CA.size()));
                var movement2 = movementListRound_Not_LIV_VUL_CA.get(random.nextInt(movementListRound_Not_LIV_VUL_CA.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_LIV_VUL_CA.remove(movement1);
                movementListRound_Not_LIV_VUL_CA.remove(movement2);
                diff -= 2;
            }
        } else if (Movement.directDiff_VUL_CA > 0) {
            var diff = Math.abs(Movement.directDiff_VUL_CA);
            while (diff > 1) {
                var movement1 = movementListDirect_Not_LIV_VUL_CA.get(random.nextInt(movementListDirect_Not_LIV_VUL_CA.size()));
                var movement2 = movementListRound_LIV_VUL_CA.get(random.nextInt(movementListRound_LIV_VUL_CA.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_Not_LIV_VUL_CA.remove(movement1);
                movementListRound_LIV_VUL_CA.remove(movement2);
                diff -= 2;
            }
        }
        if (Movement.directDiff_VUL_CPD < 0) {
            var diff = Math.abs(Movement.directDiff_VUL_CPD);
            while (diff > 1) {
                var movement1 = movementListDirect_LIV_VUL_CPD.get(random.nextInt(movementListDirect_LIV_VUL_CPD.size()));
                var movement2 = movementListRound_Not_LIV_VUL_CPD.get(random.nextInt(movementListRound_Not_LIV_VUL_CPD.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_LIV_VUL_CPD.remove(movement1);
                movementListRound_Not_LIV_VUL_CPD.remove(movement2);
                diff -= 2;
            }
        } else if (Movement.directDiff_VUL_CPD > 0) {
            var diff = Math.abs(Movement.directDiff_VUL_CPD);
            while (diff > 1) {
                var movement1 = movementListDirect_Not_LIV_VUL_CPD.get(random.nextInt(movementListDirect_Not_LIV_VUL_CPD.size()));
                var movement2 = movementListRound_LIV_VUL_CPD.get(random.nextInt(movementListRound_LIV_VUL_CPD.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_Not_LIV_VUL_CPD.remove(movement1);
                movementListRound_LIV_VUL_CPD.remove(movement2);
                diff -= 2;
            }
        }
        if (Movement.directDiff_VUL_CPE < 0) {
            var diff = Math.abs(Movement.directDiff_VUL_CPE);
            while (diff > 1) {
                var movement1 = movementListDirect_LIV_VUL_CPE.get(random.nextInt(movementListDirect_LIV_VUL_CPE.size()));
                var movement2 = movementListRound_Not_LIV_VUL_CPE.get(random.nextInt(movementListRound_Not_LIV_VUL_CPE.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_LIV_VUL_CPE.remove(movement1);
                movementListRound_Not_LIV_VUL_CPE.remove(movement2);
                diff -= 2;
            }
        } else if (Movement.directDiff_VUL_CPE > 0) {
            var diff = Math.abs(Movement.directDiff_VUL_CPE);
            while (diff > 1) {
                var movement1 = movementListDirect_Not_LIV_VUL_CPE.get(random.nextInt(movementListDirect_Not_LIV_VUL_CPE.size()));
                var movement2 = movementListRound_LIV_VUL_CPE.get(random.nextInt(movementListRound_LIV_VUL_CPE.size()));
                movement1.routeType = Movement.RouteType.round;
                movement2.routeType = Movement.RouteType.direct;
                movementListDirect_Not_LIV_VUL_CPE.remove(movement1);
                movementListRound_LIV_VUL_CPE.remove(movement2);
                diff -= 2;
            }
        }
    }


    private void writeFiles(List<DirectTour> tripsDirectList, List<RoundTour> tripsRoundList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation + "_roundRoutes.txt"))) {
            writer.write("startX;startY;score;distance;linestring");
            writer.newLine();
            for (RoundTour trip : tripsRoundList){
                writer.write(trip.toString());
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation + "_directRoutes.txt"))) {
            writer.write("startX;startY;endX;endY;score;distance;linestring");
            writer.newLine();
            for (DirectTour trip : tripsDirectList){
                writer.write(trip.toString());
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void creatingToursPreparation() throws InterruptedException {
        List<Movement> movementListRoundPL_CA = new ArrayList<>();
        List<Movement> movementListRoundPL_CPD = new ArrayList<>();
        List<Movement> movementListRoundPL_CPE = new ArrayList<>();
        List<Movement> movementListRoundVUL_CA = new ArrayList<>();
        List<Movement> movementListRoundVUL_CPD = new ArrayList<>();
        List<Movement> movementListRoundVUL_CPE = new ArrayList<>();
        List<Movement> movementListDirectPL_CA = new ArrayList<>();
        List<Movement> movementListDirectPL_CPD = new ArrayList<>();
        List<Movement> movementListDirectPL_CPE = new ArrayList<>();
        List<Movement> movementListDirectVUL_CA = new ArrayList<>();
        List<Movement> movementListDirectVUL_CPD = new ArrayList<>();
        List<Movement> movementListDirectVUL_CPE = new ArrayList<>();
        for (Movement movement : Movement.movementList) {
            if (movement.routeType.equals(Movement.RouteType.round)) {
                if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListRoundPL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListRoundPL_CPD.add(movement);
                    } else {
                        movementListRoundPL_CPE.add(movement);
                    }
                } else {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListRoundVUL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListRoundVUL_CPD.add(movement);
                    } else {
                        movementListRoundVUL_CPE.add(movement);
                    }
                }
            } else {
                if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListDirectPL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListDirectPL_CPD.add(movement);
                    } else {
                        movementListDirectPL_CPE.add(movement);
                    }
                } else {
                    if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                        movementListDirectVUL_CA.add(movement);
                    } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                        movementListDirectVUL_CPD.add(movement);
                    } else {
                        movementListDirectVUL_CPE.add(movement);
                    }
                }
            }
        }

        System.out.println("Start Threads");
        RunParallelizationRoutes t1 = new RunParallelizationRoutes(movementListRoundPL_CA);
        RunParallelizationRoutes t2 = new RunParallelizationRoutes(movementListRoundPL_CPD);
        RunParallelizationRoutes t3 = new RunParallelizationRoutes(movementListRoundPL_CPE);
        RunParallelizationRoutes t4 = new RunParallelizationRoutes(movementListRoundVUL_CA);
        RunParallelizationRoutes t5 = new RunParallelizationRoutes(movementListRoundVUL_CPD);
        RunParallelizationRoutes t6 = new RunParallelizationRoutes(movementListRoundVUL_CPE);
        RunParallelizationRoutes t7 = new RunParallelizationRoutes(movementListDirectPL_CA);
        RunParallelizationRoutes t8 = new RunParallelizationRoutes(movementListDirectPL_CPD);
        RunParallelizationRoutes t9 = new RunParallelizationRoutes(movementListDirectPL_CPE);
        RunParallelizationRoutes t10 = new RunParallelizationRoutes(movementListDirectVUL_CA);
        RunParallelizationRoutes t11 = new RunParallelizationRoutes(movementListDirectVUL_CPD);
        RunParallelizationRoutes t12 = new RunParallelizationRoutes(movementListDirectVUL_CPE);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();
        t11.start();
        t12.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();
        t8.join();
        t9.join();
        t10.join();
        t11.join();
        t12.join();
    }

    private void writeAll() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ape_all.txt"))) {
            writer.write("ape;count");
            for (Map.Entry<String, Integer> trip : ActivityClasses.allMap.entrySet()) {
                writer.newLine();
                writer.write(trip.getKey() + ";" + trip.getValue());
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printClasses(List<FreightFacility> freightFacilityList) {
        int[] amountFirm = new int[8];
        int[] employeesFirm = new int[8];
        double[] movementsFirm = new double[8];
        for (FreightFacility freightFacility : freightFacilityList) {
            if (freightFacility.getSt8() != 0) {
                int a = freightFacility.getSt8() - 1;
                int af = amountFirm[a] + 1;
                int ef = employeesFirm[a] + freightFacility.getEmployees();
                double mf = movementsFirm[a] + freightFacility.movements;
                amountFirm[a] = af;
                employeesFirm[a] = ef;
                movementsFirm[a] = mf;
            }

        }
        double[] kilometer = new double[8];
        for (Movement movement : Movement.movementList) {
            if (movement.st8 != 0) {
                double km = movement.travelDistance;
                kilometer[movement.st8 -1] = kilometer[movement.st8-1] + km;
            }
        }
        System.out.println("Movements for st8 classes " + Arrays.toString(movementsFirm));
        System.out.println("Amount of establishment for st8 classes " + Arrays.toString(amountFirm));
        System.out.println("Employees for st8 classes" + Arrays.toString(employeesFirm));
        System.out.println("driven kilometer per class"  + Arrays.toString(kilometer));
    }

    private void printEmployees() {
        System.out.println("Print employees");
        System.out.println("Employees random: " + FreightFacility.employees_random);
        System.out.println("Employees lower: " + FreightFacility.employees_lower);
        System.out.println("Employees upper: " + FreightFacility.employees_upper);
        System.out.println("Employees null: " + FreightFacility.employees_null);
        System.out.println("Employees nn: " + FreightFacility.employees_nn);
        System.out.println("Employees empty: " + FreightFacility.employees_empty);
    }


    public void runTest() throws Exception {
        List<FreightFacility> freightFacilityList = ReadFacilitiesFile.read(ETABLISSEMENT_FILE, AREA_File);
        ActivityClasses.setActivityClasses(freightFacilityList);
//        Movement.calculateMovementsForST8(freightFacilityList);
        Movement.calculateMovements(freightFacilityList);
        Movement.distributeProperties(freightFacilityList, CENTERS);
        printEmployees();
        fixDistribution();
        printClasses(freightFacilityList);
        creatingToursPreparationTest();
        writeFiles(tripsDirectList, tripsRoundList);
        List<Trips> weekDayTours = DayAndTimeDistribution.generateDistribution(tripsDirectList, tripsRoundList);
        FreightMatsimPopulation.generateMATSimFreightPopulation(weekDayTours);
        System.out.println("Done");
    }

    private void creatingToursPreparationTest() throws InterruptedException {
        List<Movement> movementListRoundPL_CA = new ArrayList<>();
        List<Movement> movementListRoundPL_CPD = new ArrayList<>();
        List<Movement> movementListRoundPL_CPE = new ArrayList<>();
        List<Movement> movementListRoundVUL_CA = new ArrayList<>();
        List<Movement> movementListRoundVUL_CPD = new ArrayList<>();
        List<Movement> movementListRoundVUL_CPE = new ArrayList<>();
        List<Movement> movementListDirectPL_CA = new ArrayList<>();
        List<Movement> movementListDirectPL_CPD = new ArrayList<>();
        List<Movement> movementListDirectPL_CPE = new ArrayList<>();
        List<Movement> movementListDirectVUL_CA = new ArrayList<>();
        List<Movement> movementListDirectVUL_CPD = new ArrayList<>();
        List<Movement> movementListDirectVUL_CPE = new ArrayList<>();
        for (Movement movement : Movement.movementList) {
            if (random.nextDouble() > 0.9) {
                if (movement.routeType.equals(Movement.RouteType.round)) {
                    if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                        if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                            movementListRoundPL_CA.add(movement);
                        } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                            movementListRoundPL_CPD.add(movement);
                        } else {
                            movementListRoundPL_CPE.add(movement);
                        }
                    } else {
                        if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                            movementListRoundVUL_CA.add(movement);
                        } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                            movementListRoundVUL_CPD.add(movement);
                        } else {
                            movementListRoundVUL_CPE.add(movement);
                        }
                    }
                } else {
                    if (movement.disVeh20.equals(Movement.DistributionVehicleST20.VehicleST20.PL)) {
                        if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                            movementListDirectPL_CA.add(movement);
                        } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                            movementListDirectPL_CPD.add(movement);
                        } else {
                            movementListDirectPL_CPE.add(movement);
                        }
                    } else {
                        if (movement.disMan.equals(Movement.DistributionManagement.Management.CA)) {
                            movementListDirectVUL_CA.add(movement);
                        } else if (movement.disMan.equals(Movement.DistributionManagement.Management.CPD)) {
                            movementListDirectVUL_CPD.add(movement);
                        } else {
                            movementListDirectVUL_CPE.add(movement);
                        }
                    }
                }
            }
        }

        System.out.println("Start Threads");
        RunParallelizationRoutes t1 = new RunParallelizationRoutes(movementListRoundPL_CA);
        RunParallelizationRoutes t2 = new RunParallelizationRoutes(movementListRoundPL_CPD);
        RunParallelizationRoutes t3 = new RunParallelizationRoutes(movementListRoundPL_CPE);
        RunParallelizationRoutes t4 = new RunParallelizationRoutes(movementListRoundVUL_CA);
        RunParallelizationRoutes t5 = new RunParallelizationRoutes(movementListRoundVUL_CPD);
        RunParallelizationRoutes t6 = new RunParallelizationRoutes(movementListRoundVUL_CPE);
        RunParallelizationRoutes t7 = new RunParallelizationRoutes(movementListDirectPL_CA);
        RunParallelizationRoutes t8 = new RunParallelizationRoutes(movementListDirectPL_CPD);
        RunParallelizationRoutes t9 = new RunParallelizationRoutes(movementListDirectPL_CPE);
        RunParallelizationRoutes t10 = new RunParallelizationRoutes(movementListDirectVUL_CA);
        RunParallelizationRoutes t11 = new RunParallelizationRoutes(movementListDirectVUL_CPD);
        RunParallelizationRoutes t12 = new RunParallelizationRoutes(movementListDirectVUL_CPE);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();
        t11.start();
        t12.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();
        t8.join();
        t9.join();
        t10.join();
        t11.join();
        t12.join();
    }
}
