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
    /*
    first connected client gets number 2 and blue color
    second: 1 green
    third: 0 red
     */
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        String teamName = "bob";
        String picPath = "src/main/resources/blank.jpg";

        Logger log = LoggerFactory.getLogger(Client.class);
        log.info("client up :)\thost=" + host + " team=" + teamName + " picture=" + picPath);

        // initialisieren... z.B. Spielbrett
        Board board = new Board();
        NetworkClient nc = new NetworkClient(host, teamName, ImageIO.read(new File(picPath)));

//        nc.getTimeLimitInSeconds();
//        System.out.println(nc.getExpectedNetworkLatencyInMilliseconds());
//        System.out.println("3 started player = " + nc.getMyPlayerNumber());

        board.owner = nc.getMyPlayerNumber();

        while (true) {
            Move newMove = nc.receiveMove();
            log.info(newMove.toString());

            if (newMove == null) {
                log.info("ich bin dran");

//                newMove = findeCleverenZug();
                newMove = new Move(0,1);
                nc.sendMove(newMove);
            }
            else {
                log.info("// integriereZugInSpielbrett(move);");
                board.updateBoard(newMove);
            }

        }
    }


}

