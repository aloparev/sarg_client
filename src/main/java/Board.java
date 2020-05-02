import lenz.htw.sarg.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * represents the "game configuration"
 * since it includes all the crucial elements:
 * who's turn it is, stones positions and score
 */
public class Board implements Cloneable {
    Logger log = LoggerFactory.getLogger(Board.class);

    /*
     * all possible moves are stored in different maps
     * when the players move their stones the stone objects are moved from one map to another
     * usually it's from <free> to one player or vice versa
     */
    TreeMap<Integer, Move> free;

    TreeMap<Integer, Move> red;
    TreeSet<Integer> redMargin;

    TreeMap<Integer, Move> green;
    TreeSet<Integer> greenMargin;

    TreeMap<Integer, Move> blue;
    TreeSet<Integer> blueMargin;

    /*
     * player scores are stored in the array
     * where the array index also corresponds to the player id
     * 0=red 1=green 2=blue
     */
    int[] points;

    boolean[] kicked;
    int expPlayer;

    /*
     * keeps track of active players
     * storage analogue to the score array
     */
    int curPlayer;
    int owner;

    public Board(int owner) {
        this.owner = owner;
        points = new int[] {0, 0, 0};
        kicked = new boolean[] {false, false, false};

//        red always starts
        expPlayer = 0;

//        all free fields
        this.free = new TreeMap<Integer, Move>() {{
                put(1, new Move(0, 1));
                put(11, new Move(1, 1));
                put(21, new Move(2, 1));
                put(31, new Move(3, 1));
                put(41, new Move(4, 1));
                put(51, new Move(5, 1));

                put(2, new Move(0, 2));
                put(12, new Move(1, 2));
                put(22, new Move(2, 2));
                put(32, new Move(3, 2));
                put(42, new Move(4, 2));
                put(52, new Move(5, 2));
                put(62, new Move(6, 2));

                put(3, new Move(0, 3));
                put(13, new Move(1, 3));
                put(23, new Move(2, 3));
                put(33, new Move(3, 3));
                put(43, new Move(4, 3));
                put(53, new Move(5, 3));
                put(63, new Move(6, 3));
                put(73, new Move(7, 3));

                put(14, new Move(1, 4));
                put(24, new Move(2, 4));
                put(34, new Move(3, 4));
                put(44, new Move(4, 4));
                put(54, new Move(5, 4));
                put(64, new Move(6, 4));
                put(74, new Move(7, 4));

                put(25, new Move(2, 5));
                put(35, new Move(3, 5));
                put(45, new Move(4, 5));
                put(55, new Move(5, 5));
                put(65, new Move(6, 5));
                put(75, new Move(7, 5));

                put(36, new Move(3, 6));
                put(46, new Move(4, 6));
                put(56, new Move(5, 6));
                put(66, new Move(6, 6));
                put(76, new Move(7, 6));

                put(47, new Move(4, 7));
                put(57, new Move(5, 7));
                put(67, new Move(6, 7));
                put(77, new Move(7, 7));

                put(58, new Move(5, 8));
                put(68, new Move(6, 8));
                put(78, new Move(7, 8));
            }};

//        fields occupied by red
        this.red = new TreeMap<Integer, Move>() {{
            put(0, new Move(0, 0));
            put(10, new Move(1, 0));
            put(20, new Move(2, 0));
            put(30, new Move(3, 0));
            put(40, new Move(4, 0));
        }};

        this.green = new TreeMap<Integer, Move>() {{
            put(4, new Move(0, 4));
            put(15, new Move(1, 5));
            put(26, new Move(2, 6));
            put(37, new Move(3, 7));
            put(48, new Move(4, 8));
        }};

        this.blue = new TreeMap<Integer, Move>() {{
            put(84, new Move(8, 4));
            put(85, new Move(8, 5));
            put(86, new Move(8, 6));
            put(87, new Move(8, 7));
            put(88, new Move(8, 8));
        }};

//        last fields before jumping off the board
        this.redMargin = new TreeSet<Integer>() {{
            add(5);
            add(16);
            add(27);
            add(38);

            add(49);
            add(59);
            add(69);
            add(79);
            add(89);

            add(99);
            add(98);
            add(97);
            add(96);
            add(95);
        }};
    }

//    general update work flow
    void updateBoard(Move newMove) {

//        find key from the received move
        int moveKey = getMoveKey(newMove);

//        find move owner
        curPlayer = getPlayer(moveKey);

//        sync who's turn it is
        if(expPlayer != curPlayer) {
            kicked[expPlayer] = true;
            expPlayer = curPlayer;
        }
        incrementExpPlayer();

//        propagate new move
        updatePlayer(moveKey);
    }

