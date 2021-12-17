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
    int logisticDirectType = 0;
    int logisticRoundType = 0;
    List<Integer> logisticDirectMatch = new ArrayList<>();
    List<Integer> logisticRoundMatch = new ArrayList<>();

    enum RouteType {direct, round}

    public Move() {
        this.id = idCount++;
    }

    void setLogistic() {
        if (routeType.equals(RouteType.direct)) {
            if (disMove.equals(Movement.enlèvements)) {
                if (disVeh20.equals(VehicleST20.PL)) {
                    if (disMan.equals(Management.CA)) {
                        logisticDirectType = 1;
                        logisticDirectMatch = Arrays.asList(7);
                    } else if (disMan.equals(Management.CPD)) {
                        logisticDirectType = 2;
                        logisticDirectMatch = Arrays.asList(8);
                    } else if (disMan.equals(Management.CPE)) {
                        logisticDirectType = 3;
                        logisticDirectMatch = Arrays.asList(9);
                    }
                } else if (disVeh20.equals(VehicleST20.VUL)) {
                    if (disMan.equals(Management.CA)) {
                        logisticDirectType = 4;
                        logisticDirectMatch = Arrays.asList(10);
                    } else if (disMan.equals(Management.CPD)) {
                        logisticDirectType = 5;
                        logisticDirectMatch = Arrays.asList(11);
                    } else if (disMan.equals(Management.CPE)) {
                        logisticDirectType = 6;
                        logisticDirectMatch = Arrays.asList(12);
                    }
                }
            } else if (disMove.equals(Movement.livraisons)) {
                if (disVeh20.equals(VehicleST20.PL)) {
                    if (disMan.equals(Management.CA)) {
                        logisticDirectType = 7;
                        logisticDirectMatch = Arrays.asList(1, 13);
                    } else if (disMan.equals(Management.CPD)) {
                        logisticDirectType = 8;
                        logisticDirectMatch = Arrays.asList(2, 14);
                    } else if (disMan.equals(Management.CPE)) {
                        logisticDirectType = 9;
                        logisticDirectMatch = Arrays.asList(3, 15);
                    }
                } else if (disVeh20.equals(VehicleST20.VUL)) {
                    if (disMan.equals(Management.CA)) {
                        logisticDirectType = 10;
                        logisticDirectMatch = Arrays.asList(4, 16);
                    } else if (disMan.equals(Management.CPD)) {
                        logisticDirectType = 11;
                        logisticDirectMatch = Arrays.asList(5, 17);
                    } else if (disMan.equals(Management.CPE)) {
                        logisticDirectType = 12;
                        logisticDirectMatch = Arrays.asList(6, 18);
                    }
                }
            } else if (disMove.equals(Movement.conjointes)) {
                if (disVeh20.equals(VehicleST20.PL)) {
                    if (disMan.equals(Management.CA)) {
                        logisticDirectType = 13;
                        logisticDirectMatch = Arrays.asList(7);
                    } else if (disMan.equals(Management.CPD)) {
                        logisticDirectType = 14;
                        logisticDirectMatch = Arrays.asList(8);
                    } else if (disMan.equals(Management.CPE)) {
                        logisticDirectType = 15;
                        logisticDirectMatch = Arrays.asList(9);
                    }
                } else if (disVeh20.equals(VehicleST20.VUL)) {
                    if (disMan.equals(Management.CA)) {
                        logisticDirectType = 16;
                        logisticDirectMatch = Arrays.asList(10);
                    } else if (disMan.equals(Management.CPD)) {
                        logisticDirectType = 17;
                        logisticDirectMatch = Arrays.asList(11);
                    } else if (disMan.equals(Management.CPE)) {
                        logisticDirectType = 18;
                        logisticDirectMatch = Arrays.asList(12);
                    }
                }
            }
            if (logisticDirectType == 0) {
                System.out.println("No logistic type was found.");
            }
        }
        if (routeType.equals(RouteType.round)) {
            if (disVeh20.equals(VehicleST20.PL)) {
                if (disMan.equals(Management.CA)) {
                    logisticRoundType = 1;
                } else if (disMan.equals(Management.CPD)) {
                    logisticRoundType = 2;
                } else if (disMan.equals(Management.CPE)) {
                    logisticRoundType = 3;
                }
            } else if (disVeh20.equals(VehicleST20.VUL)) {
                if (disMan.equals(Management.CA)) {
                    logisticRoundType = 4;
                } else if (disMan.equals(Management.CPD)) {
                    logisticRoundType = 5;
                } else if (disMan.equals(Management.CPE)) {
                    logisticRoundType = 6;
                }
            }
            if (logisticRoundType == 0) {
                System.out.println("No logistic type was found.");
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.travelDistance, ((Move) o).travelDistance);
    }

    @Override
    public String toString(){
        return "Move: " + disMove + "; Vehicle: " + disVeh20 + "; Management: " + disMan + "; Travel Distance: " + travelDistance + ": Logistic Type: " + logisticDirectType;
    }

}
