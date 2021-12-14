package lead.freturbLightV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorisationV2 {

    final private static Map<String, Integer> matchingST8 = generateMatchingST8Map();
    final public static Map<String, St45Class> st45Map = generateST45Map();

    public static List<FirmDataV2> categorise(List<FirmDataV2> firms) {
        System.out.println("Start categorise St8 and St20");
        int count = 0;
        for (FirmDataV2 firm : firms){
            Integer match = matchingST8.get(firm.ape);
            if (match == null && firm.ape.contains(".")) {
                String[] codeSplit = firm.ape.split("\\.");
                match = matchingST8.get(codeSplit[0]);
            }
            if (match == null) {
                count++;
                firm.st8 = 0;
            } else {
                firm.st8 = match;
                firm.st20 = generateST20Map(firm.st8, firm.employees);
            }
        }
        System.out.println("No category found for " + count + " firms");
        System.out.println("Finished categorise");
        return firms;
    }

    private static Map<String, Integer> generateMatchingST8Map() {
        Map<String, Integer> matchingST8 = new HashMap<>();
        {
            // 1
            {
                matchingST8.put("01.11z", 1);
                matchingST8.put("01.49z", 1);
                matchingST8.put("02.40z", 1);
                matchingST8.put("81.30z", 1);
            }

            //2
            {
                matchingST8.put("84.24z", 2);
                matchingST8.put("84.25z", 2);
                matchingST8.put("91", 2);
                matchingST8.put("92", 2);
                matchingST8.put("93", 2);
                matchingST8.put("38", 2);
                matchingST8.put("39", 2);
                matchingST8.put("75", 2);
                matchingST8.put("86", 2);
                matchingST8.put("87", 2);
                matchingST8.put("88.1", 2);
                matchingST8.put("71.12b", 2);
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
            }

            //3
            {
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
                matchingST8.put("56", 3);
                matchingST8.put("26", 3);

                // 26 à 30
                matchingST8.put("27", 3);
                matchingST8.put("28", 3);
                matchingST8.put("29", 3);

                matchingST8.put("30", 3);
                matchingST8.put("33", 3);
            }

            //4
            {
                matchingST8.put("46", 4); // ganz
            }

            //5
            {
                matchingST8.put("47.11c", 5);
                matchingST8.put("47.11d", 5);
                matchingST8.put("47.19b", 5);
                matchingST8.put("47.11f", 5);
                matchingST8.put("47.2", 5);

                // 47.2 à 47.7
                matchingST8.put("47.3", 5);
                matchingST8.put("47.4", 5);
                matchingST8.put("47.5", 5);
                matchingST8.put("47.6", 5);

                matchingST8.put("47.7", 5);
            }

            //6
            {
                matchingST8.put("45", 6); // ganz
                matchingST8.put("47", 6); // ganz
                matchingST8.put("10.13b", 6);
                matchingST8.put("10.71c", 6);
                matchingST8.put("10.71d", 6);
                //+HCR
            }

            //7
            {
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
            }

            //8
            {
                matchingST8.put("52.29a", 8);
                matchingST8.put("53.20z", 8);
            }
        }
        return matchingST8;
    }

    private static Map<String, St45Class> generateST45Map() {
        Map<String, St45Class> st45Map = new HashMap<>();
        // fill  the map, data from Adrian Beziat
        {
            st45Map.put("1", new St45Class(1, "1", "Agriculture"));

            st45Map.put("2", new St45Class(2, "2-2", "Artisans (réparations)"));
            st45Map.put("3", new St45Class(2, "2-3", "Artisans (fabrication ou installation)"));
            st45Map.put("4", new St45Class(2, "2-4", "Artisans (fabrication ou installation - petites réparations)"));
            st45Map.put("5", new St45Class(2, "26Ha", "Tertiaire autre (services flux élevés)"));
            st45Map.put("6", new St45Class(2, "26Mi", "Tertiaire autre (services flux mixtes)"));
            st45Map.put("7", new St45Class(2, "26Mo", "Tertiaire autre (services flux moyens)"));

            st45Map.put("8", new St45Class(3, "3", "Industrie chimique"));
            st45Map.put("9", new St45Class(3, "4-2", "Industrie de biens de production et intermédiaires (de base)"));
            st45Map.put("10", new St45Class(3, "5-2", "Industrie de biens de consommation (produits alimentaires fragiles)"));
            st45Map.put("11", new St45Class(3, "5-4", "Industrie de biens de consommation (produits non alimentaires, équipement de la maison et de l’individu)"));
            st45Map.put("12", new St45Class(3, "5-5", "Industrie de biens de consommation (produits alimentaires non fragiles, équipement spécifique)"));
            st45Map.put("13", new St45Class(3, "4-6", "Industrie de biens de production et intermédiaires (petits objets)"));
            st45Map.put("14", new St45Class(3, "4-7", "Industrie de biens de production et intermédiaires (objets volumineux)"));
            st45Map.put("15", new St45Class(3, "34-2", "Industrie de la construction (réparations)"));
            st45Map.put("16", new St45Class(3, "34-3", "Industrie de la construction (fabrication ou installation)"));

            st45Map.put("17", new St45Class(4, "7-2", "Commerce de gros de produits intermédiaires fragiles"));
            st45Map.put("18", new St45Class(4, "8-2", "Commerce de gros de biens de consommation non alimentaires"));
            st45Map.put("19", new St45Class(4, "9-2", "Commerce de gros de biens de consommation alimentaires fragiles"));
            st45Map.put("20", new St45Class(4, "7-3", "Commerce de gros d’autres produits intermédiaires"));
            st45Map.put("21", new St45Class(4, "8-3", "Commerce de gros de biens de consommation non alimentaires"));
            st45Map.put("22", new St45Class(4, "9-3", "Commerce de gros d’autres biens de consommation alimentaires"));

            st45Map.put("23", new St45Class(5, "10", "Hypers et grands magasins polyvalents"));
            st45Map.put("24", new St45Class(5, "11", "Supermarchés"));
            st45Map.put("25", new St45Class(5, "12", "Grands magasins spécialisés"));

            st45Map.put("26", new St45Class(6, "13", "Supérettes"));
            st45Map.put("27", new St45Class(6, "14", "Commerces de détail, habillement, chaussures, cuir"));
            st45Map.put("28", new St45Class(6, "15", "Boucheries"));
            st45Map.put("29", new St45Class(6, "16", "Epiceries, alimentation"));
            st45Map.put("30", new St45Class(6, "17", "Boulangeries - Pâtisseries"));
            st45Map.put("31", new St45Class(6, "18", "Cafés, hôtels, restaurants"));
            st45Map.put("32", new St45Class(6, "19", "Pharmacies"));
            st45Map.put("33", new St45Class(6, "20", "Quincailleries"));
            st45Map.put("34", new St45Class(6, "21", "Commerce d’ameublement"));
            st45Map.put("35", new St45Class(6, "22", "Librairie papeterie"));
            st45Map.put("36", new St45Class(6, "23", "Autres commerces de détail"));
            st45Map.put("37", new St45Class(6, "29", "Commerces non sédentaires"));

            st45Map.put("38", new St45Class(7, "26Fa", "Tertiaire autre"));
            st45Map.put("39", new St45Class(7, "6", "Transport (sans entreposage)"));
            st45Map.put("40", new St45Class(7, "25", "Tertiaire pur"));
            st45Map.put("41", new St45Class(7, "27-2", "Bureaux non tertiaires (agriculture, commerces de gros)"));
            st45Map.put("42", new St45Class(7, "27-3", "Bureaux non tertiaires (commerce de détail, industrie, transport,collectivités)"));

            st45Map.put("43", new St45Class(8, "30", "Carrières"));
            st45Map.put("44", new St45Class(8, "28-2", "Entrepôts (encombrants)"));
            st45Map.put("45", new St45Class(8, "28-3", "Entrepôts (dont transport)"));
        }
        return st45Map;
    }

    static int generateST20Map(int ST8, int employee) {
        Map<String, String> st8Map = new HashMap<>();
        // fill  the map, data from Adrian Beziat
        {
            if (ST8 == 1) {
                return 0;
            } else if (ST8 == 2 && (employee == 2 || employee == 1)) {
                return 1;
            } else if (ST8 == 2 && employee > 2 && employee < 7) {
                return 2;
            } else if (ST8 == 2 && employee > 6) {
                return 3;
            } else if (ST8 == 3 && employee > 0 && employee < 5) {
                return 4;
            } else if (ST8 == 3 && employee > 4 && employee < 11) {
                return 5;
            } else if (ST8 == 3 && employee > 10) {
                return 6;
            } else if (ST8 == 4 && employee > 0 && employee < 7) {
                return 7;
            } else if (ST8 == 4 && employee > 6 && employee < 20) {
                return 8;
            } else if (ST8 == 4 && employee > 19) {
                return 9;
            } else if (ST8 == 5 && employee > 0 && employee < 71) {
                return 10;
            } else if (ST8 == 5 && employee > 70) {
                return 11;
            } else if (ST8 == 6 && employee > 0 && employee < 3) {
                return 12;
            } else if (ST8 == 6 && employee > 2 && employee < 6) {
                return 13;
            } else if (ST8 == 6 && employee > 5) {
                return 14;
            } else if (ST8 == 7 && employee > 0 && employee < 4) {
                return 15;
            } else if (ST8 == 7 && employee > 4 && employee < 19) {
                return 16;
            } else if (ST8 == 7 && employee > 18) {
                return 17;
            } else if (ST8 == 8 && employee > 0 && employee < 15) {
                return 18;
            } else if (ST8 == 8 && employee > 14) {
                return 19;
            }
        }
        return 0;
    }

    static class St45Class {
        final int st8;
        final String st45;
        final String libellé;

        St45Class(int st8, String st45, String libellé) {
            this.st8 = st8;
            this.st45 = st45;
            this.libellé = libellé;
        }

    }

}
