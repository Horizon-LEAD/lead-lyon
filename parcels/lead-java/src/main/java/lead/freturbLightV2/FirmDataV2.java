package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;

import java.util.Random;

public class FirmDataV2 {

    static Random random = new Random(123);

    Coord coord;
    String siret;
    String ape;
    int employees;
    String siren;
    int st8 = 0;
    int st20;
    double centerDistance;
    double unrealMovements;
    int movements;
    Move[] moves;

    public FirmDataV2(String siret, String ape, String employees, String siren) throws Exception {
        this.siret = siret;
        this.ape = ape;
        this.employees = getEmployees(employees);
        this.siren = siren;
    }

    private int getEmployees(String employees) throws Exception {
        if (employees.equals("") || employees.equals("00") || employees.equals("NN")) {
            return 0;
        }
        if (employees.equals("01")) {
            int low = 1;
            int high = 3;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("02")) {
            int low = 3;
            int high = 6;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("03")) {
            int low = 6;
            int high = 10;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("11")) {
            int low = 10;
            int high = 20;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("12")) {
            int low = 20;
            int high = 50;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("21")) {
            int low = 50;
            int high = 100;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("22")) {
            int low = 100;
            int high = 200;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("31")) {
            int low = 200;
            int high = 250;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("32")) {
            int low = 250;
            int high = 500;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("41")) {
            int low = 500;
            int high = 1000;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("42")) {
            int low = 1000;
            int high = 2000;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("51")) {
            int low = 2000;
            int high = 5000;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("52")) {
            int low = 5000;
            int high = 10000;
            return random.nextInt(high - low) + low;
        } else if (employees.equals("53")) {
            return 10000;
        }
        throw new Exception("Number of employees cannot be processed: " + employees);
    }

}
