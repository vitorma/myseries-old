package br.edu.ufcg.aweseries.util;

public class Validate {

    public static void isTrue(boolean assertion, String message) {
        isTrue(assertion, new IllegalArgumentException(message));
    }

    public static void isTrue(boolean assertion, String message, Object... messageArguments) {
        if (!assertion) throw new IllegalArgumentException(String.format(message, messageArguments));
    }

    public static <E extends Exception> void isTrue(boolean assertion, E exception) throws E {
        if (!assertion) throw exception;
    }

    public static void isNonNull(Object object, String message) {
        isNonNull(object, new IllegalArgumentException(message));
    }

    public static <E extends Exception> void isNonNull(Object object, E exception) throws E {
        if (object == null) throw exception;
    }
}
