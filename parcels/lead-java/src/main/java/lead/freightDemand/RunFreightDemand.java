package lead.freightDemand;

import org.matsim.api.core.v01.Coord;

import java.util.Arrays;
import java.util.List;

public class RunFreightDemand {

    public static void main(String[] args) throws Exception {

        String etablissementFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockEtablissement_utf8.csv";
        String uniteLegaleFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/StockUniteLegale_utf8.csv";
//        String areaFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/nantes_coords.csv";
        String areaFile = "C:/lead/Marc/Freturb_Light/Input_Tabellen/sirene/idf_coords.csv";
        String apeClasses = "corrST45-NAF_MATSIM.csv";
        List<Coord> centers = Arrays.asList(new Coord(355182.5,6689309.6));

        if (args.length == 3) {
            etablissementFile = args[0];
            uniteLegaleFile = args[1];
            areaFile = args[2];
            centers = Arrays.asList();
        }

        FreightDemand freightDemand = new FreightDemand(etablissementFile, uniteLegaleFile, apeClasses, areaFile, centers);

        freightDemand.run();


    }

}
