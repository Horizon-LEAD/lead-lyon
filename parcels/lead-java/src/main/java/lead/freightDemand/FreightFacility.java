package lead.freightDemand;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.replanning.modules.ReRoute;

public class FreightFacility {

    static long employees_lower = 0;
    static long employees_upper = 0;
    static long employees_random = 0;
    static long employees_empty = 0;
    static long employees_null = 0;
    static long employees_nn = 0;

    final String siret;
    final String ape;
    final String employeesString;
    int employees;
    final String siren;
    private final Coord coord;
    private int st8 = 0;
    private String st45 = "";
    double movements = 0;

    public FreightFacility(String siret, String activitePrincipaleEtablissement, String trancheEffectifsEtablissement, String siren, Coord coord) throws Exception {
        this.siret = siret;
        this.ape = activitePrincipaleEtablissement;
        this.employeesString = trancheEffectifsEtablissement;
        this.siren = siren;
        this.coord = coord;
    }

    void setEmployees() {
        if (employeesString.equals("")) {
            employees_empty++;
            employees = 1;
        }
        if (employeesString.equals("00")) {
            employees_null++;
            employees = 1;
        }
        if (employeesString.equals("NN")) {
            employees_nn++;
            employees = 1;
        }
        if (employeesString.equals("01")) {
            int low = 1;
            int high = 3;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("02")) {
            int low = 3;
            int high = 6;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("03")) {
            int low = 6;
            int high = 10;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("11")) {
            int low = 10;
            int high = 20;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("12")) {
            int low = 20;
            int high = 50;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("21")) {
            int low = 50;
            int high = 100;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("22")) {
            int low = 100;
            int high = 200;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("31")) {
            int low = 200;
            int high = 250;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("32")) {
            int low = 250;
            int high = 500;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("41")) {
            int low = 500;
            int high = 1000;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("42")) {
            int low = 1000;
            int high = 2000;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("51")) {
            int low = 2000;
            int high = 5000;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("52")) {
            int low = 5000;
            int high = 10000;
            int x = FreightDemand.random.nextInt(high - low) + low;
            employees_random += x;
            employees_lower += low;
            employees_upper += high -1;
            employees = high -1;
        } else if (employeesString.equals("53")) {
            employees_random += 10000;
            employees_lower += 10000;
            employees_upper += 10000;
            employees = 10000;
        }
    }

    Coord getCoord() {
        return coord;
    }

    int getEmployees() {
        return employees;
    }

    String getAPE() {
        return ape;
    }

    int getSt8() {
        return st8;
    }

    void setSt8(int st8) {
        this.st8 = st8;
    }

    String getSt45() {
        return st45;
    }

    void setSt45(String st45) {
        this.st45 = st45;
    }
}
