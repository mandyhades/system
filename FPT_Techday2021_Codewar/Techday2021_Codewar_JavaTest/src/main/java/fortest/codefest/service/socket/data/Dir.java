package fortest.codefest.service.socket.data;

import com.google.gson.Gson;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class Dir {
    public static final String LEFT = "1";
    public static final String RIGHT = "2";
    public static final String UP = "3";
    public static final String DOWN = "4";
    public static final String DROP_BOMB = "b";
    public static final String INVALID = "";

    public static final Map<String, String> MOVE_TO_STRING = new HashMap<String, String>() {{
        put(UP, "UP");
        put(LEFT, "LEFT");
        put(DOWN, "DOWN");
        put(RIGHT, "RIGHT");
        put(INVALID, "INVALID");
        put(DROP_BOMB, "DROP BOMB");
    }};
    public static final Map<Integer, String> KEY_TO_STEP = new HashMap<Integer, String>() {{
        put(KeyCode.UP.ordinal(), UP);
        put(KeyCode.LEFT.ordinal(), LEFT);
        put(KeyCode.DOWN.ordinal(), DOWN);
        put(KeyCode.RIGHT.ordinal(), RIGHT);
        put(KeyCode.SPACE.ordinal(), DROP_BOMB);
    }};

    public final String direction;

    public Dir(String dir) {
        this.direction = dir;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
