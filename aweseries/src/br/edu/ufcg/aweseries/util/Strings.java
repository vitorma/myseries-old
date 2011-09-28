package br.edu.ufcg.aweseries.util;

import java.util.regex.Pattern;

public abstract class Strings {
    private static final Pattern PIPE_SEPARATED_STRING =
        Pattern.compile("(\\|([^\\|]+(\\|[^\\|]+)*)*\\|)?");

    public static boolean isBlank(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        return string.trim().isEmpty();
    }

    private static boolean isPipeSeparated(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        return PIPE_SEPARATED_STRING.matcher(string).matches();
    }

    public static String normalizePipeSeparated(String string) {
        if (!isPipeSeparated(string)) {
            throw new IllegalArgumentException("string should be pipe separated");
        }

        final String[] items = string.split("\\|");
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < items.length; i++) {
            if (!isBlank(items[i])) {
                builder.append(items[i].trim());
                if (i < items.length - 1) {
                    builder.append(", ");
                }
            }
        }

        return builder.toString();
    }
}
