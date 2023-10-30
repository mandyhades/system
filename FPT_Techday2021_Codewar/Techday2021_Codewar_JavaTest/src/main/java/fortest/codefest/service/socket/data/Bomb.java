package fortest.codefest.service.socket.data;

import com.google.gson.Gson;
import fortest.codefest.service.caculator.test.TextUtils;


public class Bomb extends Position {
    public int remainTime;
    public String playerId;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean isMyBom() {
        if (playerId != null && playerId.length() != 0) {
            if (TextUtils.isMyTeamID(playerId)) {
                return true;
            }
        }
        return false;
    }
}
