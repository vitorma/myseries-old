package br.edu.ufcg.aweseries.util;

public class Validate {
    private static final String SHOULD_BE_NON_NULL = "%s should be non-null";
    private static final String SHOULD_BE_NON_BLANK = "%s should be non-blank";

    //IsTrue------------------------------------------------------------------------------------------------------------

    public static <E extends Exception> void isTrue(boolean assertion, E exception) throws E {
        if (!assertion) throw exception;
    }

    public static void isTrue(boolean assertion, String message, Object... messageArguments) {
        if (!assertion) {
            
            if (message == null) { throw new IllegalArgumentException(); } 
            
            throw new IllegalArgumentException(String.format(message, messageArguments));
        }
    }

    //IsNonNull---------------------------------------------------------------------------------------------------------

    public static <E extends Exception> void isNonNull(Object object, E exception) throws E {
        isTrue(object != null, exception);
    }

    public static void isNonNull(Object object, String message, Object... messageArguments) {
        isTrue(object != null, message, messageArguments);
    }

    public static void isNonNull(Object object, String alias) {
        isNonNull(object, SHOULD_BE_NON_NULL, alias);
    }

    //IsNonBlank--------------------------------------------------------------------------------------------------------

    public static <E extends Exception> void isNonBlank(String string, E exception) throws E {
        isNonNull(string, exception);
        isTrue(string.trim().length() > 0, exception);
    }

    public static void isNonBlank(String string, String message, Object... messageArguments) {
        isNonNull(string, message, messageArguments);
        isTrue(string.trim().length() > 0, message, messageArguments);
    }

    public static void isNonBlank(String string, String alias) {
        isNonBlank(string, SHOULD_BE_NON_BLANK, alias);
    }
}
