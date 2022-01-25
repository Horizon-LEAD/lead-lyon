package lead.freturbLightV2.analysis;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadRouteFile {

    public static void main(String[] args) {

        List<List<Coord>> routes = new ArrayList<>();
        int maxSize = 0;
        int trips = 0;

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/lead/Marc/Freturb_Light/Status/idf_Routes.txt")))){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("idf_Routes.txt")))){
            String line;
            List<String> header = null;
            while ((line = reader.readLine()) != null) {
                List<String> row = Arrays.asList(line.split("asdf"));
                List<Coord> route = new ArrayList<>();
                if (header == null) {
                    header = row;
                } else {
                    String stringRoute = row.get(header.indexOf("coord"));
                    String[] arrayRoute = stringRoute.split(":");
                    for (int i = 0; i < arrayRoute.length; i++) {
                        String[] stringCoord = arrayRoute[i].split(",");
                        Coord coord = new Coord(Double.parseDouble(stringCoord[0]), Double.parseDouble(stringCoord[1]));
                        route.add(coord);
                    }
                }
                if (route.size() > 1) {
                    routes.add(route);
                }
                if (route.size() > maxSize) {
                    maxSize = route.size();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double distance = 0;
        for (List<Coord> route : routes) {
            Coord start = null;
            Coord first = null;
            for (Coord coord : route) {
                if (start == null) {
                    start = coord;
                    first = coord;
                } else {
                    distance += (CoordUtils.calcEuclideanDistance(start, coord)/1000) * 1.4;
                    start = coord;
                    trips++;
                }
            }
            distance += (CoordUtils.calcEuclideanDistance(start, first)/1000) * 1.4;
        }
        System.out.println(distance);

        int[] histogram = new int[(maxSize)];
        for (List<Coord> route : routes) {
            int size = route.size();
            if (size == 2) {
                continue;
            }
            histogram[route.size()-1] = histogram[route.size()-1] + 1;
        }

        System.out.println(Arrays.toString(histogram));

        System.out.println("Contains " + routes.size() + " Routes.");

        System.out.println("Trips " + trips);
        System.out.println("Distance pre trip " + (distance/trips));

        System.out.println("Done");
    }

}
