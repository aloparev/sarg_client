import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * main class
 * run: java -jar jar/SargClient.jar 127.0.0.1 abeta 2
 */
@Slf4j
public class Client {
    static int SCORE_FACTOR = 10;
    static int DEPTH = 8;

    static int evaFunc = 2;
    static String host = "127.0.0.1";
    static String teamName = "sargotron";

    /*
    first connected client gets number 2 and blue color
    second: 1 green
    third: 0 red
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            log.error("Three args expected: host, teamName and evaFunc");
        } else {
            host = args[0];
            teamName = args[1];
            evaFunc = Integer.parseInt(args[2]);
            String picPath = "src/main/resources/blank.jpg";
            boolean gameIsRunning = true;
            log.info("client up and running\n\thost=" + host + " team=" + teamName + " evaFunc=" + evaFunc);
            NetworkClient nc = new NetworkClient(host, teamName, ImageIO.read(new File(picPath)));
            Board board = new Board(nc.getMyPlayerNumber());
//        nc.getTimeLimitInSeconds();
//        System.out.println(nc.getExpectedNetworkLatencyInMilliseconds());
//        System.out.println("3 started player = " + nc.getMyPlayerNumber());

            while (gameIsRunning) {
                Move newMove = nc.receiveMove();
                if (newMove == null) {
                    log.info("move=null >> ich bin dran: " + board.owner);
//                Scanner sc = new Scanner(System.in);
//                int x = Integer.parseInt(sc.nextLine());
//                int y = Integer.parseInt(sc.nextLine());
//                newMove = new Move(x, y);
////                sc.close();
////                log.info("x=" + x + " y=" + y);
//                nc.sendMove(new Move(x,y));
//                System.in.read();
                    newMove = Logic.getBestMoveForOwner(board);
                    log.info("Logic.getBestMoveForOwner: " + newMove);
                    nc.sendMove(newMove);
                } else {
                    log.info("board.updateBoard: " + newMove);
                    if (newMove.y == -1) {
                        gameIsRunning = false;
                        log.error("GAME OVER");
                    }
                    board.updateBoard(newMove);
                    log.info("board after integration: " + board);
                }
            }
        }
    }
}

