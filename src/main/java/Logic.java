import lenz.htw.sarg.Move;

import java.util.*;

/**
 * start threads to work on moves
 * and keep track of the best pick
 */
public class Logic {
    static int getBestMove(Board baseBoard, int depth) {
        int bestPoints = -1;
        int bestMoveKey = -1;
        int points;
        TreeMap<Integer, Move> moves = null;
        int size = -1;
//        int ii = 0;
        Thread[] threads;
//        TreeSet<Integer> scores;
        List<Integer> moveKeys;
        int[] scores;

        switch(baseBoard.owner) {
            case 0:
                moves = baseBoard.red;
                break;
            case 1:
                moves = baseBoard.green;
                break;
            case 2:
                moves = baseBoard.blue;
                break;
        }

        if(moves.isEmpty())
            return -1;
        else {
            size = moves.size();
            threads = new Thread[size];
            scores = new int[size];
            moveKeys = new ArrayList<>(moves.keySet());

//            for(Map.Entry<Integer, Move> mm : moves.entrySet()) {
            for(int i = 0; i < moveKeys.size(); i++) {
                int ii = i; //final for thread lambda
                int key = moveKeys.get(i);

                threads[i] = new Thread(() -> {
                    try {
                        scores[ii] = (getMoveScoresForDepthX(baseBoard.clone(), key, 1, -1));
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        System.out.println("Running thread[" + ii + "] to inspect key=" + key + " for owner=" + baseBoard.owner + " failed");
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < size; i++) {
                try {
                    threads[i].join(); //wait till work finished
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (scores[i] > bestPoints) {
                    bestPoints = scores[i];
                    bestMoveKey = moveKeys.get(i);
                }
            }
        }

        return bestMoveKey;
    }

    /**
    for all my stones do
        imitate move
        calc enemies best moves with board update
        based on new board, pick best move
     */
    static int getMoveScoresForDepthX(Board bb, int moveKey, int depth, int bestPoints) {
        if (depth <= 0)
            return bestPoints;

        bb.updateBoard(moveKey);
        while (bb.expPlayer != bb.owner) {
            bb.updateBoard(getBestRankedMoveFromScope(bb, bb.expPlayer).moveKey);
        }

        RankedMove rankedMove = getBestRankedMoveFromScope(bb, bb.owner);
        return getMoveScoresForDepthX(bb, rankedMove.moveKey, depth-1, rankedMove.points);
    }

    /**
     * find out how many active enemies are there
     * simulate their best moves and return updated board
     * @param branch board
     * @return
     */
//    private static Board enemiesTurn(Board branch) {
//        int nextPlayer = branch.expPlayer;
//        int nextPlayerBestMove = getBestMoveFromScope(branch, nextPlayer);
//        branch.updateBoard(nextPlayerBestMove);
//
//        if(branch.expPlayer != branch.owner) {
//
//        }
//    }

    static RankedMove getBestRankedMoveFromScope(Board root, int playerId) {
        int bestMoveKey = -1;
        int bestPoints = -1;
        int points;
        TreeMap<Integer, Move> moves = null;
//        List<Move> moves = null;
//        int size = root.getStonesAmountOfPlayerX(playerId);
//        List<Move> moves = new ArrayList<>(size);
//        int[] scores = new int[size];
//        TreeSet<Integer> bestPoints = new TreeSet<>();

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
            return new RankedMove();
        else {
            for(Map.Entry<Integer, Move> mm : moves.entrySet())
                try {
                    Board branch = root.clone();
                    branch.updateBoard(mm.getValue());
                    points = branch.getBoardValueForPlayerX(playerId);
                    if (points > bestPoints) {
                        bestPoints = points;
                        bestMoveKey = mm.getKey();
                    }
                } catch (CloneNotSupportedException ee) {
                    ee.printStackTrace();
                }
        }

        return new RankedMove(bestMoveKey, bestPoints);
    }
}
