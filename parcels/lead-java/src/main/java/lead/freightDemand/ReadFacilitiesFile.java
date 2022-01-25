package lead.freightDemand;

import org.matsim.api.core.v01.Coord;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadFacilitiesFile {

    public static List<FreightFacility> read(String etablissement_file, String areaFile) throws Exception {
        RestictionFaclilies restictionFaclilies = new RestictionFaclilies(areaFile);
        List<FreightFacility> freightFacilityList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(etablissement_file)))) {
            String line;
            List<String> header = null;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));
                if (header == null) {
                    header = row;
                } else {
                    FreightFacility freightFacility = filterFreightFacilities(header, row, restictionFaclilies);
                    if (freightFacility != null) {
                        freightFacilityList.add(freightFacility);
                    }
                }
                if (count++ % 1000000 == 0) {
                    System.out.println("Read: " + (count-1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Done reading SIRENE file");
        freightFacilityList = restictionFaclilies.shellCompanies(freightFacilityList);
        System.out.println("Done removing shell companies");

        for (FreightFacility freightFacility : freightFacilityList) {
            freightFacility.setEmployees();
        }

        return freightFacilityList;
    }

    static FreightFacility filterFreightFacilities(List<String> header, List<String> row, RestictionFaclilies restictionFaclilies) throws Exception {
        if (!row.get(header.indexOf("etatAdministratifEtablissement")).equals("A") && row.size() == 48) {
            return null;
        }
        Coord coord = restictionFaclilies.inArea(row.get(header.indexOf("siret")));
        if (coord == null) {
            return null;
        }
        if (restictionFaclilies.wrongJurisdiction(row.get(header.indexOf("siret")), row.get(header.indexOf("trancheEffectifsEtablissement")))) {
            return null;
        }
        return new FreightFacility(
                row.get(header.indexOf("siret")),
                row.get(header.indexOf("activitePrincipaleEtablissement")),
                row.get(header.indexOf("trancheEffectifsEtablissement")),
                row.get(header.indexOf("siren")),
                coord
        );
    }


}
