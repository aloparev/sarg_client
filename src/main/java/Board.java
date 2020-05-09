import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * represents the "game configuration"
 * since it includes all the crucial elements:
 * who's turn it is, stones and scores
 */
@Slf4j
public class Board {
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

//    empty initializer
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

//        off the board fields as the absolute destination
        this.redMargin = new TreeSet<Integer>() {{
            add(5);
            add(16);
            add(27);
            add(38);

            add(49);
            add(59);
            add(69);
            add(79);
            add(89); //max L

            add(99); //max R
            add(98);
            add(97);
            add(96);
            add(95);
        }};

        this.greenMargin = new TreeSet<Integer>() {{
            add(-1); //min R
            add(9);
            add(19);
            add(29);
            add(39);

            add(50);
            add(61);
            add(72);
            add(83);

            add(94);
            add(95);
            add(96);
            add(97);
            add(98); //max L
        }};

        this.blueMargin = new TreeSet<Integer>() {{
            add(38);
            add(27);
            add(16);
            add(5);
            add(-6);

            add(-7);
            add(-8);
            add(-9);
            add(-10); //min R

            add(-11); //min L
            add(-1);
            add(9);
            add(19);
            add(29);
        }};
    }

//    copy constructor
    public Board(Board that) {
//        non primitives reassignment
        this.free = new TreeMap<>(that.free);
        this.red = new TreeMap<>(that.red);
        this.green = new TreeMap<>(that.green);
        this.blue = new TreeMap<>(that.blue);

        this.redMargin = that.redMargin;
        this.greenMargin = that.greenMargin;
        this.blueMargin = that.blueMargin;

//        primitives clone
        this.points = that.points.clone();
        this.kicked = that.kicked.clone();
        this.expPlayer = that.expPlayer;
        this.curPlayer = that.curPlayer;
        this.owner = that.owner;
    }

//    general board update work flow
    void updateBoard(Move newMove) {

//        find key from the received move
        int moveKey = getMoveKey(newMove);

//        find move owner
        curPlayer = getPlayerFromMove(moveKey);

//        sync who's turn it is
        if(expPlayer != curPlayer) {
            kicked[expPlayer] = true;
            expPlayer = curPlayer;
        }
        incrementExpPlayer();

//        propagate new move
        updatePlayer(moveKey);
    }

