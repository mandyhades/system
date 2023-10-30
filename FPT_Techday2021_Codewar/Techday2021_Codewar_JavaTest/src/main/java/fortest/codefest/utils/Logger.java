package fortest.codefest.utils;

public class Logger {
    private static boolean isShowLog = true;

    public static void updateDebugMode(boolean debugMode) {
        isShowLog = debugMode;
    }

    public static void println(String log) {
        if (isShowLog) {
            System.out.println(log);
        }
    }
}
