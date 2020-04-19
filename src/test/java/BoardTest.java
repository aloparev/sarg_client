import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void boardInit() {
        Board board = new Board();

        assertTrue(board.scores.length == 3);
        assertArrayEquals(new int[] {0,0,0,}, board.scores);

        assertTrue(board.turn == 0);
    }
}