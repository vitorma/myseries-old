package br.edu.ufcg.aweseries.util;

public abstract class Strings {
    public static boolean isBlank(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        return Strings.isEmpty(string.trim());
    }

    public static String normalizePipeSeparated(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        final String[] items = string.trim().split("\\|");
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

    public static boolean isEmpty(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        return string.equals("");
    }
}
