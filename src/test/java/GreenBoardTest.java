import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.TreeMap;

import static org.junit.Assert.*;

@Slf4j
public class GreenBoardTest {
    static Board bb;

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

}