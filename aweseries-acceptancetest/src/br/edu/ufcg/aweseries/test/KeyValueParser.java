package br.edu.ufcg.aweseries.test;

import br.edu.ufcg.aweseries.util.Strings;

public class KeyValueParser {

    public static class KeyValuePair {
        public String key;
        public String value;
    }

    private static final String MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE = "Malformed key value pair";

    private static String SEPARATOR = "\\s*:\\s*";

    public KeyValuePair parse(String pair) {
        if (pair == null) {
            throw new IllegalArgumentException(MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE);
        }

        String[] parts = pair.split(SEPARATOR, 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE);
        }

        KeyValuePair parsedPair = new KeyValuePair();
        parsedPair.key = parts[0];
        parsedPair.value = parts[1];

        if (Strings.isBlank(parsedPair.key)) {
            throw new IllegalArgumentException(MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE);
        }

        return parsedPair;
    }
}