    void updateBoard(int moveKey) {
        curPlayer = getPlayer(moveKey);

//        sync who's turn it is
        if(expPlayer != curPlayer) {
            kicked[expPlayer] = true;
            expPlayer = curPlayer;
        }
        incrementExpPlayer();

//        propagate new move
        updatePlayer(moveKey);
    }

    void incrementExpPlayer() {
        expPlayer = (expPlayer + 1) % 3;

        if(kicked[expPlayer])
            expPlayer = (expPlayer + 1) % 3;
    }

    /**
     * shift received move from player map to free map
     * and simulate user stone change
     * @param moveKey of newly received
     */
    void updatePlayer(int moveKey) {
        switch(curPlayer) {
            case 0:
//                log.info(String.valueOf(red));
                redMove(moveKey);
                removeFromRed(moveKey);
//                log.info(String.valueOf(red));
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    void removeFromRed(int moveKey) {
        free.put(moveKey, red.get(moveKey));
//        log.info(String.valueOf(free));
        red.remove(moveKey);
//        log.info(String.valueOf(red));
    }

    void addToRed(int moveKey) {
        red.put(moveKey, free.get(moveKey));
        free.remove(moveKey);
    }

    /**
     * following move is split into left and right
     * @param moveKey
     */
    void redMove(int moveKey) {
//        log.info("redMove.moveKey=" + moveKey);
        int leftMoveKey = getKeyLeft(moveKey);
        if(leftMoveKey != -1) {
//            log.info("redMove.leftMoveKey=" + leftMoveKey);
            red.put(leftMoveKey, free.get(leftMoveKey));
            free.remove(leftMoveKey);
        }
//        log.info(String.valueOf(leftMoveKey));
//        log.info(String.valueOf(red));

        int rightMoveKey = getKeyRight(moveKey);
        if(rightMoveKey != -1) {
            red.put(rightMoveKey, free.get(rightMoveKey));
            free.remove(rightMoveKey);
//        log.info(String.valueOf(red));
        }
    }

    int getKeyLeft(int start) {
        int ans = -1;

        switch(curPlayer) {
            case 0:
                for(int i = start; i < 9; i++) {

//                    stone reached the board end
                    if (redMargin.contains(i)) {
//                        log.info("redMargin.contains=" + i);
                        updatePlayerScores();
                        break;
                    }

//                    there is a free spot on the board
                    if(free.containsKey(i))
                        return i;
                }
                break;
            case 1:
                break;
            case 2:
                break;
        }
        return ans;
    }

    int getKeyRight(int start) {
        int ans = -1;

        switch(curPlayer) {
            case 0:
                for(int i = start; i < 99; i = i+11) {

                    if (redMargin.contains(i)) {
                        updatePlayerScores();
                        break;
                    }

                    if(free.containsKey(i)) {
                        return i;
                    }
                }
                break;
            case 1:
                break;
            case 2:
                break;
        }
        return ans;
    }

    static Move greenMove() {

        return new Move(0, 0);
    }

    static Move blueMove() {

        return new Move(0, 0);
    }

    /**
     * find which map includes the move
     * and thereby player who did the move
     * @param moveKey
     * @return
     */
    int getPlayer(int moveKey) {
        int ans = -1;

        if(red.containsKey(moveKey))
            return 0;

        if(green.containsKey(moveKey))
            return 1;

        if(blue.containsKey(moveKey))
            return 2;

        return ans;
    }

    /**
     * extract move coordinates
     * which are used as a map key
     * @param move
     * @return
     */
    int getMoveKey(Move move) {
        int ans = 0;
        int x = move.x;
        int y = move.y;

        ans = ans + (x * 10) + y;
        return ans;
    }

    private void updatePlayerScores() {
//        log.info("scores++");
        points[curPlayer]++;
    }

    int getPointsForPlayerXv1(int playerId) {
        int ans = -1;

        switch(playerId) {
            case 0:
                return red.size() * 100 / 61 + points[0] * 10;
            case 1:
                return green.size() * 100 / 61 + points[1] * 10;
            case 2:
                return blue.size() * 100 / 61 + points[2] * 10;
        }
        return ans;
    }

    int getStonesAmountOfPlayerX(int playerId) {
        int ans = -1;

        switch(playerId) {
            case 0:
                return red.size();
            case 1:
                return green.size();
            case 2:
                return blue.size();
        }
        return ans;
    }

    @Override
    public String toString() {
        return "board owner=" + owner + " scores=" + Arrays.toString(points) + " curPlayer=" + curPlayer
                + " expPlayer=" + expPlayer + " kicked=" + Arrays.toString(kicked)
                + " red stones=" + red.keySet() + " green stones=" + green.keySet() + " blue stones=" + blue.keySet()
                + "\n" + "free=" + free.keySet();
    }

    public Board clone() throws CloneNotSupportedException {
        return (Board) super.clone();
    }
}
