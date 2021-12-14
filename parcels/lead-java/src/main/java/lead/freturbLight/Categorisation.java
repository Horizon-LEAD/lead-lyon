package lead.freturbLight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Categorisation {

    final public static Map<String, String> st8Map = generateST8Map();
    final public static Map<String, St45Class> st45Map = generateST45Map();
    final public static Map<String, St115Class> st115 = generateST155Map();
    final private static Map<String, Integer> matchingST8 = generateMatchingST8Map();

    public static Set<String> matching = new HashSet<>();

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

    private static Map<String, String> generateST8Map() {
        Map<String, String> st8Map = new HashMap<>();
        // fill  the map, data from Adrian Beziat
        {
            st8Map.put("1", "Agriculture");
            st8Map.put("2", "Services-artisanat");
            st8Map.put("3", "Industrie");
            st8Map.put("4", "Commerce de gros");
            st8Map.put("5", "Grande distribution");
            st8Map.put("6", "Petit commerce de détail");
            st8Map.put("7", "Tertiaire de bureaux");
            st8Map.put("8", "Transport & Logistique");
        }
        return st8Map;
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

    private static Map<String, St115Class> generateST155Map() {
        Map<String, St115Class> st115Map = new HashMap<>();
        // fill  the map, data from Adrian Beziat
        {
            st115Map.put("1", new St115Class(1, "1", "1a", "Agriculture", "0-2"));
            st115Map.put("2", new St115Class(1, "1", "1b", "Agriculture", "3+"));

            st115Map.put("3", new St115Class(2, "2-2", "2-2b","Artisans (réparations)", "1-2"));
            st115Map.put("4", new St115Class(2, "2-2", "2-2a","Artisans (réparations)", "0"));
            st115Map.put("5", new St115Class(2, "2-2", "2-2c","Artisans (réparations)", "3-19"));
            st115Map.put("6", new St115Class(2, "2-2", "2-2d","Artisans (réparations)", "20+"));
            st115Map.put("7", new St115Class(2, "2-3", "2-3a", "Artisans (fabrication ou installation)", "0"));
            st115Map.put("8", new St115Class(2, "2-3", "2-3b", "Artisans (fabrication ou installation)", "1-2"));
            st115Map.put("9", new St115Class(2, "2-4", "2-4a", "Artisans (fabrication ou installation - petites réparations)", "0"));
            st115Map.put("10", new St115Class(2, "2-4", "2-4b", "Artisans (fabrication ou installation - petites réparations)", "1"));
            st115Map.put("11", new St115Class(2, "26Ha", "26Ha-a", "Tertiaire autre (services flux élevés)", "0"));
            st115Map.put("12", new St115Class(2, "26Ha", "26Ha-b", "Tertiaire autre (services flux élevés)", "1+"));
            st115Map.put("13", new St115Class(2, "26Mi", "26Mi-a", "Tertiaire autre (services flux mixtes)","0"));
            st115Map.put("14", new St115Class(2, "26Mi", "26Mi-b", "Tertiaire autre (services flux mixtes)","1-9"));
            st115Map.put("15", new St115Class(2, "26Mi", "26Mi-c", "Tertiaire autre (services flux mixtes)","10-49"));
            st115Map.put("16", new St115Class(2, "26Mi", "26Mid", "Tertiaire autre (services flux mixtes)","50+"));
            st115Map.put("17", new St115Class(2, "26Mo", "26Mo-a", "Tertiaire autre (services flux moyens)","0-5"));
            st115Map.put("18", new St115Class(2, "26Mo", "26Mo-b", "Tertiaire autre (services flux moyens)","6+"));

            st115Map.put("19", new St115Class(3, "3", "3a", "Industrie chimique", "0-5"));
            st115Map.put("20", new St115Class(3, "3", "3b", "Industrie chimique", "6+"));
            st115Map.put("21", new St115Class(3, "4-2", "4-2a", "Industrie de biens de production et intermédiaires (de base)", "0-5"));
            st115Map.put("22", new St115Class(3, "4-2", "4-2b", "Industrie de biens de production et intermédiaires (de base)", "6+"));
            st115Map.put("23", new St115Class(3, "5-2", "5-2a", "Industrie de biens de consommation (produits alimentaires fragiles)", "0-5"));
            st115Map.put("24", new St115Class(3, "5-2", "5-2b", "Industrie de biens de consommation (produits alimentaires fragiles)", "6+"));
            st115Map.put("25", new St115Class(3, "5-4", "5-4a", "Industrie de biens de consommation (produits non alimentaires, équipement de la maison et de l’individu)", "0-5"));
            st115Map.put("26", new St115Class(3, "5-4", "5-4b", "Industrie de biens de consommation (produits non alimentaires, équipement de la maison et de l’individu)", "6+"));
            st115Map.put("27", new St115Class(3, "5-5", "5-5a", "Industrie de biens de consommation (produits alimentaires non fragiles, équipement spécifique)", "0-5"));
            st115Map.put("28", new St115Class(3, "5-5", "5-5b", "Industrie de biens de consommation (produits alimentaires non fragiles, équipement spécifique)", "6+"));
            st115Map.put("29", new St115Class(3, "4-6", "4-6a", "Industrie de biens de production et intermédiaires (petits objets)", "0-5"));
            st115Map.put("30", new St115Class(3, "4-6", "4-6b", "Industrie de biens de production et intermédiaires (petits objets)", "6+"));
            st115Map.put("31", new St115Class(3, "4-7", "4-7a", "Industrie de biens de production et intermédiaires (objets volumineux)", "0-5"));
            st115Map.put("32", new St115Class(3, "4-7", "4-7b", "Industrie de biens de production et intermédiaires (objets volumineux)", "6+"));
            st115Map.put("33", new St115Class(3, "34-2", "34-2a", "Industrie de la construction (réparations)", "0-5"));
            st115Map.put("34", new St115Class(3, "34-2", "34-2b", "Industrie de la construction (réparations)", "6+"));
            st115Map.put("35", new St115Class(3, "34-3", "34-3a" ,"Industrie de la construction (fabrication ou installation)", "0-5"));
            st115Map.put("36", new St115Class(3, "34-3", "34-3b" ,"Industrie de la construction (fabrication ou installation)", "6+"));

            st115Map.put("37", new St115Class(4, "7-2", "7-2a", "Commerce de gros de produits intermédiaires fragiles", "0-2"));
            st115Map.put("38", new St115Class(4, "7-2", "7-2b", "Commerce de gros de produits intermédiaires fragiles", "3-9"));
            st115Map.put("39", new St115Class(4, "7-2", "7-2c", "Commerce de gros de produits intermédiaires fragiles", "10+"));
            st115Map.put("40", new St115Class(4, "8-2", "8-2a", "Commerce de gros de biens de consommation non alimentaires", "0-2"));
            st115Map.put("41", new St115Class(4, "8-2", "8-2b", "Commerce de gros de biens de consommation non alimentaires", "3-9"));
            st115Map.put("42", new St115Class(4, "8-2", "8-2c", "Commerce de gros de biens de consommation non alimentaires", "10+"));
            st115Map.put("43", new St115Class(4, "9-2", "9-2a", "Commerce de gros de biens de consommation alimentaires fragiles", "0-2"));
            st115Map.put("44", new St115Class(4, "9-2", "9-2b", "Commerce de gros de biens de consommation alimentaires fragiles", "3-9"));
            st115Map.put("45", new St115Class(4, "9-2", "9-2c", "Commerce de gros de biens de consommation alimentaires fragiles", "10+"));
            st115Map.put("46", new St115Class(4, "7-3", "7-3a", "Commerce de gros d’autres produits intermédiaires", "0-2"));
            st115Map.put("47", new St115Class(4, "7-3", "7-3b", "Commerce de gros d’autres produits intermédiaires", "3-9"));
            st115Map.put("48", new St115Class(4, "7-3", "7-3c", "Commerce de gros d’autres produits intermédiaires", "10+"));
            st115Map.put("49", new St115Class(4, "8-3", "8-3a", "Commerce de gros de biens de consommation non alimentaires", "0-2"));
            st115Map.put("50", new St115Class(4, "8-3", "8-3b", "Commerce de gros de biens de consommation non alimentaires", "3-9"));
            st115Map.put("51", new St115Class(4, "8-3", "8-3c", "Commerce de gros de biens de consommation non alimentaires", "10+"));
            st115Map.put("52", new St115Class(4, "9-3", "9-3a", "Commerce de gros d’autres biens de consommation alimentaires", "0-2"));
            st115Map.put("53", new St115Class(4, "9-3", "9-3b", "Commerce de gros d’autres biens de consommation alimentaires", "3-9"));
            st115Map.put("54", new St115Class(4, "9-3", "9-3c", "Commerce de gros d’autres biens de consommation alimentaires", "10+"));

            st115Map.put("55", new St115Class(5, "10", "10", "Hypers et grands magasins polyvalents", "-"));
            st115Map.put("56", new St115Class(5, "11", "11", "Supermarchés", "-"));
            st115Map.put("57", new St115Class(5, "12", "12a", "Grands magasins spécialisés", "10-99"));
            st115Map.put("58", new St115Class(5, "12", "12b", "Grands magasins spécialisés", "100+"));

            st115Map.put("59", new St115Class(6, "13", "13" ,"Supérettes", "-"));
            st115Map.put("60", new St115Class(6, "14", "14a" ,"Commerces de détail, habillement, chaussures, cuir", "0"));
            st115Map.put("61", new St115Class(6, "14", "14b" ,"Commerces de détail, habillement, chaussures, cuir", "1-5"));
            st115Map.put("62", new St115Class(6, "14", "14c" ,"Commerces de détail, habillement, chaussures, cuir", "6+"));
            st115Map.put("63", new St115Class(6, "15", "15a" ,"Boucheries", "0"));
            st115Map.put("64", new St115Class(6, "15", "15b" ,"Boucheries", "1-5"));
            st115Map.put("65", new St115Class(6, "15", "15c" ,"Boucheries", "6+"));
            st115Map.put("66", new St115Class(6, "16", "16a" ,"Epiceries, alimentation", "0"));
            st115Map.put("67", new St115Class(6, "16", "16b" ,"Epiceries, alimentation", "1-5"));
            st115Map.put("68", new St115Class(6, "16", "16c" ,"Epiceries, alimentation", "6+"));
            st115Map.put("69", new St115Class(6, "17", "17a" ,"Boulangeries - Pâtisseries", "0"));
            st115Map.put("70", new St115Class(6, "17", "17b" ,"Boulangeries - Pâtisseries", "1-5"));
            st115Map.put("71", new St115Class(6, "17", "17c" ,"Boulangeries - Pâtisseries", "6+"));
            st115Map.put("72", new St115Class(6, "18", "18a" ,"Cafés, hôtels, restaurants", "0"));
            st115Map.put("73", new St115Class(6, "18", "18b" ,"Cafés, hôtels, restaurants", "1-2"));
            st115Map.put("74", new St115Class(6, "18", "18c" ,"Cafés, hôtels, restaurants", "3-9"));
            st115Map.put("75", new St115Class(6, "18", "18d" ,"Cafés, hôtels, restaurants", "10-49"));
            st115Map.put("76", new St115Class(6, "18", "18e" ,"Cafés, hôtels, restaurants", "50+"));
            st115Map.put("77", new St115Class(6, "19", "19a" ,"Pharmacies", "0-2"));
            st115Map.put("78", new St115Class(6, "19", "19b" ,"Pharmacies", "3-9"));
            st115Map.put("79", new St115Class(6, "19", "19c" ,"Pharmacies", "10+"));
            st115Map.put("80", new St115Class(6, "20", "20a" ,"Quincailleries", "1-9"));
            st115Map.put("81", new St115Class(6, "20", "20b" ,"Quincailleries", "10"));
            st115Map.put("82", new St115Class(6, "21", "21a" ,"Commerce d’ameublement", "0-5"));
            st115Map.put("83", new St115Class(6, "21", "21b" ,"Commerce d’ameublement", "6-99"));
            st115Map.put("84", new St115Class(6, "22", "22a" ,"Librairie papeterie", "0"));
            st115Map.put("85", new St115Class(6, "22", "22b" ,"Librairie papeterie", "1-5"));
            st115Map.put("86", new St115Class(6, "22", "22c" ,"Librairie papeterie", "6+"));
            st115Map.put("87", new St115Class(6, "23", "23a" ,"Autres commerces de détail", "0"));
            st115Map.put("88", new St115Class(6, "23", "23b" ,"Autres commerces de détail", "1-9"));
            st115Map.put("89", new St115Class(6, "23", "23c" ,"Autres commerces de détail", "10-99"));
            st115Map.put("90", new St115Class(6, "29", "29" ,"Commerces non sédentaires", "-"));

            st115Map.put("91", new St115Class(7, "26Fa", "26Fa-a", "Tertiaire autre", "0"));
            st115Map.put("92", new St115Class(7, "26Fa", "26Fa-b", "Tertiaire autre", "1-5"));
            st115Map.put("93", new St115Class(7, "26Fa", "26Fa-c", "Tertiaire autre", "6-49"));
            st115Map.put("94", new St115Class(7, "26Fa", "26Fa-d", "Tertiaire autre", "50+"));
            st115Map.put("95", new St115Class(7, "6", "6a", "Transport (sans entreposage)", "0-2"));
            st115Map.put("96", new St115Class(7, "6", "6b", "Transport (sans entreposage)", "3-9"));
            st115Map.put("97", new St115Class(7, "6", "6c", "Transport (sans entreposage)", "10+"));
            st115Map.put("98", new St115Class(7, "25", "25a", "Tertiaire pur", "0"));
            st115Map.put("99", new St115Class(7, "25", "25b", "Tertiaire pur", "1-2"));
            st115Map.put("100", new St115Class(7, "25", "25c", "Tertiaire pur", "3-5"));
            st115Map.put("101", new St115Class(7, "25", "25d", "Tertiaire pur", "6-9"));
            st115Map.put("102", new St115Class(7, "25", "25e", "Tertiaire pur", "10-49"));
            st115Map.put("103", new St115Class(7, "25", "25f", "Tertiaire pur", "50-499"));
            st115Map.put("104", new St115Class(7, "25", "25g", "Tertiaire pur", "500+"));
            st115Map.put("105", new St115Class(7, "27-2", "27-2a", "Bureaux non tertiaires (agriculture, commerces de gros)", "0-9"));
            st115Map.put("106", new St115Class(7, "27-2", "27-2b", "Bureaux non tertiaires (agriculture, commerces de gros)", "10"));
            st115Map.put("107", new St115Class(7, "27-3", "27-3a", "Bureaux non tertiaires (commerce de détail, industrie, transport,collectivités)",  "0-9"));
            st115Map.put("108", new St115Class(7, "27-3", "27-3b", "Bureaux non tertiaires (commerce de détail, industrie, transport,collectivités)",  "10"));

            st115Map.put("109", new St115Class(8, "30", "30a", "Carrières", "0-5"));
            st115Map.put("110", new St115Class(8, "30", "30b", "Carrières", "6+"));
            st115Map.put("111", new St115Class(8, "28-2", "28-2a", "Entrepôts (encombrants)", "0-5"));
            st115Map.put("112", new St115Class(8, "28-2", "28-2b", "Entrepôts (encombrants)", "6+"));
            st115Map.put("113", new St115Class(8, "28-3", "28-3a", "Entrepôts (dont transport)", "0-5"));
            st115Map.put("114", new St115Class(8, "28-3", "28-3b", "Entrepôts (dont transport)", "6+"));
        }
        return st115Map;
    }

    public static Integer matchingst8(String APET700) {
        Integer match = matchingST8.get(APET700);
        if (match == null && APET700.contains(".")) {
            String[] codeSplit = APET700.split("\\.");
            match = matchingST8.get(codeSplit[0]);
        }
        if (match == null) {
            matching.add(APET700);
            match = 0;
        }
        return match;
    }

    static class St20Class {
        final int index;
        public St20Class(int index) {
            this.index = index;
        }
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

    static class St115Class {

        final int st8;
        final String st45;
        final String st115;
        final String libellé;
        final String employees;

        St115Class(int st8, String st45, String st115, String libellé, String employees) {
            this.st8 = st8;
            this.st45 = st45;
            this.st115 = st115;
            this.libellé = libellé;
            this.employees = employees;
        }

    }

}
