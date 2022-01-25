package lead.freightDemand;

import org.matsim.api.core.v01.Coord;

import java.io.*;
import java.util.*;

public class RestictionFaclilies {

    private final Map<Long, Coord> AREA_FILTER_MAP = new HashMap<>();
    private final Map<Long, Integer> JURISDICTION_MAP = new HashMap<>();
    private final List<Integer> filter = Arrays.asList(1400, 1500, 1700, 1900, 2110, 2120, 6521, 6532, 6533, 6534, 6535, 6536, 6537, 6537, 6539, 6540, 6541, 6542, 6543, 6544, 6551, 6554, 6558);
    private final List<Integer> filterEmployees = Arrays.asList(9150, 9210, 9220, 9221, 9222, 9223, 9224, 9230, 9240, 9260, 9300);

    RestictionFaclilies(String areaFile) {
        readAreaFile(areaFile);
    }

    private void readAreaFile(String AREA_FILE) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(AREA_FILE)))) {
            String line;
            List<String> header = null;
            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));
                if (header == null) {
                    header = row;
                } else {
                    AREA_FILTER_MAP.put(Long.parseLong(row.get(header.indexOf("siret"))), new Coord(Double.parseDouble(row.get(header.indexOf("x"))), Double.parseDouble(row.get(header.indexOf("y")))));
                    JURISDICTION_MAP.put(Long.parseLong(row.get(header.indexOf("siret"))), Integer.parseInt(row.get(header.indexOf("law_status"))));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Coord inArea(String siret) {
        if (AREA_FILTER_MAP.get(Long.parseLong(siret)) != null) {
            return AREA_FILTER_MAP.get(Long.parseLong(siret));
        }
        return null;
    }

    boolean wrongJurisdiction(String siret, String trancheEffectifsEtablissement) {
        if (trancheEffectifsEtablissement.equals("00")) {
            return filter.contains(JURISDICTION_MAP.get(Long.parseLong(siret))) || filterEmployees.contains(JURISDICTION_MAP.get(Long.parseLong(siret)));
        }
        return filter.contains(JURISDICTION_MAP.get(Long.parseLong(siret)));
    }

    List<FreightFacility> shellCompanies(List<FreightFacility> freightFacilityList) {
        Map<Coord, Integer> amountPerAddress = new HashMap<>();
        List<Coord> removeCoord = new ArrayList<>();
        for (FreightFacility freightFacility : freightFacilityList) {
            Coord coord = freightFacility.getCoord();
            if (amountPerAddress.containsKey(coord)) {
                int count = amountPerAddress.get(coord);
                count += 1;
                amountPerAddress.put(coord, count);
                if (count > 20 && !removeCoord.contains(coord)) {
                    removeCoord.add(coord);
                }
            } else {
                amountPerAddress.put(coord, 1);
            }
        }
        System.out.println("Done counting facilities per address");
        List<FreightFacility> newFreightFacilityList = new ArrayList<>();
        for (FreightFacility freightFacility : freightFacilityList) {
            if (!removeCoord.contains(freightFacility.getCoord())) {
                newFreightFacilityList.add(freightFacility);
            }
        }
        return newFreightFacilityList;
    }

}
