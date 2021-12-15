package lead.freturbLightV2;

import org.matsim.api.core.v01.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lead.freturbLightV2.DistributionV2.DistributionManagement.Management;
import static lead.freturbLightV2.DistributionV2.DistributionMovement.Movement;
import static lead.freturbLightV2.DistributionV2.DistributionVehicle.Vehicle;
import static lead.freturbLightV2.DistributionV2.DistributionVehicleST20.VehicleST20;

public class Move implements Comparable {
    public static List<Move> movementsList = new ArrayList<>();
    private static int idCount = 0;
    public int centerId;
    int id;
    int st8;
    Movement disMove;
    Vehicle disVeh;
    Management disMan;
    VehicleST20 disVeh20;
    Coord ownCoord;
    double travelDistance;
    RouteType routeType;
    int logisticType = 0;
    List<Integer> logisticMatch = new ArrayList<>();

    enum RouteType {direct, round}

    void setLogistic() {
        this.id = idCount++;
        if (disMove.equals(Movement.enlèvements)) {
            if (disVeh20.equals(VehicleST20.PL)) {
                if (disMan.equals(Management.CA)) {
                    logisticType = 1;
                    logisticMatch = Arrays.asList(7, 13);
                } else if (disMan.equals(Management.CPD)) {
                    logisticType = 2;
                    logisticMatch = Arrays.asList(8, 14);
                } else if (disMan.equals(Management.CPE)) {
                    logisticType = 3;
                    logisticMatch = Arrays.asList(9, 15);
                }
            } else if (disVeh20.equals(VehicleST20.VUL)) {
                if (disMan.equals(Management.CA)) {
                    logisticType = 4;
                    logisticMatch = Arrays.asList(10, 16);
                } else if (disMan.equals(Management.CPD)) {
                    logisticType = 5;
                    logisticMatch = Arrays.asList(11, 17);
                } else if (disMan.equals(Management.CPE)) {
                    logisticType = 6;
                    logisticMatch = Arrays.asList(12, 18);
                }
            }
        } else if (disMove.equals(Movement.livraisons)) {
            if (disVeh20.equals(VehicleST20.PL)) {
                if (disMan.equals(Management.CA)) {
                    logisticType = 7;
                    logisticMatch = Arrays.asList(1, 13);
                } else if (disMan.equals(Management.CPD)) {
                    logisticType = 8;
                    logisticMatch = Arrays.asList(2, 14);
                } else if (disMan.equals(Management.CPE)) {
                    logisticType = 9;
                    logisticMatch = Arrays.asList(3, 15);
                }
            } else if (disVeh20.equals(VehicleST20.VUL)) {
                if (disMan.equals(Management.CA)) {
                    logisticType = 10;
                    logisticMatch = Arrays.asList(4, 16);
                } else if (disMan.equals(Management.CPD)) {
                    logisticType = 11;
                    logisticMatch = Arrays.asList(5, 17);
                } else if (disMan.equals(Management.CPE)) {
                    logisticType = 12;
                    logisticMatch = Arrays.asList(6, 18);
                }
            }
        } else if (disMove.equals(Movement.conjointes)) {
            if (disVeh20.equals(VehicleST20.PL)) {
                if (disMan.equals(Management.CA)) {
                    logisticType = 13;
                    logisticMatch = Arrays.asList(7, 1, 13);
                } else if (disMan.equals(Management.CPD)) {
                    logisticType = 14;
                    logisticMatch = Arrays.asList(8, 2, 14);
                } else if (disMan.equals(Management.CPE)) {
                    logisticType = 15;
                    logisticMatch = Arrays.asList(9, 3, 14);
                }
            } else if (disVeh20.equals(VehicleST20.VUL)) {
                if (disMan.equals(Management.CA)) {
                    logisticType = 16;
                    logisticMatch = Arrays.asList(10, 4, 16);
                } else if (disMan.equals(Management.CPD)) {
                    logisticType = 17;
                    logisticMatch = Arrays.asList(11, 5, 17);
                } else if (disMan.equals(Management.CPE)) {
                    logisticType = 18;
                    logisticMatch = Arrays.asList(12, 6, 18);
                }
            }
        }
        if (logisticType == 0) {
            System.out.println("No logistic type was found.");
        }
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.travelDistance, ((Move) o).travelDistance);
    }

    @Override
    public String toString(){
        return "Move: " + disMove + "; Vehicle: " + disVeh20 + "; Management: " + disMan + "; Travel Distance: " + travelDistance + ": Logistic Type: " + logisticType;
    }

}
