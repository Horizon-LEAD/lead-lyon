package lead.freturbLight;

import org.matsim.api.core.v01.Coord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReadSireneFile {

    public static List<FirmData> readSireneFile(String sireneFile, String inLyonFile, Coord coord) throws Exception {

        List<FirmData> firmDataList = new ArrayList<>();
        Map<Long, Coord> inLyon = FilterFirms.filterArea(inLyonFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sireneFile)));
        String line;

        List<String> header = null;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            List<String> row = Arrays.asList(line.split(","));

            if (header == null) {
                header = row;
            } else if (!row.get(header.indexOf("dateDernierTraitementEtablissement")).equals("") && row.size() == 48){
                try {
                    if (inLyon.get(Long.parseLong(row.get(header.indexOf("siret")))) != null &&
                            !row.get(header.indexOf("trancheEffectifsEtablissement")).equals("") &&
                            !row.get(header.indexOf("trancheEffectifsEtablissement")).equals("00") &&
                            !row.get(header.indexOf("trancheEffectifsEtablissement")).equals("NN")) {
                        FirmData firmData = new FirmData(
                                coord,
                                row.get(header.indexOf("siret")),
                                row.get(header.indexOf("libelleVoieEtablissement")),
                                row.get(header.indexOf("activitePrincipaleEtablissement")),
                                Integer.parseInt(row.get(header.indexOf("trancheEffectifsEtablissement"))),
                                inLyon.get(Long.parseLong(row.get((header.indexOf("siret"))))),
                                row.get(header.indexOf("siren"))
                        );
                        firmDataList.add(firmData);
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            if (count++ % 1000000 == 0) {
                System.out.println("Read: " + (count-1));
            }

        }
        return firmDataList;
    }
}
