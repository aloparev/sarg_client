import lenz.htw.sarg.Move;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * represents "game configuration"
 * since it includes all core elements:
 * who's turn it is, stones positions and score
 * with score being the player loss function
 */
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
    int[] scores;

    /*
     * keeps track of active players
     * storage analogue to the score array
     */
    int currPlayer;
    int owner;

    public Board(int player) {
        this.owner = player;
        this.scores = new int[] {0, 0, 0};

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
        this.currPlayer = getPlayer(moveKey);

//        propagate new move
        updatePlayer(moveKey);
    }

    /**
     * shift received move from player map to free map
     * and simulate user stone change
     * @param moveKey of newly received
     */
    void updatePlayer(int moveKey) {
        switch(this.currPlayer) {
            case 0:
                removeFromRed(moveKey);
                redMove(moveKey);
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    private void removeFromRed(int moveKey) {
        free.put(moveKey, red.get(moveKey));
        red.remove(moveKey);
    }

    private void addToRed(int moveKey) {
        red.put(moveKey, free.get(moveKey));
        free.remove(moveKey);
    }

    /**
     * following move is split into left and right
     * @param moveKey
     */
    void redMove(int moveKey) {
        int leftMoveKey = getKeyLeft(moveKey);
        addToRed(leftMoveKey);

        int rightMoveKey = getKeyRight(moveKey);
        addToRed(rightMoveKey);
    }

    int getKeyLeft(int start) {
        int ans = -1;

        switch(this.currPlayer) {
            case 0:
                for(int i = start; i < 9; i++) {

//                    there is a free spot on the board
                    if(free.containsKey(i))
                        return i;

//                    stone reached the board end
                    if (redMargin.contains(i))
                        updateScores();
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

        switch(this.currPlayer) {
            case 0:
                for(int i = start; i < 99; i = i+11) {
                    if(free.containsKey(i))
                        return i;

                    if (redMargin.contains(i))
                        updateScores();
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

    private void updateScores() {
        this.scores[this.currPlayer]++;
    }
}
