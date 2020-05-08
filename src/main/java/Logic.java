import lenz.htw.sarg.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * start threads to work on moves
 * and keep track of the best pick
 */
@Slf4j
public class Logic {
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
//                    } catch (CloneNotSupportedException cnse) {
//                        cnse.printStackTrace();
//                        System.out.println("Running thread[" + ii + "] to inspect key=" + key + " for owner=" + baseBoard.owner + " failed");
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
    for all my stones do
        imitate move
        calc enemies best moves with board update
        based on new board, pick best move
     */
    static int getMovePointsForDepthX(Board bb, int moveKey, int depth, int bestPoints) {
        int firstEnemyMove = -1, secondEnemyMove = -1;
        if (depth <= 0)
            return bestPoints;

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
//        log.info("rm=" + rankedMove);
        return getMovePointsForDepthX(bb, rankedMove.moveKey, depth-1, rankedMove.points);
    }

    /**
     * used to generate min/max scores
     * @param root board
     * @param playerId
     * @return
     */
    static RankedMove getRankedMoveFromScope(Board root, int playerId, boolean minimize) {
        int bestMoveKey = -1;
        int bestPoints = -1;
        int points = -1;
        List<Integer> moves = null;
        Board branch;

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
                    branch = new Board(root);
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
