package mobi.myseries.application;

public class Log {

    public static interface Logger {
        public void d(String tag, String message);
        public void w(String tag, String message, Throwable tr);
    }

    public static final Logger ANDROID_LOGGER = new Logger() {

        @Override
        public void d(String tag, String message) {
            android.util.Log.d(tag, message);
        }

        @Override
        public void w(String tag, String message, Throwable tr) {
            android.util.Log.w(tag, message, tr);
        }
    };

    private static volatile Logger logger;

    public static void setLogger(Logger newLogger) {
        logger = newLogger;
    }

    public static void d(String tag, String message) {
        Logger currentLogger = logger;  // avoiding concurrency issues.

        if (currentLogger != null) {
            currentLogger.d(tag, message);
        }
    }

    public static void w(String tag, String message, Throwable tr) {
        Logger currentLogger = logger;  // avoiding concurrency issues.

        if (currentLogger != null) {
            currentLogger.w(tag, message, tr);
        }
    }
}
