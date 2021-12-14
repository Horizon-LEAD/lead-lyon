package lead.freturbLightV2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadSireneFileV2 {

    public static List<FirmDataV2> readFile(String sireneFile) {

        List<FirmDataV2> firms = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sireneFile)));) {

            String line;
            List<String> header = null;

            int count = 0;
            int incomplete = 0;

            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));

                if (header == null) {
                    header = row;
                } else if (!row.get(header.indexOf("dateDernierTraitementEtablissement")).equals("") && row.size() ==48) {

                    FirmDataV2 firmData = new FirmDataV2(
                            row.get(header.indexOf("siret")),
                            row.get(header.indexOf("activitePrincipaleEtablissement")),
                            row.get(header.indexOf("trancheEffectifsEtablissement")),
                            row.get(header.indexOf("siren"))
                    );
                    if (FilterFirmsV2.checkInLyon(firmData)) {
                        firms.add(firmData);
                    }
                } else {
                    incomplete++;
                }

                if (count++ % 1000000 == 0) {
                    System.out.println("Read: " + (count-1));
                }
            }
            System.out.println("Incomplete lines: " + incomplete);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return firms;
    }
}
