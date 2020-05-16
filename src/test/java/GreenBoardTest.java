import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import sun.rmi.runtime.Log;

import java.util.Arrays;
import java.util.TreeMap;

import static org.junit.Assert.*;

@Slf4j
public class GreenBoardTest {
    static Board bb;
    static int gid = 1;

    static Move move0;
    static Move move3;

    static int move0key = 0;
    static int move3key = 3;

    @Before
    public void setup() {
        bb = new Board(1);
        move0 = new Move(0, 0);
        move3 = new Move(0, 3);
    }

    @Test
    public void updateBoardFromMoveZero() {
        bb.red.remove(move0key);
        bb.red.remove(10);
        bb.free.put(10, new Move(1, 0));
        bb.green.put(move0key, move0);
        bb.curPlayer = bb.owner;
        log.info("bb init: " + bb);

        bb.updateBoard(move0key);
        log.info("bb updated: " + bb);
        assertArrayEquals(new int[] {0, 1, 0}, bb.points);
    }

    @Test
    public void moveFromZeroToOffAndTen() {
        bb.red.remove(move0key);
        bb.red.remove(10);
        bb.free.put(10, new Move(1, 0));
        bb.green.put(move0key, move0);
        bb.curPlayer = bb.owner;

        assertEquals(10, bb.getKeyLeft(move0key));
        assertEquals(-1, bb.getKeyRight(move0key));
    }

    @Test
    public void initFromList() {
        Board bb = new Board(1, new int[] {2,1,1}, 0, 1, new boolean[] {false, false, false},
                Arrays.asList(20, 21, 24, 31, 34, 40, 41),
                Arrays.asList(10, 11),
                Arrays.asList(37,43,44,57,63,73),
                Arrays.asList(0, 1, 2, 3, 4, 12, 13, 14, 15, 22, 23, 25, 26, 30, 32, 33, 35, 36, 42, 45, 46, 47, 48, 51, 52, 53, 54, 55, 56, 58, 62, 64, 65, 66, 67, 68, 74, 75, 76, 77, 78, 84, 85, 86, 87, 88));
//        System.out.println(bb);

        assertEquals(1, bb.owner);
        assertEquals(0, bb.curPlayer);
        assertEquals(1, bb.expPlayer);
        assertEquals(Integer.valueOf(41), bb.red.lastKey());
        assertEquals(Integer.valueOf(73), bb.blue.lastKey());
        assertEquals(Integer.valueOf(64), bb.free.keySet().toArray()[31]);
    }

    @Test
    public void deadlock1() {
    /*
09:07:05.105 [main] INFO  Client - board.updateBoard: 2,3
09:07:05.106 [main] INFO  Client - board after integration: board owner=1 scores=[2, 1, 1] curPlayer=0 expPlayer=1 kicked=[false, false, false]
red stones=[20, 21, 24, 31, 34, 40, 41]
green stones=[10, 11]
blue stones=[37, 43, 44, 57, 63, 73]
free=[0, 1, 2, 3, 4, 12, 13, 14, 15, 22, 23, 25, 26, 30, 32, 33, 35, 36, 42, 45, 46, 47, 48, 51, 52, 53, 54, 55, 56, 58, 62, 64, 65, 66, 67, 68, 74, 75, 76, 77, 78, 84, 85, 86, 87, 88]
     */
        Board board = new Board(1, new int[] {2,1,1}, 0, 1, new boolean[] {false, false, false},
                Arrays.asList(20, 21, 24, 31, 34, 40, 41),
                Arrays.asList(10, 11),
                Arrays.asList(37,43,44,57,63,73),
                Arrays.asList(0, 1, 2, 3, 4, 12, 13, 14, 15, 22, 23, 25, 26, 30, 32, 33, 35, 36, 42, 45, 46, 47, 48, 51, 52, 53, 54, 55, 56, 58, 62, 64, 65, 66, 67, 68, 74, 75, 76, 77, 78, 84, 85, 86, 87, 88));
//        System.out.println(board.free);
        assertTrue(board.free.containsKey(30));

        RankedMove rm = Logic.getRankedMoveFromScope(board, gid, false);
        System.out.println(rm);

        board.updateBoard(rm.moveKey);
//        int left = board.getKeyLeft(rm.moveKey);
        System.out.println(board);
    }
}