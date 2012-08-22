package mobi.myseries.shared;

import java.util.Collection;

public class Validate {
    private static final String SHOULD_BE_NON_NULL = "%s should be non-null";
    private static final String SHOULD_BE_NON_BLANK = "%s should be non-blank";
    private static final String SHOULD_ALL_BE_NON_NULL = "all elements of %s should be non-null";
    private static final String SHOULD_BE_NON_NEGATIVE = "%s should be non-negative";

    //IsTrue------------------------------------------------------------------------------------------------------------

    public static <E extends Exception> void isTrue(boolean assertion, E exception) throws E {
        if (!assertion) throw exception;
    }

    public static void isTrue(boolean assertion, String message, Object... messageArguments) {
        if (!assertion) {
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

    //AllNonNull--------------------------------------------------------------------------------------------------------

    public static <T> void allNonNull(Collection<T> collection, String alias) {
        isNonNull(collection, SHOULD_BE_NON_NULL, alias);

        for (T t : collection) {
            isNonNull(t, SHOULD_ALL_BE_NON_NULL, alias);
        }
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

    //IsNonNegative-----------------------------------------------------------------------------------------------------

    public static void isNonNegative(int value, String alias) {
        isTrue(value >= 0, SHOULD_BE_NON_NEGATIVE, alias);
    }
}
