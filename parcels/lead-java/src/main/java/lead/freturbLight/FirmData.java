package lead.freturbLight;

import lead.freturbLight.Distribution.DistributionVehicleST20.VehicleST20;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static lead.freturbLight.Distribution.DistributionManagement.Management;
import static lead.freturbLight.Distribution.DistributionMovement.Movement;
import static lead.freturbLight.Distribution.DistributionVehicle.Vehicle;

public class FirmData {

    static final Random random = new Random(21656368);
    public static List<Move> movementsList = new ArrayList<>();

    public final String SIRET;
    final String L4_VOIE;
    public final String APET700;
    public final int ST8;
    final int ST45;
    public final int EFETCENT;
    public int MOVEMENTS;
    final Coord COORD;
    final String SIREN;
    final int ST20;
    final Move[] moves;
    final double centerDistance;


    FirmData(Coord center, String SIRET, String L4_VOIE, String APET700, int EFETCENT, Coord coord, String SIREN) throws Exception {
        this.SIRET = SIRET;
        this.L4_VOIE = L4_VOIE;
        this.APET700 = APET700;
        this.ST8 = Categorisation.matchingst8(APET700);
//       this.ST45 = Categorisation.matchingst45(APET700);
        this.EFETCENT = EFETCENT;
//        this.MOVEMENTS = CreateMovement.calculateMovement(""+ST45, EFETCENT);
        this.COORD = coord;
        this.SIREN = SIREN;
        this.centerDistance = CoordUtils.calcEuclideanDistance(center, coord)/1000;
        this.ST45 = Categorisation.matchingst8(APET700);
        this.MOVEMENTS = (int) CreateMovement.calculateMovementTest(ST8, EFETCENT);
        this.ST20 = Categorisation.generateST20Map(ST8, EFETCENT);
        this.moves = generateMoves();
    }

    private Move[] generateMoves() {

        Move[] moves = new Move[MOVEMENTS];

        for (int i = 0; i < moves.length; i++) {
               Move move = Distribution.distributeLogistics(new Move(), this.ST8, this.ST20, this.centerDistance, random);
               move.ownCoord = this.COORD;
               moves[i] = move;
        }
        return moves;
    }


    @Override
    public String toString() {
        return "" + SIRET + ";" + ST8 + ";" + APET700 + ";" + EFETCENT + ";" + MOVEMENTS + ";" + COORD.getX() + ";" + COORD.getY() + ";" + centerDistance;
    }

    public static class Move {

        Movement disMove;
        Vehicle disVeh;
        Management disMan;
        VehicleST20 disVeh20;
        Coord ownCoord;

        double travelDistance;

    }
}
