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
     * @param root game board
     * @return best found move
     */
    static Move getBestMoveForOwner(Board root) {
        float bestPoints = -1;
        int bestMoveKey = -1;
        TreeMap<Integer, Move> moves = null;
        int size = -1;
        Thread[] threads;
        List<Integer> moveKeys;
        float[] scores;

        switch(root.owner) {
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
            return new Move(-1, -1);
        else {
//            log.info("moves to inspect: " + moves);
            size = moves.size();
            threads = new Thread[size];
            scores = new float[size];
            moveKeys = new ArrayList<>(moves.keySet());

//            for(Map.Entry<Integer, Move> mm : moves.entrySet()) {
            for(int i = 0; i < moveKeys.size(); i++) {
                int ii = i; //final for thread lambda
                int key = moveKeys.get(i);

                threads[i] = new Thread(() -> {
                    try {
                        scores[ii] = runMinMax(new Board(root), key, Client.DEPTH, -1);
//                        scores[ii] = runAlphaBeta(new Board(root), key, Client.DEPTH, -1);
//                        log.info("START thread[" + ii + "] to inspect key=" + key + " for owner=" + root.owner);
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
     * starts alpha beta cut by using the generic call head from the driver
     * @param root game board
     * @param moveKey first branching move
     * @return max points that can be get following this branch
     */
    static float runAlphaBeta(Board root, int moveKey, int depth, float bestPoints) {
        Board branch = new Board(root);
        branch.updateBoard(moveKey);

//        start with min cos max was already run on the from driver provided stone
        bestPoints = alphaBetaMin(new Board(branch), depth, Float.MIN_VALUE, Float.MAX_VALUE);
        return bestPoints;
    }

    /**
     * enemies try to minimize the outcome for the target player
     * @param alpha = global max
     * @param beta = global min
     */
    static float alphaBetaMin(Board root, int depth, float alpha, float beta) {
        if(depth <= 0) return root.getPoints(root.owner);

        int enemy1 = (root.expPlayer + 1) % 3;
        int enemy2 = (root.expPlayer + 2) % 3;
        int[] enemies = new int[] {(root.expPlayer + 1) % 3, (root.expPlayer + 2) % 3};
        float points = -1;
        float worstPoints = beta;
        List<Integer> moves = null;

        for(int enemy : enemies) {
            if (!root.kicked[enemy]) {
                moves = getMovesOfPlayerX(root, enemy);

                for (int moveKey : moves) {
                    Board branch = new Board(root);
                    branch.updateBoard(moveKey);
                    points = alphaBetaMax(new Board(branch), depth-1, alpha, worstPoints);
                    if (points < worstPoints) {
                        worstPoints = points;
                        if (worstPoints <= alpha) break;
                    }
                }
            }
        }

        return worstPoints;
    }

    /**
     * target player, i.e. board owner try to get the best points possible
     */
    static float alphaBetaMax(Board root, int depth, float alpha, float beta) {
        if(depth <= 0) return root.getPoints(root.owner);

        float points = -1;
        float bestPoints = beta;
        List<Integer> moves = getMovesOfPlayerX(root, root.owner);

        for (int moveKey : moves) {
            Board branch = new Board(root);
            branch.updateBoard(moveKey);
            points = alphaBetaMin(new Board(branch), depth-1, bestPoints, beta);

            if (points > bestPoints) {
                bestPoints = points;
                if (bestPoints >= beta) break;
            }
        }

        return bestPoints;
    }

    /**
     * run min-max alg on the branch without any cuts
     *
        the steps are:
            for all my stones, imitate moves
            calc enemies best (actually my worst) moves
            based on new board, pick the best points for me
        >> the discovery here is that the enemy's best move is not necessarily my worst
     *
     * @param depth to be inspected
     */
    static float runMinMax(Board board, int moveKey, int depth, float bestPoints) {
        if (depth <= 0) //exit condition
            return bestPoints;

//        int firstEnemyMove = -1;
//        int secondEnemyMove = -1;

        board.updateBoard(moveKey);

        try {
            if (!board.kicked[board.expPlayer]) {
//            firstEnemyMove = getRankedMoveFromScope(board, board.expPlayer, true).moveKey;
                board.updateBoard(getRankedMoveFromScope(board, board.expPlayer, true).moveKey);
            }
            if (!board.kicked[board.expPlayer]) {
//            secondEnemyMove = getRankedMoveFromScope(board, board.expPlayer, true).moveKey;
                board.updateBoard(getRankedMoveFromScope(board, board.expPlayer, true).moveKey);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("!board.kicked[board.expPlayer] OUT OF BOUND, expPlayer=" + board.expPlayer);
        }

        RankedMove rankedMove = getRankedMoveFromScope(board, board.owner, false);
//        log.info("in=" + moveKey + " depth=" + depth + " out=" + rankedMove + " board=" + board);
        return runMinMax(new Board(board), rankedMove.moveKey, depth-1, rankedMove.points);
    }

    /**
     * given player stones, finds the best or worse move
     * @param root game board
     * @param playerId used to chose stones to run scrutiny on,
     *      the actual points are calculated for the board owner (who invokes the f.)
     * @param minimize indicates whether we are interested in minimizing
     * @return found best/worst move
     */
    static RankedMove getRankedMoveFromScope(Board root, int playerId, boolean minimize) {
        int bestMoveKey = -1;
        float points = -1;
        List<Integer> moves = getMovesOfPlayerX(root, playerId);
//        List<Integer> moves = null;

        float bestPoints = Float.MIN_VALUE;
        if(minimize) bestPoints = Float.MAX_VALUE;

        if(moves.isEmpty()) //test
            return new RankedMove();
        else {
//            log.info("start args: board=" + root + " pid=" + playerId + " moves.size=" + moves.size());
            for (int moveKey : moves) {
                    Board branch = new Board(root);
                    branch.updateBoard(moveKey);
                    points = branch.getPoints(root.owner);
//                    points = branch.getPointsTwo(root.owner);

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

    static List<Integer> getMovesOfPlayerX(Board root, int playerId) {
        switch(playerId) {
            case 0:
                return new ArrayList<>(root.red.keySet());
//                moves = root.red;
//                log.info("moves init=" + moves);
            case 1:
//                moves = root.green;
                return new ArrayList<>(root.green.keySet());
            case 2:
//                moves = root.blue;
                return new ArrayList<>(root.blue.keySet());
            default:
                return new ArrayList<>();
        }
    }
}
