package lead.freightDemand;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ActivityClasses {

    static Map<String, Integer> allMap = new HashMap<>();
    static Map<String, Integer> fewMap = new HashMap<>();

    static Map<String, APEClass> readActivityClasses(String file) {
        Map<String, APEClass> activityClasses = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            List<String> header = null;
            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split(";"));
                if (header == null) {
                    header = row;
                } else {
                    activityClasses.put(row.get(header.indexOf("Code")), new APEClass(row.get(header.indexOf("ST8")), row.get(header.indexOf("ST45"))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activityClasses;
    }

    static void setActivityClassesOriginal(String file, List<FreightFacility> freightFacilityList) {
        Map<String, APEClass> activityClasses = readActivityClasses(file);
        for (FreightFacility freightFacility : freightFacilityList) {
            if (activityClasses.get(freightFacility.ape) != null) {
                freightFacility.setSt8(activityClasses.get(freightFacility.ape).st8);
                freightFacility.setSt45(activityClasses.get(freightFacility.ape).st45);
            }
        }
    }

    public static void setActivityClasses(List<FreightFacility> freightFacilityList) {
        Map<String, Integer> matchingST8 = generateMatchingST8Map();
        List<FreightFacility> remove = new ArrayList<>();
        for (FreightFacility freightFacility : freightFacilityList) {
            Integer match = matchingST8.get(freightFacility.ape);
            if (!freightFacility.ape.contains(".") || freightFacility.ape.length() != 6) {
                remove.add(freightFacility);
                continue;
            }
            if (match == null) {
                if (allMap.get(freightFacility.ape) == null) {
                    allMap.put(freightFacility.ape, 1);
                } else {
                    int x = allMap.get(freightFacility.ape);
                    x++;
                    allMap.put(freightFacility.ape, x);
                }
            }
            if (match == null && freightFacility.ape.contains(".")) {
                String[] codeSplit = freightFacility.ape.split("\\.");
                match = matchingST8.get(codeSplit[0]);
            }
            if (match == null) {
                freightFacility.setSt8(0);
                remove.add(freightFacility);
                if (fewMap.get(freightFacility.ape) == null) {
                    fewMap.put(freightFacility.ape, 1);
                } else {
                    int x = fewMap.get(freightFacility.ape);
                    x++;
                    fewMap.put(freightFacility.ape, x);
                }
            } else {
                freightFacility.setSt8(match);
            }
        }
        freightFacilityList.removeAll(remove);
    }

    static class APEClass {
        int st8;
        String st45;

        APEClass(String st8, String st45) {
            this.st8 = Integer.parseInt(st8);
            this.st45 = st45;
        }

    }

    private static Map<String, Integer> generateMatchingST8Map() {
        Map<String, Integer> matchingST8 = new HashMap<>();

        // 1
        matchingST8.put("01.11Z", 1);
        matchingST8.put("01.49Z", 1);
        matchingST8.put("02.40Z", 1);
        matchingST8.put("81.30Z", 1);
        //own
        matchingST8.put("01", 1);
        matchingST8.put("03", 1);
        matchingST8.put("08", 1);
        matchingST8.put("02", 1);


        //2
        matchingST8.put("84.24Z", 2);
        matchingST8.put("84.25Z", 2);
        matchingST8.put("91", 2);
        matchingST8.put("92", 2);
        matchingST8.put("93", 2);
        matchingST8.put("38", 2);
        matchingST8.put("39", 2);
        matchingST8.put("75", 2);
        matchingST8.put("86", 2);
        matchingST8.put("87", 2);
        matchingST8.put("88.1", 2);
        matchingST8.put("71.12B", 2);
        matchingST8.put("73", 2);
        matchingST8.put("82", 2);
        matchingST8.put("96", 2);
        matchingST8.put("77", 2);
        matchingST8.put("79", 2);
        matchingST8.put("33.1", 2);
        matchingST8.put("33.2", 2);
        matchingST8.put("43.2", 2);
        matchingST8.put("43.3", 2);
        matchingST8.put("81", 2);
        matchingST8.put("45.2", 2);
        matchingST8.put("46.1", 2);
        matchingST8.put("58", 2);
        matchingST8.put("59", 2);
        matchingST8.put("60", 2);
        matchingST8.put("61", 2);
        matchingST8.put("63", 2);
        matchingST8.put("62", 2);
        matchingST8.put("95", 2);
        matchingST8.put("90", 2);
        //own


        //3
        matchingST8.put("13", 3);

        // 13 à 18
        matchingST8.put("14", 3);
        matchingST8.put("15", 3);
        matchingST8.put("16", 3);
        matchingST8.put("17", 3);
        matchingST8.put("18", 3);
        matchingST8.put("31", 3);
        matchingST8.put("32", 3);
        matchingST8.put("19", 3);

        // 19 à 25
        matchingST8.put("20", 3);
        matchingST8.put("21", 3);
        matchingST8.put("22", 3);
        matchingST8.put("23", 3);
        matchingST8.put("24", 3);
        matchingST8.put("25", 3);
        matchingST8.put("10", 3);
        matchingST8.put("11", 3);
        matchingST8.put("12", 3);
        matchingST8.put("26", 3);

        // 26 à 30
        matchingST8.put("27", 3);
        matchingST8.put("28", 3);
        matchingST8.put("29", 3);
        matchingST8.put("30", 3);
        matchingST8.put("33", 3);
        //own
        matchingST8.put("09", 3);
        matchingST8.put("35", 3);
        matchingST8.put("41", 3);
        matchingST8.put("42", 3);
        matchingST8.put("43", 3);
        matchingST8.put("07", 3);
        matchingST8.put("37", 2);


        //4
        matchingST8.put("46", 4); // all


        //5
        matchingST8.put("47.11C", 5);
        matchingST8.put("47.11D", 5);
        matchingST8.put("47.19B", 5);
        matchingST8.put("47.11F", 5);
        matchingST8.put("47.2", 5);
        // 47.2 à 47.7
        matchingST8.put("47.3", 5);
        matchingST8.put("47.4", 5);
        matchingST8.put("47.5", 5);
        matchingST8.put("47.6", 5);
        matchingST8.put("47.7", 5);


        //6
        matchingST8.put("45", 6); // all
        matchingST8.put("47", 6); // all
        matchingST8.put("10.13B", 6);
        matchingST8.put("10.71C", 6);
        matchingST8.put("10.71D", 6);
        //own
        matchingST8.put("56", 6);
        matchingST8.put("50", 6);
        matchingST8.put("53", 6);
        matchingST8.put("55", 6);
        //+HCR


        //7
//            matchingST8.put("10", 7);
        // 10  à  56
//            matchingST8.put("56", 7);
        matchingST8.put("94", 7);
        matchingST8.put("64", 7);
        // 64  à  68
        matchingST8.put("68", 7);
//            matchingST8.put("58", 7);
        // 58 à 63
//            matchingST8.put("63", 7);
        matchingST8.put("69", 7);
        // 69 à 82
//            matchingST8.put("82", 7);
        matchingST8.put("84", 7);
        matchingST8.put("88", 7);
        // 84 à 88
        matchingST8.put("99", 7);
        //own
        matchingST8.put("49", 7);
        matchingST8.put("72", 7);
        matchingST8.put("66", 7);
        matchingST8.put("78", 7);
        matchingST8.put("65", 7);
        matchingST8.put("85", 7);
        matchingST8.put("80", 7);
        matchingST8.put("70", 7);
        matchingST8.put("51", 7);
        matchingST8.put("71", 7);
        matchingST8.put("74", 7);
        matchingST8.put("36", 7);


        //8
        matchingST8.put("52", 8);
        matchingST8.put("53.20Z", 8);
        //own
        matchingST8.put("50", 8);
        matchingST8.put("51.21", 8);
        matchingST8.put("51.21Z", 8);

        return matchingST8;
    }
}
