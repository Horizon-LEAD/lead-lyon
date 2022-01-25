package lead.freturbLightV2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * provides a methode that reads and processes a StockEtablissement_utf8.csv file
 */
public class ReadSireneFileV2 {

    static List<String> in = new ArrayList<>();
    static List<String> out = new ArrayList<>();

    /**
     * reads a StockEtablissement_utf8.csv file from the french government and creates a firmData object for establishments where complete information is available, file can be found here: https://www.data.gouv.fr/fr/datasets/base-sirene-des-entreprises-et-de-leurs-etablissements-siren-siret/
     * @param - sireneFile csv-File with all establishments
     * @return - a list with establishments
     */
    public static List<FirmDataV2> readFile(String sireneFile) {

        List<FirmDataV2> firms = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sireneFile)));) {

            String line;
            List<String> header = null;

            int count = 0;
            int incomplete = 0;
            int count1 = 0;
            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));
                if (header == null) {
                    header = row;
                } else if (row.get(header.indexOf("etatAdministratifEtablissement")).equals("A") && row.size() == 48) {

                    FirmDataV2 firmData = new FirmDataV2(
                            row.get(header.indexOf("siret")),
                            row.get(header.indexOf("activitePrincipaleEtablissement")),
                            row.get(header.indexOf("trancheEffectifsEtablissement")),
                            row.get(header.indexOf("siren"))
                    );
                    if (FilterFirmsV2.checkInLyon(firmData)) {
                        firms.add(firmData);
                        count1++;
//                    } else {
//                          if (row.get(header.indexOf("codeCommuneEtablissement")).startsWith("75")) {
////                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("77")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("78")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("91")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("92")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("93")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("94")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }
//                            if (row.get(header.indexOf("codePostalEtablissement")).startsWith("95")) {
//                                System.out.println(row.get(header.indexOf("trancheEffectifsEtablissement")));
//                                count1++;
//                                firms.add(firmData);
//                            }

                    }
                } else {
                    incomplete++;
                }

                if (count++ % 1000000 == 0) {
                    System.out.println("Read: " + (count-1));
                }
            }
            System.out.println("Count " + count1);
            System.out.println("Not taken establishments: " + incomplete);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return firms;
    }
}
