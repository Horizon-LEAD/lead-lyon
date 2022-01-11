package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * filters establishments based on location, number of employees, jurisdiction and shell companies
 */
public class FilterFirmsV2 {

    private static int amountFirms = 0;
    private static final Map<Long, Coord> AREA_FILTER_MAP = readAreaFilterFile();

    /**
     *filter
     *
     * @param firms -  a list with all establishments from the StockEtablissement_utf8.csv file
     * @param filterFile - location of a file with the study area
     * @param sirenFile - location of a StockUniteLegale_utf8.csv file, can be found here https://www.data.gouv.fr/fr/datasets/base-sirene-des-entreprises-et-de-leurs-etablissements-siren-siret/
     * @return - a new list with the filtered establishments
     */
    public static List<FirmDataV2> filter(List<FirmDataV2> firms, String filterFile, String sirenFile) {
        System.out.println("Starting filter firms");

//        System.out.println("Amount of firms before filtering: " + firms.size());
        System.out.println("Amount of firms before filtering: " + amountFirms);

//        inArea(filterFile, firms);
        System.out.println("Amount of firms after area check: " + firms.size());

        employeesNull(firms);
        System.out.println("Amount of firms after 0 employees check: " + firms.size());

        wrongJuridique(sirenFile, firms);
        System.out.println("Amount of firms after juridique check: " + firms.size());

        shellCompanies(firms);
        System.out.println("Amount of firms after shell companies check: " + firms.size());

        return firms;
    }

    private static void shellCompanies(List<FirmDataV2> firms) {
        Map<Coord, Integer> amountPerAddress = new HashMap<>();

        for (FirmDataV2 firm : firms) {
            if (amountPerAddress.containsKey(firm.coord)) {
                int count = amountPerAddress.get(firm.coord);
                count += 1;
                amountPerAddress.put(firm.coord, count);
            } else {
                amountPerAddress.put(firm.coord, 1);
            }
        }

        List<Coord> removeCoord = new ArrayList<>();
        List<FirmDataV2> removeFirms = new ArrayList<>();
        for (Map.Entry<Coord, Integer> x : amountPerAddress.entrySet()) {
            if (x.getValue() > 15) {
                removeCoord.add(x.getKey());
            }
        }
        for (FirmDataV2 firm : firms) {
            if (removeCoord.contains(firm.coord)) {
                removeFirms.add(firm);
            }
        }
        firms.removeAll(removeFirms);
    }

    private static void wrongJuridique(String file, List<FirmDataV2> firms) {
        System.out.println("Many columns can contain a comma");

        List<Integer> filter = Arrays.asList(1400, 1500, 1700, 1900, 2110, 2120, 6521, 6532, 6533, 6534, 6535, 6536, 6537, 6537, 6539, 6540, 6541, 6542, 6543, 6544, 6551, 6554, 6558);
        List<Integer> filterEmployees = Arrays.asList(9150, 9210, 9220, 9221, 9222, 9223, 9224, 9230, 9240, 9260, 9300);
        List<String> deleteFirms = new ArrayList<>();

        int count = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            List<String> header = null;

            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));

                if (header == null) {
                    header = row;
                } else if (row.size() != header.size()) {
                    count++;
                    continue;
                } else if (!filter.contains(Integer.parseInt(row.get(header.indexOf("categorieJuridiqueUniteLegale"))))
                        && (filterEmployees.contains(Integer.parseInt(row.get(header.indexOf("categorieJuridiqueUniteLegale")))))
                        && !(row.get(header.indexOf("trancheEffectifsUniteLegale")).equals("") || row.get(header.indexOf("trancheEffectifsUniteLegale")).equals("00") || row.get(header.indexOf("trancheEffectifsUniteLegale")).equals("NN"))) {
                    deleteFirms.add(row.get(header.indexOf("siren")));
                }
            }
        } catch (Exception e) {
            count++;
        }

        System.out.println("Problems with " + count + " firms");

        List<FirmDataV2> removeFirms = new ArrayList<>();
        for (FirmDataV2 firmData : firms) {
            if (deleteFirms.contains(firmData.siren)) {
                removeFirms.add(firmData);
            }
        }
        firms.removeAll(removeFirms);
    }

    private static void employeesNull(List<FirmDataV2> firms) {
        List<FirmDataV2> removeFirms = new ArrayList<>();
        for (FirmDataV2 firm : firms) {
            if (firm.employees == 0) {
                removeFirms.add(firm);
            }
        }
        firms.removeAll(removeFirms);
    }

    private static void inArea(String filterFile, List<FirmDataV2> firms) {
        Map<Long, Coord> inLyon = new HashMap();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filterFile)))){

            String line;
            List<String> header = null;

            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));

                if (header == null) {
                    header = row;
                } else {
                    inLyon.put(Long.parseLong(row.get(header.indexOf("siret"))), new Coord(Double.parseDouble(row.get(header.indexOf("x"))), Double.parseDouble(row.get(header.indexOf("y")))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Done reading in Lyon File");
        int count = 0;

        Iterator<FirmDataV2> iterator = firms.iterator();
        while (iterator.hasNext()) {
            FirmDataV2 firm = iterator.next();
            if (inLyon.containsKey(Long.parseLong(firm.siret))) {
                firm.coord = inLyon.get(Long.parseLong(firm.siret));
            } else {
                iterator.remove();
            }
            if (count++ % 1000000 == 0) {
                System.out.println("Read: " + (count-1));
            }
        }
    }

    private static Map<Long, Coord> readAreaFilterFile() {
        Map<Long, Coord> inLyon = new HashMap();

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/lead/Marc/Freturb_Light/Input_Tabellen/nantes_coords.csv")))){
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("lyons_coords.csv")))){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("idf_coords.csv")))){
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("nantes_coords.csv")))){
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/lead/Marc/Freturb_Light/Input_Tabellen/idf_coords.csv")))){
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:/lead/Marc/Freturb_Light/Filter/lyons_coords.csv")))){
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/Praktikum/lyons_coords.csv")))){

            String line;
            List<String> header = null;

            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(","));

                if (header == null) {
                    header = row;
                } else {
                    inLyon.put(Long.parseLong(row.get(header.indexOf("siret"))), new Coord(Double.parseDouble(row.get(header.indexOf("x"))), Double.parseDouble(row.get(header.indexOf("y")))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inLyon;
    }

    public static boolean checkInLyon(FirmDataV2 firmData) {
        amountFirms++;
        if (AREA_FILTER_MAP.get(Long.parseLong(firmData.siret)) != null) {
            firmData.coord = AREA_FILTER_MAP.get(Long.parseLong(firmData.siret));
            return true;
        }
        return false;
    }
}
