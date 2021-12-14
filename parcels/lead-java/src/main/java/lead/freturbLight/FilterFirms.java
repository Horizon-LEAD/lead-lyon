package lead.freturbLight;

import org.matsim.api.core.v01.Coord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FilterFirms {

    public static Map<Long, Coord> filterArea(String file) throws IOException {

        Map<Long, Coord> inLyon = new HashMap();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
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
        return inLyon;
    }

    public static void wrongJuridique(List<FirmData> firmDataList, String file) throws IOException {
        List<String> toDelete = readStockUniteLegaleFile(file);
        List<FirmData> delete = new ArrayList<>();
        for (FirmData firmData : firmDataList) {
            if (toDelete.contains(firmData.SIREN)) {
                delete.add(firmData);
            }
        }
        firmDataList.removeAll(delete);
    }

    public static void shellCompanies(List<FirmData> firmDataList) {

        Map<Coord, Integer> amountPerAddress = new HashMap<>();

        for (FirmData firmData : firmDataList) {
            if (amountPerAddress.containsKey(firmData.COORD)) {
                int count = amountPerAddress.get(firmData.COORD);
                count += 1;
                amountPerAddress.put(firmData.COORD, count);
            } else {
                amountPerAddress.put(firmData.COORD, 1);
            }
        }

        List<Coord> removeCoord = new ArrayList<>();
        List<FirmData> removeFirm = new ArrayList<>();
        for (Map.Entry<Coord, Integer> x : amountPerAddress.entrySet()) {
            if (x.getValue() > 15) {
                removeCoord.add(x.getKey());
            }
        }
        for (FirmData firmData : firmDataList) {
            if (removeCoord.contains(firmData.COORD)) {
                removeFirm.add(firmData);
            }
        }
        firmDataList.removeAll(removeFirm);
    }

    public static List<String> readStockUniteLegaleFile(String file) throws IOException {

        List<Integer> filter = Arrays.asList(1400, 1500, 1700, 1900, 2110, 2120, 6521, 6532, 6533, 6534, 6535, 6536, 6537, 6537, 6539, 6540, 6541, 6542, 6543, 6544, 6551, 6554, 6558);
        List<Integer> filterEmployees = Arrays.asList(9150, 9210, 9220, 9221, 9222, 9223, 9224, 9230, 9240, 9260, 9300);
        List<String> firmsToDelete = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        String line;
        List<String> header = null;

        int count = 0;

        while ((line = reader.readLine()) != null) {
            List<String> row = Arrays.asList(line.split(","));

            try {
                if (header == null) {
                    header = row;
                } else if (row.size() != header.size()) {
                    continue;
                } else if (!filter.contains(Integer.parseInt(row.get(header.indexOf("categorieJuridiqueUniteLegale")))) && (filterEmployees.contains(Integer.parseInt(row.get(header.indexOf("categorieJuridiqueUniteLegale"))))) && !(row.get(header.indexOf("trancheEffectifsUniteLegale")).equals("") || row.get(header.indexOf("trancheEffectifsUniteLegale")).equals("00") || row.get(header.indexOf("trancheEffectifsUniteLegale")).equals("NN"))) {
                    firmsToDelete.add(row.get(header.indexOf("siren")));
                }
            } catch (NumberFormatException e) {
                count++;
                System.out.println("Many columns can contain a comma");
            }
        }
        System.out.println("Problems with " + count + " firms");
        return firmsToDelete;
    }

    public static void filterFirms(List<FirmData> firms, String file) throws IOException {
        int size = firms.size();
        System.out.println("Frims before filtering: " + size);
        wrongJuridique(firms, file);
        size = firms.size();
        System.out.println("Frims after Juridique: " + size);
        shellCompanies(firms);
        size = firms.size();
        System.out.println("Frims after shellCompanies: " + size);
        for (FirmData firmData : firms) {
            for (FirmData.Move move : firmData.moves) {
                FirmData.movementsList.add(move);
            }
        }
    }

}
