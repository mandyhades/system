package fortest.codefest.service.socket.data;

public class Spoil extends Position {
    public static final int SPEED = 0;
    public static final int DELAY = 1;
    public static final int POWER = 2;
    public static final int SPACE_STONE = 3;
    public static final int MIND_STONE = 4;
    public static final int REALITY_STONE = 5;
    public static final int POWER_STONE = 6;
    public static final int TIME_STONE = 7;
    public static final int SOUL_STONE = 8;

    public int spoil_type;

    @Override
    public String toString() {
        return "["+getX()+","+getY()+"]";
    }
}
