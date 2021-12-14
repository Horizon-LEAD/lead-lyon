package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;

public class FirmDataV2 {

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

    public FirmDataV2(String siret, String ape, String employees, String siren) {
        this.siret = siret;
        this.ape = ape;
        this.employees = getEmployes(employees);
        this.siren = siren;
    }

    private int getEmployes(String employees) {
        if (employees.equals("") || employees.equals("00") || employees.equals("NN")) {
            return 0;
        }
        return Integer.parseInt(employees);
    }

}
