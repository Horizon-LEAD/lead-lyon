package lead.freturbLight;

import org.matsim.api.core.v01.Coord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RunFreturbLight {

    public static void main(String[] args) throws Exception {

        String sireneFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockEtablissement_utf8.csv";
        String filterFile = "C:/lead/Marc/Freturb_Light/Filter/lyons_coords.csv";
//        String sireneFile = "D:/Praktikum/StockEtablissement_utf8.csv";
//        String fillterFile = "D:/Praktikum/lyon.csv";

        Coord center = new Coord(842443.74, 6519278.68);

        List<FirmData> firms = ReadSireneFile.readSireneFile(sireneFile, filterFile, center);
//        List<FirmData> firms = ReadSireneFile.readSireneFile(sireneFiel, filterFile, center);

        FilterFirms.filterFirms(firms, "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockUniteLegale_utf8.csv");
//        Distribution.distributeLogistics(firms);

        int x = 0;
        int y = 0;
        Set<String> suc = new HashSet<>();

        for (FirmData firm : firms) {
            if (firm.ST8 == 0) {
                x++;
            } else {
                y++;
                suc.add(firm.APET700);
            }
        }

        System.out.println("Anzahl null: " + x);
        System.out.println("Anzahl zugeordnet: " + y);

        System.out.println("---------------");
        System.out.println(suc.size());
        System.out.println(Categorisation.matching.size());
        for (String s : Categorisation.matching) {
            System.out.println(s);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("firms.txt"));
        writer.write("SIRET;" + "ST8;" + "APET700;" + "EFETCENT;" + "Movements;" + "X;" + "Y;" + "centerDistance");
        for (FirmData firm : firms) {
            writer.newLine();
            writer.write(firm.toString());
            writer.flush();
        }


        int[] amountFirm = new int[8];
        int[] employeesFirm = new int[8];
        double[] movementsFirm = new double[8];
        for (FirmData firmData : firms) {
            if (firmData.ST8 != 0) {
                int a = firmData.ST8 - 1;
                int af = amountFirm[a] + 1;
                int ef = employeesFirm[a] + firmData.EFETCENT;
                double mf = movementsFirm[a] + firmData.MOVEMENTS;
                amountFirm[a] = af;
                employeesFirm[a] = ef;
                movementsFirm[a] = mf;
            }

        }
        System.out.println(Arrays.toString(movementsFirm));
        System.out.println(Arrays.toString(amountFirm));
        System.out.println(Arrays.toString(employeesFirm));

        List<FirmData.Move> moves = FirmData.movementsList;

        System.out.println("Done");

    }
}
