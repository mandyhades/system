package fortest.codefest.utils;

import fortest.codefest.service.socket.data.Dir;

public class TextUtils {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }

    public static String chainOfStepsToString(String steps) {
        if (!isEmpty(steps)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < steps.length(); i++) {
                int dir;
                try {
                    String step = String.valueOf(steps.charAt(i));
                    dir = Integer.parseInt(step);
                } catch (NumberFormatException e) {
                    Logger.println("ERROR: INVALID STEP!");
                    return "INVALID STEP!!!";
                }
                builder.append("<");
                builder.append(Dir.MOVE_TO_STRING.get(dir));
                builder.append(">");
            }
            return builder.toString();
        }
        return Dir.INVALID;
    }
}
