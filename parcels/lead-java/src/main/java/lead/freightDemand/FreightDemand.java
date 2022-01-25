package lead.freightDemand;

import lead.freturbLightV2.CategorisationV2;
import lead.freturbLightV2.FirmDataV2;
import org.checkerframework.checker.units.qual.A;
import org.matsim.api.core.v01.Coord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class FreightDemand {

    static Random random = new Random(123);

    private final String ETABLISSEMENT_FILE;
    private final String UNITE_LEGALE_FILE;
    private final String ACTIVITY_CLASSES;
    private final String AREA_File;
    private final List<Coord> CENTERS;

    private String outputLocation = "";

    public FreightDemand (String etablissementFile, String uniteLegaleFile, String activityClasses, String areaFile, List<Coord> centers) {
        this.ETABLISSEMENT_FILE = etablissementFile;
        this.UNITE_LEGALE_FILE = uniteLegaleFile;
        this.AREA_File = areaFile;
        this.CENTERS = centers;
        this.ACTIVITY_CLASSES = activityClasses;
    }

    public void run() throws Exception {
        List<FreightFacility> freightFacilityList = ReadFacilitiesFile.read(ETABLISSEMENT_FILE, AREA_File);
//        ActivityClasses.setActivityClassesOriginal(ACTIVITY_CLASSES, freightFacilityList);
        ActivityClasses.setActivityClasses(freightFacilityList);
        Movement.calculateMovements(freightFacilityList);
        printEmployees();
        printClasses(freightFacilityList);
        writeAll();
        System.out.println("Done");
    }

    private void writeAll() {
        HashMap<String, Integer> reverseSortedMap = new HashMap<>();
        ActivityClasses.allMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ape_all.txt"))) {
            writer.write("ape;count");
            for (Map.Entry<String, Integer> trip : reverseSortedMap.entrySet()) {
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
        System.out.println("Movements for st8 classes " + Arrays.toString(movementsFirm));
        System.out.println("Amount of establishment for st8 classes " + Arrays.toString(amountFirm));
        System.out.println("Employees for st8 classes" + Arrays.toString(employeesFirm));
        System.out.println("Done");
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


}
