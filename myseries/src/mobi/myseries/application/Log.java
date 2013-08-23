package mobi.myseries.application;

public class Log {

    public static interface Logger {
        public void d(String tag, String message);
    }

    public static final Logger ANDROID_LOGGER = new Logger() {

        @Override
        public void d(String tag, String message) {
            android.util.Log.d(tag, message);
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
}
