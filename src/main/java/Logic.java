import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * start threads to work on moves
 * and keep track of the best pick
 */
@Slf4j
public class Logic {

    /**
     * main driver function to get the best move
     * makes use of helper methods to get points calculated and moves to be picked from scope
     * @param baseBoard game board
     * @return best found move
     */
    static Move getBestMoveForOwner(Board baseBoard) {
        int bestPoints = -1;
        int bestMoveKey = -1;
        TreeMap<Integer, Move> moves = null;
        int size = -1;
        Thread[] threads;
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
            return new Move(-1, -1);
        else {
//            log.info("moves to inspect: " + moves);
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
                        scores[ii] = getMovePointsForDepthX(new Board(baseBoard), key, 1, -1);
//                        log.info("START thread[" + ii + "] to inspect key=" + key + " for owner=" + baseBoard.owner);
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < size; i++) {
                try {
                    threads[i].join(); //wait till work finished
//                    log.info("END thread[" + i + "] updated score[" + i + "] to " + scores[i]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (scores[i] > bestPoints) {
                    bestPoints = scores[i];
                    bestMoveKey = moveKeys.get(i);
                }
            }
        }

        return moves.get(bestMoveKey);
    }

    /**
        for all my stones, imitate moves
        calc enemies best moves with board update
        based on new board, pick best score for me
     */
    static int getMovePointsForDepthX(Board bb, int moveKey, int depth, int bestPoints) {
        if (depth <= 0) //exit condition
            return bestPoints;

        int firstEnemyMove = -1;
        int secondEnemyMove = -1;

        bb.updateBoard(moveKey);

        if(!bb.kicked[bb.expPlayer]) {
            firstEnemyMove = getRankedMoveFromScope(bb, bb.expPlayer, true).moveKey;
            bb.updateBoard(firstEnemyMove);
        }

        if(!bb.kicked[bb.expPlayer]) {
            secondEnemyMove = getRankedMoveFromScope(bb, bb.expPlayer, true).moveKey;
            bb.updateBoard(secondEnemyMove);
        }

        RankedMove rankedMove = getRankedMoveFromScope(bb, bb.owner, false);
//        log.info("in=" + moveKey + " depth=" + depth + " out=" + rankedMove + " bb=" + bb);
        return getMovePointsForDepthX(bb, rankedMove.moveKey, depth-1, rankedMove.points);
    }

    /**
     * given players stones, finds the best or worse move
     * @param root game board
     * @param playerId used to chose stones to run scrutiny on,
     *      the actual points are calculated for "currPlayer"!!!
     * @param minimize indicates whether we are interested in minimizing
     * @return found best/worst move
     */
    static RankedMove getRankedMoveFromScope(Board root, int playerId, boolean minimize) {
        int bestMoveKey = -1;
        int bestPoints = -1;
        int points = -1;
        List<Integer> moves = null;

        switch(playerId) {
            case 0:
                moves = new ArrayList<>(root.red.keySet());
//                moves = root.red;
//                log.info("moves init=" + moves);
                break;
            case 1:
//                moves = root.green;
                moves = new ArrayList<>(root.green.keySet());
                break;
            case 2:
//                moves = root.blue;
                moves = new ArrayList<>(root.blue.keySet());
                break;
        }

        if(moves.isEmpty()) //test
            return new RankedMove();
        else {
//            log.info("start args: board=" + root + " pid=" + playerId + " moves.size=" + moves.size());
            for (int moveKey : moves) {
                    Board branch = new Board(root);
                    branch.updateBoard(moveKey);
                    points = branch.getPointsForPlayerXv2(root.curPlayer);

                if (minimize && points < bestPoints) {
                    bestPoints = points;
                    bestMoveKey = moveKey;
                } else if (points > bestPoints) {
                    bestPoints = points;
                    bestMoveKey = moveKey;
                }
            }
        }

//        log.info("best move from scope is [" + bestMoveKey + "] with points= " + bestPoints);
        return new RankedMove(bestMoveKey, bestPoints);
    }
}
