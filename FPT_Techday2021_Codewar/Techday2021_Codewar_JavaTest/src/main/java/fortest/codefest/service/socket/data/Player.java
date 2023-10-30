package fortest.codefest.service.socket.data;

import com.google.gson.Gson;
import fortest.codefest.service.caculator.test.TextUtils;
import fortest.codefest.utils.constant.Constants;

public class Player {
    public String id;
    public String skin;
    public Position spawnBegin;
    public boolean isAlive;
    public Position currentPosition;
    public int spaceStone;
    public int mindStone;
    public int realityStone;
    public int timeStone;
    public int soulStone;
    public int powerStone;
    public int power = 1;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void reInitValue() {
        power = 1;
        power = powerStone + power;
    }

    public boolean isMyPlayer() {
        if (Constants.MY_ID != null && Constants.MY_ID.length() != 0) {
            if (TextUtils.equals(Constants.MY_ID, id)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (TextUtils.equals(Constants.KEY_TEAM, id)) {
                return true;
            } else {
                return false;
            }
        }
    }
}

