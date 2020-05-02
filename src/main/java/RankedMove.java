import java.util.Arrays;

public class RankedMove {
    public RankedMove(int moveKey, int points) {
        this.moveKey = moveKey;
        this.points = points;
    }
    public RankedMove() {
        this.moveKey = -1;
        this.points = -1;
    }

    int moveKey;
    int points;

    @Override
    public String toString() {
        return "ranked move with key=" + moveKey + " and points=" + points;
    }
}
