import lenz.htw.sarg.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * start threads to work on moves
 * and keep track of the best pick
 */
public class Logic {
    static Logger log = LoggerFactory.getLogger(Logic.class);

    static Move getBestMove(Board baseBoard) {
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
            return new Move(-1, -1);
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
                        scores[ii] = getMovePointsForDepthX(baseBoard.clone(), key, 1, -1);
                        log.info("START thread[" + ii + "] to inspect key=" + key + " for owner=" + baseBoard.owner);
                    } catch (CloneNotSupportedException cnse) {
                        cnse.printStackTrace();
                        System.out.println("Running thread[" + ii + "] to inspect key=" + key + " for owner=" + baseBoard.owner + " failed");
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < size; i++) {
                try {
                    threads[i].join(); //wait till work finished
                    log.info("END thread[" + i + "] updated score[" + i + "] to " + scores[i]);
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

        log.info("bb before: " + bb);
        bb.updateBoard(moveKey);
        log.info("bb after my move: " + bb);

//        while (bb.expPlayer != bb.owner) {
            if(!bb.kicked[bb.expPlayer]) {
                firstEnemyMove = getBestRankedMoveFromScope(bb, bb.expPlayer).moveKey;
                bb.updateBoard(firstEnemyMove);
            }
            log.info("enemy move 1=" + firstEnemyMove);
            log.info("bb updated enemy1: " + bb);

            if(!bb.kicked[bb.expPlayer]) {
                secondEnemyMove = getBestRankedMoveFromScope(bb, bb.expPlayer).moveKey;
                bb.updateBoard(secondEnemyMove);
            }
        log.info("enemy move 2=" + firstEnemyMove);
        log.info("bb updated enemy2: " + bb);

//        }

        RankedMove rankedMove = getBestRankedMoveFromScope(bb, bb.owner);
        log.info("rm=" + rankedMove);
        return getMovePointsForDepthX(bb, rankedMove.moveKey, depth-1, rankedMove.points);
    }

    static RankedMove getBestRankedMoveFromScope(Board root, int playerId) {
        int bestMoveKey = -1;
        int bestPoints = -1;
        int points;
//        TreeMap<Integer, Move> moves = null;
        List<Integer> moves = Collections.emptyList();
//        int size = root.getStonesAmountOfPlayerX(playerId);
//        List<Move> moves = new ArrayList<>(size);
//        int[] scores = new int[size];
//        TreeSet<Integer> bestPoints = new TreeSet<>();

        switch(playerId) {
            case 0:
                moves = new ArrayList<>(root.red.keySet());
//                moves = root.red;
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

        if(moves.isEmpty())
            return new RankedMove();
        else {
//            log.info("start args: board=" + root + " pid=" + playerId + " moves.size=" + moves.size());

//            for(Map.Entry<Integer, Move> mm : moves.entrySet())
            for (int moveKey : moves)
                try {
                    Board branch = root.clone();
                    branch.updateBoard(moveKey);
//                    branch.updateBoard(mm.getValue());
                    points = branch.getPointsForPlayerXv1(playerId);
                    if (points > bestPoints) {
                        bestPoints = points;
                        bestMoveKey = moveKey;
                    }
                } catch (CloneNotSupportedException ee) {
                    ee.printStackTrace();
                }
        }

        return new RankedMove(bestMoveKey, bestPoints);
    }
}