package fortest.codefest.service.socket.data;

import com.google.gson.Gson;

public class GameInfo {
    public MapInfo map_info;
    public String tag;
    public String player_id;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