//    same as above, but with map key as input
    void updateBoard(int moveKey) {
        curPlayer = getPlayerFromMove(moveKey);

        if(expPlayer != curPlayer) {
            kicked[expPlayer] = true;
            expPlayer = curPlayer;
        }

        incrementExpPlayer();
        updatePlayer(moveKey);
    }

    void incrementExpPlayer() {
        expPlayer = (expPlayer + 1) % 3;

//        skip kicked players
        while(kicked[expPlayer])
            expPlayer = (expPlayer + 1) % 3;
    }

    /**
     * shift received move from "player" to "free"
     * and run user stone update
     * @param moveKey of new stone
     */
    void updatePlayer(int moveKey) {
        int leftMoveKey = getKeyLeft(moveKey);
        int rightMoveKey = getKeyRight(moveKey);

//        if(leftMoveKey != -1 && rightMoveKey != -1)
        switch (curPlayer) {
            case 0:
                if (leftMoveKey != -1) {
                    red.put(leftMoveKey, free.get(leftMoveKey));
                    free.remove(leftMoveKey);
                }

                if (rightMoveKey != -1) {
                    red.put(rightMoveKey, free.get(rightMoveKey));
                    free.remove(rightMoveKey);
                }

                removeFromRed(moveKey);
                break;
            case 1:
                if (leftMoveKey != -1) {
                    green.put(leftMoveKey, free.get(leftMoveKey));
                    free.remove(leftMoveKey);
                }

                if (rightMoveKey != -1) {
                    green.put(rightMoveKey, free.get(rightMoveKey));
                    free.remove(rightMoveKey);
                }

                removeFromGreen(moveKey);
                break;
            case 2:
                if (leftMoveKey != -1) {
                    blue.put(leftMoveKey, free.get(leftMoveKey));
                    free.remove(leftMoveKey);
                }

                if (rightMoveKey != -1) {
                    blue.put(rightMoveKey, free.get(rightMoveKey));
                    free.remove(rightMoveKey);
                }

                removeFromBlue(moveKey);
                break;
        }
    }

    void removeFromRed(int moveKey) {
        free.put(moveKey, red.get(moveKey));
//        log.info(String.valueOf(free));
        red.remove(moveKey);
//        log.info(String.valueOf(red));
    }

    void removeFromGreen(int moveKey) {
        free.put(moveKey, green.get(moveKey));
        green.remove(moveKey);
    }

    void removeFromBlue(int moveKey) {
        free.put(moveKey, blue.get(moveKey));
        blue.remove(moveKey);
    }

    void addToRed(int moveKey) {
        red.put(moveKey, free.get(moveKey));
        free.remove(moveKey);
    }

    boolean stoneRemover(int i) {
        boolean ans = false;
        if (red.containsKey(i)) {
            removeFromRed(i);
            ans = true;
//            log.info("red: removing red stone");
        }
        if (green.containsKey(i)) {
            removeFromGreen(i);
            ans = true;
//            log.info("red: removing green stone");
        }
        if (blue.containsKey(i)) {
            removeFromBlue(i);
            ans = true;
//            log.info("red: removing blue stone");
        }

        return ans;
    }

    int getKeyLeft(int start) {
        int ans = -1;

        switch(curPlayer) {
            case 0:
                for(int i = start+1; i < 90; i++) {
//                    log.info("i=" + i);

                    if(stoneRemover(i))
                        continue;

//                    stone reached the board end
                    if (redMargin.contains(i)) {
                        updateCurrPlayerScores();
                        break;
                    }

//                    there is a free spot on the board
                    if(free.containsKey(i)) {
//                        log.info("free.contains=" + i);
                        return i;
                    }
                }
                break;
            case 1:
                for(int i = start+10; i < 108; i=i+10) {

                    if(stoneRemover(i))
                        continue;
                    if (greenMargin.contains(i)) {
//                        log.info("redMargin.contains=" + i);
                        updateCurrPlayerScores();
                        break;
                    }
                    if (free.containsKey(i))
                        return i;
                }
                break;
            case 2:
                for(int i = start-11; i > -22; i=i-11) {

                    if(stoneRemover(i))
                        continue;
                    if (greenMargin.contains(i)) {
//                        log.info("redMargin.contains=" + i);
                        updateCurrPlayerScores();
                        break;
                    }
                    if (free.containsKey(i))
                        return i;
                }
                break;
        }
        return ans;
    }

    int getKeyRight(int start) {
        int ans = -1;

        switch(curPlayer) {
            case 0:
                for(int i = start+11; i < 110; i = i+11) {

                    if(stoneRemover(i))
                        continue;

                    if (redMargin.contains(i)) {
                        updateCurrPlayerScores();
                        break;
                    }

                    if(free.containsKey(i)) {
                        return i;
                    }
                }
                break;
            case 1:
                for(int i = start-1; i > -2; i--) {

                    if(stoneRemover(i))
                        continue;
                    if (greenMargin.contains(i)) {
//                        log.info("redMargin.contains=" + i);
                        updateCurrPlayerScores();
                        break;
                    }
                    if (free.containsKey(i))
                        return i;
                }
                break;
            case 2:
                for(int i = start-10; i > -20; i=i-10) {

                    if(stoneRemover(i))
                        continue;
                    if (blueMargin.contains(i)) {
//                        log.info("redMargin.contains=" + i);
                        updateCurrPlayerScores();
                        break;
                    }
                    if (free.containsKey(i))
                        return i;
                }
                break;
        }
        return ans;
    }

    /**
     * find which map includes the move
     * and thereby player who did the move
     * @param moveKey
     * @return
     */
    int getPlayerFromMove(int moveKey) {
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

    private void updateCurrPlayerScores() {
        points[curPlayer]++;
        log.info("points[" + curPlayer + "]++");
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

//    TODO distance from margin and eaten enemy stones
    int getPointsForPlayerXv2(int playerId) {
        int ans = -1;

        switch(playerId) {
            case 0:
                return 100 - (green.size() + blue.size() * 100 / 61) + points[0] * 10;
            case 1:
                return 100 - (red.size() + blue.size() * 100 / 61) + points[1] * 10;
            case 2:
                return 100 - (red.size() + green.size() * 100 / 61) + points[2] * 10;
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
                + "\nred stones=" + red.keySet()
                + "\ngreen stones=" + green.keySet()
                + "\nblue stones=" + blue.keySet()
                + "\n" + "free=" + free.keySet()
                + "\n";
    }

//    public Board clone() throws CloneNotSupportedException {
//        return (Board) super.clone();
//    }
}
