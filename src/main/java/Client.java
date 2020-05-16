import lenz.htw.sarg.Move;
import lenz.htw.sarg.net.NetworkClient;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main class to call all the other in a while loop
 */
public class Client {
    static int SCORE_FACTOR = 10;
    static int DEPTH = 8;

    /*
    first connected client gets number 2 and blue color
    second: 1 green
    third: 0 red
     */
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        String teamName = "f2";
        String picPath = "src/main/resources/blank.jpg";
        boolean gameIsRunning = true;

        Logger log = LoggerFactory.getLogger(Client.class);
        log.info("client up and running\n\thost=" + host + " team=" + teamName + " picture=" + picPath);

        // initialisieren... z.B. Spielbrett
        NetworkClient nc = new NetworkClient(host, teamName, ImageIO.read(new File(picPath)));
        Board board = new Board(nc.getMyPlayerNumber());

//        nc.getTimeLimitInSeconds();
//        System.out.println(nc.getExpectedNetworkLatencyInMilliseconds());
//        System.out.println("3 started player = " + nc.getMyPlayerNumber());

        while(gameIsRunning) {
            Move newMove = nc.receiveMove();

            if (newMove == null) {
                log.info("move=null >> ich bin dran: " + board.owner);

//                Scanner sc = new Scanner(System.in);
//                int x = Integer.parseInt(sc.nextLine());
//                int y = Integer.parseInt(sc.nextLine());
//                sc.close();
//                log.info("x=" + x + " y=" + y);
//                nc.sendMove(new Move(x,y));

//                System.in.read();
                newMove = Logic.getBestMoveForOwner(board);
                log.info("Logic.getBestMoveForOwner: " + newMove);
                nc.sendMove(newMove);
            }
            else {
                log.info("board.updateBoard: " + newMove);
                if(newMove.y == -1) {
                    gameIsRunning = false;
                    log.info("game over!");
                }

                board.updateBoard(newMove);
                log.info("board after integration: " + board);
            }

        }
    }


}

