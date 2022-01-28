package lead.freightDemand;

import org.matsim.api.core.v01.Coord;

import java.util.Arrays;
import java.util.List;

public class RunFreightDemand {

    public static void main(String[] args) throws Exception {

        String etablissementFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockEtablissement_utf8.csv";
//        String areaFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/nantes_coords.csv";
        String areaFile = "nantes_coords.csv";
        String apeClasses = "corrST45-NAF_MATSIM.csv";
        // idf
//        List<Coord> centers = Arrays.asList(new Coord(652111.1,6861807.2));
        // nantes
        List<Coord> centers = Arrays.asList(new Coord(355182.5,6689309.6));
        // lyon
//        List<Coord> centers = Arrays.asList(new Coord(844819.280, 6517939.271), new Coord(913487.627, 6458394.690), new Coord(808804.412, 6484085.296), new Coord(783594.005, 6550352.652), new Coord(872190.579, 6569800.681));
        // touluse
//        List<Coord> centers = Arrays.asList(new Coord(574309.9,6279302.2));

        String output = "";

        if (args.length == 3) {
            etablissementFile = args[0];
            areaFile = args[1];
            if (args[2].equals("idf")) {
                centers = Arrays.asList(new Coord(652111.1,6861807.2));
            } else if (args[2].equals("nantes")) {
                centers = Arrays.asList(new Coord(355182.5,6689309.6));
            } else if (args[2].equals("lyon")) {
                centers = Arrays.asList(new Coord(844819.280, 6517939.271), new Coord(913487.627, 6458394.690), new Coord(808804.412, 6484085.296), new Coord(783594.005, 6550352.652), new Coord(872190.579, 6569800.681));
            } else if (args[2].equals("toulouse")) {
                centers = Arrays.asList(new Coord(574309.9,6279302.2));
            }
            output = args[3];
        }

        FreightDemand freightDemand = new FreightDemand(etablissementFile, apeClasses, areaFile, centers, output);

        freightDemand.run();


    }

}
