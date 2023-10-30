package fortest.codefest.service.caculator.test;

import fortest.codefest.utils.constant.Constants;

public class TextUtils {
    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }

    public static boolean equals(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.length() == 0 && s2.length() == 0) {
            return true;
        } else {
            if (s1.trim().toLowerCase().equals(s2.trim().toLowerCase())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isMyTeamID(String s1) {
        return equals(Constants.KEY_TEAM, s1);
    }
}
