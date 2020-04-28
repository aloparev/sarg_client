import lenz.htw.sarg.Move;

import java.util.*;

/**
 * min/max or alpha-beta algorithm will go here
 * it will construct the tree using <Tree> and <Node>
 */
public class Logic {
    static int getBestMoveWithDepth3(Board root, int playerId) {
        int bestMoveKey = -1;
        int maxPoints = -1;
        int points;
        TreeMap<Integer, Move> moves = null;

        switch(playerId) {
            case 0:
                moves = root.red;
                break;
            case 1:
                moves = root.green;
                break;
            case 2:
                moves = root.blue;
                break;
        }

        if(moves.isEmpty())
            return -1;
        else {
            for(Map.Entry<Integer, Move> mm : moves.entrySet())
                try {
                    Board branch = root.clone();
                    branch.updateBoard(mm.getValue(), true);
                    points = branch.getBoardValueForPlayerX(playerId);
                    if (points > maxPoints) {
                        maxPoints = points;
                        bestMoveKey = mm.getKey();
                    }
                } catch (CloneNotSupportedException ee) {
                    ee.printStackTrace();
                }
        }

        return bestMoveKey;
    }

    static int getBestMoveFromCurrentPossible(Board root, int playerId) {
        int bestMoveKey = -1;
        int maxPoints = -1;
        int points;
        TreeMap<Integer, Move> moves = null;
//        List<Move> moves = null;
//        int size = root.getStonesAmountOfPlayerX(playerId);
//        List<Move> moves = new ArrayList<>(size);
//        int[] scores = new int[size];
//        TreeSet<Integer> maxPoints = new TreeSet<>();

        switch(playerId) {
            case 0:
//                moves = new ArrayList<>(root.red.values());
                moves = root.red;
                break;
            case 1:
                moves = root.green;
                break;
            case 2:
                moves = root.blue;
                break;
        }

        if(moves.isEmpty())
            return -1;
        else {
            for(Map.Entry<Integer, Move> mm : moves.entrySet())
                try {
                    Board branch = root.clone();
                    branch.updateBoard(mm.getValue(), true);
                    points = branch.getBoardValueForPlayerX(playerId);
                    if (points > maxPoints) {
                        maxPoints = points;
                        bestMoveKey = mm.getKey();
                    }
                } catch (CloneNotSupportedException ee) {
                    ee.printStackTrace();
                }
        }

        return bestMoveKey;
    }
}
