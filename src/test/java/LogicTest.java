import lenz.htw.sarg.Move;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class LogicTest {
    Logger log = LoggerFactory.getLogger(LogicTest.class);

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
    public void getBestMovePreferScoring() {
        bb.red.put(move3key, move3);
        bb.free.remove(move3key);

        Move bestMove = Logic.getBestMoveForOwner(bb);
        log.info(bestMove.toString());
    }

    @Test
    public void getMovePointsForDepthX_0() {
        int points = Logic.getMovePointsForDepthX(bb, 0, 1, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getMovePointsForDepthX_10() {
        int points = Logic.getMovePointsForDepthX(bb, 10, 1, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getMovePointsForDepthX_20() {
        int points = Logic.getMovePointsForDepthX(bb, 20, 1, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getMovePointsForDepthX_30() {
        int points = Logic.getMovePointsForDepthX(bb, 30, 1, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getMovePointsForDepthX_40() {
        int points = Logic.getMovePointsForDepthX(bb, 40, 1, -1);
        log.info(String.valueOf(points));
    }

    @Test
    public void getBestRankedMoveFromScope0() {
        RankedMove rm = Logic.getRankedMoveFromScope(bb, bb.owner, false);
        log.info(rm.toString());
        assertEquals(0, rm.moveKey);
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