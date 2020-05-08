import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

import static org.junit.Assert.*;

@Slf4j
public class RedBoardTest {
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
    public void boardInit() {
        assertEquals(0, bb.owner);
        assertTrue(bb.points.length == 3);
        assertArrayEquals(new int[] {0,0,0,}, bb.points);
    }

    @Test
    public void getMoveKeyAndPlayer0() {
        int moveKey = bb.getMoveKey(move0);
        assertEquals(0, moveKey);

        int player = bb.getPlayerFromMove(moveKey);
        assertEquals(0, player);
    }

    @Test
    public void updatePlayer0() {
        bb.curPlayer = rid;
        bb.updatePlayer(move0key);

        TreeMap<Integer, Move> expectedRedStones;
        expectedRedStones = new TreeMap<Integer, Move>() {{
            put(1, new Move(0, 1));
            put(11, new Move(1, 1));

            put(10, new Move(1, 0));
            put(20, new Move(2, 0));
            put(30, new Move(3, 0));
            put(40, new Move(4, 0));
        }};

        log.info("board after updatePlayer: " + bb);
        assertTrue(expectedRedStones.keySet().equals(bb.red.keySet()));
    }

    @Test
    public void getKeyLeft0() {
        assertEquals(1, bb.getKeyLeft(move0key));
    }

    @Test
    public void getKeyLeft3() {
        bb.red.put(move3key, move3);
        bb.free.remove(move3key);

        assertEquals(-1, bb.getKeyLeft(move3key));
    }

    @Test
    public void getKeyRight0() {
        assertEquals(11, bb.getKeyRight(move0key));
    }

    @Test
    public void removeFromRed0() {
        TreeMap<Integer, Move> expectedRedStones;
        expectedRedStones = new TreeMap<Integer, Move>() {{
            put(10, new Move(1, 0));
            put(20, new Move(2, 0));
            put(30, new Move(3, 0));
            put(40, new Move(4, 0));
        }};

        bb.removeFromRed(move0key);
//        log.info("boards.red=" + bb.red);
        assertTrue(expectedRedStones.keySet().equals(bb.red.keySet()));
    }

//    @Test
//    public void redMove0() {
//        TreeMap<Integer, Move> expectedRedStones;
//        expectedRedStones = new TreeMap<Integer, Move>() {{
//            put(1, new Move(0, 1));
//            put(11, new Move(1, 1));
//
//            put(0, new Move(0, 0));
//            put(10, new Move(1, 0));
//            put(20, new Move(2, 0));
//            put(30, new Move(3, 0));
//            put(40, new Move(4, 0));
//        }};
//
//        bb.redMove(move0key);
////        log.info("boards.red=" + bb.red);
//        assertTrue(expectedRedStones.keySet().equals(bb.red.keySet()));
//    }

    @Test
    public void getPointsForPlayerXv1() {
        assertEquals(8, bb.getPointsForPlayerXv1(rid));

        bb.red.put(1, new Move(0, 1));
        bb.red.put(11, new Move(1, 1));
        assertEquals(11, bb.getPointsForPlayerXv1(rid));

        bb.points[rid] = 2;
        assertEquals(31, bb.getPointsForPlayerXv1(rid));
    }

    /*
    20:50:58.209 [main] INFO  Client - Logic.getBestMoveForOwner: 4,2
20:50:58.209 [main] INFO  Client - board.updateBoard: 4,2
20:50:58.209 [main] INFO  Board - red: removing green stone
20:50:58.209 [main] INFO  Board - left/right moveKey: 31/22
20:50:58.209 [main] INFO  Client - board after integration: board owner=2 scores=[3, 2, 1] curPlayer=2 expPlayer=0 kicked=[false, false, false] red stones=[1, 11, 43] green stones=[2, 13, 44, 46, 58] blue stones=[20, 22, 31, 41, 63, 74, 75, 76, 85, 87, 88]
free=[0, 3, 4, 10, 12, 14, 15, 21, 23, 24, 25, 26, 30, 32, 33, 34, 35, 36, 37, 40, 42, 45, 47, 48, 51, 52, 53, 54, 55, 56, 57, 62, 64, 65, 66, 67, 68, 73, 77, 78, 84, 86]

>> find out why not score
     */

    @Test
    public void updatePlayer3() {
        bb.curPlayer = rid;
        bb.free.remove(move3key);
        bb.red.put(move3key, move3);
//        log.info("boards.red=" + bb.red);
        bb.updatePlayer(move3key);

        TreeMap<Integer, Move> expectedRedStones;
        expectedRedStones = new TreeMap<Integer, Move>() {{
            put(14, new Move(1, 4));

            put(0, new Move(0, 0));
            put(10, new Move(1, 0));
            put(20, new Move(2, 0));
            put(30, new Move(3, 0));
            put(40, new Move(4, 0));
        }};

//        log.info("boards.red=" + bb.red);
//        log.info("boards.free=" + bb.free);
        assertTrue(expectedRedStones.keySet().equals(bb.red.keySet()));
        assertEquals(1, bb.points[rid]);
    }

}