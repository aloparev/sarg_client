import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class LogicTest {
    static Board bb;

    static Move move0;
    static Move move3;

    static int move0key = 0;
    static int move3key = 3;

    static int rid = 0;
    static int gid = 1;
    static int bid = 2;

    @Before
    public void setup() {
        bb = new Board(rid);
        move0 = new Move(0, 0);
        move3 = new Move(0, 3);
    }

    @Test
    public void getBestMove() {
        Move bestMove = Logic.getBestMoveForOwner(bb);
        log.info(bestMove.toString());
        assertEquals(0, bestMove.x + bestMove.y);
    }

    @Test
    public void getBestMoveShouldPreferScoring() {
        bb.red.put(move3key, move3);
        bb.free.remove(move3key);

        Move bestMove = Logic.getBestMoveForOwner(bb);
        log.info(bestMove.toString());
    }

    @Test
    public void getMovePointsForDepthX_0() {
        float points = Logic.runMinMax(bb, 0, 1, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getMovePointsForDepthX_10() {
        float points = Logic.runMinMax(bb, 10, 1, -1);
        log.info(String.valueOf(points));
    }
//
//    @Test
//    public void getMovePointsForDepthX_20() {
//        int points = Logic.getMovePointsForDepthX(bb, 20, 1, -1);
//        log.info(String.valueOf(points));
//    }
//
//    @Test
//    public void getMovePointsForDepthX_30() {
//        int points = Logic.getMovePointsForDepthX(bb, 30, 1, -1);
//        log.info(String.valueOf(points));
//    }
//
//    @Test
//    public void getMovePointsForDepthX_40() {
//        int points = Logic.getMovePointsForDepthX(bb, 40, 1, -1);
//        log.info(String.valueOf(points));
//    }

    @Test
    public void getMovePointsForDepthFour() {
        float points = Logic.runMinMax(bb, 0, 4, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getBestRankedMoveFromScopeZero() {
        RankedMove rm = Logic.getRankedMoveFromScope(bb, bb.owner, false);
        log.info(rm.toString());
        assertEquals(0, rm.moveKey);
    }

    @Test
    public void getBestRankedMoveFromScopeOneEleven() {
        bb.updatePlayer(move0key);
        RankedMove rm = Logic.getRankedMoveFromScope(bb, bb.owner, false);
        log.info(rm.toString());
//        assertEquals(0, rm.moveKey);
    }

    @Test
    public void getBestRankedMoveFromScope3() {
        bb.red.put(move3key, move3);
        bb.free.remove(move3key);

        RankedMove rm = Logic.getRankedMoveFromScope(bb, bb.owner, false);
        log.info(rm.toString());
        assertEquals(3, rm.moveKey);
    }
}