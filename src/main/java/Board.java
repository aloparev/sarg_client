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

    /**
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

    /**
     * player scores are stored in the array
     * where the array index also corresponds to the player id
     * 0=red 1=green 2=blue
     */
    int[] scores;

    /**
     * keeps track of active players
     * storage analogue to the score array
     */
    boolean[] activePlayers;
    int turn;
    int owner;

    public Board() {
        this.scores = new int[] {0, 0, 0};
        this.activePlayers = new boolean[] {true, true, true};
        this.turn = 2;

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
            add(4);
            add(15);
            add(26);
            add(37);
            add(48);
            add(58);
            add(68);
            add(78);
            add(88);
            add(87);
            add(86);
            add(85);
            add(84);
        }};
    }

//    general update work flow
    void updateBoard(Move newMove) {
//        iterate who's turn it is
        incrementTurn();

//        find key from the received move
        int moveKey = getKeyForMove(newMove);

//        find owner of that stone
        int playerReal = getPlayer(moveKey);

//        compare real and planed player and disable when not equal
        if(playerReal != turn) {
            activePlayers[turn] = false;
            incrementTurn();
        }

//        propagate new move
        updatePlayer(playerReal, moveKey);
    }

    void incrementTurn() {
        turn = (turn + 1) % 3;
    }

    void updatePlayer(int playerId, int moveKey) {
                Move ans = null;

        switch(playerId) {
            case 0:
                Move thisMove = red.get(moveKey);
                red.remove(moveKey);

                free.put(moveKey, thisMove);
                redMove(moveKey);
                break;
            case 1:
                ans = greenMove();
                break;
            case 2:
                ans = blueMove();
                break;
        }
    }

    int getKeyLeft(int start, int pid) {
        int ans = -1;

        switch(pid) {
            case 0:
                for(int i = start; i < 9; i++)
//                    if(redMargin.contains(i))
//                        return i;
                    if(free.containsKey(i)) {
                        if (redMargin.contains(i))
                            scores[pid]++;
                        return i;
                    }
            case 1:
                break;
            case 2:
                break;
        }
        return ans;
    }

    int getKeyRight(int start, int pid) {
        int ans = -1;

        switch(pid) {
            case 0:
                for(int i = start; i < 99; i = i+11)
                    if(free.containsKey(i)) {
                        if (redMargin.contains(i))
                            scores[pid]++;
                        return i;
                    }
            case 1:
                break;
            case 2:
                break;
        }
        return ans;
    }

    void redMove(int mm) {
        int leftMoveKey = getKeyLeft(mm, 0);
        Move leftMove = free.get(leftMoveKey);
        free.remove(leftMoveKey);
        red.put(leftMoveKey, leftMove);

        int rightMoveKey = getKeyRight(mm, 0);
        Move rightMove = free.get(rightMoveKey);
        free.remove(rightMoveKey);
        red.put(rightMoveKey, rightMove);
    }

    static Move greenMove() {

        return new Move(0, 0);
    }

    static Move blueMove() {

        return new Move(0, 0);
    }

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

    int getKeyForMove(Move mm) {
        int ans = 0;
        int x = mm.x;
        int y = mm.y;

        ans = ans + (x * 10) + y;
        return ans;
    }

//    int[][] ar = new int[9][9];

//
//        ar[0][5] = -1;
//        ar[0][6] = -1;
//        ar[0][7] = -1;
//        ar[0][8] = -1;
//        ar[1][6] = -1;
//        ar[1][7] = -1;
//        ar[1][8] = -1;
//        ar[2][7] = -1;
//        ar[2][8] = -1;
//        ar[3][8] = -1;
//
//        ar[5][0] = -1;
//        ar[6][0] = -1;
//        ar[7][0] = -1;
//        ar[8][0] = -1;
//        ar[6][1] = -1;
//        ar[7][1] = -1;
//        ar[8][1] = -1;
//        ar[7][2] = -1;
//        ar[8][2] = -1;
//        ar[8][3] = -1;
//
//        // red
//        ar[0][0] = 0;
//        ar[1][0] = 0;
//        ar[2][0] = 0;
//        ar[3][0] = 0;
//        ar[4][0] = 0;
//    }

}